package com.gp.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Security 配置属性
 *
 * @author Lion Li
 */
@Data
@ConfigurationProperties(prefix = "ignore")
public class IgnoreProperties {

    /**
     * 排除路径
     */
    private String[] excludes;


}
