package vn.edu.stu.WebBlogNauAn.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.edu.stu.WebBlogNauAn.dto.RoleRequest;
import vn.edu.stu.WebBlogNauAn.exception.ConflicException;
import vn.edu.stu.WebBlogNauAn.mapper.RoleMapper;
import vn.edu.stu.WebBlogNauAn.model.Role;
import vn.edu.stu.WebBlogNauAn.repository.RoleRepo;
import vn.edu.stu.WebBlogNauAn.response.RoleResponse;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleService {
    private final RoleRepo roleRepo;
    private final RoleMapper roleMapper;
    private static final Logger logger = LoggerFactory.getLogger(RoleService.class);

    @PostConstruct
    public void initDefaultRoles() {
        if (roleRepo.findAll().size() > 0) {
            return;
        }
        Role admin = new Role();
        Role user = new Role();

        admin.setName("ADMIN");
        user.setName("USER");

        roleRepo.save(admin);
        roleRepo.save(user);
    }

    public List<RoleResponse> findAll() {
        List<Role> roles = roleRepo.findAll();
        return roleMapper.toRoleResponses(roles);
    }

    public String getRoleNameById(int roleId) {
        if (roleRepo.findById(roleId).isPresent())
            return roleRepo.findById(roleId).get().getName();
        return "NULL";// Chắc chắn k xảy ra!!
    }

    public Role findRoleByName(String roleName) {
        return roleRepo.findByName(roleName).orElseGet(() -> {
            logger.warn("Role not found");
            return null;
        });
    }

    public ResponseEntity<RoleResponse> createNewRole(RoleRequest roleRequest) {
        Optional<Role> roleOptional = roleRepo.findByName(roleRequest.getRole());
        if (roleOptional.isPresent()) {
            throw new ConflicException("Role đã tồn tại!");
        }
        Role newRole = new Role();
        newRole.setName(roleRequest.getRole().trim().toUpperCase());
        roleRepo.save(newRole);
        String message = String.format("Đã tạo vai trò '%s'", newRole.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(new RoleResponse(message));
    }
}
