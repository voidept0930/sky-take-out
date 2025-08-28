package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {

    private final AddressBookMapper addressBookMapper;
    @Autowired
    public AddressBookServiceImpl(AddressBookMapper addressBookMapper) {
        this.addressBookMapper = addressBookMapper;
    }

    /**
     * 新增地址
     * @param addressBook
     */
    @Override
    public void save(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentUserId());
        addressBookMapper.save(addressBook);
    }

    /**
     * 查询当前登录用户的所有位置信息
     * @return
     */
    @Override
    public List<AddressBook> get() {
        return addressBookMapper.get(BaseContext.getCurrentUserId());
    }

    /**
     * 设置默认地址
     * @param id
     */
    @Override
    public void setDefaultAddress(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentUserId());
        addressBookMapper.setDefaultAddress(addressBook);
        addressBookMapper.setNotDefaultAddress(addressBook);
    }

    /**
     * 查询默认地址
     * @return
     */
    @Override
    public AddressBook getDefaultAddress() {
        return addressBookMapper.getDefaultAddress(BaseContext.getCurrentUserId());
    }

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    @Override
    public AddressBook getById(Long id) {
        return addressBookMapper.getById(id);
    }

    /**
     * 根据id修改地址
     * @param addressBook
     */
    @Override
    public void update(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }

    /**
     * 根据id删除地址
     * @param id
     */
    @Override
    public void delete(Long id) {
        addressBookMapper.delete(id);
    }
}
