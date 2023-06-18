package cn.api.apiinterface.mapper;

import cn.api.apiinterface.model.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

public interface OrderMapper extends BaseMapper<OrderEntity> {

    void createOrder(@Param("userId") long userId,@Param("interfaceId") long interfaceId);
}
