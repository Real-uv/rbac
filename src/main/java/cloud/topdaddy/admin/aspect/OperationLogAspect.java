package cloud.topdaddy.admin.aspect;

import cloud.topdaddy.admin.entity.SysOperationLog;
import cloud.topdaddy.admin.mapper.SysOperationLogMapper;
import cloud.topdaddy.admin.security.UserDetailsImpl;
import cloud.topdaddy.admin.utils.IpUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalDateTime;

/**
 * 操作日志切面
 * 
 * @author topdaddy
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final SysOperationLogMapper operationLogMapper;
    
    private final ThreadLocal<Long> startTimeThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<SysOperationLog> logThreadLocal = new ThreadLocal<>();

    /**
     * 操作日志注解
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface OperationLog {
        /**
         * 操作名称
         */
        String value() default "";
        
        /**
         * 操作类型
         */
        String type() default "";
    }

    /**
     * 前置通知
     */
    @Before("@annotation(operationLog)")
    public void before(JoinPoint joinPoint, OperationLog operationLog) {
        startTimeThreadLocal.set(System.currentTimeMillis());
        
        try {
            // 创建操作日志对象
            SysOperationLog log = new SysOperationLog();
            log.setTraceId(IdUtil.simpleUUID());
            log.setOperation(operationLog.value());
            log.setMethod(joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
            
            // 获取请求参数
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                try {
                    log.setParams(JSON.toJSONString(args));
                } catch (Exception e) {
                    log.setParams("参数序列化失败");
                }
            }
            
            // 获取用户信息
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
                log.setUserId(userDetails.getUserId());
                log.setUsername(userDetails.getUsername());
            }
            
            // 获取请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                log.setIp(IpUtil.getClientIp(request));
                log.setUserAgent(request.getHeader("User-Agent"));
            }
            
            logThreadLocal.set(log);
            
        } catch (Exception e) {
            log.error("记录操作日志前置处理失败", e);
        }
    }

    /**
     * 成功返回通知
     */
    @AfterReturning(value = "@annotation(operationLog)", returning = "result")
    public void afterReturning(JoinPoint joinPoint, OperationLog operationLog, Object result) {
        handleLog(result, null);
    }

    /**
     * 异常通知
     */
    @AfterThrowing(value = "@annotation(operationLog)", throwing = "exception")
    public void afterThrowing(JoinPoint joinPoint, OperationLog operationLog, Exception exception) {
        handleLog(null, exception);
    }

    /**
     * 处理日志
     */
    private void handleLog(Object result, Exception exception) {
        try {
            SysOperationLog operationLog = logThreadLocal.get();
            if (operationLog == null) {
                return;
            }
            
            Long startTime = startTimeThreadLocal.get();
            if (startTime != null) {
                operationLog.setCostTime((int) (System.currentTimeMillis() - startTime));
            }
            
            if (exception != null) {
                operationLog.setStatus(0);
                operationLog.setErrorMsg(exception.getMessage());
            } else {
                operationLog.setStatus(1);
                if (result != null) {
                    try {
                        operationLog.setResult(JSON.toJSONString(result));
                    } catch (Exception e) {
                        operationLog.setResult("返回结果序列化失败");
                    }
                }
            }
            
            operationLog.setCreateTime(LocalDateTime.now());
            
            // 异步保存日志
            saveLogAsync(operationLog);
            
        } catch (Exception e) {
            log.error("记录操作日志失败", e);
        } finally {
            // 清理ThreadLocal
            startTimeThreadLocal.remove();
            logThreadLocal.remove();
        }
    }

    /**
     * 异步保存日志
     */
    private void saveLogAsync(SysOperationLog operationLog) {
        try {
            operationLogMapper.insert(operationLog);
        } catch (Exception e) {
            log.error("保存操作日志失败", e);
        }
    }
}