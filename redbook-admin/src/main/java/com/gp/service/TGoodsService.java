package com.gp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gp.dto.req.goods.Goods;
import com.gp.dto.req.goods.GoodsDao;
import com.gp.dto.resp.goods.GoodsVo;
import com.gp.entity.TGoods;
import com.gp.page.PageResponse;
import com.gp.result.Result;

import java.util.List;

/**
* @author Administrator
* @description 针对表【tb_goods(商品表)】的数据库操作Service
* @createDate 2024-10-29 10:12:51
*/
public interface TGoodsService extends IService<TGoods> {

    PageResponse<GoodsVo> findGoods(Goods requestParam);

    Result saveOrUpdateGoods(GoodsDao requestParam);


    TGoods findGoodsById(Long goodsId);

    Result deleteGoods(Long goodsId);

    Result isPubGoods(Long goodsId,Integer pubId);

    PageResponse<GoodsVo> findGoodsList(Goods requestParam);
}
