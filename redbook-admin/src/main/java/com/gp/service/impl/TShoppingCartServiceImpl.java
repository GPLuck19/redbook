package com.gp.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.dto.req.goods.AddShoppingCart;
import com.gp.dto.req.goods.Goods;
import com.gp.entity.TShoppingCart;
import com.gp.exception.ServiceException;
import com.gp.mapper.TShoppingCartMapper;
import com.gp.page.PageResponse;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.TShoppingCartService;
import com.gp.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【t_shopping_cart(购物车表)】的数据库操作Service实现
* @createDate 2024-11-12 15:49:51
*/
@Service
@RequiredArgsConstructor
public class TShoppingCartServiceImpl extends ServiceImpl<TShoppingCartMapper, TShoppingCart>
    implements TShoppingCartService{

    private final TShoppingCartMapper shoppingCartMapper;

    @Override
    public Result plusShoppingCart(Long shopId) {
        TShoppingCart shoppingCart = shoppingCartMapper.selectById(shopId);
        if(ObjectUtil.isNotEmpty(shoppingCart)){
            shoppingCart.setNumber(shoppingCart.getNumber()+1);
            shoppingCartMapper.updateById(shoppingCart);
            return Results.success();
        }
        return Results.failure();
    }

    @Override
    public Result reductionShoppingCart(Long shopId) {
        TShoppingCart shoppingCart = shoppingCartMapper.selectById(shopId);
        if(ObjectUtil.isNotEmpty(shoppingCart)){
            if(shoppingCart.getNumber()>1){
                shoppingCart.setNumber(shoppingCart.getNumber()-1);
                shoppingCartMapper.updateById(shoppingCart);
                return Results.success();
            }else {
                shoppingCartMapper.deleteById(shopId);
                return Results.success();
            }
        }
        return Results.failure();
    }

    @Override
    public PageResponse<TShoppingCart> findShoppingCartList(Goods requestParam) {
        LambdaQueryWrapper<TShoppingCart> queryWrapper = Wrappers.lambdaQuery(TShoppingCart.class)
                .eq(TShoppingCart::getUserId,requestParam.getUserId())
                .orderByDesc(TShoppingCart::getCreateTime);
        if(ObjectUtil.isNotEmpty(requestParam.getGoodsname())){
            queryWrapper.like(TShoppingCart::getGoodsname, requestParam.getGoodsname());
        }
        IPage<TShoppingCart> shoppingCartPage = shoppingCartMapper.selectPage(PageUtil.convert(requestParam), queryWrapper);
        return PageUtil.convert(shoppingCartPage);
    }

    @Override
    public Result addShoppingCart(AddShoppingCart requestParam) {
        TShoppingCart shoppingCart = TShoppingCart.builder().userId(requestParam.getUserId())
                .goodsId(requestParam.getGoodsId())
                .pic(requestParam.getPic())
                .description(requestParam.getDescription())
                .price(requestParam.getPrice())
                .goodsname(requestParam.getGoodsname())
                .number(1)
                .build();
        int insert = shoppingCartMapper.insert(shoppingCart);
        if(insert==0){
            throw new ServiceException("添加购物车失败！");
        }else {
            return Results.success();
        }
    }

    @Override
    public Result removeShoppingCart(Long shopId) {
        int count = shoppingCartMapper.deleteById(shopId);
        if(count==0){
            throw new ServiceException("移除购物车失败！");
        }else {
            return Results.success();
        }
    }

}




