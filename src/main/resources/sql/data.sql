-- RBAC后端管理系统初始化数据
USE `rbac_db`;

-- 初始化用户数据
INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `email`, `phone`, `avatar`, `gender`, `status`, `remark`, `creator`, `create_time`) VALUES
(1, 'admin', '$2a$10$7JB720yubVSOfvVWbazBuOWShWzhlicrpc3Dt2R/JpaCkFC4eMBcG', '超级管理员', 'admin@example.com', '13800138000', NULL, 1, 1, '系统超级管理员', 1, NOW()),
(2, 'test', '$2a$10$7JB720yubVSOfvVWbazBuOWShWzhlicrpc3Dt2R/JpaCkFC4eMBcG', '测试用户', 'test@example.com', '13800138001', NULL, 1, 1, '测试用户账号', 1, NOW());

-- 初始化角色数据
INSERT INTO `sys_role` (`id`, `name`, `code`, `description`, `sort`, `status`, `data_scope`, `remark`, `creator`, `create_time`) VALUES
(1, '超级管理员', 'SUPER_ADMIN', '拥有系统所有权限', 1, 1, 1, '系统超级管理员角色', 1, NOW()),
(2, '系统管理员', 'SYSTEM_ADMIN', '系统管理员，拥有大部分权限', 2, 1, 2, '系统管理员角色', 1, NOW()),
(3, '普通用户', 'USER', '普通用户，拥有基础权限', 3, 1, 5, '普通用户角色', 1, NOW());

-- 初始化权限数据
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `code`, `type`, `path`, `component`, `icon`, `sort`, `status`, `visible`, `remark`, `creator`, `create_time`) VALUES
-- 系统管理
(1, 0, '系统管理', 'system', 1, '/system', 'Layout', 'system', 1, 1, 1, '系统管理目录', 1, NOW()),
(2, 1, '用户管理', 'system:user', 2, '/system/user', 'system/user/index', 'user', 1, 1, 1, '用户管理菜单', 1, NOW()),
(3, 2, '用户查询', 'system:user:query', 3, '', '', '', 1, 1, 0, '用户查询权限', 1, NOW()),
(4, 2, '用户新增', 'system:user:add', 3, '', '', '', 2, 1, 0, '用户新增权限', 1, NOW()),
(5, 2, '用户修改', 'system:user:edit', 3, '', '', '', 3, 1, 0, '用户修改权限', 1, NOW()),
(6, 2, '用户删除', 'system:user:delete', 3, '', '', '', 4, 1, 0, '用户删除权限', 1, NOW()),
(7, 2, '重置密码', 'system:user:resetPwd', 3, '', '', '', 5, 1, 0, '重置密码权限', 1, NOW()),

(8, 1, '角色管理', 'system:role', 2, '/system/role', 'system/role/index', 'role', 2, 1, 1, '角色管理菜单', 1, NOW()),
(9, 8, '角色查询', 'system:role:query', 3, '', '', '', 1, 1, 0, '角色查询权限', 1, NOW()),
(10, 8, '角色新增', 'system:role:add', 3, '', '', '', 2, 1, 0, '角色新增权限', 1, NOW()),
(11, 8, '角色修改', 'system:role:edit', 3, '', '', '', 3, 1, 0, '角色修改权限', 1, NOW()),
(12, 8, '角色删除', 'system:role:delete', 3, '', '', '', 4, 1, 0, '角色删除权限', 1, NOW()),
(13, 8, '分配权限', 'system:role:assignPermission', 3, '', '', '', 5, 1, 0, '角色分配权限', 1, NOW()),

(14, 1, '权限管理', 'system:permission', 2, '/system/permission', 'system/permission/index', 'permission', 3, 1, 1, '权限管理菜单', 1, NOW()),
(15, 14, '权限查询', 'system:permission:query', 3, '', '', '', 1, 1, 0, '权限查询权限', 1, NOW()),
(16, 14, '权限新增', 'system:permission:add', 3, '', '', '', 2, 1, 0, '权限新增权限', 1, NOW()),
(17, 14, '权限修改', 'system:permission:edit', 3, '', '', '', 3, 1, 0, '权限修改权限', 1, NOW()),
(18, 14, '权限删除', 'system:permission:delete', 3, '', '', '', 4, 1, 0, '权限删除权限', 1, NOW()),

-- 日志管理
(19, 0, '日志管理', 'log', 1, '/log', 'Layout', 'log', 2, 1, 1, '日志管理目录', 1, NOW()),
(20, 19, '操作日志', 'log:operation', 2, '/log/operation', 'log/operation/index', 'operation', 1, 1, 1, '操作日志菜单', 1, NOW()),
(21, 20, '操作日志查询', 'log:operation:query', 3, '', '', '', 1, 1, 0, '操作日志查询权限', 1, NOW()),
(22, 20, '操作日志删除', 'log:operation:delete', 3, '', '', '', 2, 1, 0, '操作日志删除权限', 1, NOW()),

(23, 19, '登录日志', 'log:login', 2, '/log/login', 'log/login/index', 'login', 2, 1, 1, '登录日志菜单', 1, NOW()),
(24, 23, '登录日志查询', 'log:login:query', 3, '', '', '', 1, 1, 0, '登录日志查询权限', 1, NOW()),
(25, 23, '登录日志删除', 'log:login:delete', 3, '', '', '', 2, 1, 0, '登录日志删除权限', 1, NOW()),

-- 个人中心
(26, 0, '个人中心', 'profile', 2, '/profile', 'profile/index', 'user', 3, 1, 1, '个人中心菜单', 1, NOW()),
(27, 26, '个人信息', 'profile:info', 3, '', '', '', 1, 1, 0, '查看个人信息权限', 1, NOW()),
(28, 26, '修改密码', 'profile:updatePwd', 3, '', '', '', 2, 1, 0, '修改密码权限', 1, NOW());

-- 初始化用户角色关联
INSERT INTO `sys_user_role` (`user_id`, `role_id`, `creator`, `create_time`) VALUES
(1, 1, 1, NOW()),  -- admin用户分配超级管理员角色
(2, 3, 1, NOW());  -- test用户分配普通用户角色

-- 初始化角色权限关联（超级管理员拥有所有权限）
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`, `creator`, `create_time`)
SELECT 1, id, 1, NOW() FROM `sys_permission` WHERE `deleted` = 0;

-- 普通用户角色权限（只有个人中心相关权限）
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`, `creator`, `create_time`) VALUES
(3, 26, 1, NOW()),  -- 个人中心
(3, 27, 1, NOW()),  -- 个人信息
(3, 28, 1, NOW());  -- 修改密码

-- 系统管理员角色权限（除了用户删除外的大部分权限）
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`, `creator`, `create_time`) VALUES
(2, 1, 1, NOW()),   -- 系统管理
(2, 2, 1, NOW()),   -- 用户管理
(2, 3, 1, NOW()),   -- 用户查询
(2, 4, 1, NOW()),   -- 用户新增
(2, 5, 1, NOW()),   -- 用户修改
(2, 7, 1, NOW()),   -- 重置密码
(2, 8, 1, NOW()),   -- 角色管理
(2, 9, 1, NOW()),   -- 角色查询
(2, 10, 1, NOW()),  -- 角色新增
(2, 11, 1, NOW()),  -- 角色修改
(2, 13, 1, NOW()),  -- 分配权限
(2, 14, 1, NOW()),  -- 权限管理
(2, 15, 1, NOW()),  -- 权限查询
(2, 19, 1, NOW()),  -- 日志管理
(2, 20, 1, NOW()),  -- 操作日志
(2, 21, 1, NOW()),  -- 操作日志查询
(2, 23, 1, NOW()),  -- 登录日志
(2, 24, 1, NOW()),  -- 登录日志查询
(2, 26, 1, NOW()),  -- 个人中心
(2, 27, 1, NOW()),  -- 个人信息
(2, 28, 1, NOW());  -- 修改密码