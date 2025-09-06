package cloud.topdaddy.admin.servcie;

import cloud.topdaddy.admin.dto.UserCreateRequest;
import cloud.topdaddy.admin.dto.UserQueryRequest;
import cloud.topdaddy.admin.dto.UserUpdateRequest;
import cloud.topdaddy.admin.entity.SysUser;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * 用户服务接口
 * 
 * @author topdaddy
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 分页查询用户列表
     */
    IPage<SysUser> pageUsers(UserQueryRequest request);

    /**
     * 根据用户名查询用户
     */
    SysUser getByUsername(String username);

    /**
     * 根据邮箱查询用户
     */
    SysUser getByEmail(String email);

    /**
     * 根据手机号查询用户
     */
    SysUser getByPhone(String phone);

    /**
     * 创建用户
     */
    Long createUser(UserCreateRequest request);

    /**
     * 更新用户
     */
    boolean updateUser(UserUpdateRequest request);

    /**
     * 删除用户
     */
    boolean deleteUser(Long userId);

    /**
     * 批量删除用户
     */
    boolean deleteUsers(List<Long> userIds);

    /**
     * 启用/禁用用户
     */
    boolean updateUserStatus(Long userId, Integer status);

    /**
     * 重置用户密码
     */
    boolean resetPassword(Long userId, String newPassword);

    /**
     * 修改用户密码
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 分配用户角色
     */
    boolean assignRoles(Long userId, List<Long> roleIds);

    /**
     * 获取用户权限列表
     */
    List<String> getUserPermissions(Long userId);

    /**
     * 获取用户角色列表
     */
    List<String> getUserRoles(Long userId);

    /**
     * 获取用户角色ID列表
     */
    List<Long> getUserRoleIds(Long userId);

    /**
     * 更新用户最后登录信息
     */
    boolean updateLastLoginInfo(Long userId, String loginIp);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 检查手机号是否存在
     */
    boolean existsByPhone(String phone);
}