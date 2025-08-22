package com.sky.mapper;

import com.sky.entity.Employee;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    /**
     * 新增员工
     * @param employee
     */
    @Insert("INSERT INTO employee (name, username, password, phone, sex, id_number, create_time, update_time, create_user, update_user) VALUES " +
            "(#{name}, #{username}, #{password}, #{phone}, #{sex}, #{idNumber}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void save(Employee employee);

    /**
     * 员工分页查询
     * @param name
     * @return
     */
    List<Employee> pageQuery(String name);

    /**
     * 启用、禁用员工账号
     * @param status
     * @param id
     * @param updateTime
     * @param updateUser
     */
    @Update("UPDATE employee SET status = #{status}, update_time = #{updateTime}, update_user = #{updateUser} WHERE id = #{id}")
    void startOrStop(Integer status, Long id, LocalDateTime updateTime, Long updateUser);

    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    @Select("SELECT * FROM employee WHERE id = #{id}")
    Employee getById(Long id);

    /**
     * 编辑员工信息
     * @param employee
     */
    @Update("UPDATE employee SET id_number = #{idNumber}, name = #{name}, phone = #{phone}, sex = #{sex}, username = #{username}, update_user = #{updateUser}, update_time = #{updateTime} WHERE id = #{id}")
    void update(Employee employee);

    /**
     * 修改密码
     * @param employee
     */
    @Update("UPDATE employee SET password = #{password}, update_time = #{updateTime} WHERE id = #{id}")
    void updatePassword(Employee employee);

}
