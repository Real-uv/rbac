package cloud.topdaddy.admin.servcie.impl;

import cloud.topdaddy.admin.common.ResultCode;
import cloud.topdaddy.admin.entity.SysPermission;
import cloud.topdaddy.admin.exception.BusinessException;
import cloud.topdaddy.admin.mapper.SysPermissionMapper;
import cloud.topdaddy.admin.mapper.SysRolePermissionMapper;
import cloud.topdaddy.admin.servcie.SysPermissionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 权限服务实现类
 * 
 * @author topdaddy
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements SysPermissionService {

    private final SysPermissionMapper permissionMapper;
    private final SysRolePermissionMapper rolePermissionMapper;

    @Override
    public List<SysPermission> getPermissionTree() {
        List<SysPermission> allPermissions = getAllEnabledPermissions();
        return buildPermissionTree(allPermissions);
    }

    @Override
    public SysPermission getByCode(String code) {
        return permissionMapper.selectByCode(code);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPermission(SysPermission permission) {
        // 检查权限编码是否存在
        if (existsByCode(permission.getCode())) {
            throw BusinessException.of(ResultCode.PERMISSION_CODE_EXISTS);
        }

        // 如果有父权限，检查父权限是否存在
        if (permission.getParentId() != null && permission.getParentId() > 0) {
            SysPermission parent = this.getById(permission.getParentId());
            if (parent == null) {
                throw BusinessException.of("父权限不存在");
            }
        }

        if (!this.save(permission)) {
            throw BusinessException.of("创建权限失败");
        }

        log.info("创建权限成功，权限ID: {}, 权限编码: {}", permission.getId(), permission.getCode());
        return permission.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePermission(SysPermission permission) {
        SysPermission existPermission = this.getById(permission.getId());
        if (existPermission == null) {
            throw BusinessException.of(ResultCode.PERMISSION_NOT_FOUND);
        }

        // 检查权限编码是否被其他权限使用
        if (!permission.getCode().equals(existPermission.getCode())) {
            SysPermission codePermission = getByCode(permission.getCode());
            if (codePermission != null && !codePermission.getId().equals(permission.getId())) {
                throw BusinessException.of(ResultCode.PERMISSION_CODE_EXISTS);
            }
        }

        // 检查父权限设置是否合法
        if (permission.getParentId() != null && permission.getParentId() > 0) {
            if (permission.getParentId().equals(permission.getId())) {
                throw BusinessException.of("不能将自己设置为父权限");
            }
            
            SysPermission parent = this.getById(permission.getParentId());
            if (parent == null) {
                throw BusinessException.of("父权限不存在");
            }
        }

        boolean result = this.updateById(permission);
        
        if (result) {
            log.info("更新权限成功，权限ID: {}", permission.getId());
        }
        
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePermission(Long permissionId) {
        SysPermission permission = this.getById(permissionId);
        if (permission == null) {
            throw BusinessException.of(ResultCode.PERMISSION_NOT_FOUND);
        }

        // 检查是否存在子权限
        if (hasChildren(permissionId)) {
            throw BusinessException.of("存在子权限，无法删除");
        }

        // 检查权限是否被使用
        if (isPermissionInUse(permissionId)) {
            throw BusinessException.of("权限正在使用中，无法删除");
        }

        // 删除权限角色关联
        rolePermissionMapper.deleteByPermissionId(permissionId);
        
        // 删除权限
        boolean result = this.removeById(permissionId);
        
        if (result) {
            log.info("删除权限成功，权限ID: {}, 权限编码: {}", permissionId, permission.getCode());
        }
        
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePermissions(List<Long> permissionIds) {
        if (CollectionUtils.isEmpty(permissionIds)) {
            return true;
        }

        // 检查权限是否被使用或存在子权限
        for (Long permissionId : permissionIds) {
            if (hasChildren(permissionId)) {
                SysPermission permission = this.getById(permissionId);
                throw BusinessException.of("权限 [" + (permission != null ? permission.getName() : permissionId) + "] 存在子权限，无法删除");
            }
            
            if (isPermissionInUse(permissionId)) {
                SysPermission permission = this.getById(permissionId);
                throw BusinessException.of("权限 [" + (permission != null ? permission.getName() : permissionId) + "] 正在使用中，无法删除");
            }
        }

        // 删除权限角色关联
        permissionIds.forEach(permissionId -> rolePermissionMapper.deleteByPermissionId(permissionId));
        
        // 批量删除权限
        boolean result = this.removeByIds(permissionIds);
        
        if (result) {
            log.info("批量删除权限成功，权限ID列表: {}", permissionIds);
        }
        
        return result;
    }

    @Override
    public boolean updatePermissionStatus(Long permissionId, Integer status) {
        SysPermission permission = this.getById(permissionId);
        if (permission == null) {
            throw BusinessException.of(ResultCode.PERMISSION_NOT_FOUND);
        }

        permission.setStatus(status);
        boolean result = this.updateById(permission);
        
        if (result) {
            log.info("更新权限状态成功，权限ID: {}, 状态: {}", permissionId, status);
        }
        
        return result;
    }

    @Override
    public List<SysPermission> getPermissionsByUserId(Long userId) {
        return permissionMapper.selectPermissionsByUserId(userId);
    }

    @Override
    public List<SysPermission> getPermissionsByRoleId(Long roleId) {
        return permissionMapper.selectPermissionsByRoleId(roleId);
    }

    @Override
    public List<SysPermission> getChildrenByParentId(Long parentId) {
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPermission::getParentId, parentId)
               .eq(SysPermission::getStatus, 1)
               .orderByAsc(SysPermission::getSort)
               .orderByDesc(SysPermission::getCreateTime);
        
        return this.list(wrapper);
    }

    @Override
    public boolean existsByCode(String code) {
        return getByCode(code) != null;
    }

    @Override
    public boolean isPermissionInUse(Long permissionId) {
        return permissionMapper.countRolesByPermissionId(permissionId) > 0;
    }

    @Override
    public boolean hasChildren(Long parentId) {
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPermission::getParentId, parentId);
        
        return this.count(wrapper) > 0;
    }

    @Override
    public List<SysPermission> buildPermissionTree(List<SysPermission> permissions) {
        if (CollectionUtils.isEmpty(permissions)) {
            return new ArrayList<>();
        }

        // 按父ID分组
        Map<Long, List<SysPermission>> parentIdMap = permissions.stream()
                .collect(Collectors.groupingBy(permission -> 
                    permission.getParentId() == null ? 0L : permission.getParentId()));

        // 构建树形结构
        List<SysPermission> rootPermissions = parentIdMap.getOrDefault(0L, new ArrayList<>());
        
        for (SysPermission permission : rootPermissions) {
            buildChildren(permission, parentIdMap);
        }

        return rootPermissions;
    }

    /**
     * 递归构建子权限
     */
    private void buildChildren(SysPermission parent, Map<Long, List<SysPermission>> parentIdMap) {
        List<SysPermission> children = parentIdMap.get(parent.getId());
        if (!CollectionUtils.isEmpty(children)) {
            parent.setChildren(children);
            for (SysPermission child : children) {
                buildChildren(child, parentIdMap);
            }
        }
    }

    @Override
    public List<SysPermission> getAllEnabledPermissions() {
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPermission::getStatus, 1)
               .orderByAsc(SysPermission::getSort)
               .orderByDesc(SysPermission::getCreateTime);
        
        return this.list(wrapper);
    }
}