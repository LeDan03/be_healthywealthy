package vn.edu.stu.WebBlogNauAn.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.stu.WebBlogNauAn.model.Account;
import vn.edu.stu.WebBlogNauAn.model.Follow;


@Repository
public interface FollowRepo extends JpaRepository<Follow, Long> {

    boolean existsByFolloweeAndFollower(Account followee, Account follower);
    Optional<Follow> findByFollowerAndFollowee(Account follower, Account followee);
    void deleteByFollowerAndFollowee(Account follower, Account followee);
}
