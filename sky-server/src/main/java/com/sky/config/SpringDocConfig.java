package com.sky.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.interceptor.JwtTokenAdminInterceptor;
import com.sky.json.JacksonObjectMapper;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置类，注册web层相关组件
 */
@Configuration
@Slf4j
public class SpringDocConfig implements WebMvcConfigurer {

    private final JwtTokenAdminInterceptor jwtTokenAdminInterceptor;

	@Autowired
	public SpringDocConfig(JwtTokenAdminInterceptor jwtTokenAdminInterceptor) {
		this.jwtTokenAdminInterceptor = jwtTokenAdminInterceptor;
	}

	/**
     * 注册自定义拦截器
     *
     * @param registry 拦截器注册器
     */
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
        registry.addInterceptor(jwtTokenAdminInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/employee/login");
    }


	// 配置 OpenAPI 信息
	@Bean
	public OpenAPI skyOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("苍穹外卖项目接口文档")
						.version("1.0")
						.description("苍穹外卖项目的接口文档"));
	}

	/**
	 * 配置 Jackson 的 ObjectMapper
	 * 标记为 @Primary，确保在有多个 ObjectMapper Bean 时优先使用此配置
	 *
	 * @return 自定义的 JacksonObjectMapper 实例
	 */
	@Bean
	@Primary
	public ObjectMapper jacksonObjectMapper() {
		return new JacksonObjectMapper();
	}

}