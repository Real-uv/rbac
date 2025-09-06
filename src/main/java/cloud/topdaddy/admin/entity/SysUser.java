package cloud.topdaddy.admin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serial;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户实体类
 * 
 * @author topdaddy
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 备注
     */
    private String remark;

    /**
     * 角色列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<SysRole> roles;

    /**
     * 角色ID列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<Long> roleIds;

    /**
     * 权限列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<String> permissions;
}