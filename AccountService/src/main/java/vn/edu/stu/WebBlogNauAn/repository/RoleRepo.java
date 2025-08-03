package vn.edu.stu.WebBlogNauAn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.edu.stu.WebBlogNauAn.model.Role;

import java.util.Optional;

@Repository
public interface RoleRepo extends JpaRepository<Role, Integer> {
    @Query("SELECT r.id FROM Role r WHERE r.name = :name")
    Integer findIdByName(String name);//Integer hỗ trợ null neu không tìm thấy
    Optional<Role> findByName(String name);
    Optional<Role> findById(int id);
}
