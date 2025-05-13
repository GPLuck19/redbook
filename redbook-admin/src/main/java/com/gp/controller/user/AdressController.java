package com.gp.controller.user;


import cn.dev33.satoken.annotation.SaCheckPermission;
import com.gp.annotation.Log;
import com.gp.entity.TUserAddress;
import com.gp.enums.OperatorType;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.TUserAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * 地址控制层
 */
@RestController
@RequiredArgsConstructor
public class AdressController {

    private final TUserAddressService addressService;


    /**
     * 获取用户地址信息
     */
    @SaCheckPermission("/user/findAddressList")
    @GetMapping("/api/address-service/findAddressList")
    public Result<List<TUserAddress>> findAddressList(@RequestParam("userId") Long userId) {
        return Results.success(addressService.findAddressList(userId));
    }

    /**
     * 删除用户地址信息
     */
    @Log(title = "删除用户地址信息", operatorType = OperatorType.MANAGE)
    @SaCheckPermission("/user/removeAddress")
    @DeleteMapping("/api/address-service/removeAddress")
    public Result removeAdress(@RequestParam("adressId") Long adressId) {
        return Results.success(addressService.removeAdress(adressId));
    }

    /**
     * 新增用户地址信息
     */
    @Log(title = "新增用户地址信息", operatorType = OperatorType.MANAGE)
    @SaCheckPermission("/user/addAddress")
    @PostMapping("/api/address-service/addAddress")
    public Result addAdress(@RequestBody TUserAddress requestParam) {
        return Results.success(addressService.addAdress(requestParam));
    }
}
