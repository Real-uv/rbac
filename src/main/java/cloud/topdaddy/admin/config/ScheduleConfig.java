package cloud.topdaddy.admin.config;

import cloud.topdaddy.admin.servcie.CacheService;
import cloud.topdaddy.admin.servcie.SysLoginLogService;
import cloud.topdaddy.admin.servcie.SysOperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 定时任务配置
 * 
 * @author topdaddy
 */
@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
@ConditionalOnProperty(name = "rbac.schedule.enabled", havingValue = "true", matchIfMissing = true)
public class ScheduleConfig {

    private final SysOperationLogService operationLogService;
    private final SysLoginLogService loginLogService;
    private final CacheService cacheService;

    /**
     * 每天凌晨2点清理过期操作日志（保留30天）
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanExpiredOperationLogs() {
        try {
            log.info("开始清理过期操作日志");
            operationLogService.cleanExpiredLogs(30);
            log.info("清理过期操作日志完成");
        } catch (Exception e) {
            log.error("清理过期操作日志失败", e);
        }
    }

    /**
     * 每天凌晨2点30分清理过期登录日志（保留30天）
     */
    @Scheduled(cron = "0 30 2 * * ?")
    public void cleanExpiredLoginLogs() {
        try {
            log.info("开始清理过期登录日志");
            loginLogService.cleanLogs(30);
            log.info("清理过期登录日志完成");
        } catch (Exception e) {
            log.error("清理过期登录日志失败", e);
        }
    }

    /**
     * 每小时清理过期缓存
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void cleanExpiredCache() {
        try {
            log.debug("开始清理过期缓存");
            cacheService.cleanExpiredCache();
            log.debug("清理过期缓存完成");
        } catch (Exception e) {
            log.error("清理过期缓存失败", e);
        }
    }

    /**
     * 每天凌晨1点执行垃圾回收
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void performGarbageCollection() {
        try {
            log.info("开始执行垃圾回收");
            System.gc();
            log.info("垃圾回收执行完成");
        } catch (Exception e) {
            log.error("执行垃圾回收失败", e);
        }
    }
}