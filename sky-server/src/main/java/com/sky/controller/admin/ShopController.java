package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Slf4j
@Tag(name = "店铺相关接口")
public class ShopController {

	public static final String SHOP_STATUS_KEY = "SHOP_STATUS";

	private final RedisTemplate<String,Object> redisTemplate;

	@Autowired
	public ShopController(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	/**
	 * 店铺营业状态设置
	 * @param status 1-营业中，0-打烊中
	 * @return 操作结果
	 */
	@PutMapping("/{status}")
	@Operation(summary = "店铺营业状态设置")
	public Result<Object> set(@PathVariable Integer status) {
		log.info("店铺营业状态设置: {}", status == 1 ? "营业中" : "打烊中");
		redisTemplate.opsForValue().set(SHOP_STATUS_KEY, status);
		return Result.success();
	}

	/**
	 * 查询店铺营业状态
	 * @return 店铺营业状态
	 */
	@GetMapping("/status")
	@Operation(summary = "查询店铺营业状态")
	public Result<Integer> getStatus() {
		Integer status = (Integer) redisTemplate.opsForValue().get(SHOP_STATUS_KEY);
		if (status != null) {
			log.info("查询到店铺营业状态：{}", status.equals(1) ? "营业中" : "打烊中");
		}
		return Result.success(status);
	}
}
