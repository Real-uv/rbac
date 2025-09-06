package cloud.topdaddy.admin.servcie;

import cloud.topdaddy.admin.dto.LoginRequest;
import java.util.Map;

/**
 * 认证服务接口
 *
 * @author topdaddy
 */
public interface AuthService {

    /**
     * 用户登录
     */
    Map<String, Object> login(LoginRequest request);

    /**
     * 用户登出
     */
    boolean logout(String token);

    /**
     * 刷新令牌
     */
    Map<String, Object> refreshToken(String refreshToken);

    /**
     * 获取当前用户信息
     */
    Map<String, Object> getCurrentUser();

    /**
     * 生成验证码
     */
    Map<String, Object> generateCaptcha();

    /**
     * 验证验证码
     */
    boolean verifyCaptcha(String captchaKey, String captcha);
}
