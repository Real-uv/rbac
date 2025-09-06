package cloud.topdaddy.admin.controller;

import cloud.topdaddy.admin.common.Result;
import cloud.topdaddy.admin.entity.SysPermission;
import cloud.topdaddy.admin.servcie.SysPermissionService;
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
 * 权限管理控制器
 * 
 * @author topdaddy
 */
@Slf4j
@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@Tag(name = "权限管理", description = "权限管理相关接口")
public class SysPermissionController {

    private final SysPermissionService permissionService;

    @GetMapping("/tree")
    @Operation(summary = "查询权限树形结构")
    public Result<List<SysPermission>> getPermissionTree() {
        List<SysPermission> tree = permissionService.getPermissionTree();
        return Result.success(tree);
    }

    @GetMapping("/list")
    @Operation(summary = "查询所有启用的权限")
    public Result<List<SysPermission>> listAllEnabledPermissions() {
        List<SysPermission> permissions = permissionService.getAllEnabledPermissions();
        return Result.success(permissions);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询权限详情")
    public Result<SysPermission> getPermissionById(@PathVariable Long id) {
        SysPermission permission = permissionService.getById(id);
        return Result.success(permission);
    }

    @GetMapping("/children/{parentId}")
    @Operation(summary = "查询子权限列表")
    @PreAuthorize("hasAuthority('permission:list')")
    public Result<List<SysPermission>> getChildrenByParentId(@PathVariable Long parentId) {
        List<SysPermission> children = permissionService.getChildrenByParentId(parentId);
        return Result.success(children);
    }

    @PostMapping
    @Operation(summary = "创建权限")
    @PreAuthorize("hasAuthority('permission:create')")
    public Result<Long> createPermission(@Valid @RequestBody SysPermission permission) {
        Long permissionId = permissionService.createPermission(permission);
        return Result.success(permissionId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新权限")
    @PreAuthorize("hasAuthority('permission:update')")
    public Result<Boolean> updatePermission(@PathVariable Long id, @Valid @RequestBody SysPermission permission) {
        permission.setId(id);
        boolean result = permissionService.updatePermission(permission);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除权限")
    @PreAuthorize("hasAuthority('permission:delete')")
    public Result<Boolean> deletePermission(@PathVariable Long id) {
        boolean result = permissionService.deletePermission(id);
        return Result.success(result);
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除权限")
    @PreAuthorize("hasAuthority('permission:delete')")
    public Result<Boolean> deletePermissions(@RequestBody List<Long> permissionIds) {
        boolean result = permissionService.deletePermissions(permissionIds);
        return Result.success(result);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新权限状态")
    @PreAuthorize("hasAuthority('permission:update')")
    public Result<Boolean> updatePermissionStatus(
            @PathVariable Long id,
            @Parameter(description = "状态：1-启用，0-禁用") @RequestParam Integer status) {
        boolean result = permissionService.updatePermissionStatus(id, status);
        return Result.success(result);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "查询用户权限列表")
    @PreAuthorize("hasAuthority('permission:list')")
    public Result<List<SysPermission>> getPermissionsByUserId(@PathVariable Long userId) {
        List<SysPermission> permissions = permissionService.getPermissionsByUserId(userId);
        return Result.success(permissions);
    }

    @GetMapping("/role/{roleId}")
    @Operation(summary = "查询角色权限列表")
    @PreAuthorize("hasAuthority('permission:list')")
    public Result<List<SysPermission>> getPermissionsByRoleId(@PathVariable Long roleId) {
        List<SysPermission> permissions = permissionService.getPermissionsByRoleId(roleId);
        return Result.success(permissions);
    }

    @GetMapping("/check-code")
    @Operation(summary = "检查权限编码是否存在")
    @PreAuthorize("hasAuthority('permission:list')")
    public Result<Boolean> checkPermissionCode(@RequestParam String code) {
        boolean exists = permissionService.existsByCode(code);
        return Result.success(exists);
    }

    @GetMapping("/{id}/has-children")
    @Operation(summary = "检查是否存在子权限")
    @PreAuthorize("hasAuthority('permission:list')")
    public Result<Boolean> hasChildren(@PathVariable Long id) {
        boolean hasChildren = permissionService.hasChildren(id);
        return Result.success(hasChildren);
    }
}