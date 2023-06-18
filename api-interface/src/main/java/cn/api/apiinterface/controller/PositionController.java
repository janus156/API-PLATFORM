package cn.api.apiinterface.controller;

import cn.api.apiinterface.common.BaseResponse;
import cn.api.apiinterface.common.ResultUtils;
import cn.api.apiinterface.model.entity.PositionEntity;
import cn.api.apiinterface.service.PositionService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/position")
public class PositionController {
    @Autowired
    private PositionService positionService;

    @GetMapping
    public BaseResponse<PositionEntity> getPosition(@Param(value = "ip")String ip){
        PositionEntity entity = positionService.getPos(ip);
        return ResultUtils.success(entity);
    }

}
