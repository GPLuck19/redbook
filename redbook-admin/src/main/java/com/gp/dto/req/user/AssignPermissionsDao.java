package com.gp.dto.req.user;

import lombok.Data;

import java.util.List;

@Data
public class AssignPermissionsDao {

    private Long roleId;

    private List<Long> permissionIds;

}
