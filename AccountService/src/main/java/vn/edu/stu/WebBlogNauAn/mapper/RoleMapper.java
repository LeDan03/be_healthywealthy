package vn.edu.stu.WebBlogNauAn.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.edu.stu.WebBlogNauAn.dto.RoleRequest;
import vn.edu.stu.WebBlogNauAn.model.Role;
import vn.edu.stu.WebBlogNauAn.response.RoleResponse;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RoleMapper {
    private final ModelMapper modelMapper;

    @Autowired
    public RoleMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public RoleResponse toRoleResponse(Role role) {
        return modelMapper.map(role, RoleResponse.class);
    }

    public List<RoleResponse> toRoleResponses(List<Role> roles) {
        return roles.stream().map(this::toRoleResponse).collect(Collectors.toList());
    }

    public Role toRole(RoleRequest roleRequest) {
        return modelMapper.map(roleRequest, Role.class);
    }
}
