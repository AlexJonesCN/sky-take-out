package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtils;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

	/**
	 * 员工登录
	 * @param employeeLoginDTO 登录信息
	 * @return 登录结果
	 */
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


	/**
	 * 员工登出
	 * @return 登出结果
	 */
    @PostMapping("/logout")
    @Operation(summary = "员工登出")
    public Result<Object> logout() {
		log.info("员工登出");
        return Result.success();
    }

	/**
	 * 新增员工
	 * @param employeeDTO 员工信息
	 * @return 新增结果
	 */
	@PostMapping
	@Operation(summary = "新增员工")
	public Result<Object> save(@RequestBody EmployeeDTO employeeDTO, @RequestAttribute Long currentUserId) {
		log.info("新增员工：{}", employeeDTO);
		employeeService.save(employeeDTO, currentUserId);
		return Result.success();
	}

	/**
	 * 员工信息分页查询
	 * @param employeePageQueryDTO 分页查询参数
	 * @return 分页查询结果
	 */
	@GetMapping("/page")
	@Operation(summary = "员工信息分页查询")
	public Result<PageResult<Employee>> page(EmployeePageQueryDTO employeePageQueryDTO) {
		log.info("员工信息分页查询：{}", employeePageQueryDTO);
		PageResult<Employee> pageResult = employeeService.pageQuery(employeePageQueryDTO);
		return Result.success(pageResult);
	}

	/**
	 * 启用或禁用员工账号
	 * @param status 员工状态
	 * @param id 员工ID
	 * @return 操作结果
	 */
	@PostMapping("/status/{status}")
	@Operation(summary = "启用或禁用员工账号")
	public Result<Object> startOrStop(@PathVariable Integer status, @RequestParam Long id) {
		log.info("启用或禁用员工账号：{}, {}", status, id);
		employeeService.startOrStop(status, id);
		return Result.success();
	}

	/**
	 * 根据ID查询员工信息
	 * @param id 员工ID
	 * @return 员工信息
	 */
	@GetMapping("/{id}")
	@Operation(summary = "根据ID查询员工信息")
	public Result<Employee> getById(@PathVariable Long id) {
		log.info("根据ID查询员工信息：{}", id);
		Employee employee = employeeService.getById(id);
		return Result.success(employee);
	}

	/**
	 * 更新员工信息
	 * @param employeeDTO 员工信息
	 * @param currentUserId 当前登录的用户ID
	 * @return 更新结果
	 */
	@PutMapping
	@Operation(summary = "更新员工信息")
	public Result<Object> update(@RequestBody EmployeeDTO employeeDTO, @RequestAttribute Long currentUserId) {
		log.info("更新员工信息：{}", employeeDTO);
		employeeService.update(employeeDTO, currentUserId);
		return Result.success();
	}
}
