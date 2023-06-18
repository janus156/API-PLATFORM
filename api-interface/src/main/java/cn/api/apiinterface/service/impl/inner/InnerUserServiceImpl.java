package cn.api.apiinterface.service.impl.inner;

import cn.api.apiinterface.common.ErrorCode;
import cn.api.apiinterface.exception.BusinessException;
import cn.api.apiinterface.mapper.UserMapper;
import cn.api.apiinterface.model.entity.User;
import cn.api.apiinterface.service.InnerUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class InnerUserServiceImpl implements InnerUserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public User getInvokeUser(String accessKey) {
        if (StringUtils.isAnyBlank(accessKey)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("accessKey", accessKey);
        return userMapper.selectOne(queryWrapper);
    }
}
