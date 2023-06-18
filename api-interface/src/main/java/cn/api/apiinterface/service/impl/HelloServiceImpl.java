package cn.api.apiinterface.service.impl;

import cn.api.apiinterface.mapper.HelloMapper;
import cn.api.apiinterface.model.entity.HelloEntity;
import cn.api.apiinterface.service.HelloService;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;


@Service
@Slf4j
public class HelloServiceImpl extends ServiceImpl<HelloMapper, HelloEntity>
        implements HelloService {


    @Override
    public String getRandomExpression() {
        int number = new Random().nextInt(93);
        HelloEntity helloEntity = getById(number);
        return helloEntity.getExpression();
    }

    public static void main(String args[]) {
        String s="abc cd ddd   d";
        String[] strings = s.split(" ");
        System.out.println(strings);
    }
}
