package com.gp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gp.entity.TUserAddress;
import com.gp.result.Result;

import java.util.List;

/**
* @author Administrator
* @description 针对表【t_user_address】的数据库操作Service
* @createDate 2024-11-20 15:02:07
*/
public interface TUserAddressService extends IService<TUserAddress> {

    List<TUserAddress> findAddressList(Long userId);

    Result removeAdress(Long adressId);

    Result addAdress(TUserAddress requestParam);
}
