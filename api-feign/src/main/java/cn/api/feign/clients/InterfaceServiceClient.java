package cn.api.feign.clients;



import cn.api.model.entity.InterfaceInfo;
import cn.api.model.entity.User;
import cn.api.model.entity.interfaceinfo.InterfaceInfoInvokeRequest;
import cn.api.model.response.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "interfaceservice",url = "http://localhost:7529/api")
public interface InterfaceServiceClient {


    @GetMapping("/inner/getInterInfo")
    BaseResponse<InterfaceInfo>
    getInterfaceInfoByUrlAndPath(@RequestParam(value = "url") String url,
                                 @RequestParam(value = "method") String method);

    @GetMapping("/inner/getUserInfo")
    BaseResponse<User> getUserInfo(@RequestParam(value = "accesskey") String accesskey);


    @GetMapping("/inner/invokeTime")
    BaseResponse<Boolean> getInvoke
            (@RequestParam(value = "interfaceInfoId") String interfaceInfoId,
             @RequestParam(value = "userId") String userId                     );


    @PostMapping("/hello/user")
    String sayHelloByPost(@RequestBody User user);



}
