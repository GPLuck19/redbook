package com.gp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.entity.TUserAddress;
import com.gp.exception.ServiceException;
import com.gp.mapper.TUserAddressMapper;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.TUserAddressService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【t_user_address】的数据库操作Service实现
* @createDate 2024-11-20 15:02:07
*/
@Service
public class TUserAddressServiceImpl extends ServiceImpl<TUserAddressMapper, TUserAddress>
    implements TUserAddressService{



    @Override
    public Result removeAdress(Long adressId) {
        boolean removed = this.removeById(adressId);
        if(removed){
            return Results.success();
        }else {
            throw new ServiceException("地址移除失败！");
        }
    }

    @Override
    public Result addAdress(TUserAddress address) {
        boolean save = this.save(address);
        if(save){
            return Results.success();
        }else {
            throw new ServiceException("地址保存失败！");
        }
    }

    @Override
    public List<TUserAddress> findAddressList(Long userId) {
        LambdaQueryWrapper<TUserAddress> wrapper = Wrappers.lambdaQuery(TUserAddress.class).eq(TUserAddress::getUserId, userId);
        List<TUserAddress> list = this.list(wrapper);
        return list;
    }
}




