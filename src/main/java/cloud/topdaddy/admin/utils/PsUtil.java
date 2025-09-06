package cloud.topdaddy.admin.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码加密工具类
 * 可独立运行测试自定义加密算法
 */
public class PsUtil {

    /**
     * 密码编码器 - 使用BCrypt算法
     */
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 加密密码
     *
     * @param rawPassword 原始密码
     * @return 加密后的密码
     */
    public static String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 验证密码
     *
     * @param rawPassword     原始密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 主方法 - 用于测试密码加密功能
     */
    public static void main(String[] args) {
        System.out.println("======= 密码加密工具测试 =======");

        // 测试密码
        String password = "admin123";
        System.out.println("原始密码: " + password);

        // 加密密码
        String encodedPassword = encode(password);
        System.out.println("加密后的密码: " + encodedPassword);

        // 验证正确密码
        boolean isMatch = matches(password, encodedPassword);
        System.out.println("密码匹配结果: " + isMatch);

        // 验证错误密码
        boolean isNotMatch = matches("wrong_password", encodedPassword);
        System.out.println("错误密码匹配结果: " + isNotMatch);

        // 测试不同密码的加密结果
        System.out.println("\n======= 不同密码加密结果 =======");
        String[] testPasswords = {"admin", "user123", "test_password"};
        for (String pwd : testPasswords) {
            System.out.println("密码: " + pwd + " => " + encode(pwd));
        }
    }
}
