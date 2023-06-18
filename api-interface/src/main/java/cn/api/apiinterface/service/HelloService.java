package cn.api.apiinterface.service;

import cn.api.apiinterface.model.entity.HelloEntity;
import com.baomidou.mybatisplus.extension.service.IService;

public interface HelloService extends IService<HelloEntity> {

    String getRandomExpression();
}
