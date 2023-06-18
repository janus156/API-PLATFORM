package cn.api.apiinterface.service;

import cn.api.apiinterface.Util.GlobalIdUtils;
import cn.api.apiinterface.constant.RedisConstants;
import cn.api.apiinterface.listener.DelayListener;
import cn.api.apiinterface.model.entity.HelloEntity;
import cn.api.apiinterface.model.entity.User;
import com.alibaba.nacos.client.naming.utils.RandomUtils;
import com.sun.mail.imap.protocol.ID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
@Slf4j
public class Test1 {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private HelloService helloService;


    @Test
    void generateId(){
        String onlyId = GlobalIdUtils.getOnlyId(redisTemplate, RedisConstants.ONLY_ID);
        System.out.println(onlyId);
    }

    @Test
    void sendMsg(){
        String message = "一个简单的消息!";

        // 2.全局唯一的消息ID，唯一标识
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

    @Test
    void contextLoads() {
        List<Long> ids=new ArrayList<>();
        ids.add(2L);
        ids.add(1L);
        ids.add(5L);
        ids.add(6L);

        List<User> userList = userService.listByIds(ids);
        System.out.println(userList);
    }

    @Test
    void testHello(){
        HelloEntity service = helloService.getById(5L);
        System.out.println(service.getExpression());
    }





}