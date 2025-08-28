package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AddressBookMapper {

    /**
     * 新增地址
     * @param addressBook
     */
    void save(AddressBook addressBook);

    /**
     * 查询当前登录用户的所有地址信息
     * @param userId
     * @return
     */
    List<AddressBook> get(String userId);

    /**
     * 设置默认地址
     * @param id
     * @param userId
     */
    void setDefaultAddress(AddressBook addressBook);

    /**
     * 设置非默认地址
     * @param addressBook
     */
    void setNotDefaultAddress(AddressBook addressBook);

    /**
     * 查询默认地址
     * @param userId
     * @return
     */
    AddressBook getDefaultAddress(String userId);

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    AddressBook getById(Long id);

    /**
     * 根据id修改地址
     * @param addressBook
     */
    void update(AddressBook addressBook);

    /**
     * 根据id删除地址
     * @param id
     */
    void delete(Long id);

}
