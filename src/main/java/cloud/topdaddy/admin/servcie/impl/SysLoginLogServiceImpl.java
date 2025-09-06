package cloud.topdaddy.admin.servcie.impl;

import cloud.topdaddy.admin.entity.SysLoginLog;
import cloud.topdaddy.admin.mapper.SysLoginLogMapper;
import cloud.topdaddy.admin.servcie.SysLoginLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 登录日志服务实现类
 * 
 * @author topdaddy
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysLoginLogServiceImpl extends ServiceImpl<SysLoginLogMapper, SysLoginLog> implements SysLoginLogService {

    private final SysLoginLogMapper loginLogMapper;

    @Override
    public IPage<SysLoginLog> pageLoginLogs(Integer pageNum, Integer pageSize, String username, Integer status, String startTime, String endTime) {
        Page<SysLoginLog> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<SysLoginLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(username), SysLoginLog::getUsername, username)
                .eq(status != null, SysLoginLog::getStatus, status);

        // 时间范围查询
        if (StringUtils.hasText(startTime)) {
            LocalDateTime start = LocalDateTime.parse(startTime + " 00:00:00",
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            wrapper.ge(SysLoginLog::getLoginTime, start);
        }

        if (StringUtils.hasText(endTime)) {
            LocalDateTime end = LocalDateTime.parse(endTime + " 23:59:59",
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            wrapper.le(SysLoginLog::getLoginTime, end);
        }

        wrapper.orderByDesc(SysLoginLog::getLoginTime);

        return this.page(page, wrapper);
    }

    @Override
    public void cleanLogs(Integer days) {
        try {
            int count = loginLogMapper.deleteByDaysAgo(days);
            log.info("清理{}天前的登录日志，共清理{}条记录", days, count);
        } catch (Exception e) {
            log.error("清理登录日志失败", e);
        }
    }

    @Override
    public Long countTodayLogins() {
        return loginLogMapper.countTodayLogs();
    }

    @Override
    public Long countFailedLogins() {
        return loginLogMapper.countFailedLogs();
    }
}