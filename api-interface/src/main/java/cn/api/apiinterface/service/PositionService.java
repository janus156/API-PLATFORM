package cn.api.apiinterface.service;

import cn.api.apiinterface.model.entity.PositionEntity;

public interface PositionService {

    PositionEntity getPos(String ip);
}
