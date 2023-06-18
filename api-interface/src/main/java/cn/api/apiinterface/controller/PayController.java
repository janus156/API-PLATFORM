package cn.api.apiinterface.controller;

import cn.api.apiinterface.common.ResultUtils;
import cn.api.apiinterface.model.dto.order.OrderMessage;
import cn.api.apiinterface.model.entity.PayEntity;
import cn.api.apiinterface.service.PayInterfaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/pay")
public class PayController {

    @Autowired
    private PayInterfaceService payInterfaceService;


    //TODO 购买
    @PostMapping("/buy")
    public String buy(@RequestBody PayEntity payEntity){
        payInterfaceService.buy(payEntity.getUserId(),payEntity.getInterfaceId());
        return "ok";
    }

    //todo 支付

    @PostMapping("/afford")
    public Boolean afford(@RequestBody PayEntity payEntity){
        boolean afford = payInterfaceService.afford(payEntity.getUserId(), payEntity.getInterfaceId());
        return afford;
    }

}