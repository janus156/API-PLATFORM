package cn.api.apiinterface.controller;

import clientsdk.client.PlatClient;
import cn.api.apiinterface.Util.JwtUtil;
import cn.api.apiinterface.Util.UserIdDecodeUtil;
import cn.api.apiinterface.common.*;
import cn.api.apiinterface.constant.CommonConstant;
import cn.api.apiinterface.exception.BusinessException;
import cn.api.apiinterface.model.dto.interfaceinfo.InterfaceInfoAddRequest;
import cn.api.apiinterface.model.dto.interfaceinfo.InterfaceInfoInvokeRequest;
import cn.api.apiinterface.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import cn.api.apiinterface.model.dto.interfaceinfo.InterfaceInfoUpdateRequest;
import cn.api.apiinterface.model.entity.InterfaceInfo;
import cn.api.apiinterface.model.entity.LoginUser;
import cn.api.apiinterface.model.entity.LoginUserDTO;
import cn.api.apiinterface.model.entity.User;
import cn.api.apiinterface.model.enums.InterfaceInfoStatusEnum;
import cn.api.apiinterface.service.InnerInterfaceInfoService;
import cn.api.apiinterface.service.InterfaceInfoService;
import cn.api.apiinterface.service.UserService;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import nonapi.io.github.classgraph.json.JSONUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static cn.api.apiinterface.common.RedisConstants.LOGIN_USER_KEY;

/**
 * 接口管理
 *
 */
@RestController
@RequestMapping("/interfaceInfo")
@Slf4j
public class InterfaceInfoController {

    @Resource
    private InterfaceInfoService interfaceInfoService;


    @Resource
    private UserService userService;

    @Resource
    private PlatClient platClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 创建
     *
     * @param interfaceInfoAddRequest
     * @return
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('add')")
    public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest,
                                               @RequestHeader("authorization") String token) {
        if (interfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);
        // 校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, true);
        //解密
        String userId = UserIdDecodeUtil.getUserId(token);

        interfaceInfo.setUserId(Long.valueOf(userId));

        boolean result = interfaceInfoService.save(interfaceInfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        long newInterfaceInfoId = interfaceInfo.getId();
        return ResultUtils.success(newInterfaceInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('delete')")
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long id = deleteRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        boolean b = interfaceInfoService.removeById(id);

        return ResultUtils.success(b);
    }

    /**
     * 更新
     *
     * @param interfaceInfoUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('update')")
    public BaseResponse<Boolean>
    updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest,
                                                     HttpServletRequest request) {
        if (interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);
        // 参数校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, false);

        long id = interfaceInfoUpdateRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    @PreAuthorize("hasAuthority('read')")
    public BaseResponse<InterfaceInfo> getInterfaceInfoById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        return ResultUtils.success(interfaceInfo);
    }



    /**
     * 获取列表（仅管理员可使用）
     *
     * @param interfaceInfoQueryRequest
     * @return
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('read')")
    public BaseResponse<List<InterfaceInfo>> listInterfaceInfo(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        if (interfaceInfoQueryRequest != null) {
            BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        List<InterfaceInfo> interfaceInfoList = interfaceInfoService.list(queryWrapper);
        return ResultUtils.success(interfaceInfoList);
    }

    /**
     * 分页获取列表
     *
     * @param interfaceInfoQueryRequest
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<InterfaceInfo>>
    listInterfaceInfoByPage(InterfaceInfoQueryRequest
                                    interfaceInfoQueryRequest) {
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
        String description = interfaceInfoQuery.getDescription();
        // description 需支持模糊搜索
        interfaceInfoQuery.setDescription(null);
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(interfaceInfoPage);
    }

    /**
     * 接口发布上线
     * @param idRequest
     * @return
     */
    @PostMapping("/online")
    @PreAuthorize("hasAuthority('add')")
    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody IdRequest idRequest) {
        //1.判断请求存不存在
        if (idRequest==null || idRequest.getId()<=0){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //2.判断老接口存不存在
        long id=idRequest.getId();
        InterfaceInfo oldinterfaceInfo = interfaceInfoService.getById(id);

        if (oldinterfaceInfo==null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"验证失败");
        }
        //3.判断是否可以调用
        clientsdk.model.User user=new clientsdk.model.User();
        user.setUsername("test online call");
        //生成时间戳
        String username = platClient.getUsernameByPost(user);

        if (StringUtils.isBlank(username)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口调用失败");
        }
        //修改
        InterfaceInfo interfaceInfo=new InterfaceInfo();
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.ONLINE.getValue());
        interfaceInfo.setId(id);
        boolean update = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(update);
    }

    //接口下线
    @PostMapping("/offline")
    @PreAuthorize("hasAuthority('delete')")
    public BaseResponse<Boolean> offlineInterfaceInfo(@RequestBody IdRequest idRequest) {
        //1.判断请求存不存在
        if (idRequest==null || idRequest.getId()<=0){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //2.判断老接口可不可以被调用
        long id=idRequest.getId();
        InterfaceInfo oldinterfaceInfo = interfaceInfoService.getById(id);

        if (oldinterfaceInfo==null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"验证失败");
        }
        //3.判断是否可以调用
        clientsdk.model.User user=new clientsdk.model.User();
        user.setUsername("admin");
        String username = platClient.getUsernameByPost(user);

        if (StringUtils.isBlank(username)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口调用失败");
        }
        //修改
        InterfaceInfo interfaceInfo=new InterfaceInfo();
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.OFFLINE.getValue());
        interfaceInfo.setId(id);
        boolean update = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(update);
    }


    //接口调用
    @PostMapping("/invoke")
    public BaseResponse<Object> invokeInterfaceInfo(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest,
                                                    @RequestHeader("authorization") String token) {
        if (interfaceInfoInvokeRequest == null || interfaceInfoInvokeRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = interfaceInfoInvokeRequest.getId();
        String userRequestParams = interfaceInfoInvokeRequest.getUserRequestParams();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (oldInterfaceInfo.getStatus() == InterfaceInfoStatusEnum.OFFLINE.getValue()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口已关闭");
        }
        //从redis获取user
        String userId = UserIdDecodeUtil.getUserId(token);
        String jsonStr = redisTemplate.opsForValue().get(LOGIN_USER_KEY + userId);
        LoginUserDTO userDTO = JSONUtil.toBean(jsonStr, LoginUserDTO.class);

        String accessKey = userDTO.getUser().getAccessKey();
        String secretKey = userDTO.getUser().getAccessKey();
        //创建客户端
        PlatClient platClient=new PlatClient(accessKey,secretKey);
        //调用获取用户名接口
        //接口，包含acess key 、secret key
        clientsdk.model.User user = JSONUtil.toBean(userRequestParams, clientsdk.model.User.class);
        String username = platClient.getUsernameByPost(user);

        return ResultUtils.success(username);
    }

    //调用hello接口
    @PostMapping("/invokeHello")
    public BaseResponse<Object> invokeHelloInterfaceInfo(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest,
                                                         @RequestHeader("authorization") String token) {
        if (interfaceInfoInvokeRequest == null || interfaceInfoInvokeRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = interfaceInfoInvokeRequest.getId();
        String userRequestParams = interfaceInfoInvokeRequest.getUserRequestParams();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (oldInterfaceInfo.getStatus() == InterfaceInfoStatusEnum.OFFLINE.getValue()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口已关闭");
        }

        //从redis获取
        String userId = UserIdDecodeUtil.getUserId(token);
        String jsonStr = redisTemplate.opsForValue().get(LOGIN_USER_KEY + userId);
        LoginUserDTO userDTO = JSONUtil.toBean(jsonStr, LoginUserDTO.class);

        String accessKey = userDTO.getUser().getAccessKey();
        String secretKey = userDTO.getUser().getAccessKey();
        String nonce= RandomUtil.randomNumbers(8);
        redisTemplate.opsForValue().set("nonce",nonce,5L,TimeUnit.MINUTES);
        //创建客户端`
        PlatClient platClient=new PlatClient(accessKey,secretKey,nonce);

        //调用获取用户名接口
        //接口，包含acess key 、secret key
        clientsdk.model.User user =new clientsdk.model.User();
        user.setUsername(userRequestParams);
        String username = platClient.sayHello(user);

        return ResultUtils.success(username);
    }

    //统计调用次数
    @PostMapping("/invokeTime")
    public BaseResponse<Object> invokeTimeInterfaceInfo(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest,
                                                        @RequestHeader("authorization") String token) {
        if (interfaceInfoInvokeRequest == null || interfaceInfoInvokeRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = interfaceInfoInvokeRequest.getId();
        String userRequestParams = interfaceInfoInvokeRequest.getUserRequestParams();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (oldInterfaceInfo.getStatus() == InterfaceInfoStatusEnum.OFFLINE.getValue()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口已关闭");
        }

        //从redis获取
        String userId = UserIdDecodeUtil.getUserId(token);
        String jsonStr = redisTemplate.opsForValue().get(LOGIN_USER_KEY + userId);
        LoginUserDTO userDTO = JSONUtil.toBean(jsonStr, LoginUserDTO.class);

        String accessKey = userDTO.getUser().getAccessKey();
        String secretKey = userDTO.getUser().getAccessKey();


        //创建客户端
        PlatClient platClient=new PlatClient(accessKey,secretKey);
        //调用获取用户名接口
        //接口，包含acess key 、secret key
        clientsdk.model.User user = JSONUtil.toBean(userRequestParams, clientsdk.model.User.class);
        String username = platClient.getTime(user);

        return ResultUtils.success(username);
    }

}
