package cloud.topdaddy.admin.servcie.impl;

import cloud.topdaddy.admin.common.ResultCode;
import cloud.topdaddy.admin.dto.UserCreateRequest;
import cloud.topdaddy.admin.dto.UserQueryRequest;
import cloud.topdaddy.admin.dto.UserUpdateRequest;
import cloud.topdaddy.admin.entity.SysUser;
import cloud.topdaddy.admin.entity.SysUserRole;
import cloud.topdaddy.admin.exception.BusinessException;
import cloud.topdaddy.admin.mapper.SysUserMapper;
import cloud.topdaddy.admin.mapper.SysUserRoleMapper;
import cloud.topdaddy.admin.servcie.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 * 
 * @author topdaddy
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    @Lazy
    private final PasswordEncoder passwordEncoder;

    @Override
    public IPage<SysUser> pageUsers(UserQueryRequest request) {
        Page<SysUser> page = new Page<>(request.getPageNum(), request.getPageSize());
        
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(request.getUsername()), SysUser::getUsername, request.getUsername())
               .like(StringUtils.hasText(request.getNickname()), SysUser::getNickname, request.getNickname())
               .like(StringUtils.hasText(request.getEmail()), SysUser::getEmail, request.getEmail())
               .like(StringUtils.hasText(request.getPhone()), SysUser::getPhone, request.getPhone())
               .eq(request.getGender() != null, SysUser::getGender, request.getGender())
               .eq(request.getStatus() != null, SysUser::getStatus, request.getStatus())
               .ge(request.getCreateTimeStart() != null, SysUser::getCreateTime, request.getCreateTimeStart())
               .le(request.getCreateTimeEnd() != null, SysUser::getCreateTime, request.getCreateTimeEnd());

        // 排序
        if (StringUtils.hasText(request.getOrderBy())) {
            if ("desc".equalsIgnoreCase(request.getOrderDirection())) {
                orderByDesc(wrapper, request.getOrderBy());
            } else {
                orderByAsc(wrapper, request.getOrderBy());
            }
        } else {
            wrapper.orderByDesc(SysUser::getCreateTime);
        }


        IPage<SysUser> result = this.page(page, wrapper);
        
        // 填充角色信息
        result.getRecords().forEach(user -> {
            user.setRoleIds(getUserRoleIds(user.getId()));
        });
        
        return result;
    }

    @Override
    public SysUser getByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public SysUser getByEmail(String email) {
        return userMapper.selectByEmail(email);
    }

    @Override
    public SysUser getByPhone(String phone) {
        return userMapper.selectByPhone(phone);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createUser(UserCreateRequest request) {
        // 检查用户名是否存在
        if (existsByUsername(request.getUsername())) {
            throw BusinessException.of(ResultCode.USERNAME_EXISTS);
        }
        
        // 检查邮箱是否存在
        if (StringUtils.hasText(request.getEmail()) && existsByEmail(request.getEmail())) {
            throw BusinessException.of(ResultCode.EMAIL_EXISTS);
        }
        
        // 检查手机号是否存在
        if (StringUtils.hasText(request.getPhone()) && existsByPhone(request.getPhone())) {
            throw BusinessException.of(ResultCode.PHONE_EXISTS);
        }

        // 创建用户
        SysUser user = new SysUser();
        BeanUtils.copyProperties(request, user);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        if (!this.save(user)) {
            throw BusinessException.of("创建用户失败");
        }

        // 分配角色
        if (!CollectionUtils.isEmpty(request.getRoleIds())) {
            assignRoles(user.getId(), request.getRoleIds());
        }

        log.info("创建用户成功，用户ID: {}, 用户名: {}", user.getId(), user.getUsername());
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(UserUpdateRequest request) {
        SysUser existUser = this.getById(request.getId());
        if (existUser == null) {
            throw BusinessException.of(ResultCode.USER_NOT_FOUND);
        }

        // 检查邮箱是否被其他用户使用
        if (StringUtils.hasText(request.getEmail()) && !request.getEmail().equals(existUser.getEmail())) {
            SysUser emailUser = getByEmail(request.getEmail());
            if (emailUser != null && !emailUser.getId().equals(request.getId())) {
                throw BusinessException.of(ResultCode.EMAIL_EXISTS);
            }
        }

        // 检查手机号是否被其他用户使用
        if (StringUtils.hasText(request.getPhone()) && !request.getPhone().equals(existUser.getPhone())) {
            SysUser phoneUser = getByPhone(request.getPhone());
            if (phoneUser != null && !phoneUser.getId().equals(request.getId())) {
                throw BusinessException.of(ResultCode.PHONE_EXISTS);
            }
        }

        // 更新用户信息
        SysUser user = new SysUser();
        BeanUtils.copyProperties(request, user);
        
        boolean result = this.updateById(user);
        
        // 更新角色关联
        if (request.getRoleIds() != null) {
            assignRoles(request.getId(), request.getRoleIds());
        }

        if (result) {
            log.info("更新用户成功，用户ID: {}", request.getId());
        }
        
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(Long userId) {
        SysUser user = this.getById(userId);
        if (user == null) {
            throw BusinessException.of(ResultCode.USER_NOT_FOUND);
        }

        // 删除用户角色关联
        userRoleMapper.deleteByUserId(userId);
        
        // 删除用户
        boolean result = this.removeById(userId);
        
        if (result) {
            log.info("删除用户成功，用户ID: {}, 用户名: {}", userId, user.getUsername());
        }
        
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUsers(List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return true;
        }

        // 删除用户角色关联
        userIds.forEach(userRoleMapper::deleteByUserId);
        
        // 批量删除用户
        boolean result = this.removeByIds(userIds);
        
        if (result) {
            log.info("批量删除用户成功，用户ID列表: {}", userIds);
        }
        
        return result;
    }

    @Override
    public boolean updateUserStatus(Long userId, Integer status) {
        SysUser user = this.getById(userId);
        if (user == null) {
            throw BusinessException.of(ResultCode.USER_NOT_FOUND);
        }

        user.setStatus(status);
        boolean result = this.updateById(user);
        
        if (result) {
            log.info("更新用户状态成功，用户ID: {}, 状态: {}", userId, status);
        }
        
        return result;
    }

    @Override
    public boolean resetPassword(Long userId, String newPassword) {
        SysUser user = this.getById(userId);
        if (user == null) {
            throw BusinessException.of(ResultCode.USER_NOT_FOUND);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        boolean result = this.updateById(user);
        
        if (result) {
            log.info("重置用户密码成功，用户ID: {}", userId);
        }
        
        return result;
    }

    @Override
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = this.getById(userId);
        if (user == null) {
            throw BusinessException.of(ResultCode.USER_NOT_FOUND);
        }

        // 验证原密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw BusinessException.of(ResultCode.OLD_PASSWORD_ERROR);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        boolean result = this.updateById(user);
        
        if (result) {
            log.info("修改用户密码成功，用户ID: {}", userId);
        }
        
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRoles(Long userId, List<Long> roleIds) {
        // 删除原有角色关联
        userRoleMapper.deleteByUserId(userId);
        
        // 添加新的角色关联
        if (!CollectionUtils.isEmpty(roleIds)) {
            List<SysUserRole> userRoles = roleIds.stream()
                    .map(roleId -> {
                        SysUserRole userRole = new SysUserRole();
                        userRole.setUserId(userId);
                        userRole.setRoleId(roleId);
                        return userRole;
                    })
                    .collect(Collectors.toList());
            
            userRoleMapper.batchInsert(userRoles);
        }
        
        log.info("分配用户角色成功，用户ID: {}, 角色ID列表: {}", userId, roleIds);
        return true;
    }

    @Override
    public List<String> getUserPermissions(Long userId) {
        return userMapper.selectUserPermissions(userId);
    }

    @Override
    public List<String> getUserRoles(Long userId) {
        return userMapper.selectUserRoles(userId);
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        return userMapper.selectUserRoleIds(userId);
    }

    @Override
    public boolean updateLastLoginInfo(Long userId, String loginIp) {
        return userMapper.updateLastLoginInfo(userId, LocalDateTime.now().toString(), loginIp) > 0;
    }

    @Override
    public boolean existsByUsername(String username) {
        return getByUsername(username) != null;
    }

    @Override
    public boolean existsByEmail(String email) {
        return getByEmail(email) != null;
    }

    @Override
    public boolean existsByPhone(String phone) {
        return getByPhone(phone) != null;
    }

    /**
     * 根据属性名获取数据库字段
     */
    private String getColumnByProperty(String property) {
        return switch (property) {
            case "username" -> "username";
            case "nickname" -> "nickname";
            case "email" -> "email";
            case "phone" -> "phone";
            case "createTime" -> "create_time";
            case "updateTime" -> "update_time";
            default -> "";
        };
    }

    private void orderByDesc(LambdaQueryWrapper<SysUser> wrapper, String property) {
        switch (property) {
            case "username":
                wrapper.orderByDesc(SysUser::getUsername);
                break;
            case "createTime":
                wrapper.orderByDesc(SysUser::getCreateTime);
                break;
            case "nickname":
                wrapper.orderByDesc(SysUser::getNickname);
                break;
            // 添加其他需要排序的字段
            default:
                wrapper.orderByDesc(SysUser::getCreateTime);
                break;
        }
    }

    private void orderByAsc(LambdaQueryWrapper<SysUser> wrapper, String property) {
        switch (property) {
            case "username":
                wrapper.orderByAsc(SysUser::getUsername);
                break;
            case "createTime":
                wrapper.orderByAsc(SysUser::getCreateTime);
                break;
            case "nickname":
                wrapper.orderByAsc(SysUser::getNickname);
                break;
            // 添加其他需要排序的字段
            default:
                wrapper.orderByAsc(SysUser::getCreateTime);
                break;
        }
    }
}