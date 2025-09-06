package cloud.topdaddy.admin.security;

import cloud.topdaddy.admin.entity.SysUser;
import cloud.topdaddy.admin.servcie.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Spring Security用户详情服务实现
 * 
 * @author topdaddy
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("加载用户信息: {}", username);
        
        // 根据用户名查询用户信息
        SysUser user = userService.getByUsername(username);
        if (user == null) {
            log.warn("用户不存在: {}", username);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // 查询用户权限
        List<String> permissions = userService.getUserPermissions(user.getId());
        
        // 查询用户角色
        List<String> roles = userService.getUserRoles(user.getId());

        log.debug("用户 {} 拥有权限: {}", username, permissions);
        log.debug("用户 {} 拥有角色: {}", username, roles);

        return new UserDetailsImpl(user, permissions, roles);
    }
}