package cloud.topdaddy.admin.mapper;

import cloud.topdaddy.admin.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 用户Mapper接口
 * 
 * @author topdaddy
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户信息
     */
    SysUser selectByUsername(@Param("username") String username);

    /**
     * 根据邮箱查询用户信息
     */
    SysUser selectByEmail(@Param("email") String email);

    /**
     * 根据手机号查询用户信息
     */
    SysUser selectByPhone(@Param("phone") String phone);

    /**
     * 查询用户的权限列表
     */
    List<String> selectUserPermissions(@Param("userId") Long userId);

    /**
     * 查询用户的角色列表
     */
    List<String> selectUserRoles(@Param("userId") Long userId);

    /**
     * 查询用户的角色ID列表
     */
    List<Long> selectUserRoleIds(@Param("userId") Long userId);

    /**
     * 根据角色ID查询用户列表
     */
    List<SysUser> selectUsersByRoleId(@Param("roleId") Long roleId);

    /**
     * 更新用户最后登录信息
     */
    int updateLastLoginInfo(@Param("userId") Long userId, @Param("loginTime") String loginTime, @Param("loginIp") String loginIp);
}