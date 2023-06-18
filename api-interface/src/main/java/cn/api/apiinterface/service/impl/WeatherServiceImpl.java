package cn.api.apiinterface.service.impl;


import cn.api.apiinterface.model.entity.WeatherInfoEntity;
import cn.api.apiinterface.service.WeatherService;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Service;

@Service
public class WeatherServiceImpl implements WeatherService {

    @Override
    public WeatherInfoEntity getWeather(String cityCode) {
        String key = "81a6787682b9b4d2f35194188c834409";
        String url_head = "https://restapi.amap.com/v3/weather/weatherInfo?output=json&key=" ;
        String url_str = url_head + key + "&city=" + cityCode ;

        HttpResponse httpResponse = HttpRequest.get(url_str).execute();
        String jsonStr = httpResponse.body();
        WeatherInfoEntity weatherInfoEntity = JSONUtil.toBean(jsonStr, WeatherInfoEntity.class);

        return weatherInfoEntity;
    }



}
