package cloud.topdaddy.admin.aspect;

import java.lang.annotation.*;

/**
 * 权限验证注解
 * 
 * @author topdaddy
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {
    
    /**
     * 权限编码
     */
    String value();
    
    /**
     * 权限描述
     */
    String description() default "";
    
    /**
     * 是否记录操作日志
     */
    boolean log() default true;
}