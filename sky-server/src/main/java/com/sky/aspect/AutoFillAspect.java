package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.enumeration.OperationType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面，实现公共字段的自动填充
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

	/**
	 * 切入点，匹配mapper包下且所有标记了@AutoFill注解的方法
	 */
	@Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
	public void autoFillPointCut() {}

	/**
	 * 前置通知，用于在执行插入或更新操作之前进行自动填充
	 */
	@Before("autoFillPointCut()")
	public void autoFill(JoinPoint joinPoint) {
		log.info("开始进行公共字段自动填充...");
		// 获取当前被拦截方法上的数据库操作类型
		MethodSignature signature = (MethodSignature) joinPoint.getSignature(); // 获取方法签名
		AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class); // 获取方法上的注解
		OperationType operationType = autoFill.value(); // 获取操作类型
		// 获取当前被拦截的方法参数--实体类对象
		Object[] args = joinPoint.getArgs();
		if(args == null || args.length == 0) {
			log.info("在方法参数中未获取到实体类对象，无法进行公共字段自动填充");
			return;
		}
		Object entity = args[0]; // 实体类对象
		// 准备填充的数据--当前时间和当前登录用户ID
		LocalDateTime now = LocalDateTime.now();
		Long currentUserId = getCurrentUserId();

		// 4. 根据操作类型填充数据，使用反射机制
		if (operationType == OperationType.UPDATE || operationType == OperationType.INSERT) {
			try {
				Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
				Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
				setUpdateTime.invoke(entity, now);
				setUpdateUser.invoke(entity, currentUserId);
			} catch (Exception e) {
				throw new RuntimeException("填充更新时间和更新人失败", e);
			}
		}
		if (operationType == OperationType.INSERT) {
			try {
				Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
				Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
				setCreateTime.invoke(entity, now);
				setCreateUser.invoke(entity, currentUserId);
			} catch (Exception e) {
				throw new RuntimeException("填充创建时间和创建人失败", e);
			}
		}
	}

	/**
	 * 获取当前登录用户ID
	 * 通过RequestContextHolder获取请求对象，再从请求对象中获取用户ID
	 * @return Long 当前登录用户ID，如果在非Web环境或未登录则返回null
	 */
	private Long getCurrentUserId() {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		if (requestAttributes instanceof ServletRequestAttributes) {
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			Object currentUserId = request.getAttribute("currentUserId");
			if (currentUserId instanceof Long) {
				return (Long) currentUserId;
			}
		}
		// 在非Web环境（如单元测试、定时任务）或未登录时返回null
		return null;
	}
}
