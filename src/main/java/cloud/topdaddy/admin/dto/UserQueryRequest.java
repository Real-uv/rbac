package cloud.topdaddy.admin.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 用户查询请求DTO
 * 
 * @author topdaddy
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserQueryRequest extends PageQuery {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 创建时间开始
     */
    private LocalDateTime createTimeStart;

    /**
     * 创建时间结束
     */
    private LocalDateTime createTimeEnd;
}