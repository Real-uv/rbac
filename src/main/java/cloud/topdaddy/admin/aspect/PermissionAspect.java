package cloud.topdaddy.admin.aspect;

import cloud.topdaddy.admin.common.ResultCode;
import cloud.topdaddy.admin.entity.SysPermission;
import cloud.topdaddy.admin.exception.BusinessException;
import cloud.topdaddy.admin.security.UserDetailsImpl;
import cloud.topdaddy.admin.servcie.SysPermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 权限验证切面
 * 
 * @author topdaddy
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    private final SysPermissionService permissionService;

    @Around("@annotation(RequirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        // 获取权限注解
        RequirePermission requirePermission = method.getAnnotation(RequirePermission.class);
        if (requirePermission == null) {
            return joinPoint.proceed();
        }

        String permissionCode = requirePermission.value();
        
        // 获取当前用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw BusinessException.of(ResultCode.UNAUTHORIZED);
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetailsImpl)) {
            throw BusinessException.of(ResultCode.UNAUTHORIZED);
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) principal;
        Long userId = userDetails.getUserId();

        // 检查用户是否具有所需权限
        if (!hasPermission(userId, permissionCode)) {
            log.warn("用户 {} 尝试访问无权限的资源: {}", userId, permissionCode);
            throw BusinessException.of(ResultCode.FORBIDDEN);
        }

        // 执行目标方法
        return joinPoint.proceed();
    }

    /**
     * 检查用户是否具有指定权限
     */
    private boolean hasPermission(Long userId, String permissionCode) {
        try {
            // 获取用户所有权限
            List<SysPermission> userPermissions = permissionService.getPermissionsByUserId(userId);
            
            if (CollectionUtils.isEmpty(userPermissions)) {
                return false;
            }

            // 检查是否包含所需权限
            return userPermissions.stream()
                    .anyMatch(permission -> permissionCode.equals(permission.getCode()));
                    
        } catch (Exception e) {
            log.error("检查用户权限时发生异常，用户ID: {}, 权限编码: {}", userId, permissionCode, e);
            return false;
        }
    }
}