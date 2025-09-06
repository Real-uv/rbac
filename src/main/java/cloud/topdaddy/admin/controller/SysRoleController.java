package cloud.topdaddy.admin.controller;

import cloud.topdaddy.admin.common.Result;
import cloud.topdaddy.admin.entity.SysRole;
import cloud.topdaddy.admin.servcie.SysRoleService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 角色管理控制器
 * 
 * @author topdaddy
 */
@Slf4j
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "角色管理", description = "角色管理相关接口")
public class SysRoleController {

    private final SysRoleService roleService;

    @GetMapping("/page")
    @Operation(summary = "分页查询角色列表")
    @PreAuthorize("hasAuthority('role:list')")
    public Result<IPage<SysRole>> pageRoles(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "角色名称") @RequestParam(required = false) String name,
            @Parameter(description = "角色编码") @RequestParam(required = false) String code,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        
        IPage<SysRole> page = roleService.pageRoles(pageNum, pageSize, name, code, status);
        return Result.success(page);
    }

    @GetMapping("/list")
    @Operation(summary = "查询所有启用的角色")
    @PreAuthorize("hasAuthority('role:list')")
    public Result<List<SysRole>> listAllEnabledRoles() {
        List<SysRole> roles = roleService.getAllEnabledRoles();
        return Result.success(roles);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询角色详情")
    @PreAuthorize("hasAuthority('role:detail')")
    public Result<SysRole> getRoleById(@PathVariable Long id) {
        SysRole role = roleService.getById(id);
        if (role != null) {
            // 填充权限信息
            role.setPermissionIds(roleService.getRolePermissionIds(id));
        }
        return Result.success(role);
    }

    @PostMapping
    @Operation(summary = "创建角色")
    @PreAuthorize("hasAuthority('role:create')")
    public Result<Long> createRole(@Valid @RequestBody SysRole role) {
        Long roleId = roleService.createRole(role);
        return Result.success(roleId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新角色")
    @PreAuthorize("hasAuthority('role:update')")
    public Result<Boolean> updateRole(@PathVariable Long id, @Valid @RequestBody SysRole role) {
        role.setId(id);
        boolean result = roleService.updateRole(role);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色")
    @PreAuthorize("hasAuthority('role:delete')")
    public Result<Boolean> deleteRole(@PathVariable Long id) {
        boolean result = roleService.deleteRole(id);
        return Result.success(result);
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除角色")
    @PreAuthorize("hasAuthority('role:delete')")
    public Result<Boolean> deleteRoles(@RequestBody List<Long> roleIds) {
        boolean result = roleService.deleteRoles(roleIds);
        return Result.success(result);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新角色状态")
    @PreAuthorize("hasAuthority('role:update')")
    public Result<Boolean> updateRoleStatus(
            @PathVariable Long id,
            @Parameter(description = "状态：1-启用，0-禁用") @RequestParam Integer status) {
        boolean result = roleService.updateRoleStatus(id, status);
        return Result.success(result);
    }

    @PutMapping("/{id}/permissions")
    @Operation(summary = "分配角色权限")
    @PreAuthorize("hasAuthority('role:assign')")
    public Result<Boolean> assignPermissions(
            @PathVariable Long id,
            @RequestBody List<Long> permissionIds) {
        boolean result = roleService.assignPermissions(id, permissionIds);
        return Result.success(result);
    }

    @GetMapping("/{id}/permissions")
    @Operation(summary = "查询角色权限ID列表")
    @PreAuthorize("hasAuthority('role:detail')")
    public Result<List<Long>> getRolePermissionIds(@PathVariable Long id) {
        List<Long> permissionIds = roleService.getRolePermissionIds(id);
        return Result.success(permissionIds);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "查询用户的角色列表")
    @PreAuthorize("hasAuthority('role:list')")
    public Result<List<SysRole>> getRolesByUserId(@PathVariable Long userId) {
        List<SysRole> roles = roleService.getRolesByUserId(userId);
        return Result.success(roles);
    }

    @GetMapping("/check-code")
    @Operation(summary = "检查角色编码是否存在")
    @PreAuthorize("hasAuthority('role:list')")
    public Result<Boolean> checkRoleCode(@RequestParam String code) {
        boolean exists = roleService.existsByCode(code);
        return Result.success(exists);
    }
}