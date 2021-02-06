package com.linghang.wusthelper.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;


/**
 * graduate_student 实体类, 数据库表参考附录文件
 * @author
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GraduateStudent implements Serializable {
    /**
     * 学号(主键)
     */
    private String stuNum;

    /**
     * 研究生登录密码(加密)
     */
    @JsonIgnore
    private String yjsPassword;

    /**
     * 研究生姓名
     */
    private String stuName;

    /**
     * 年级
     */
    private Integer stuGrade;

    /**
     * 导师姓名
     */
    private String mentorName;

    /**
     * 类别
     */
    private String stuCategory;

    /**
     * 院系
     */
    private String department;

    /**
     * 专业
     */
    private String major;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 头像
     */
    private String pic;

    @JsonIgnore
    private String uuid;

    /**
     * 邮箱
     */
    private String mail;

    /**
     * 注册时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}