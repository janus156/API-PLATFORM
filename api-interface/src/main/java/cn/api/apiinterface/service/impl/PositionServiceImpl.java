package cn.api.apiinterface.service.impl;

import cn.api.apiinterface.model.entity.PositionEntity;
import cn.api.apiinterface.service.PositionService;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Service;

@Service
public class PositionServiceImpl implements PositionService {


    @Override
    public PositionEntity getPos(String ip) {
        String key = "81a6787682b9b4d2f35194188c834409";
        String url_head = "https://restapi.amap.com/v3/ip?output=json&key=" ;
        String url_str = url_head + key + "&ip=" + ip ;
        HttpResponse httpResponse = HttpRequest.get(url_str).execute();
        String jsonStr = httpResponse.body();

        PositionEntity posEntity = JSONUtil.toBean(jsonStr, PositionEntity.class);
        System.out.println(posEntity);
        return posEntity;
    }

}
