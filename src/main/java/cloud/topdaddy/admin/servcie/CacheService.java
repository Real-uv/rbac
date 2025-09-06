package cloud.topdaddy.admin.servcie;

import cloud.topdaddy.admin.entity.SysPermission;
import cloud.topdaddy.admin.entity.SysRole;
import cloud.topdaddy.admin.entity.SysUser;
import java.util.List;
import java.util.Set;

/**
 * 缓存服务接口
 * 
 * @author topdaddy
 */
public interface CacheService {

    /**
     * 缓存用户信息
     */
    void cacheUser(String token, SysUser user);

    /**
     * 获取缓存的用户信息
     */
    SysUser getCachedUser(String token);

    /**
     * 删除用户缓存
     */
    void removeCachedUser(String token);

    /**
     * 缓存用户权限
     */
    void cacheUserPermissions(Long userId, List<SysPermission> permissions);

    /**
     * 获取缓存的用户权限
     */
    List<SysPermission> getCachedUserPermissions(Long userId);

    /**
     * 删除用户权限缓存
     */
    void removeCachedUserPermissions(Long userId);

    /**
     * 缓存用户角色
     */
    void cacheUserRoles(Long userId, List<SysRole> roles);

    /**
     * 获取缓存的用户角色
     */
    List<SysRole> getCachedUserRoles(Long userId);

    /**
     * 删除用户角色缓存
     */
    void removeCachedUserRoles(Long userId);

    /**
     * 添加在线用户
     */
    void addOnlineUser(String token, Long userId);

    /**
     * 移除在线用户
     */
    void removeOnlineUser(String token);

    /**
     * 获取在线用户列表
     */
    Set<String> getOnlineUsers();

    /**
     * 检查用户是否在线
     */
    boolean isUserOnline(String token);

    /**
     * 强制用户下线
     */
    void forceUserOffline(Long userId);

    /**
     * 清理过期缓存
     */
    void cleanExpiredCache();

    /**
     * 刷新用户相关缓存
     */
    void refreshUserCache(Long userId);

    /**
     * 清除所有用户缓存
     */
    void clearAllUserCache();
}