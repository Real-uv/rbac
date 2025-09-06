package cloud.topdaddy.admin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serial;
import java.util.List;

/**
 * 角色实体类
 * 
 * @author topdaddy
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色编码
     */
    private String code;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 数据范围：1-全部数据，2-自定义数据，3-本部门数据，4-本部门及以下数据，5-仅本人数据
     */
    private Integer dataScope;

    /**
     * 备注
     */
    private String remark;

    /**
     * 权限列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<SysPermission> permissions;

    /**
     * 权限ID列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<Long> permissionIds;
}