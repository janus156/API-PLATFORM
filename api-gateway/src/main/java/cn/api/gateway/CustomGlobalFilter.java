package cn.api.gateway;



import clientsdk.utils.SignUtils;
import cn.api.feign.clients.InterfaceServiceClient;
import cn.api.model.entity.InterfaceInfo;
import cn.api.model.entity.User;
import cn.api.model.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 全局过滤
 */
@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @Autowired
    private InterfaceServiceClient client;

    private static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1");

    private static final String INTERFACE_HOST = "http://localhost:7529";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        ServerHttpResponse response = exchange.getResponse();

        String sourceAddress = request.getLocalAddress().getHostString();
        log.info("请求的ip:{}",sourceAddress);
        //获取请求头
        HttpHeaders headers = request.getHeaders();
        String nonce = headers.getFirst("nonce");
        String accessKey = headers.getFirst("accessKey");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body = headers.getFirst("body");


        Long currentTime = System.currentTimeMillis() / 1000;
        final Long FIVE_MINUTES = 60 * 5L;
        if ((currentTime - Long.parseLong(timestamp)) >= FIVE_MINUTES) {
            return handleNoAuth(response);
        }
        if (currentTime-Long.valueOf(timestamp)>=FIVE_MINUTES){
            return handleNoAuth(response);
        }

        //1.判断调用者ak
        BaseResponse<User> userInfo = client.getUserInfo(accessKey);
        if (userInfo==null || userInfo.getData()==null){
            log.error("调用者ak异常,ak:{}",userInfo);
            handleNoAuth(response);
        }
        String serverSign = SignUtils.genSign(body, userInfo.getData().getSecretKey());

        if (!serverSign.equals(sign)){
            log.info("调用者sign异常,禁止调用！sign:{}",sign);
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }

        //2.判断调用接口路径、方法
        String path = INTERFACE_HOST + request.getPath().value();
        path=path.replace("api/", "");
        String method = request.getMethod().toString();
        log.info("请求的路径:{}",path);
        log.info("请求的方法:{}",method);
        BaseResponse<InterfaceInfo> interfaceInfo = client.getInterfaceInfoByUrlAndPath(path, method);
        if (interfaceInfo==null || interfaceInfo.getData()==null){
            log.error("调用接口异常,url:{},path{}",path,method);
        }

        //3.调用成功后次数-1

        Long userId =userInfo.getData().getId();
        Long interfaceId = interfaceInfo.getData().getId();
        BaseResponse<Boolean> invokeInfo = client.getInvoke (String.valueOf(interfaceId),String.valueOf(userId));
        if (invokeInfo==null || invokeInfo.getData()==null || Boolean.FALSE.equals(invokeInfo.getData())){
            log.error("调用次数-1异常,userId:{},interfaceId{}",userId,interfaceId);
        } else if (Boolean.TRUE.equals(invokeInfo.getData())){
            log.error("调用成功,userId:{},interfaceId{}",userId,interfaceId);
        }



        if (response.getStatusCode()!=HttpStatus.OK){
            return handleNoInvoke(response);
        }

        return handleResponse(exchange,chain);

    }


    /**
     * invoke次数+1
     * @param exchange
     * @param chain
     * @return
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓存数据的工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到响应码
            HttpStatus statusCode = originalResponse.getStatusCode();

            if (statusCode == HttpStatus.OK) {
                // 装饰，增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    // 等调用完转发的接口后才会执行
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {

                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            // 往返回值里写数据
                            // 拼接字符串
                            return super.writeWith(
                                    fluxBody.map(dataBuffer -> {
                                        byte[] content = new byte[dataBuffer.readableByteCount()];
                                        dataBuffer.read(content);
                                        DataBufferUtils.release(dataBuffer);//释放掉内存
                                        // 构建日志
                                        StringBuilder sb2 = new StringBuilder(200);
                                        List<Object> rspArgs = new ArrayList<>();
                                        rspArgs.add(originalResponse.getStatusCode());
                                        String data = new String(content, StandardCharsets.UTF_8); //data
                                        sb2.append(data);
                                        // 打印日志
                                        log.info("网关响应结果：" + data);
                                        return bufferFactory.wrap(content);
                                    }));
                        } else {
                            // 调用失败，返回一个规范的错误码
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 设置 response 对象为装饰过的
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange); // 降级处理返回数据
        } catch (Exception e) {
            log.error("网关处理响应异常" + e);
            return chain.filter(exchange);
        }
    }


    @Override
    public int getOrder() {
        return -1;
    }


    public Mono<Void> handleNoAuth(ServerHttpResponse response){
        return response.setComplete();
    }

    public Mono<Void> handleNoInvoke(ServerHttpResponse response){
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }



}