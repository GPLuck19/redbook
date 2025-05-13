package com.gp.controller.user;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.gp.annotation.Log;
import com.gp.dto.resp.user.FriendVo;
import com.gp.enums.OperatorType;
import com.gp.result.Result;
import com.gp.result.Results;
import com.gp.service.TUserFriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 好友控制层
 */
@RestController
@RequiredArgsConstructor
public class UserFriendController {

    private final TUserFriendService userFriendService;

    /**
     * 获取好友关系信息列表
     */
    @SaCheckPermission("/user/getFriendList")
    @GetMapping("/api/user-service/getFriendList")
    public Result<List<FriendVo>> getFriendList(@RequestParam("userId") Long userId, @RequestParam("search") String search) {
        return Results.success(userFriendService.getFriendList(userId,search));
    }

    /**
     * 删除好友关系
     */
    @Log(title = "删除好友关系", operatorType = OperatorType.MANAGE)
    @SaCheckPermission("/user/removeFriend")
    @DeleteMapping("/api/user-service/removeFriend")
    public Result removeFriend(@RequestParam("userId") Long userId,@RequestParam("friendId") Long friendId) {
        return Results.success(userFriendService.removeFriend(userId,friendId));
    }


    /**
     * 拒绝好友关系
     */
    @Log(title = "拒绝好友关系", operatorType = OperatorType.MANAGE)
    @SaCheckPermission("/user/refuseFriend")
    @DeleteMapping("/api/user-service/refuseFriend")
    public Result refuseFriend(@RequestParam("userId") Long userId,@RequestParam("friendId") Long friendId) {
        return Results.success(userFriendService.refuseFriend(userId,friendId));
    }

    /**
     * 新增好友关系
     */
    @Log(title = "新增好友关系", operatorType = OperatorType.MANAGE)
    @SaCheckPermission("/user/addFrined")
    @PostMapping("/api/user-service/addFrined")
    public Result addFrined(@RequestParam("userId") Long userId,@RequestParam("friendId") Long friendId) {
        return Results.success(userFriendService.addFrined(userId,friendId));
    }


    /**
     * 好友同意申请
     */
    @Log(title = "好友同意申请", operatorType = OperatorType.MANAGE)
    @SaCheckPermission("/user/acceptFriend")
    @PostMapping("/api/user-service/acceptFriend")
    public Result acceptFriend(@RequestParam("userId") Long userId,@RequestParam("friendId") Long friendId) {
        return Results.success(userFriendService.acceptFriend(userId,friendId));
    }

    /**
     * 查看待验证好友列表
     */
    @SaCheckPermission("/user/getPendingFriends")
    @GetMapping("/api/user-service/getPendingFriends")
    public Result<List<FriendVo>> getPendingFriends(@RequestParam("userId") Long userId,@RequestParam("search") String search) {
        return Results.success(userFriendService.getPendingFriends(userId,search));
    }
}
