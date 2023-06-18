package clientsdk.client;


import clientsdk.utils.SignUtils;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.ssh.JschUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import clientsdk.model.User;


import java.util.HashMap;
import java.util.Map;

import static clientsdk.utils.SignUtils.genSign;


public class PlatClient {

    //走网关
    private static final String GATEWAY_HOST = "http://localhost:8090";

    private String accessKey;

    private String secretKey;

    private String nonce;

    public PlatClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public PlatClient(String accessKey, String secretKey, String nonce) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.nonce = nonce;
    }

    private Map<String, String> getHeaderMap(String body) {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("accessKey", accessKey);
        hashMap.put("nonce", nonce);
        hashMap.put("body", body);
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        hashMap.put("sign", SignUtils.genSign(body,secretKey));
        return hashMap;
    }


    public String getUsernameByPost(User user) {
        String json = JSONUtil.toJsonStr(user);
        HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST + "/api/name/user")
                .addHeaders(getHeaderMap(json))
                .body(json)
                .execute();
        String result = httpResponse.body();
        return result;
    }

    //说你好
    public String sayHello(User user){
        String json = JSONUtil.toJsonStr(user);
        HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST + "/api/hello/user")
                .addHeaders(getHeaderMap(json))
                .body(json)
                .execute();
        String body = httpResponse.body();
        return body;
    }

    //得到调用次数
    public String getTime(User user){
        String json = JSONUtil.toJsonStr(user);
        HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST + "/api/time/user")
                .addHeaders(getHeaderMap(json))
                .body(json)
                .execute();
        String body = httpResponse.body();
        return body;
    }



}
