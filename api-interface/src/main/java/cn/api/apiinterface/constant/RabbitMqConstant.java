package cn.api.apiinterface.constant;

public class RabbitMqConstant {

    public static final String ROUTING_KEY= "pay";

    public static final String ROUTING_DEAD_KEY= "pay.dead";

    public static final String EXCHANGE="apiplat.pay.exchange";

    public static final String QUEUE="apiplat.pay.queue";

    public static final String DEAD_EXCHANGE="apiplat.pay.deadexchange";

    public static final String DEAD_QUEUE="apiplat.pay.deadqueue";

}