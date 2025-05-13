package com.gp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gp.dto.req.goods.AddShoppingCart;
import com.gp.dto.req.goods.Goods;
import com.gp.entity.TShoppingCart;
import com.gp.page.PageResponse;
import com.gp.result.Result;

/**
* @author Administrator
* @description 针对表【t_shopping_cart(购物车表)】的数据库操作Service
* @createDate 2024-11-12 15:49:51
*/
public interface TShoppingCartService extends IService<TShoppingCart> {

    Result addShoppingCart(AddShoppingCart requestParam);

    Result removeShoppingCart(Long shopId);

    PageResponse<TShoppingCart> findShoppingCartList(Goods requestParam);

    Result reductionShoppingCart(Long shopId);

    Result plusShoppingCart(Long shopId);
}
