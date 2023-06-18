package cn.api.apiinterface.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class DelayListener {
    public static final String JAVABOY_QUEUE_NAME = "javaboy_queue_name";
    public static final String JAVABOY_EXCHANGE_NAME = "javaboy_exchange_name";
    public static final String JAVABOY_ROUTING_KEY = "javaboy_routing_key";
    public static final String DLX_QUEUE_NAME = "dlx_queue_name";
    public static final String DLX_EXCHANGE_NAME = "dlx_exchange_name";
    public static final String DLX_ROUTING_KEY = "dlx_routing_key";


    //死信队列
    @RabbitListener(queues = DelayListener.DLX_QUEUE_NAME)
    public void handle(String msg) {
        log.info("收到了消息:{}",msg);
    }


}
