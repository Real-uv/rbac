package cloud.topdaddy.admin.mapper;

import cloud.topdaddy.admin.entity.SysOperationLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志Mapper接口
 * 
 * @author topdaddy
 */
@Mapper
public interface SysOperationLogMapper extends BaseMapper<SysOperationLog> {

    /**
     * 根据用户ID查询操作日志
     */
    List<SysOperationLog> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据操作名称查询操作日志
     */
    List<SysOperationLog> selectByOperation(@Param("operation") String operation);

    /**
     * 根据时间范围查询操作日志
     */
    List<SysOperationLog> selectByTimeRange(@Param("startTime") LocalDateTime startTime, 
                                           @Param("endTime") LocalDateTime endTime);

    /**
     * 根据状态查询操作日志
     */
    List<SysOperationLog> selectByStatus(@Param("status") Integer status);

    /**
     * 清理指定天数之前的日志
     */
    int deleteByDaysAgo(@Param("days") Integer days);

    /**
     * 统计操作日志数量
     */
    Long countLogs();

    /**
     * 统计今日操作日志数量
     */
    Long countTodayLogs();

    /**
     * 统计失败操作日志数量
     */
    Long countFailedLogs();
}