package com.gp.dto.resp.user;

import com.gp.entity.TPermissions;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class PermissionNode {
    private Long id;
    private String name;
    private String path;
    private String component;
    private Integer operationType;
    private Integer sortNumber;

    private Long parentId;
    private List<PermissionNode> children = new ArrayList<>();

    public PermissionNode(TPermissions permission) {
        this.id = permission.getId();
        this.name = permission.getName();
        this.path = permission.getRoutePath();
        this.component = permission.getComponent();
        this.operationType = permission.getOperationType();
        this.sortNumber = permission.getSortNumber();
        this.parentId = permission.getParentId();
    }


}
