package cloud.topdaddy.admin.servcie.impl;

import cloud.topdaddy.admin.common.ResultCode;
import cloud.topdaddy.admin.dto.LoginRequest;
import cloud.topdaddy.admin.entity.SysLoginLog;
import cloud.topdaddy.admin.entity.SysUser;
import cloud.topdaddy.admin.exception.BusinessException;
import cloud.topdaddy.admin.security.JwtAuthenticationFilter;
import cloud.topdaddy.admin.security.JwtTokenUtil;
import cloud.topdaddy.admin.security.UserDetailsImpl;
import cloud.topdaddy.admin.servcie.AuthService;
import cloud.topdaddy.admin.servcie.SysLoginLogService;
import cloud.topdaddy.admin.servcie.SysUserService;
import cloud.topdaddy.admin.utils.IpUtil;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.servlet.ServletUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现类
 * 
 * @author topdaddy
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final SysUserService userService;
    private final SysLoginLogService loginLogService;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CAPTCHA_PREFIX = "captcha:";
    private static final long CAPTCHA_EXPIRE_TIME = 5; // 验证码过期时间（分钟）

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> login(LoginRequest request) {
        String clientIp = getClientIp();
        
        try {
            // 验证验证码
            if (StringUtils.hasText(request.getCaptchaKey()) && StringUtils.hasText(request.getCaptcha())) {
                if (!verifyCaptcha(request.getCaptchaKey(), request.getCaptcha())) {
                    recordLoginLog(request.getUsername(), clientIp, 0, "验证码错误");
                    throw BusinessException.of(ResultCode.PARAM_ERROR, "验证码错误");
                }
            }

            // 执行认证
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            // 检查用户状态
            if (!userDetails.isEnabled()) {
                recordLoginLog(request.getUsername(), clientIp, 0, "账号已禁用");
                throw BusinessException.of(ResultCode.ACCOUNT_DISABLED);
            }

            // 生成令牌
            String accessToken = jwtTokenUtil.generateAccessToken(userDetails.getUserId(), userDetails.getUsername());
            String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails.getUserId(), userDetails.getUsername());

            // 更新用户最后登录信息
            userService.updateLastLoginInfo(userDetails.getUserId(), clientIp);

            // 记录登录日志
            recordLoginLog(request.getUsername(), clientIp, 1, "登录成功");

            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("accessToken", accessToken);
            result.put("refreshToken", refreshToken);
            result.put("tokenType", "Bearer");
            result.put("expiresIn", jwtTokenUtil.getExpirationDateFromToken(accessToken).getTime());
            result.put("user", buildUserInfo(userDetails));

            log.info("用户登录成功: {}", request.getUsername());
            return result;

        } catch (Exception e) {
            recordLoginLog(request.getUsername(), clientIp, 0, e.getMessage());
            if (e instanceof BusinessException) {
                throw e;
            }
            throw BusinessException.of(ResultCode.LOGIN_FAILED, "登录失败");
        }
    }

    @Override
    public boolean logout(String token) {
        try {
            if (StringUtils.hasText(token)) {
                // 将令牌加入黑名单
                jwtAuthenticationFilter.addTokenToBlacklist(token);
                
                // 清除安全上下文
                SecurityContextHolder.clearContext();
                
                log.info("用户登出成功");
                return true;
            }
        } catch (Exception e) {
            log.error("用户登出失败", e);
        }
        return false;
    }

    @Override
    public Map<String, Object> refreshToken(String refreshToken) {
        try {
            // 验证刷新令牌
            if (!jwtTokenUtil.isValidTokenFormat(refreshToken) || !jwtTokenUtil.isRefreshToken(refreshToken)) {
                throw BusinessException.of(ResultCode.TOKEN_INVALID, "刷新令牌无效");
            }

            String username = jwtTokenUtil.getUsernameFromToken(refreshToken);
            Long userId = jwtTokenUtil.getUserIdFromToken(refreshToken);

            if (!StringUtils.hasText(username) || userId == null) {
                throw BusinessException.of(ResultCode.TOKEN_INVALID, "刷新令牌无效");
            }

            // 验证令牌是否过期
            if (jwtTokenUtil.isTokenExpired(refreshToken)) {
                throw BusinessException.of(ResultCode.TOKEN_EXPIRED, "刷新令牌已过期");
            }

            // 验证用户是否存在且有效
            SysUser user = userService.getByUsername(username);
            if (user == null || user.getStatus() != 1) {
                throw BusinessException.of(ResultCode.USER_NOT_FOUND, "用户不存在或已禁用");
            }

            // 生成新的访问令牌
            String newAccessToken = jwtTokenUtil.generateAccessToken(userId, username);
            String newRefreshToken = jwtTokenUtil.generateRefreshToken(userId, username);

            // 将旧的刷新令牌加入黑名单
            jwtAuthenticationFilter.addTokenToBlacklist(refreshToken);

            Map<String, Object> result = new HashMap<>();
            result.put("accessToken", newAccessToken);
            result.put("refreshToken", newRefreshToken);
            result.put("tokenType", "Bearer");
            result.put("expiresIn", jwtTokenUtil.getExpirationDateFromToken(newAccessToken).getTime());

            log.info("刷新令牌成功: {}", username);
            return result;

        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw e;
            }
            throw BusinessException.of(ResultCode.TOKEN_INVALID, "刷新令牌失败");
        }
    }

    @Override
    public Map<String, Object> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            return buildUserInfo(userDetails);
        }
        throw BusinessException.of(ResultCode.UNAUTHORIZED, "未认证");
    }

    @Override
    public Map<String, Object> generateCaptcha() {
        // 生成验证码
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(120, 40, 4, 20);
        String captchaKey = IdUtil.simpleUUID();
        String captchaCode = captcha.getCode();

        // 存储到Redis
        redisTemplate.opsForValue().set(
            CAPTCHA_PREFIX + captchaKey, 
            captchaCode.toLowerCase(), 
            CAPTCHA_EXPIRE_TIME, 
            TimeUnit.MINUTES
        );

        Map<String, Object> result = new HashMap<>();
        result.put("captchaKey", captchaKey);
        result.put("captchaImage", captcha.getImageBase64Data());
        result.put("expiresIn", CAPTCHA_EXPIRE_TIME * 60); // 秒

        return result;
    }

    @Override
    public boolean verifyCaptcha(String captchaKey, String captcha) {
        if (!StringUtils.hasText(captchaKey) || !StringUtils.hasText(captcha)) {
            return false;
        }

        try {
            String key = CAPTCHA_PREFIX + captchaKey;
            String storedCaptcha = (String) redisTemplate.opsForValue().get(key);
            
            if (storedCaptcha != null && storedCaptcha.equals(captcha.toLowerCase())) {
                // 验证成功后删除验证码
                redisTemplate.delete(key);
                return true;
            }
        } catch (Exception e) {
            log.error("验证码验证失败", e);
        }
        
        return false;
    }

    /**
     * 构建用户信息
     */
    private Map<String, Object> buildUserInfo(UserDetailsImpl userDetails) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", userDetails.getUserId());
        userInfo.put("username", userDetails.getUsername());
        userInfo.put("nickname", userDetails.getNickname());
        userInfo.put("email", userDetails.getEmail());
        userInfo.put("phone", userDetails.getPhone());
        userInfo.put("avatar", userDetails.getAvatar());
        userInfo.put("gender", userDetails.getGender());
        userInfo.put("status", userDetails.getStatus());
        userInfo.put("permissions", userDetails.getPermissions());
        userInfo.put("roles", userDetails.getRoles());
        return userInfo;
    }

    /**
     * 记录登录日志
     */
    private void recordLoginLog(String username, String ip, Integer status, String message) {
        try {
            SysLoginLog loginLog = new SysLoginLog();
            loginLog.setUsername(username);
            loginLog.setIp(ip);
            loginLog.setStatus(status);
            // 限制message字段长度，避免数据截断
            if (message != null && message.length() > 200) {
                message = message.substring(0, 200) + "...";
            }
            loginLog.setMessage(message);
            loginLog.setUserAgent(getUserAgent());
            loginLogService.save(loginLog);
        } catch (Exception e) {
            log.error("记录登录日志失败", e);
        }
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return IpUtil.getClientIp(request);
            }
        } catch (Exception e) {
            log.debug("获取客户端IP失败", e);
        }
        return "unknown";
    }

    /**
     * 获取用户代理
     */
    private String getUserAgent() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return request.getHeader("User-Agent");
            }
        } catch (Exception e) {
            log.debug("获取用户代理失败", e);
        }
        return "unknown";
    }
}