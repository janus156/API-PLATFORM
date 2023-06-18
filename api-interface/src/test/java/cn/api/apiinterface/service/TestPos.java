package cn.api.apiinterface.service;

import cn.api.apiinterface.model.entity.PositionEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

@SpringBootTest
@Slf4j
public class TestPos {
    @Autowired
    private PositionService positionService;

    @Test
    void getPos(){
        PositionEntity pos = positionService.getPos("114.247.50.2");
        System.out.println(pos);
    }


}
