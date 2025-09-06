package cloud.topdaddy.admin.servcie;

import cloud.topdaddy.admin.entity.SysPermission;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * 权限服务接口
 * 
 * @author topdaddy
 */
public interface SysPermissionService extends IService<SysPermission> {

    /**
     * 查询权限树形结构
     */
    List<SysPermission> getPermissionTree();

    /**
     * 根据权限编码查询权限
     */
    SysPermission getByCode(String code);

    /**
     * 创建权限
     */
    Long createPermission(SysPermission permission);

    /**
     * 更新权限
     */
    boolean updatePermission(SysPermission permission);

    /**
     * 删除权限
     */
    boolean deletePermission(Long permissionId);

    /**
     * 批量删除权限
     */
    boolean deletePermissions(List<Long> permissionIds);

    /**
     * 启用/禁用权限
     */
    boolean updatePermissionStatus(Long permissionId, Integer status);

    /**
     * 根据用户ID查询权限列表
     */
    List<SysPermission> getPermissionsByUserId(Long userId);

    /**
     * 根据角色ID查询权限列表
     */
    List<SysPermission> getPermissionsByRoleId(Long roleId);

    /**
     * 根据父权限ID查询子权限列表
     */
    List<SysPermission> getChildrenByParentId(Long parentId);

    /**
     * 检查权限编码是否存在
     */
    boolean existsByCode(String code);

    /**
     * 检查权限是否被使用
     */
    boolean isPermissionInUse(Long permissionId);

    /**
     * 检查是否存在子权限
     */
    boolean hasChildren(Long parentId);

    /**
     * 构建权限树
     */
    List<SysPermission> buildPermissionTree(List<SysPermission> permissions);

    /**
     * 获取所有启用的权限
     */
    List<SysPermission> getAllEnabledPermissions();
}