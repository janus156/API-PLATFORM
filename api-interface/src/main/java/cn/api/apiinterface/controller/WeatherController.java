package cn.api.apiinterface.controller;


import cn.api.apiinterface.common.BaseResponse;
import cn.api.apiinterface.common.ResultUtils;
import cn.api.apiinterface.model.entity.PositionEntity;
import cn.api.apiinterface.model.entity.WeatherInfoEntity;
import cn.api.apiinterface.service.WeatherService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/weather")
public class WeatherController {
    @Autowired
    private WeatherService weatherService;

    @GetMapping
    public BaseResponse<WeatherInfoEntity> getPosition(@Param(value = "cityCode")String cityCode){
        WeatherInfoEntity weather = weatherService.getWeather(cityCode);
        return ResultUtils.success(weather);
    }
}
