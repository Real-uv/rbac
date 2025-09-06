package cloud.topdaddy.admin.controller;

import cloud.topdaddy.admin.common.Result;
import cloud.topdaddy.admin.servcie.CacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 系统管理控制器
 * 
 * @author topdaddy
 */
@Slf4j
@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
@Tag(name = "系统管理", description = "系统监控和管理相关接口")
public class SystemController {

    private final CacheService cacheService;

    @GetMapping("/info")
    @Operation(summary = "获取系统信息")
    @PreAuthorize("hasAuthority('system:info')")
    public Result<Map<String, Object>> getSystemInfo() {
        Map<String, Object> systemInfo = new HashMap<>();
        
        // JVM信息
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        
        Map<String, Object> jvmInfo = new HashMap<>();
        jvmInfo.put("javaVersion", System.getProperty("java.version"));
        jvmInfo.put("javaVendor", System.getProperty("java.vendor"));
        jvmInfo.put("jvmName", runtimeMXBean.getVmName());
        jvmInfo.put("jvmVersion", runtimeMXBean.getVmVersion());
        jvmInfo.put("startTime", runtimeMXBean.getStartTime());
        jvmInfo.put("uptime", runtimeMXBean.getUptime());
        
        // 内存信息
        Map<String, Object> memoryInfo = new HashMap<>();
        memoryInfo.put("heapMemoryUsed", memoryMXBean.getHeapMemoryUsage().getUsed());
        memoryInfo.put("heapMemoryMax", memoryMXBean.getHeapMemoryUsage().getMax());
        memoryInfo.put("nonHeapMemoryUsed", memoryMXBean.getNonHeapMemoryUsage().getUsed());
        memoryInfo.put("nonHeapMemoryMax", memoryMXBean.getNonHeapMemoryUsage().getMax());
        
        // 系统信息
        Map<String, Object> osInfo = new HashMap<>();
        osInfo.put("osName", System.getProperty("os.name"));
        osInfo.put("osArch", System.getProperty("os.arch"));
        osInfo.put("osVersion", System.getProperty("os.version"));
        osInfo.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        
        systemInfo.put("jvm", jvmInfo);
        systemInfo.put("memory", memoryInfo);
        systemInfo.put("os", osInfo);
        
        return Result.success(systemInfo);
    }

    @GetMapping("/online-users")
    @Operation(summary = "获取在线用户列表")
    @PreAuthorize("hasAuthority('system:online:list')")
    public Result<Set<String>> getOnlineUsers() {
        Set<String> onlineUsers = cacheService.getOnlineUsers();
        return Result.success(onlineUsers);
    }

    @PostMapping("/force-offline/{userId}")
    @Operation(summary = "强制用户下线")
    @PreAuthorize("hasAuthority('system:online:offline')")
    public Result<String> forceUserOffline(@PathVariable Long userId) {
        cacheService.forceUserOffline(userId);
        return Result.success("用户已强制下线");
    }

    @DeleteMapping("/cache/clear")
    @Operation(summary = "清除所有缓存")
    @PreAuthorize("hasAuthority('system:cache:clear')")
    public Result<String> clearAllCache() {
        cacheService.clearAllUserCache();
        return Result.success("缓存清除成功");
    }

    @DeleteMapping("/cache/user/{userId}")
    @Operation(summary = "清除指定用户缓存")
    @PreAuthorize("hasAuthority('system:cache:clear')")
    public Result<String> clearUserCache(@PathVariable Long userId) {
        cacheService.refreshUserCache(userId);
        return Result.success("用户缓存清除成功");
    }

    @PostMapping("/gc")
    @Operation(summary = "执行垃圾回收")
    @PreAuthorize("hasAuthority('system:gc')")
    public Result<String> performGC() {
        System.gc();
        return Result.success("垃圾回收执行完成");
    }

    @GetMapping("/health")
    @Operation(summary = "健康检查")
    public Result<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        
        // 检查数据库连接等
        // 这里可以添加更多的健康检查逻辑
        
        return Result.success(health);
    }
}