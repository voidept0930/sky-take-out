package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;

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
}
