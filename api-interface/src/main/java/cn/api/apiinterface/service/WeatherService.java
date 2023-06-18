package cn.api.apiinterface.service;

import cn.api.apiinterface.model.entity.WeatherInfoEntity;

public interface WeatherService {

    WeatherInfoEntity getWeather(String cityCode);
}
