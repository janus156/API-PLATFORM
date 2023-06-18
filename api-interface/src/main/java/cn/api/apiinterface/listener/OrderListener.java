package cn.api.apiinterface.listener;

import cn.api.apiinterface.model.dto.order.OrderMessage;
import cn.api.apiinterface.model.entity.OrderEntity;
import cn.api.apiinterface.service.PayInterfaceService;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class OrderListener {

    @Autowired
    private PayInterfaceService payInterfaceService;

    //这里必须注释，保证能发到死信队列
////    @RabbitListener(queues = "apiplat.pay.queue")
////    public void payQueue(String msg){
////        log.info("提前");
////        log.info("收到了msg：{}",msg);
////
////    }

    //死信队列,错误消息直接入库,人工处理
    @RabbitListener(queues = "apiplat.paydead.queue")
    public void deadQueue(String msg){
        log.info("收到了延时msg：{}",msg);

        OrderMessage orderEntity = JSONUtil.toBean(msg, OrderMessage.class);

        boolean isAfford = payInterfaceService.isAfford(orderEntity.getUserId(), orderEntity.getInterfaceId());
        if (!isAfford){
            log.warn("订单未支付,userId:{},interfaceId:{}",orderEntity.getUserId(),orderEntity.getInterfaceId());
            payInterfaceService.deleteOrder(orderEntity.getUserId(), orderEntity.getInterfaceId());
        }

        if (isAfford){
            log.info("订单成功支付");
        }

    }




}