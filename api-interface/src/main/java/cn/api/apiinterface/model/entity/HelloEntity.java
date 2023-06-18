package cn.api.apiinterface.model.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@TableName(value ="hello")
@Data
public class HelloEntity implements Serializable{
    @TableId(type = IdType.AUTO)
    private Long id;

    private String expression;

    @TableLogic
    private Integer isDelete;

    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
