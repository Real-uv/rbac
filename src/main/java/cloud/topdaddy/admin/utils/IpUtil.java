package cloud.topdaddy.admin.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * IP地址工具类
 *
 * @author topdaddy
 */
@Slf4j
public class IpUtil {

    /**
     * 获取客户端真实IP地址
     *
     * @param request HttpServletRequest对象
     * @return 客户端IP地址
     */
    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }

        String ip = null;

        // X-Forwarded-For：Squid服务代理
        ip = request.getHeader("X-Forwarded-For");
        if (isInvalidIp(ip)) {
            // Proxy-Client-IP：apache服务代理
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (isInvalidIp(ip)) {
            // WL-Proxy-Client-IP：weblogic服务代理
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (isInvalidIp(ip)) {
            // HTTP_CLIENT_IP：有些代理服务器
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (isInvalidIp(ip)) {
            // X-Real-IP：nginx服务代理
            ip = request.getHeader("X-Real-IP");
        }
        if (isInvalidIp(ip)) {
            // 获取发送请求的客户端的IP地址
            ip = request.getRemoteAddr();
        }

        // 处理多个IP的情况（只取第一个IP）
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0];
        }

        // 本地回环地址处理
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }

        return ip;
    }

    /**
     * 判断IP是否无效
     *
     * @param ip IP地址
     * @return true-无效，false-有效
     */
    private static boolean isInvalidIp(String ip) {
        return !StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip);
    }
}

