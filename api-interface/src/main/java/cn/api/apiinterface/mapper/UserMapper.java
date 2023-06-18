package cn.api.apiinterface.mapper;


import cn.api.apiinterface.model.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * 用户
 */
public interface UserMapper extends BaseMapper<User> {
    List<String> getPermByUserId(Long userId);
}




