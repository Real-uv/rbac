package cloud.topdaddy.admin.security;

import cloud.topdaddy.admin.config.JwtProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * JWT认证过滤器
 * 
 * @author topdaddy
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final JwtProperties jwtProperties;
    private final UserDetailsService userDetailsService;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String TOKEN_BLACKLIST_PREFIX = "jwt:blacklist:";

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // 获取JWT令牌
            String token = getTokenFromRequest(request);
            
            if (StringUtils.hasText(token) && jwtTokenUtil.isValidTokenFormat(token)) {
                // 检查令牌是否在黑名单中
                if (isTokenBlacklisted(token)) {
                    log.debug("令牌已被加入黑名单");
                    filterChain.doFilter(request, response);
                    return;
                }
                
                // 获取用户名
                String username = jwtTokenUtil.getUsernameFromToken(token);
                
                if (StringUtils.hasText(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 加载用户详情
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    
                    // 验证令牌
                    if (jwtTokenUtil.validateToken(token, userDetails.getUsername())) {
                        // 创建认证对象
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        
                        // 设置到安全上下文
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        
                        log.debug("用户 {} 认证成功", username);
                    }
                }
            }
        } catch (Exception e) {
            log.error("JWT认证过程中发生异常", e);
        }
        
        filterChain.doFilter(request, response);
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

    /**
     * 检查令牌是否在黑名单中
     */
    private boolean isTokenBlacklisted(String token) {
        try {
            String key = TOKEN_BLACKLIST_PREFIX + token;
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("检查令牌黑名单失败", e);
            return false;
        }
    }

    /**
     * 将令牌加入黑名单
     */
    public void addTokenToBlacklist(String token) {
        try {
            String key = TOKEN_BLACKLIST_PREFIX + token;
            // 设置过期时间为令牌的剩余有效期
            long expiration = jwtTokenUtil.getExpirationDateFromToken(token).getTime() - System.currentTimeMillis();
            if (expiration > 0) {
                redisTemplate.opsForValue().set(key, "blacklisted", expiration, TimeUnit.MILLISECONDS);
                log.debug("令牌已加入黑名单");
            }
        } catch (Exception e) {
            log.error("将令牌加入黑名单失败", e);
        }
    }
}