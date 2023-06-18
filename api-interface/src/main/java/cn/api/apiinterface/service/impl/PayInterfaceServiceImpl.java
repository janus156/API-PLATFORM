package cn.api.apiinterface.service.impl;

import cn.api.apiinterface.Util.GlobalIdUtils;
import cn.api.apiinterface.constant.RedisConstants;
import cn.api.apiinterface.mapper.OrderMapper;
import cn.api.apiinterface.model.dto.order.OrderMessage;
import cn.api.apiinterface.model.entity.OrderEntity;
import cn.api.apiinterface.service.PayInterfaceService;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Wrapper;

@Service
@Slf4j
public class PayInterfaceServiceImpl extends ServiceImpl<OrderMapper,OrderEntity>
        implements PayInterfaceService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void buy(long userId, long interfaceId) {
        //1.创建订单
        orderMapper.createOrder(userId,interfaceId);
        //2.发送队列
        OrderMessage orderMessage = new OrderMessage(userId, interfaceId);
        String message = JSONUtil.toJsonStr(orderMessage);

        //唯一标识
        String onlyId = GlobalIdUtils.getOnlyId(redisTemplate, RedisConstants.ONLY_ID);
        CorrelationData correlationData = new CorrelationData(onlyId);

        correlationData.getFuture().addCallback(
                result -> {
                    if(result.isAck()){
                        // 3.1.ack，消息成功
                        System.out.println("消息发送成功,id:"+correlationData.getId());
                    }else{
                        // 3.2.nack，消息失败
                        System.out.println("消息发送失败,id:"+correlationData.getId());
                    }
                },
                ex -> {
                    System.out.println("消息发送失败,id:"+correlationData.getId());
                }
        );
        // 3.发送消息
        rabbitTemplate.convertAndSend("apiplat.pay.exchange", "pay", message, correlationData);

        // 休眠一会儿，等待ack回执
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean afford(long userId, long interfaceId) {
        //1.查出订单
        QueryWrapper<OrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId)
                .eq("interfaceId",interfaceId)
                .eq("isDelete",0);
        OrderEntity orderEntity = orderMapper.selectOne(queryWrapper);
        if (orderEntity==null) return false;
        //2.修改订单
        orderEntity.setStatus(1);
        updateById(orderEntity);

        System.out.println(orderEntity);
        return updateById(orderEntity);

    }

    @Override
    public boolean isAfford(long userId, long interfaceId) {
        //查询订单是否为1
        QueryWrapper<OrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId)
                .eq("interfaceId",interfaceId)
                .eq("isDelete",0);
        OrderEntity orderEntity = orderMapper.selectOne(queryWrapper);

        if (orderEntity==null) return false;

        return orderEntity.getStatus()==1;
    }

    @Override
    public boolean deleteOrder(long userId, long interfaceId) {
        QueryWrapper<OrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId)
                .eq("interfaceId",interfaceId)
                .eq("isDelete",0);
        OrderEntity orderEntity = orderMapper.selectOne(queryWrapper);
        return removeById(orderEntity);
    }

}
