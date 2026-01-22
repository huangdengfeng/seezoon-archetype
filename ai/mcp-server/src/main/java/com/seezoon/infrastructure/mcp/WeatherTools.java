package com.seezoon.infrastructure.mcp;

import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class WeatherTools {

    @McpTool(description = "根据城市获取天气")
    public String getWeather(@McpToolParam(required = true, description = "城市") String city) {
        return "28度，下雨";
    }
}
