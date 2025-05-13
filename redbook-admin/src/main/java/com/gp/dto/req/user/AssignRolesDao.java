package com.gp.dto.req.user;

import lombok.Data;

import java.util.List;
@Data
public class AssignRolesDao {
    private Long userId;
    private List<Long> roleIds;


}
