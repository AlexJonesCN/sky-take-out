package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.entity.Employee;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO 登录信息
     * @return 登录成功的员工信息
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

	/**
	 * 新增员工
	 * @param employeeDTO 员工信息
	 * @param currentUserId 当前登录的用户ID
	 */
	void save(EmployeeDTO employeeDTO, Long currentUserId);
}
