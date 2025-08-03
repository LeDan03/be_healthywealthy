package vn.edu.stu.WebBlogNauAn.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import vn.edu.stu.WebBlogNauAn.dto.CommenterDto;
import vn.edu.stu.WebBlogNauAn.model.Account;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface AccountRepo extends JpaRepository<Account, Long> {
        Optional<Account> findByEmail(String email);

        Optional<Account> findById(long id);

        List<Account> findByIdIn(List<Long> accountIds);

        Page<Account> findAll(Pageable pageable);

        boolean existsByEmail(String email);

        @Query("""
                        SELECT COUNT(a) FROM Account a
                        WHERE FUNCTION('MONTH', a.createdAt) = :month
                        AND FUNCTION('YEAR', a.createdAt) = :year""")
        Long countAccountsRegisteredByMonth(@Param("month") int month, @Param("year") int year);

        @Modifying
        @Query(value = """
                        UPDATE Account a
                        SET a.avatarUrl =:avatarUrl, a.avatarPublicId=:avatarPublicId ,a.updatedAt=:time
                        WHERE a.email=:email
                        """)
        void updateAvatarUrlByEmail(
                        @Param("email") String email,
                        @Param("avatarUrl") String avatarUrl,
                        @Param("avatarPublicId") String avatarPublicId,
                        @Param("time") Timestamp time);

        @Modifying
        @Query(value = "UPDATE Account a SET a.username =:newUsername, a.updatedAt =:time WHERE a.email=:email ")
        void updateUsernameByEmail(
                        @Param("email") String email,
                        @Param("newUsername") String newUsername,
                        @Param("time") Timestamp time);

        // neu viet CommenterDto thi JPA khong hieu do no dang doc String
        @Query("""
                            SELECT new vn.edu.stu.WebBlogNauAn.dto.CommenterDto(a.id,  a.username, a.avatarUrl)
                            FROM Account a
                            WHERE a.id IN :accountIds
                        """)
        List<CommenterDto> getCommenterDetail(@Param("accountIds") List<Long> accountIds);
}
