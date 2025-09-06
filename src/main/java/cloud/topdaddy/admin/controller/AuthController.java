package cloud.topdaddy.admin.controller;

import cloud.topdaddy.admin.common.Result;
import cloud.topdaddy.admin.config.JwtProperties;
import cloud.topdaddy.admin.dto.LoginRequest;
import cloud.topdaddy.admin.servcie.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * 认证控制器
 * 
 * @author topdaddy
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtProperties jwtProperties;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Validated @RequestBody LoginRequest request) {
        Map<String, Object> result = authService.login(request);
        return Result.success("登录成功", result);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        authService.logout(token);
        return Result.success("登出成功");
    }

    /**
     * 刷新令牌
     */
    @PostMapping("/refresh")
    public Result<Map<String, Object>> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        Map<String, Object> result = authService.refreshToken(refreshToken);
        return Result.success("刷新令牌成功", result);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/user")
    public Result<Map<String, Object>> getCurrentUser() {
        Map<String, Object> user = authService.getCurrentUser();
        return Result.success(user);
    }

    /**
     * 生成验证码
     */
    @GetMapping("/captcha")
    public Result<Map<String, Object>> generateCaptcha() {
        Map<String, Object> captcha = authService.generateCaptcha();
        return Result.success(captcha);
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("系统运行正常");
    }

    /**
     * 从请求中获取JWT令牌
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(jwtProperties.getTokenHeader());
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(jwtProperties.getTokenPrefix())) {
            return bearerToken.substring(jwtProperties.getTokenPrefix().length());
        }
        
        return null;
    }
}