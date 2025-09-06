package cloud.topdaddy.admin.servcie.impl;

import cloud.topdaddy.admin.common.ResultCode;
import cloud.topdaddy.admin.entity.SysRole;
import cloud.topdaddy.admin.entity.SysRolePermission;
import cloud.topdaddy.admin.exception.BusinessException;
import cloud.topdaddy.admin.mapper.SysRoleMapper;
import cloud.topdaddy.admin.mapper.SysRolePermissionMapper;
import cloud.topdaddy.admin.mapper.SysUserRoleMapper;
import cloud.topdaddy.admin.servcie.SysRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现类
 * 
 * @author topdaddy
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysRoleMapper roleMapper;
    private final SysRolePermissionMapper rolePermissionMapper;
    private final SysUserRoleMapper userRoleMapper;

    @Override
    public IPage<SysRole> pageRoles(Integer pageNum, Integer pageSize, String name, String code, Integer status) {
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(name), SysRole::getName, name)
               .like(StringUtils.hasText(code), SysRole::getCode, code)
               .eq(status != null, SysRole::getStatus, status)
               .orderByAsc(SysRole::getSort)
               .orderByDesc(SysRole::getCreateTime);

        IPage<SysRole> result = this.page(page, wrapper);
        
        // 填充权限信息
        result.getRecords().forEach(role -> {
            role.setPermissionIds(getRolePermissionIds(role.getId()));
        });
        
        return result;
    }

    @Override
    public SysRole getByCode(String code) {
        return roleMapper.selectByCode(code);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRole(SysRole role) {
        // 检查角色编码是否存在
        if (existsByCode(role.getCode())) {
            throw BusinessException.of(ResultCode.ROLE_CODE_EXISTS);
        }

        if (!this.save(role)) {
            throw BusinessException.of("创建角色失败");
        }

        // 分配权限
        if (!CollectionUtils.isEmpty(role.getPermissionIds())) {
            assignPermissions(role.getId(), role.getPermissionIds());
        }

        log.info("创建角色成功，角色ID: {}, 角色编码: {}", role.getId(), role.getCode());
        return role.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRole(SysRole role) {
        SysRole existRole = this.getById(role.getId());
        if (existRole == null) {
            throw BusinessException.of(ResultCode.ROLE_NOT_FOUND);
        }

        // 检查角色编码是否被其他角色使用
        if (!role.getCode().equals(existRole.getCode())) {
            SysRole codeRole = getByCode(role.getCode());
            if (codeRole != null && !codeRole.getId().equals(role.getId())) {
                throw BusinessException.of(ResultCode.ROLE_CODE_EXISTS);
            }
        }

        boolean result = this.updateById(role);
        
        // 更新权限关联
        if (role.getPermissionIds() != null) {
            assignPermissions(role.getId(), role.getPermissionIds());
        }

        if (result) {
            log.info("更新角色成功，角色ID: {}", role.getId());
        }
        
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(Long roleId) {
        SysRole role = this.getById(roleId);
        if (role == null) {
            throw BusinessException.of(ResultCode.ROLE_NOT_FOUND);
        }

        // 检查角色是否被使用
        if (isRoleInUse(roleId)) {
            throw BusinessException.of(ResultCode.ROLE_IN_USE);
        }

        // 删除角色权限关联
        rolePermissionMapper.deleteByRoleId(roleId);
        
        // 删除角色
        boolean result = this.removeById(roleId);
        
        if (result) {
            log.info("删除角色成功，角色ID: {}, 角色编码: {}", roleId, role.getCode());
        }
        
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRoles(List<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return true;
        }

        // 检查角色是否被使用
        for (Long roleId : roleIds) {
            if (isRoleInUse(roleId)) {
                SysRole role = this.getById(roleId);
                throw BusinessException.of(ResultCode.ROLE_IN_USE.getCode(), 
                    "角色 [" + (role != null ? role.getName() : roleId) + "] 正在使用中，无法删除");
            }
        }

        // 删除角色权限关联
        roleIds.forEach(roleId -> rolePermissionMapper.deleteByRoleId(roleId));
        
        // 批量删除角色
        boolean result = this.removeByIds(roleIds);
        
        if (result) {
            log.info("批量删除角色成功，角色ID列表: {}", roleIds);
        }
        
        return result;
    }

    @Override
    public boolean updateRoleStatus(Long roleId, Integer status) {
        SysRole role = this.getById(roleId);
        if (role == null) {
            throw BusinessException.of(ResultCode.ROLE_NOT_FOUND);
        }

        role.setStatus(status);
        boolean result = this.updateById(role);
        
        if (result) {
            log.info("更新角色状态成功，角色ID: {}, 状态: {}", roleId, status);
        }
        
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignPermissions(Long roleId, List<Long> permissionIds) {
        // 删除原有权限关联
        rolePermissionMapper.deleteByRoleId(roleId);
        
        // 添加新的权限关联
        if (!CollectionUtils.isEmpty(permissionIds)) {
            List<SysRolePermission> rolePermissions = permissionIds.stream()
                    .map(permissionId -> {
                        SysRolePermission rolePermission = new SysRolePermission();
                        rolePermission.setRoleId(roleId);
                        rolePermission.setPermissionId(permissionId);
                        return rolePermission;
                    })
                    .collect(Collectors.toList());
            
            rolePermissionMapper.batchInsert(rolePermissions);
        }
        
        log.info("分配角色权限成功，角色ID: {}, 权限ID列表: {}", roleId, permissionIds);
        return true;
    }

    @Override
    public List<Long> getRolePermissionIds(Long roleId) {
        return roleMapper.selectRolePermissionIds(roleId);
    }

    @Override
    public List<SysRole> getRolesByUserId(Long userId) {
        return roleMapper.selectRolesByUserId(userId);
    }

    @Override
    public boolean existsByCode(String code) {
        return getByCode(code) != null;
    }

    @Override
    public boolean isRoleInUse(Long roleId) {
        return roleMapper.countUsersByRoleId(roleId) > 0;
    }

    @Override
    public List<SysRole> getAllEnabledRoles() {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getStatus, 1)
               .orderByAsc(SysRole::getSort)
               .orderByDesc(SysRole::getCreateTime);
        
        return this.list(wrapper);
    }
}