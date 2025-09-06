package cloud.topdaddy.admin.controller;

import cloud.topdaddy.admin.common.Result;
import cloud.topdaddy.admin.dto.UserCreateRequest;
import cloud.topdaddy.admin.dto.UserQueryRequest;
import cloud.topdaddy.admin.dto.UserUpdateRequest;
import cloud.topdaddy.admin.entity.SysUser;
import cloud.topdaddy.admin.servcie.SysUserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 用户管理控制器
 * 
 * @author topdaddy
 */
@Slf4j
@RestController
@RequestMapping("/system/users")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService userService;

    /**
     * 分页查询用户列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('system:user:query')")
    public Result<IPage<SysUser>> pageUsers(@Validated UserQueryRequest request) {
        IPage<SysUser> page = userService.pageUsers(request);
        return Result.success(page);
    }

    /**
     * 根据ID查询用户详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:query')")
    public Result<SysUser> getUserById(@PathVariable Long id) {
        SysUser user = userService.getById(id);
        if (user != null) {
            // 填充角色信息
            user.setRoleIds(userService.getUserRoleIds(id));
            // 清空密码字段
            user.setPassword(null);
        }
        return Result.success(user);
    }

    /**
     * 创建用户
     */
    @PostMapping
    @PreAuthorize("hasAuthority('system:user:add')")
    public Result<Long> createUser(@Validated @RequestBody UserCreateRequest request) {
        Long userId = userService.createUser(request);
        return Result.success("创建用户成功", userId);
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Result<Void> updateUser(@PathVariable Long id, @Validated @RequestBody UserUpdateRequest request) {
        request.setId(id);
        userService.updateUser(request);
        return Result.success("更新用户成功");
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:delete')")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success("删除用户成功");
    }

    /**
     * 批量删除用户
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('system:user:delete')")
    public Result<Void> deleteUsers(@RequestBody List<Long> userIds) {
        userService.deleteUsers(userIds);
        return Result.success("批量删除用户成功");
    }

    /**
     * 启用/禁用用户
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateUserStatus(id, status);
        String message = status == 1 ? "启用用户成功" : "禁用用户成功";
        return Result.success(message);
    }

    /**
     * 重置用户密码
     */
    @PutMapping("/{id}/reset-password")
    @PreAuthorize("hasAuthority('system:user:resetPwd')")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestParam String newPassword) {
        userService.resetPassword(id, newPassword);
        return Result.success("重置密码成功");
    }

    /**
     * 分配用户角色
     */
    @PutMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Result<Void> assignRoles(@PathVariable Long id, @RequestBody List<Long> roleIds) {
        userService.assignRoles(id, roleIds);
        return Result.success("分配角色成功");
    }

    /**
     * 获取用户权限列表
     */
    @GetMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('system:user:query')")
    public Result<List<String>> getUserPermissions(@PathVariable Long id) {
        List<String> permissions = userService.getUserPermissions(id);
        return Result.success(permissions);
    }

    /**
     * 获取用户角色列表
     */
    @GetMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('system:user:query')")
    public Result<List<String>> getUserRoles(@PathVariable Long id) {
        List<String> roles = userService.getUserRoles(id);
        return Result.success(roles);
    }

    /**
     * 检查用户名是否存在
     */
    @GetMapping("/check-username")
    public Result<Boolean> checkUsername(@RequestParam String username) {
        boolean exists = userService.existsByUsername(username);
        return Result.success(exists);
    }

    /**
     * 检查邮箱是否存在
     */
    @GetMapping("/check-email")
    public Result<Boolean> checkEmail(@RequestParam String email) {
        boolean exists = userService.existsByEmail(email);
        return Result.success(exists);
    }

    /**
     * 检查手机号是否存在
     */
    @GetMapping("/check-phone")
    public Result<Boolean> checkPhone(@RequestParam String phone) {
        boolean exists = userService.existsByPhone(phone);
        return Result.success(exists);
    }
}