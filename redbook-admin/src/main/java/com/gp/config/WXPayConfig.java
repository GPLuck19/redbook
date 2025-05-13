package com.gp.config;



import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <P>
 * WebMvc Config
 * </p>
 */
@Configuration
public class WXPayConfig {
	/**
	 * 第三方jar包已存在微信支付配置类，这里直接和配置属性关联
	 * @return
	 */
	@Bean("wxPayConfig")
	@ConfigurationProperties(prefix = "weixinpay")
	public WxPayConfig wxPayConfig(){
		return new WxPayConfig();
	}
	/**
	 * 创建 WxPayService 实现类，并将其 WxPayConfig 配置类配置到支付服务类中
	 * @return
	 */
	@Bean
	public WxPayService wxPayService(@Qualifier("wxPayConfig") WxPayConfig wxConfig) {
		WxPayService wxPayService = new WxPayServiceImpl();
		wxPayService.setConfig(wxConfig);
		return wxPayService;
	}
}