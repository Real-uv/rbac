package cloud.topdaddy.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志实体类
 * 
 * @author topdaddy
 */
@Data
@TableName("sys_operation_log")
public class SysOperationLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 链路追踪ID
     */
    private String traceId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 操作名称
     */
    private String operation;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求参数
     */
    private String params;

    /**
     * 返回结果
     */
    private String result;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 操作地点
     */
    private String location;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 操作状态：0-失败，1-成功
     */
    private Integer status;

    /**
     * 错误消息
     */
    private String errorMsg;

    /**
     * 执行时间(毫秒)
     */
    private Integer costTime;

    /**
     * 模块
     */
    private String module;

    /**
     * 操作类型
     */
    private String type;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}