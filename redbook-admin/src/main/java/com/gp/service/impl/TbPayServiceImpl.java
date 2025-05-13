package com.gp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.entity.TPay;
import com.gp.mapper.TPayMapper;
import com.gp.service.TbPayService;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【tb_pay(支付表)】的数据库操作Service实现
* @createDate 2024-09-24 10:40:52
*/
@Service
public class TbPayServiceImpl extends ServiceImpl<TPayMapper, TPay>
    implements TbPayService{

}




