package cn.api.apiinterface.controller;

import clientsdk.model.User;
import clientsdk.utils.SignUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/name")
public class NameController {

    @GetMapping("/")
    public String getNameByGet(String name){
        return "Get方法，名字："+name;
    }

    @PostMapping("/")
    public String getNameByPost(@RequestParam(value = "name") String name){
        return "Post方法，名字："+name;
    }

    @PostMapping("/user")
    public String getNameByPostRestful(@RequestBody User user, HttpServletRequest request){
        String accessKey = request.getHeader("accessKey");
        return "Post方法，名字："+user.getUsername();
    }

}
