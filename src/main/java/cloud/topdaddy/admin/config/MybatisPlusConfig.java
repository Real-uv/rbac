package cloud.topdaddy.admin.config;

import cloud.topdaddy.admin.security.UserDetailsImpl;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.LocalDateTime;

/**
 * MyBatis Plus配置
 *
 * @author topdaddy
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * MyBatis Plus拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 分页插件
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        paginationInterceptor.setMaxLimit(1000L); // 设置最大单页限制数量
        paginationInterceptor.setOverflow(false); // 溢出总页数后是否进行处理

        interceptor.addInnerInterceptor(paginationInterceptor);

        return interceptor;
    }

    /**
     * 字段自动填充处理器
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                // 创建时间
                if (metaObject.hasSetter("createTime")) {
                    this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
                }

                // 更新时间
                if (metaObject.hasSetter("updateTime")) {
                    this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
                }

                // 创建者
                if (metaObject.hasSetter("creator")) {
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
                        this.strictInsertFill(metaObject, "creator", Long.class, userDetails.getUserId());
                    }
                }
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                // 更新时间
                if (metaObject.hasSetter("updateTime")) {
                    this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
                }
            }
        };
    }
}
