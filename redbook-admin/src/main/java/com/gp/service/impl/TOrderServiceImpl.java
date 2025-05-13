package com.gp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.chain.AbstractChainContext;
import com.gp.common.constant.UserRedisKey;
import com.gp.common.enums.GoodsChainMarkEnum;
import com.gp.dto.req.order.OrderDao;
import com.gp.dto.req.order.OrderListVo;
import com.gp.dto.req.order.OrderVo;
import com.gp.dto.resp.goods.GoodsVo;
import com.gp.dto.resp.order.OrderDetails;
import com.gp.dto.resp.order.OrderList;
import com.gp.dto.resp.user.UserInfo;
import com.gp.entity.*;
import com.gp.enums.GoodsEnum;
import com.gp.enums.OrderEnum;
import com.gp.exception.ServiceException;
import com.gp.mapper.*;
import com.gp.page.PageResponse;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.TOrderService;
import com.gp.service.TUserService;
import com.gp.utils.PageUtil;
import com.gp.utils.RedisUtils;
import com.gp.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.gp.common.constant.GoodsRedisKey.GOODS_INFO;
import static com.gp.common.constant.GoodsRedisKey.GOODS_LOCK_PAY_KEY;
import static com.gp.common.constant.UserRedisKey.USER_ADDRESS_INFO;


/**
* @author Administrator
* @description 针对表【t_order(订单表)】的数据库操作Service实现
* @createDate 2024-11-07 16:22:30
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class TOrderServiceImpl extends ServiceImpl<TOrderMapper, TOrder>
    implements TOrderService{

    private final TOrderMapper orderMapper;

    private final TUserService userService;

    private final TUserAddressMapper userAddressMapper;

    private final TShoppingCartMapper shoppingCartMapper;

    private final TGoodsMapper goodsMapper;

    private final TPayMapper payMapper;

    private final AbstractChainContext<OrderDao> goodsQueryAbstractChainContext;

    @Override
    public List<OrderList> findOrderList(OrderListVo requestParam) {
        List<TOrder> orderList = getOrders(requestParam);

        // 按 parent_order_sn 分组，合并订单
        Map<String, List<TOrder>> groupedOrders = orderList.stream()
                .collect(Collectors.groupingBy(TOrder::getPOrderSn));

        // 合并后的订单列表
        List<OrderList> mergedOrderList = new ArrayList<>();
        groupedOrders.forEach((parentOrderSn, orders) -> {
            // 合并每个主订单下的子订单
            BigDecimal totalAmount = BigDecimal.ZERO;
            int totalNumber = 0;
            String subject = orders.get(0).getGoodsName()+"等"; // 主订单的商品名称
            for (TOrder order : orders) {
                totalAmount = totalAmount.add(order.getPrice().multiply(BigDecimal.valueOf(order.getNumber())));
                totalNumber += order.getNumber();
            }
            // 创建合并后的订单对象
            OrderList mergedOrder = OrderList.builder()
                    .orderSn(parentOrderSn)  // 合并后的订单号（主订单号）
                    .subject(subject)
                    .orderTime(orders.get(0).getOrderTime())  // 使用第一个订单的下单时间
                    .status(orders.get(0).getStatus())  // 使用第一个订单的支付方式
                    .totalAmount(totalAmount)  // 合并后的总金额
                    .address(orders.get(0).getAddress())  // 使用第一个订单的地址
                    .number(totalNumber)  // 合并后的商品数量
                    .build();

            mergedOrderList.add(mergedOrder);
        });

        return mergedOrderList;
    }

    private List<TOrder> getOrders(OrderListVo requestParam) {
        LambdaQueryWrapper<TOrder> wrapper = Wrappers.lambdaQuery(TOrder.class)
                .eq(ObjectUtil.isNotNull(requestParam.getStatus()),TOrder::getUserId, requestParam.getUserId())
                .eq(ObjectUtil.isNotNull(requestParam.getStatus()),TOrder::getStatus, requestParam.getStatus())
                .orderByDesc(TOrder::getCreateTime);
        List<TOrder> orderList = orderMapper.selectList(wrapper);
        return orderList;
    }

    @Override

    public OrderDetails createOrder(OrderVo requestParam) {
        List<TOrder> orderList = new ArrayList<>();
        //构建订单数据
        String POrderSn=UUID.randomUUID().toString();
        //批量保存订单信息
        requestParam.getGoodsList().forEach(item -> {
            // 责任链模式 验证商品库存是否存在、不存在加载缓存
            goodsQueryAbstractChainContext.handler(GoodsChainMarkEnum.GOODS_QUERY_FILTER.name(), item);
            String lockKey = GOODS_LOCK_PAY_KEY+item.getGoodsId();
            RLock lock = RedisUtils.getClient().getLock(lockKey);
            try {
                if(lock.tryLock(UserRedisKey.REDIS_WAIT_TIME,
                        UserRedisKey.REDIS_LEASETIME,
                        TimeUnit.SECONDS)){
                    String id = String.valueOf(item.getGoodsId());
                    TGoods goods=RedisUtils.getOrLoad(
                            GOODS_INFO + id,
                            TGoods.class,
                            () -> goodsMapper.selectById(id), // 数据库查询
                            30,
                            TimeUnit.DAYS // 缓存有效期
                    );

                    String addressId = String.valueOf(requestParam.getAddressId());
                    TUserAddress address=RedisUtils.getOrLoad(
                            USER_ADDRESS_INFO + addressId,
                            TUserAddress.class,
                            () -> userAddressMapper.selectById(addressId), // 数据库查询
                            30,
                            TimeUnit.DAYS // 缓存有效期
                    );
                    if(Objects.isNull(goods)){
                        throw new ServiceException(GoodsEnum.GOODS_DELIST.getMsg());
                    }
                    //减库存，先数据库操作，后期替换成redis控制
                    if(goods.getNumber()>0){
                        goods.setNumber(goods.getNumber()-1);
                        goodsMapper.updateById(goods);
                    }else {
                        throw new ServiceException(GoodsEnum.GOODS_NUMBER_BAD.getMsg());
                    }
                    UserInfo userInfo = userService.getUserInfoById(requestParam.getUserId());
                    String orderSn = UUID.randomUUID().toString();
                    //构建订单数据
                    TOrder order = TOrder.builder().orderTime(new Date())
                            .price(item.getPrice())
                            .userId(requestParam.getUserId())
                            .goodsId(item.getGoodsId())
                            .POrderSn(POrderSn)
                            .orderSn(orderSn)
                            .goodsName(goods.getGoodsname())
                            .username(userInfo.getRealName())
                            .status(1)
                            .sourceId(goods.getUserId())
                            .address(address.toString())
                            .number(item.getQuantity())
                            .build();
                    int insert = orderMapper.insert(order);
                    //清楚购物车数据
                    shoppingCartMapper.delete(Wrappers.lambdaQuery(TShoppingCart.class).eq(TShoppingCart::getUserId,requestParam.getUserId()).eq(TShoppingCart::getGoodsId,item.getGoodsId()));
                    if(insert==0){
                        throw new ServiceException(OrderEnum.ORDER_ADD_BAD.getMsg());
                    }
                    orderList.add(order);
                }else {
                    throw new ServiceException(GoodsEnum.GOODS_LUCK_BAD.getMsg());
                }
            }catch (Exception e){
                throw new ServiceException(GoodsEnum.GOODS_ADD_BAD.getMsg());
            }finally {
                if (lock != null && lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        });
        return new OrderDetails(POrderSn,orderList);
    }

    @Override
    @Transactional
    public Result cancelOrder(String orderSn) {
        //取消支付订单信息
        LambdaQueryWrapper<TPay> wrapper1 = Wrappers.lambdaQuery(TPay.class).eq(TPay::getOrderSn, orderSn);
        TPay pay=new TPay();
        pay.setStatus(0);
        payMapper.update(pay,wrapper1);
        //取消订单信息
        LambdaQueryWrapper<TOrder> wrapper2 = Wrappers.lambdaQuery(TOrder.class).eq(TOrder::getPOrderSn, orderSn);
        TOrder order = new TOrder();
        order.setStatus(2);
        orderMapper.update(order,wrapper2);
        return Results.success();
    }

    @Override
    public List<TOrder> findOrderListBySn(String orderSn) {
        if(StringUtils.isNotBlank(orderSn)){
            LambdaQueryWrapper<TOrder> wrapper = Wrappers.lambdaQuery(TOrder.class)
                    .eq(TOrder::getOrderSn, orderSn);
            List<TOrder> orderList = orderMapper.selectList(wrapper);
            return orderList;
        }else {
            throw new ServiceException(OrderEnum.ORDER_BAD.getMsg());
        }
    }

    @Override
    public Result deleteOrder(String orderSn) {
        //移除支付订单信息
        payMapper.delete(Wrappers.lambdaQuery(TPay.class).eq(TPay::getOrderSn,orderSn));
        //移除订单信息
        orderMapper.delete(Wrappers.lambdaQuery(TOrder.class).eq(TOrder::getPOrderSn,orderSn));
        return Results.success();
    }

    @Override
    public PageResponse<OrderList> findAdminOrderList(OrderListVo requestParam) {
        LambdaQueryWrapper<TOrder> queryWrapper = Wrappers.lambdaQuery(TOrder.class)
                .eq(ObjectUtil.isNotNull(requestParam.getStatus()),TOrder::getStatus,requestParam.getStatus())
                .eq(ObjectUtil.isNotNull(requestParam.getSourceId()),TOrder::getSourceId,requestParam.getSourceId())
                .like(ObjectUtil.isNotNull(requestParam.getGoodsName()),TOrder::getGoodsName,"%" + requestParam.getGoodsName() + "%")
                .ge(ObjectUtil.isNotNull(requestParam.getStartTime()),TOrder::getCreateTime,requestParam.getStartTime())
                .le(ObjectUtil.isNotNull(requestParam.getStopTime()),TOrder::getCreateTime,requestParam.getStopTime())
                .orderByDesc(TOrder::getCreateTime);
        IPage<TOrder> orderPage = orderMapper.selectPage(PageUtil.convert(requestParam), queryWrapper);
        return PageUtil.convert(orderPage, each -> {
            BigDecimal totalAmount = BigDecimal.ZERO;
            totalAmount = totalAmount.add(each.getPrice().multiply(BigDecimal.valueOf(each.getNumber())));
            OrderList actualResult = BeanUtil.toBean(each, OrderList.class);
            actualResult.setSubject(each.getGoodsName());
            actualResult.setTotalAmount(totalAmount);
            return actualResult;
        });
    }

}


