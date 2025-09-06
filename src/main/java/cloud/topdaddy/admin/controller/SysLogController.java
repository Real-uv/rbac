package cloud.topdaddy.admin.controller;

import cloud.topdaddy.admin.common.Result;
import cloud.topdaddy.admin.entity.SysLoginLog;
import cloud.topdaddy.admin.entity.SysOperationLog;
import cloud.topdaddy.admin.servcie.SysLoginLogService;
import cloud.topdaddy.admin.servcie.SysOperationLogService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 日志管理控制器
 * 
 * @author topdaddy
 */
@Slf4j
@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
@Tag(name = "日志管理", description = "系统日志管理相关接口")
public class SysLogController {

    private final SysOperationLogService operationLogService;
    private final SysLoginLogService loginLogService;

    @GetMapping("/operation/page")
    @Operation(summary = "分页查询操作日志")
    @PreAuthorize("hasAuthority('log:operation:list')")
    public Result<IPage<SysOperationLog>> pageOperationLogs(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "操作模块") @RequestParam(required = false) String module,
            @Parameter(description = "操作类型") @RequestParam(required = false) String type,
            @Parameter(description = "用户名") @RequestParam(required = false) String username,
            @Parameter(description = "开始时间") @RequestParam(required = false) String startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) String endTime) {
        
        IPage<SysOperationLog> page = operationLogService.pageOperationLogs(
            pageNum, pageSize, module, type, username, startTime, endTime);
        return Result.success(page);
    }

    @GetMapping("/login/page")
    @Operation(summary = "分页查询登录日志")
    @PreAuthorize("hasAuthority('log:login:list')")
    public Result<IPage<SysLoginLog>> pageLoginLogs(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "用户名") @RequestParam(required = false) String username,
            @Parameter(description = "登录状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "开始时间") @RequestParam(required = false) String startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) String endTime) {
        
        IPage<SysLoginLog> page = loginLogService.pageLoginLogs(
            pageNum, pageSize, username, status, startTime, endTime);
        return Result.success(page);
    }

    @DeleteMapping("/operation/clean")
    @Operation(summary = "清理过期操作日志")
    @PreAuthorize("hasAuthority('log:operation:clean')")
    public Result<String> cleanExpiredOperationLogs(
            @Parameter(description = "保留天数") @RequestParam(defaultValue = "30") Integer days) {
        operationLogService.cleanExpiredLogs(days);
        return Result.success("清理操作日志完成");
    }

    @DeleteMapping("/login/clean")
    @Operation(summary = "清理过期登录日志")
    @PreAuthorize("hasAuthority('log:login:clean')")
    public Result<String> cleanExpiredLoginLogs(
            @Parameter(description = "保留天数") @RequestParam(defaultValue = "30") Integer days) {
        loginLogService.cleanLogs(days);
        return Result.success("清理登录日志完成");
    }
}