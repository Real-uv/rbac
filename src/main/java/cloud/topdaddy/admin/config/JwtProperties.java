package cloud.topdaddy.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT配置属性
 * 
 * @author topdaddy
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT密钥
     */
    private String secret = "rbac-admin-system-jwt-secret-key-2024";

    /**
     * JWT过期时间（毫秒）
     */
    private Long expiration = 86400000L; // 24小时

    /**
     * 刷新Token过期时间（毫秒）
     */
    private Long refreshExpiration = 604800000L; // 7天

    /**
     * Token前缀
     */
    private String tokenPrefix = "Bearer ";

    /**
     * Token请求头名称
     */
    private String tokenHeader = "Authorization";
}