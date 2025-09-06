package cloud.topdaddy.admin.servcie.impl;

import cloud.topdaddy.admin.entity.SysPermission;
import cloud.topdaddy.admin.entity.SysRole;
import cloud.topdaddy.admin.entity.SysUser;
import cloud.topdaddy.admin.servcie.CacheService;
import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 缓存服务实现类
 * 
 * @author topdaddy
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    // 缓存键前缀
    private static final String USER_TOKEN_PREFIX = "rbac:user:token:";
    private static final String USER_PERMISSIONS_PREFIX = "rbac:user:permissions:";
    private static final String USER_ROLES_PREFIX = "rbac:user:roles:";
    private static final String ONLINE_USERS_KEY = "rbac:online:users";
    
    // 缓存过期时间（秒）
    private static final long USER_CACHE_EXPIRE = 7200; // 2小时
    private static final long PERMISSION_CACHE_EXPIRE = 3600; // 1小时
    private static final long ROLE_CACHE_EXPIRE = 3600; // 1小时

    @Override
    public void cacheUser(String token, SysUser user) {
        try {
            String key = USER_TOKEN_PREFIX + token;
            redisTemplate.opsForValue().set(key, JSON.toJSONString(user), USER_CACHE_EXPIRE, TimeUnit.SECONDS);
            log.debug("缓存用户信息成功，用户ID: {}, Token: {}", user.getId(), token);
        } catch (Exception e) {
            log.error("缓存用户信息失败，用户ID: {}", user.getId(), e);
        }
    }

    @Override
    public SysUser getCachedUser(String token) {
        try {
            String key = USER_TOKEN_PREFIX + token;
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                return JSON.parseObject(value.toString(), SysUser.class);
            }
        } catch (Exception e) {
            log.error("获取缓存用户信息失败，Token: {}", token, e);
        }
        return null;
    }

    @Override
    public void removeCachedUser(String token) {
        try {
            String key = USER_TOKEN_PREFIX + token;
            redisTemplate.delete(key);
            log.debug("删除用户缓存成功，Token: {}", token);
        } catch (Exception e) {
            log.error("删除用户缓存失败，Token: {}", token, e);
        }
    }

    @Override
    public void cacheUserPermissions(Long userId, List<SysPermission> permissions) {
        try {
            String key = USER_PERMISSIONS_PREFIX + userId;
            redisTemplate.opsForValue().set(key, JSON.toJSONString(permissions), PERMISSION_CACHE_EXPIRE, TimeUnit.SECONDS);
            log.debug("缓存用户权限成功，用户ID: {}, 权限数量: {}", userId, permissions.size());
        } catch (Exception e) {
            log.error("缓存用户权限失败，用户ID: {}", userId, e);
        }
    }

    @Override
    public List<SysPermission> getCachedUserPermissions(Long userId) {
        try {
            String key = USER_PERMISSIONS_PREFIX + userId;
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                return JSON.parseArray(value.toString(), SysPermission.class);
            }
        } catch (Exception e) {
            log.error("获取缓存用户权限失败，用户ID: {}", userId, e);
        }
        return null;
    }

    @Override
    public void removeCachedUserPermissions(Long userId) {
        try {
            String key = USER_PERMISSIONS_PREFIX + userId;
            redisTemplate.delete(key);
            log.debug("删除用户权限缓存成功，用户ID: {}", userId);
        } catch (Exception e) {
            log.error("删除用户权限缓存失败，用户ID: {}", userId, e);
        }
    }

    @Override
    public void cacheUserRoles(Long userId, List<SysRole> roles) {
        try {
            String key = USER_ROLES_PREFIX + userId;
            redisTemplate.opsForValue().set(key, JSON.toJSONString(roles), ROLE_CACHE_EXPIRE, TimeUnit.SECONDS);
            log.debug("缓存用户角色成功，用户ID: {}, 角色数量: {}", userId, roles.size());
        } catch (Exception e) {
            log.error("缓存用户角色失败，用户ID: {}", userId, e);
        }
    }

    @Override
    public List<SysRole> getCachedUserRoles(Long userId) {
        try {
            String key = USER_ROLES_PREFIX + userId;
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                return JSON.parseArray(value.toString(), SysRole.class);
            }
        } catch (Exception e) {
            log.error("获取缓存用户角色失败，用户ID: {}", userId, e);
        }
        return null;
    }

    @Override
    public void removeCachedUserRoles(Long userId) {
        try {
            String key = USER_ROLES_PREFIX + userId;
            redisTemplate.delete(key);
            log.debug("删除用户角色缓存成功，用户ID: {}", userId);
        } catch (Exception e) {
            log.error("删除用户角色缓存失败，用户ID: {}", userId, e);
        }
    }

    @Override
    public void addOnlineUser(String token, Long userId) {
        try {
            redisTemplate.opsForHash().put(ONLINE_USERS_KEY, token, userId.toString());
            log.debug("添加在线用户成功，用户ID: {}, Token: {}", userId, token);
        } catch (Exception e) {
            log.error("添加在线用户失败，用户ID: {}", userId, e);
        }
    }

    @Override
    public void removeOnlineUser(String token) {
        try {
            redisTemplate.opsForHash().delete(ONLINE_USERS_KEY, token);
            log.debug("移除在线用户成功，Token: {}", token);
        } catch (Exception e) {
            log.error("移除在线用户失败，Token: {}", token, e);
        }
    }

    @Override
    public Set<String> getOnlineUsers() {
        try {
            Set<Object> keys = redisTemplate.opsForHash().keys(ONLINE_USERS_KEY);
            return keys.stream()
                    .map(Object::toString)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            log.error("获取在线用户列表失败", e);
            return Set.of();
        }
    }

    @Override
    public boolean isUserOnline(String token) {
        try {
            return redisTemplate.opsForHash().hasKey(ONLINE_USERS_KEY, token);
        } catch (Exception e) {
            log.error("检查用户在线状态失败，Token: {}", token, e);
            return false;
        }
    }

    @Override
    public void forceUserOffline(Long userId) {
        try {
            // 获取所有在线用户
            Set<String> onlineTokens = getOnlineUsers();
            
            for (String token : onlineTokens) {
                Object cachedUserId = redisTemplate.opsForHash().get(ONLINE_USERS_KEY, token);
                if (cachedUserId != null && userId.toString().equals(cachedUserId.toString())) {
                    // 移除在线状态
                    removeOnlineUser(token);
                    // 删除用户缓存
                    removeCachedUser(token);
                }
            }
            
            // 清除用户相关缓存
            refreshUserCache(userId);
            
            log.info("强制用户下线成功，用户ID: {}", userId);
        } catch (Exception e) {
            log.error("强制用户下线失败，用户ID: {}", userId, e);
        }
    }

    @Override
    public void cleanExpiredCache() {
        try {
            // Redis会自动清理过期的键，这里可以添加一些自定义的清理逻辑
            log.debug("清理过期缓存完成");
        } catch (Exception e) {
            log.error("清理过期缓存失败", e);
        }
    }

    @Override
    public void refreshUserCache(Long userId) {
        try {
            // 清除用户权限和角色缓存
            removeCachedUserPermissions(userId);
            removeCachedUserRoles(userId);
            
            log.debug("刷新用户缓存成功，用户ID: {}", userId);
        } catch (Exception e) {
            log.error("刷新用户缓存失败，用户ID: {}", userId, e);
        }
    }

    @Override
    public void clearAllUserCache() {
        try {
            // 清除所有用户相关缓存
            Set<String> keys = redisTemplate.keys("rbac:*");
            if (!CollectionUtils.isEmpty(keys)) {
                redisTemplate.delete(keys);
            }
            
            log.info("清除所有用户缓存成功");
        } catch (Exception e) {
            log.error("清除所有用户缓存失败", e);
        }
    }
}