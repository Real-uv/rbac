package cloud.topdaddy.admin.mapper;

import cloud.topdaddy.admin.entity.SysLoginLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 登录日志Mapper接口
 * 
 * @author topdaddy
 */
@Mapper
public interface SysLoginLogMapper extends BaseMapper<SysLoginLog> {

    /**
     * 根据用户名查询登录日志
     */
    List<SysLoginLog> selectByUsername(@Param("username") String username);

    /**
     * 根据IP地址查询登录日志
     */
    List<SysLoginLog> selectByIp(@Param("ip") String ip);

    /**
     * 根据时间范围查询登录日志
     */
    List<SysLoginLog> selectByTimeRange(@Param("startTime") LocalDateTime startTime, 
                                       @Param("endTime") LocalDateTime endTime);

    /**
     * 根据状态查询登录日志
     */
    List<SysLoginLog> selectByStatus(@Param("status") Integer status);

    /**
     * 清理指定天数之前的日志
     */
    int deleteByDaysAgo(@Param("days") Integer days);

    /**
     * 统计登录日志数量
     */
    Long countLogs();

    /**
     * 统计今日登录日志数量
     */
    Long countTodayLogs();

    /**
     * 统计失败登录日志数量
     */
    Long countFailedLogs();

    /**
     * 统计最近登录的用户数量
     */
    Long countRecentUsers(@Param("hours") Integer hours);
}