package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.BaseException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeMapper employeeMapper;

	@Autowired
	public EmployeeServiceImpl(EmployeeMapper employeeMapper) {
		this.employeeMapper = employeeMapper;
	}

	/**
     * 员工登录
	 * @param employeeLoginDTO 登录信息
	 * @return 登录成功的员工信息
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

	    //使用MD5加密后再进行比对
	    String encryptedPassword = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!encryptedPassword.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus().equals(StatusConstant.DISABLE)) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

	/**
	 * 新增员工
	 * @param employeeDTO 员工信息
	 */
	public void save(EmployeeDTO employeeDTO) {
		Employee employee = new Employee();
		//将employeeDTO中的属性拷贝到employee中
		BeanUtils.copyProperties(employeeDTO, employee);
		//设置账号的状态，默认是启用状态 1-启用，0-禁用
		employee.setStatus(StatusConstant.ENABLE);
		//设置初始密码为123456，进行MD5加密
		employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
		//将数据插入到数据库中
		employeeMapper.insert(employee);
	}

	/**
	 * 员工信息分页查询
	 * @param employeePageQueryDTO 分页查询参数
	 * @return 分页查询结果
	 */
	@Override
	public PageResult<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO) throws BaseException {
		try {
			PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
			try (Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO)){
				return new PageResult<>(page.getTotal(), page.getResult());
			}
		} catch (Exception e) {
			log.error("分页查询员工信息异常", e);
			throw new RuntimeException("查询员工信息失败", e);
		} finally {
			PageHelper.clearPage();
		}
	}

	/**
	 * 启用禁用员工账号
	 * @param status 员工状态
	 * @param id 员工ID
	 */
	@Override
	public void startOrStop(Integer status, Long id) {
		Employee employee = Employee.builder()
				.id(id)
				.status(status)
				.build();
		employeeMapper.update(employee);
	}

	/**
	 * 根据ID查询员工信息
	 * @param id 员工ID
	 * @return 员工信息
	 */
	@Override
	public Employee getById(Long id) {
		return employeeMapper.getById(id);
	}

	/**
	 * 更新员工信息
	 * @param employeeDTO 员工信息
	 */
	@Override
	public void update(EmployeeDTO employeeDTO) {
		Employee employee = new Employee();
		//将employeeDTO中的属性拷贝到employee中
		BeanUtils.copyProperties(employeeDTO, employee);
		//更新数据库中的数据
		employeeMapper.update(employee);
	}

}
