package vn.edu.stu.WebBlogNauAn.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.lettuce.core.dynamic.annotation.Param;
import vn.edu.stu.WebBlogNauAn.model.CommunityMessage;
import vn.edu.stu.WebBlogNauAn.model.Message;

@Repository
@Transactional
public interface MessageRepo extends JpaRepository<Message, Long> {
        // List<Message> findByReadFalse();

        long countByReasonId(int reasonId);

        Optional<CommunityMessage> findFirstByPinnedTrue();

        @Query(value = """
                        SELECT m
                        FROM Message m
                        WHERE m.reason.id =:reasonId
                        """)
        List<Message> findMessagesByReasonId(@Param("reasonId") int reasonId);

        Page<Message> getMessagesByReasonId(@Param("reasonId") int reasonId, Pageable pageable);

        @Query(value = "SELECT COUNT(m)>0 FROM Message m WHERE m.reason.id=:id")
        Boolean isUseReasonById(@Param("id") int id);

        @Modifying
        @Query(value = """
                        INSERT INTO accounts_messages (account_id, message_id)
                        SELECT a.id, :messageId FROM account a
                        """, nativeQuery = true)
        void insertMessageToAllAccount(long messageId);

        @Modifying
        @Query("UPDATE Message m SET m.sentToAll=true WHERE m.id=:id")
        void updateSentToAllTrue(@Param("id") long id);
}
