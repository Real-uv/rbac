package cloud.topdaddy.admin.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应结果码枚举
 * 
 * @author topdaddy
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    // 通用结果码
    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),
    PARAM_ERROR(400, "参数错误"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    
    // 认证授权相关
    UNAUTHORIZED(401, "未认证"),
    FORBIDDEN(403, "无权限访问"),
    TOKEN_INVALID(4001, "Token无效"),
    TOKEN_EXPIRED(4002, "Token已过期"),
    LOGIN_FAILED(4003, "登录失败"),
    ACCOUNT_DISABLED(4004, "账号已禁用"),
    ACCOUNT_LOCKED(4005, "账号已锁定"),
    PASSWORD_ERROR(4006, "密码错误"),
    
    // 用户相关
    USER_NOT_FOUND(5001, "用户不存在"),
    USERNAME_EXISTS(5002, "用户名已存在"),
    EMAIL_EXISTS(5003, "邮箱已存在"),
    PHONE_EXISTS(5004, "手机号已存在"),
    OLD_PASSWORD_ERROR(5005, "原密码错误"),
    
    // 角色相关
    ROLE_NOT_FOUND(6001, "角色不存在"),
    ROLE_CODE_EXISTS(6002, "角色编码已存在"),
    ROLE_IN_USE(6003, "角色正在使用中，无法删除"),
    
    // 权限相关
    PERMISSION_NOT_FOUND(7001, "权限不存在"),
    PERMISSION_CODE_EXISTS(7002, "权限编码已存在"),
    PERMISSION_IN_USE(7003, "权限正在使用中，无法删除"),
    
    // 系统相关
    SYSTEM_ERROR(9001, "系统异常"),
    DATABASE_ERROR(9002, "数据库异常"),
    REDIS_ERROR(9003, "Redis异常"),
    FILE_UPLOAD_ERROR(9004, "文件上传失败"),
    FILE_DELETE_ERROR(9005, "文件删除失败");

    /**
     * 响应码
     */
    private final Integer code;

    /**
     * 响应消息
     */
    private final String message;
}