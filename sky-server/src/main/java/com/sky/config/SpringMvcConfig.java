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
 * Web层配置类
 * 负责配置拦截器、消息转换器、API文档等Web相关组件
 */
@Configuration
@Slf4j
public class SpringMvcConfig implements WebMvcConfigurer {

    private final JwtTokenAdminInterceptor jwtTokenAdminInterceptor;

	@Autowired
	public SpringMvcConfig(JwtTokenAdminInterceptor jwtTokenAdminInterceptor) {
		this.jwtTokenAdminInterceptor = jwtTokenAdminInterceptor;
	}

	/**
     * 注册自定义拦截器
     * @param registry 拦截器注册器
     */
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册JWT拦截器...");
        registry.addInterceptor(jwtTokenAdminInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns(
						"/admin/employee/login",
		                "/admin/*/swagger-ui/**",
		                "/admin/*/v3/api-docs/**"
                );
		log.info("JWT拦截器注册完成！");
    }


	/**
	 * 配置 OpenAPI 文档信息
	 * @return OpenAPI 实例，包含标题、版本和描述信息
	 */
	@Bean
	public OpenAPI skyOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("苍穹外卖项目接口文档")
						.version("1.0")
						.description("苍穹外卖项目的RESTful API接口文档，提供完整的接口说明和测试功能"));
	}

	/**
	 * 配置自定义的Jackson ObjectMapper
	 * 统一处理JSON序列化和反序列化，特别是时间格式
	 *
	 * @return 自定义的JacksonObjectMapper实例
	 */
	@Bean
	@Primary
	public ObjectMapper jacksonObjectMapper() {
		log.info("注册自定义Jackson ObjectMapper...");
		return new JacksonObjectMapper();
	}

}