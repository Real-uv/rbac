package cloud.topdaddy.admin.mapper;

import cloud.topdaddy.admin.entity.SysRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 角色Mapper接口
 * 
 * @author topdaddy
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 根据角色编码查询角色信息
     */
    SysRole selectByCode(@Param("code") String code);

    /**
     * 查询角色的权限ID列表
     */
    List<Long> selectRolePermissionIds(@Param("roleId") Long roleId);

    /**
     * 根据用户ID查询角色列表
     */
    List<SysRole> selectRolesByUserId(@Param("userId") Long userId);

    /**
     * 根据权限ID查询角色列表
     */
    List<SysRole> selectRolesByPermissionId(@Param("permissionId") Long permissionId);

    /**
     * 检查角色是否被用户使用
     */
    int countUsersByRoleId(@Param("roleId") Long roleId);
}