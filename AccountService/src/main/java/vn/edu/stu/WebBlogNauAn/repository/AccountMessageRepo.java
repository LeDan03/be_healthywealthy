package vn.edu.stu.WebBlogNauAn.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.edu.stu.WebBlogNauAn.model.AccountMessage;

public interface AccountMessageRepo extends JpaRepository<AccountMessage, Long> {

    boolean existsByMessageId(long messageId);

    List<AccountMessage> findByAccountIdOrderByCreatedAtDesc(long accountId);

    @Modifying
    @Query(value = "UPDATE account_message am SET am.is_read=true WHERE am.id=:accountMessageId", nativeQuery = true)
    void updateReadTrueById(@Param("accountMessageId") long accountMessageId);
}
