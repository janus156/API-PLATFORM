package cn.api.apiinterface.controller;


import clientsdk.model.User;
import cn.api.apiinterface.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/hello")
public class HelloController {

    @Autowired
    private HelloService helloService;

    @PostMapping("/user")
    public String sayHelloByPost(@RequestBody User user){
        return helloService.getRandomExpression()+"! "+user.getUsername();
    }

    @GetMapping("/usr")
    public String sayHelloByGET(@RequestParam(value = "name")String name){
        return helloService.getRandomExpression()+"! "+name;
    }

}
