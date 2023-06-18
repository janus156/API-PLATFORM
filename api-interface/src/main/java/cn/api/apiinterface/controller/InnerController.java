package cn.api.apiinterface.controller;

import cn.api.apiinterface.common.BaseResponse;
import cn.api.apiinterface.common.ErrorCode;
import cn.api.apiinterface.common.RedisConstants;
import cn.api.apiinterface.common.ResultUtils;
import cn.api.apiinterface.exception.BusinessException;
import cn.api.apiinterface.model.entity.InterfaceInfo;
import cn.api.apiinterface.model.entity.User;
import cn.api.apiinterface.service.InnerInterfaceInfoService;
import cn.api.apiinterface.service.InnerUserInterfaceInfoService;
import cn.api.apiinterface.service.InnerUserService;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/inner")
@Slf4j
public class InnerController {

    @Resource
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @Resource
    private InnerUserService innerUserService;

    @Resource
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    @Autowired
    private RedissonClient redissonClient;

    @GetMapping("/getInterInfo")
    public BaseResponse<InterfaceInfo>
    getInterfaceInfoByUrlAndPath(@RequestParam(value = "url") String url,
                                 @RequestParam(value = "method") String method) {

        if (StrUtil.isBlank(url) || StrUtil.isBlank(method)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        InterfaceInfo interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(url, method);

        return ResultUtils.success(interfaceInfo);
    }

    @GetMapping("/getUserInfo")
    public BaseResponse<User> getUserInfo(@RequestParam(value = "accesskey") String accesskey) {

        if (StrUtil.isBlank(accesskey)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User invokeUser = innerUserService.getInvokeUser(accesskey);

        return ResultUtils.success(invokeUser);
    }

    @GetMapping("/invokeTime")
    public BaseResponse<Boolean> getInvoke
            (@RequestParam(value = "interfaceInfoId") String interfaceInfoId,
             @RequestParam(value = "userId") String userId                     ) {

        if (StrUtil.isBlank(interfaceInfoId) || StrUtil.isBlank(userId)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        RLock lock = redissonClient.getLock(RedisConstants.LOCK_SHOP_KEY + interfaceInfoId);

        boolean isLock = false;
        try {
            log.info("尝试获取redisson锁");
            isLock=lock.tryLock(1,3, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            log.error("尝试获取失败");
            e.printStackTrace();
        }
        //调用失败
        if (!isLock){
            return ResultUtils.success(false);
        }
        Boolean invokeCount = innerUserInterfaceInfoService.invokeCount(Long.valueOf(interfaceInfoId), Long.valueOf(userId));
        //调用完，解锁.
        log.info("解锁");
        lock.unlock();
        return ResultUtils.success(invokeCount);
    }


}
