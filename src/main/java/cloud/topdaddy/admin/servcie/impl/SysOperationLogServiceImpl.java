package cloud.topdaddy.admin.servcie.impl;

import cloud.topdaddy.admin.entity.SysOperationLog;
import cloud.topdaddy.admin.mapper.SysOperationLogMapper;
import cloud.topdaddy.admin.servcie.SysOperationLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 操作日志服务实现类
 * 
 * @author topdaddy
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysOperationLogServiceImpl extends ServiceImpl<SysOperationLogMapper, SysOperationLog> implements SysOperationLogService {

    private final SysOperationLogMapper operationLogMapper;

    @Override
    public IPage<SysOperationLog> pageOperationLogs(Integer pageNum, Integer pageSize, 
                                                  String module, String type, String username, 
                                                  String startTime, String endTime) {
        Page<SysOperationLog> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<SysOperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(module), SysOperationLog::getModule, module)
               .like(StringUtils.hasText(type), SysOperationLog::getType, type)
               .like(StringUtils.hasText(username), SysOperationLog::getUsername, username);
        
        // 时间范围查询
        if (StringUtils.hasText(startTime)) {
            LocalDateTime start = LocalDateTime.parse(startTime + " 00:00:00", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            wrapper.ge(SysOperationLog::getCreateTime, start);
        }
        
        if (StringUtils.hasText(endTime)) {
            LocalDateTime end = LocalDateTime.parse(endTime + " 23:59:59", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            wrapper.le(SysOperationLog::getCreateTime, end);
        }
        
        wrapper.orderByDesc(SysOperationLog::getCreateTime);
        
        return this.page(page, wrapper);
    }

    @Override
    @Async
    public void saveOperationLog(SysOperationLog operationLog) {
        try {
            this.save(operationLog);
        } catch (Exception e) {
            log.error("保存操作日志失败", e);
        }
    }

    @Override
    public void cleanExpiredLogs(Integer days) {
        if (days == null || days <= 0) {
            days = 30; // 默认清理30天前的日志
        }
        
        LocalDateTime expireTime = LocalDateTime.now().minusDays(days);
        
        LambdaQueryWrapper<SysOperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(SysOperationLog::getCreateTime, expireTime);
        
        long count = this.count(wrapper);
        if (count > 0) {
            this.remove(wrapper);
            log.info("清理过期操作日志完成，清理数量: {}", count);
        }
    }
}