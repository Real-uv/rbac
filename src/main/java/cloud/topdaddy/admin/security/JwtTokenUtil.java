package cloud.topdaddy.admin.security;

import cloud.topdaddy.admin.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * 
 * @author topdaddy
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenUtil {

    private final JwtProperties jwtProperties;

    /**
     * 获取签名密钥
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成访问令牌
     */
    public String generateAccessToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("type", "access");
        
        return createToken(claims, username, jwtProperties.getExpiration());
    }

    /**
     * 生成刷新令牌
     */
    public String generateRefreshToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("type", "refresh");
        
        return createToken(claims, username, jwtProperties.getRefreshExpiration());
    }

    /**
     * 创建令牌
     */
    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 从令牌中获取用户名
     */
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getSubject();
        } catch (Exception e) {
            log.error("获取用户名失败", e);
            return null;
        }
    }

    /**
     * 从令牌中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.get("userId", Long.class);
        } catch (Exception e) {
            log.error("获取用户ID失败", e);
            return null;
        }
    }

    /**
     * 从令牌中获取过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getExpiration();
        } catch (Exception e) {
            log.error("获取过期时间失败", e);
            return null;
        }
    }

    /**
     * 从令牌中获取声明
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 验证令牌是否过期
     */
    public Boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration != null && expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 验证令牌
     */
    public Boolean validateToken(String token, String username) {
        try {
            String tokenUsername = getUsernameFromToken(token);
            return tokenUsername != null && 
                   tokenUsername.equals(username) && 
                   !isTokenExpired(token);
        } catch (Exception e) {
            log.error("令牌验证失败", e);
            return false;
        }
    }

    /**
     * 验证令牌格式
     */
    public Boolean isValidTokenFormat(String token) {
        // 基本格式检查：JWT应该包含两个点号分隔符
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        
        // JWT格式检查：应该有3个部分（header.payload.signature）
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return false;
        }
        
        try {
            Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 只在非空令牌时记录日志，避免垃圾日志
            if (token.length() > 10) {
                log.debug("令牌格式无效: {}", e.getMessage());
            }
            return false;
        }
    }

    /**
     * 获取令牌类型
     */
    public String getTokenType(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.get("type", String.class);
        } catch (Exception e) {
            log.error("获取令牌类型失败", e);
            return null;
        }
    }

    /**
     * 检查是否为访问令牌
     */
    public Boolean isAccessToken(String token) {
        return "access".equals(getTokenType(token));
    }

    /**
     * 检查是否为刷新令牌
     */
    public Boolean isRefreshToken(String token) {
        return "refresh".equals(getTokenType(token));
    }
}