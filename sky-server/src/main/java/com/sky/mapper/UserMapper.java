package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

@Mapper
public interface UserMapper {

    /**
     * 根据openid查询用户
     * @param openid
     * @return
     */
    User getByOpenid(String openid);

    /**
     * 创建一个用户
     * @param user
     */
    void save(User user);

    /**
     * 查询某一日新增用户数
     * @param zero
     * @param twentyFour
     * @return
     */
    Integer getNewUserByDate(LocalDateTime zero, LocalDateTime twentyFour);

    /**
     * 查询某一日总用户数
     * @param twentyFour
     * @return
     */
    Integer getTotalUserByDate(LocalDateTime twentyFour);
}
