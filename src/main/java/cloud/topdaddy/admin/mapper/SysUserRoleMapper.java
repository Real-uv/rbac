package cloud.topdaddy.admin.mapper;

import cloud.topdaddy.admin.entity.SysUserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 用户角色关联Mapper接口
 * 
 * @author topdaddy
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 根据用户ID删除用户角色关联
     */
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID删除用户角色关联
     */
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量插入用户角色关联
     */
    int batchInsert(@Param("userRoles") List<SysUserRole> userRoles);

    /**
     * 根据用户ID查询角色ID列表
     */
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询用户ID列表
     */
    List<Long> selectUserIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 检查用户角色关联是否存在
     */
    int countByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);
}