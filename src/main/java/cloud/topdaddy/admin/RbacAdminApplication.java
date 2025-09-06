package cloud.topdaddy.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 启动类
 *
 * @author topdaddy
 * @since 2025-08-10
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableConfigurationProperties
@MapperScan("cloud.topdaddy.admin.mapper") // 添加Mapper扫描
public class RbacAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(RbacAdminApplication.class, args);
        System.out.println("=================================");
        System.out.println("RBAC后端管理系统启动成功！");
        System.out.println("接口文档地址: http://localhost:8080/api/swagger-ui/index.html");
        System.out.println("=================================");
    }
}
