package com.gp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.dto.resp.user.FriendVo;
import com.gp.entity.TUserFriend;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author Administrator
* @description 针对表【t_user_friend(好友关系表)】的数据库操作Mapper
* @createDate 2024-11-29 09:24:21
* @Entity com.gp.entity.TUserFriend
*/
public interface TUserFriendMapper extends BaseMapper<TUserFriend> {

    @Select("""
    <script>
        SELECT 
            u.id As friendId,
            u.username  ,
            u.real_name AS realName,
            u.user_pic AS userPic,
            f.create_time AS addedTime
        FROM 
            t_user_friend f
        JOIN 
            t_user u
        ON 
            f.friend_id = u.id
        WHERE 
            f.user_id = #{userId} 
            <if test="status != null">
                AND f.status = #{status}
            </if>
            <if test="search != null and search != ''">
                AND (u.username LIKE CONCAT('%', #{search}, '%') 
                    OR u.real_name LIKE CONCAT('%', #{search}, '%'))
            </if>
        ORDER BY f.create_time DESC
    </script>
""")
    List<FriendVo> getFriendList(@Param("userId") Long userId,
                                 @Param("status") Integer status,
                                 @Param("search") String search);

    @Select("""
    <script>
        SELECT 
            u.id As friendId,
            u.username  ,
            u.real_name AS realName,
            u.user_pic AS userPic,
            f.create_time AS addedTime
        FROM 
            t_user_friend f
        JOIN 
            t_user u
        ON 
            f.user_id = u.id
        WHERE 
            f.friend_id = #{userId} 
            AND f.request_initiator != #{userId}
            <if test="status != null">
                AND f.status = #{status}
            </if>
            <if test="search != null and search != ''">
                AND (u.username LIKE CONCAT('%', #{search}, '%') 
                    OR u.real_name LIKE CONCAT('%', #{search}, '%'))
            </if>
        ORDER BY f.create_time DESC
    </script>
""")
    List<FriendVo> PendingFriends(@Param("userId") Long userId,
                                  @Param("status") Integer status,
                                  @Param("search") String search);
}




