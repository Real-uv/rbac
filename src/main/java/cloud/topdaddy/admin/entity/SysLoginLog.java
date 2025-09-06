package cloud.topdaddy.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录日志实体类
 * 
 * @author topdaddy
 */
@Data
@TableName("sys_login_log")
public class SysLoginLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 登录地点
     */
    private String location;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 登录状态：0-失败，1-成功
     */
    private Integer status;

    /**
     * 提示消息
     */
    private String message;

    /**
     * 登录时间
     */
    @TableField(value = "login_time", fill = FieldFill.INSERT)
    private LocalDateTime loginTime;
}