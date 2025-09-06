package cloud.topdaddy.admin.aspect;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * 
 * @author topdaddy
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {
    
    /**
     * 操作模块
     */
    String module() default "";
    
    /**
     * 操作类型
     */
    String type() default "";
    
    /**
     * 操作描述
     */
    String description() default "";
    
    /**
     * 是否记录请求参数
     */
    boolean includeArgs() default true;
    
    /**
     * 是否记录返回结果
     */
    boolean includeResult() default false;
}