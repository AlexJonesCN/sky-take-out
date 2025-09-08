package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtils;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Tag(name = "员工相关接口")
public class EmployeeController {

    private final EmployeeService employeeService;
	private final JwtProperties jwtProperties;
	@Autowired
	public EmployeeController(EmployeeService employeeService, JwtProperties jwtProperties) {
		this.employeeService = employeeService;
		this.jwtProperties = jwtProperties;
	}

	@PostMapping("/login")
    @Operation(summary = "员工登录")
	public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
		Employee employee = employeeService.login(employeeLoginDTO);
		log.info("员工登录：{}", employeeLoginDTO);
		//登录成功后，生成jwt令牌
		Map<String, Object> claims = new HashMap<>();
		// 生成包含员工ID的token，确保不同员工获得不同token
		claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
		String token = JwtUtils.generateToken(jwtProperties.getAdminSecretKey(), jwtProperties.getAdminTtl(), claims);
		// 组装返回对象
		EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
				.id(employee.getId())
				.userName(employee.getUsername())
				.name(employee.getName())
				.token(token)
				.build();
		return Result.success(employeeLoginVO);
	}

    @PostMapping("/logout")
    @Operation(summary = "员工登出")
    public Result<String> logout() {
		log.info("员工登出");
        return Result.success();
    }
}
