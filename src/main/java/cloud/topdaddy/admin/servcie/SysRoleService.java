package cloud.topdaddy.admin.servcie;

import cloud.topdaddy.admin.entity.SysRole;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * 角色服务接口
 * 
 * @author topdaddy
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 分页查询角色列表
     */
    IPage<SysRole> pageRoles(Integer pageNum, Integer pageSize, String name, String code, Integer status);

    /**
     * 根据角色编码查询角色
     */
    SysRole getByCode(String code);

    /**
     * 创建角色
     */
    Long createRole(SysRole role);

    /**
     * 更新角色
     */
    boolean updateRole(SysRole role);

    /**
     * 删除角色
     */
    boolean deleteRole(Long roleId);

    /**
     * 批量删除角色
     */
    boolean deleteRoles(List<Long> roleIds);

    /**
     * 启用/禁用角色
     */
    boolean updateRoleStatus(Long roleId, Integer status);

    /**
     * 分配角色权限
     */
    boolean assignPermissions(Long roleId, List<Long> permissionIds);

    /**
     * 获取角色权限ID列表
     */
    List<Long> getRolePermissionIds(Long roleId);

    /**
     * 根据用户ID查询角色列表
     */
    List<SysRole> getRolesByUserId(Long userId);

    /**
     * 检查角色编码是否存在
     */
    boolean existsByCode(String code);

    /**
     * 检查角色是否被使用
     */
    boolean isRoleInUse(Long roleId);

    /**
     * 获取所有启用的角色
     */
    List<SysRole> getAllEnabledRoles();
}