package com.linghang.wusthelper.spider.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 研究生的成绩
 * 作为一个实体类, 除了爬虫可获取的信息外, 附加了编号和学生学号
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GraduateScore {

    @JsonIgnore
    private Integer achievementId;// 编号

    @JsonIgnore
    private String stuNum;  // 学号

    private String courseName;//课程名

    private Double credit;//课程学分

    private String term;//选修学期

    private String score;//成绩分数

}
