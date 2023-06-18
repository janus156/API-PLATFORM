package cn.api.apiinterface.service;

import cn.api.apiinterface.model.entity.HelloEntity;
import cn.api.apiinterface.model.entity.OrderEntity;
import com.baomidou.mybatisplus.extension.service.IService;

public interface PayInterfaceService extends IService<OrderEntity> {

    void buy(long userId,long interfaceId);

    boolean afford(long userId,long interfaceId);

    boolean isAfford(long userId,long interfaceId);

    boolean deleteOrder(long userId,long interfaceId);
}
