package cloud.topdaddy.admin.servcie;

import cloud.topdaddy.admin.entity.SysLoginLog;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 登录日志服务接口
 * 
 * @author topdaddy
 */
public interface SysLoginLogService extends IService<SysLoginLog> {

    /**
     * 分页查询登录日志
     */
    IPage<SysLoginLog> pageLoginLogs(Integer pageNum, Integer pageSize,
                                     String username, Integer status,
                                     String startTime, String endTime);


    /**
     * 清理指定天数之前的日志
     */
    void cleanLogs(Integer days);

    /**
     * 统计今日登录数量
     */
    Long countTodayLogins();

    /**
     * 统计失败登录数量
     */
    Long countFailedLogins();
}