package com.stdc.visual.auth.entity.user.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.stdc.visual.common.utils.StdcVisualConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: wang_jie
 * @data: 2022/5/23--9:51
 * @describe: 用户表
 */
@Data
@TableName(StdcVisualConstant.PROJECT_PREFIX + "sys_user")
@ApiModel(value = "SysUser对象", description = "用户表")
public class SysUser implements Serializable {

    @TableId
    @ApiModelProperty("ID")
    private String userId;

    @ApiModelProperty("组织ID")
    private String deptId;

    @ApiModelProperty("账号")
    private String username;

    @ApiModelProperty("姓名")
    private String nickName;

    @ApiModelProperty("性别")
    private String gender;

    @ApiModelProperty("电话")
    private String phone;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty("是否管理员")
    private Long admin;

    @ApiModelProperty("状态")
    private Long enabled;

    @ApiModelProperty("创建人")
    private String createBy;

    @ApiModelProperty("修改人")
    private String updateBy;

    @ApiModelProperty("密码重置时间")
    private Long pwdResetTime;

    @ApiModelProperty("创建时间")
    private Long createTime;

    @ApiModelProperty("修改时间")
    private Long updateTime;


    private static final long serialVersionUID = 1L;

//    public Long getUserId() {
//        return userId;
//    }
//
//    public void setUserId(Long userId) {
//        this.userId = userId;
//    }
//
//    public Long getDeptId() {
//        return deptId;
//    }
//
//    public void setDeptId(Long deptId) {
//        this.deptId = deptId;
//    }
//
//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public String getNickName() {
//        return nickName;
//    }
//
//    public void setNickName(String nickName) {
//        this.nickName = nickName;
//    }
//
//    public String getGender() {
//        return gender;
//    }
//
//    public void setGender(String gender) {
//        this.gender = gender;
//    }
//
//    public String getPhone() {
//        return phone;
//    }
//
//    public void setPhone(String phone) {
//        this.phone = phone;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    /**
//     * 设置密码加密
//     * @param password
//     */
//    public void setPassword(String password) {
//        this.password = password;
////        this.password = PBKDF2.getPBKDF2(password, String.valueOf(this.username));
//    }
    public Boolean isAdmin() {
        return this.admin != null ? this.admin > 0 : false;
    }
//    public Long getAdmin() {
//        return this.admin;
//    }
//
//    public void setAdmin(Long admin) {
//        this.admin = admin;
//    }
//
//    public Long getEnabled() {
//        return enabled;
//    }
//
//    public void setEnabled(Long enabled) {
//        this.enabled = enabled;
//    }
//
//    public String getCreateBy() {
//        return createBy;
//    }
//
//    public void setCreateBy(String createBy) {
//        this.createBy = createBy;
//    }
//
//    public String getUpdateBy() {
//        return updateBy;
//    }
//
//    public void setUpdateBy(String updateBy) {
//        this.updateBy = updateBy;
//    }
//
//    public Long getPwdResetTime() {
//        return pwdResetTime;
//    }
//
//    public void setPwdResetTime(Long pwdResetTime) {
//        this.pwdResetTime = pwdResetTime;
//    }
//
//    public Long getCreateTime() {
//        return createTime;
//    }
//
//    public void setCreateTime(Long createTime) {
//        this.createTime = createTime;
//    }
//
//    public Long getUpdateTime() {
//        return updateTime;
//    }
//
//    public void setUpdateTime(Long updateTime) {
//        this.updateTime = updateTime;
//    }

}
