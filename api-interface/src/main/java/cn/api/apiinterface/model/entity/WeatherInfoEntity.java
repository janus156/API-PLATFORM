package cn.api.apiinterface.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherInfoEntity {
    private int status;
    private int count;
    private String info;
    private int infocode;
    private List<LiveInfo> lives;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class LiveInfo {
        private String province;
        private String city;
        private String adcode;
        private String weather;
        private String temperature;
        private String winddirection;
        private String windpower;
        private String humidity;
        private String reporttime;
        private float temperature_float;
        private float humidity_float;

    }
}
