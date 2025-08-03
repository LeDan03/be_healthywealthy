package vn.edu.stu.WebBlogNauAn.mapper;

import org.springframework.stereotype.Component;
import vn.edu.stu.WebBlogNauAn.model.Account;
import vn.edu.stu.WebBlogNauAn.model.Follow;

import java.util.ArrayList;
import java.util.List;

@Component
public class FollowMapper {

    public List<Account> followeesToAccounts(List<Follow> follows) {
        List<Account> followingAccounts = new ArrayList<Account>();
        followingAccounts = follows.stream().map(Follow::getFollowee).toList();
        return followingAccounts;
    }
    public List<Account> followersToAccounts(List<Follow> follows) {
        List<Account> followerAccounts = new ArrayList<Account>();
        followerAccounts = follows.stream().map(Follow::getFollower).toList();
        return followerAccounts;
    }
}
