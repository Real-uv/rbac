package cloud.topdaddy.admin.servcie;

import cloud.topdaddy.admin.entity.SysOperationLog;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 操作日志服务接口
 * 
 * @author topdaddy
 */
public interface SysOperationLogService extends IService<SysOperationLog> {

    /**
     * 分页查询操作日志
     */
    IPage<SysOperationLog> pageOperationLogs(Integer pageNum, Integer pageSize, 
                                           String module, String type, String username, 
                                           String startTime, String endTime);

    /**
     * 记录操作日志
     */
    void saveOperationLog(SysOperationLog operationLog);

    /**
     * 清理过期日志
     */
    void cleanExpiredLogs(Integer days);
}