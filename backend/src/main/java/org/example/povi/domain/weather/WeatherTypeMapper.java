package org.example.povi.domain.weather;

import org.example.povi.domain.mission.entity.Mission;
import org.springframework.stereotype.Component;

@Component
public class WeatherTypeMapper {
    private static final double HOT_C_THRESHOLD_C   = 28.0; // >= 덥다
    private static final double COLD_C_THRESHOLD_C  = 5.0;  // <= 춥다
    private static final double WINDY_THRESHOLD_MS  = 10.0; // >= 강풍

    /**
     * OpenWeather 'main', 'temp(°C)', 'wind(m/s)' 기반으로 WeatherType 결정.
     * 1) 강수/특수현상(main) 우선
     * 2) 하늘상태(main)
     * 3) 바람/온도 임계치
     * 4) 매칭 실패 시 ANY
     */
    public Mission.WeatherType decide(String main, double tempC, double windMs) {
        String m = (main == null ? "" : main).toLowerCase();

        // 1) 강수/특수현상
        if (m.contains("thunder")) return Mission.WeatherType.THUNDER;
        if (m.contains("drizzle")) return Mission.WeatherType.DRIZZLE;
        if (m.contains("rain"))    return Mission.WeatherType.RAINY;
        if (m.contains("snow"))    return Mission.WeatherType.SNOWY;

        // 2) 하늘상태
        if (m.contains("mist") || m.contains("fog") || m.contains("haze") || m.contains("smoke"))
            return Mission.WeatherType.FOGGY;
        if (m.contains("cloud"))   return Mission.WeatherType.CLOUDY;
        if (m.contains("clear"))   return Mission.WeatherType.CLEAR;

        // 3) 바람/온도
        if (Double.isFinite(windMs) && windMs >= WINDY_THRESHOLD_MS)
            return Mission.WeatherType.WINDY;

        if (Double.isFinite(tempC)) {
            if (tempC >= HOT_C_THRESHOLD_C)  return Mission.WeatherType.HOT;
            if (tempC <= COLD_C_THRESHOLD_C) return Mission.WeatherType.COLD;
        }

        // 4) 매칭 실패
        return Mission.WeatherType.ANY;
    }
}