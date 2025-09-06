package cloud.topdaddy.admin.mapper;

import cloud.topdaddy.admin.entity.SysPermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 权限Mapper接口
 * 
 * @author topdaddy
 */
@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    /**
     * 根据权限编码查询权限信息
     */
    SysPermission selectByCode(@Param("code") String code);

    /**
     * 根据用户ID查询权限列表
     */
    List<SysPermission> selectPermissionsByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询权限列表
     */
    List<SysPermission> selectPermissionsByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据父权限ID查询子权限列表
     */
    List<SysPermission> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 查询所有权限树形结构
     */
    List<SysPermission> selectPermissionTree();

    /**
     * 检查权限是否被角色使用
     */
    int countRolesByPermissionId(@Param("permissionId") Long permissionId);

    /**
     * 检查是否存在子权限
     */
    int countChildrenByParentId(@Param("parentId") Long parentId);
}