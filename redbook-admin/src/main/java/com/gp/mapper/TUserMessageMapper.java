package com.gp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.dto.resp.user.FriendMessageListVo;
import com.gp.entity.TUserMessage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author Administrator
* @description 针对表【t_user_message(用户私信表)】的数据库操作Mapper
* @createDate 2024-11-29 10:19:42
* @Entity com.gp.entity.TUserMessage
*/
public interface TUserMessageMapper extends BaseMapper<TUserMessage> {

    @Select("""
        <script>
        SELECT 
            u.id AS friendId,
            u.username,
            u.real_name AS realName,
            u.user_pic AS userPic,
            m.content AS lastMessage,
            MAX(m.messageType) AS messageType,  -- 使用 MAX 聚合 messageType
            m.lastMessageTime
        FROM 
            t_user_friend f
        JOIN 
            t_user u ON u.id = CASE 
                WHEN f.user_id = #{userId} THEN f.friend_id
                ELSE f.user_id
            END
        LEFT JOIN (
            SELECT 
                CASE 
                    WHEN sender_id = #{userId} THEN receiver_id
                    ELSE sender_id
                END AS friendId,
                MAX(messageType) AS messageType,  -- 聚合 messageType
                MAX(create_time) AS lastMessageTime,
                SUBSTRING_INDEX(GROUP_CONCAT(content ORDER BY create_time DESC), ',', 1) AS content
            FROM 
                t_user_message
            WHERE 
                sender_id = #{userId} OR receiver_id = #{userId}
            GROUP BY 
                friendId
        ) m ON m.friendId = u.id
        WHERE 
            (f.user_id = #{userId} OR f.friend_id = #{userId})
            AND f.status = 1
        GROUP BY 
            u.id, m.lastMessageTime  -- 确保在 GROUP BY 中包含所有非聚合字段
        ORDER BY 
            m.lastMessageTime DESC
        </script>
    """)
    List<FriendMessageListVo> FriendMessageList(@Param("userId") Long userId);


}




