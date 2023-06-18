package cn.api.apiinterface.service;


import cn.api.apiinterface.model.entity.InterfaceInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 */
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);
}
