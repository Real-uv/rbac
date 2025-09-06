package cloud.topdaddy.admin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serial;
import java.util.List;

/**
 * 权限实体类
 * 
 * @author topdaddy
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_permission")
public class SysPermission extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 父权限ID
     */
    private Long parentId;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 权限编码
     */
    private String code;

    /**
     * 权限类型：1-目录，2-菜单，3-按钮
     */
    private Integer type;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 图标
     */
    private String icon;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 是否显示：0-隐藏，1-显示
     */
    private Integer visible;

    /**
     * 是否缓存：0-不缓存，1-缓存
     */
    private Integer keepAlive;

    /**
     * 是否总是显示：0-否，1-是
     */
    private Integer alwaysShow;

    /**
     * 备注
     */
    private String remark;

    /**
     * 子权限列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<SysPermission> children;
}