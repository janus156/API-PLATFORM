package cn.api.apiinterface.service;

import cn.api.apiinterface.mapper.OrderMapper;
import cn.api.apiinterface.model.entity.OrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class TestOrder {
    @Autowired
    private PayInterfaceService payInterfaceService;

    @Autowired
    private OrderMapper orderMapper;


    @Test
    void test1(){
        OrderEntity orderEntity = payInterfaceService.getById(1L);
        System.out.println(orderEntity);
    }

    @Test
    void insert(){
        orderMapper.createOrder(1L,1L);
    }

    @Test
    void testBuy(){
        payInterfaceService.buy(1L,2L);
    }

    @Test
    void testAfford(){
        boolean afford = payInterfaceService.afford(1L, 2L);
        System.out.println(afford);
    }

    @Test
    void testIsAfford(){
        boolean isAfford = payInterfaceService.isAfford(1L, 2L);
        System.out.println(isAfford);
    }

}
