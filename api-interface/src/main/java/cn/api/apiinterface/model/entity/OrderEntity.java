package cn.api.apiinterface.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;


@TableName(value = "interface_order")
@Data
public class OrderEntity {
    @TableId(type = IdType.AUTO)
    private long id;

    private long userId;

    private long interfaceId;

    private int status;

    private Date createTime;

    private Date updateTime;

    @TableLogic
    private int isDelete;
}
