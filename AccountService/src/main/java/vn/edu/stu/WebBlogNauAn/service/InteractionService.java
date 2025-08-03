package vn.edu.stu.WebBlogNauAn.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vn.edu.stu.WebBlogNauAn.exception.ResourceNotFoundException;
import vn.edu.stu.WebBlogNauAn.mapper.AccountMapper;
import vn.edu.stu.WebBlogNauAn.mapper.FollowMapper;
import vn.edu.stu.WebBlogNauAn.model.Account;
import vn.edu.stu.WebBlogNauAn.model.Follow;
import vn.edu.stu.WebBlogNauAn.model.LovedRecipe;
import vn.edu.stu.WebBlogNauAn.model.SavedRecipe;
import vn.edu.stu.WebBlogNauAn.producer.InteractionProducer;
import vn.edu.stu.WebBlogNauAn.repository.AccountRepo;
import vn.edu.stu.WebBlogNauAn.repository.FollowRepo;
import vn.edu.stu.WebBlogNauAn.repository.LovedRecipeRepo;
import vn.edu.stu.WebBlogNauAn.repository.SavedRecipeRepo;
import vn.edu.stu.WebBlogNauAn.response.AccountResponse;
import vn.edu.stu.WebBlogNauAn.response.RecipeResponse;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class InteractionService {

    private final RedisService redisService;
    private final SavedRecipeRepo savedRecipeRepo;
    private final AccountRepo accountRepo;
    private final FollowMapper followMapper;
    private final AccountMapper accountMapper;
    private final FollowRepo followRepo;
    private final RecipeClient recipeClient;
    private final LovedRecipeRepo lovedRecipeRepo;
    private final InteractionProducer interactionProducer;


    public void toggleSaveRecipe(long recipeId, long accountId) {
        boolean isSaved = isUserSavedRecipe(accountId, recipeId);
        if (isSaved) {
            redisService.unsaveRecipe(accountId, recipeId);
            redisService.markUnsave(accountId, recipeId);
            interactionProducer.sendUnsaveEvent(recipeId, accountId);
        } else {
            redisService.saveRecipe(accountId, recipeId);
            redisService.removeUnsaveMark(accountId, recipeId);
            interactionProducer.sendSaveEvent(recipeId, accountId);
        }
    }

    public boolean isUserSavedRecipe(long accountId, long recipeId) {
        if (redisService.isSavedRecipe(accountId, recipeId)) {
            return true;
        } else {
            if (savedRecipeRepo.existsByAccountIdAndRecipeId(accountId, recipeId))
                return true;
            return false;
        }
    }

    @Scheduled(fixedRate = 60000) // 60s
    @Transactional
    public void reloadSavedList() {
        Set<String> savedKeys = redisService.getKeysByPattern("saved:*");
        Set<String> unsavedKeys = redisService.getKeysByPattern("unsaved:*");

        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(savedKeys);
        allKeys.addAll(unsavedKeys);

        for (String key : allKeys) {
            String accountIdStr = key.split(":")[1];
            long accountId = Long.parseLong(accountIdStr);

            Optional<Account> accOptional = accountRepo.findById(accountId);
            if (!accOptional.isPresent())
                continue;
            Account account = accOptional.get();

            Set<String> accSavedIds = redisService.getSavedRecipeIds(accountId);
            for (String recipeIdStr : accSavedIds) {
                long recipeId = Long.parseLong(recipeIdStr);
                if (!savedRecipeRepo.existsByAccountIdAndRecipeId(accountId, recipeId)) {
                    SavedRecipe savedRecipe = SavedRecipe.builder()
                            .account(account)
                            .recipeId(recipeId)
                            .saveAt(Timestamp.from(Instant.now()))
                            .build();

                    savedRecipeRepo.save(savedRecipe);
                }
                redisService.removeFromCache("saved:" + accountId, recipeIdStr);
            }
            Set<String> accUnsavedIds = redisService.getUnsavedRecipeIds(accountId);
            for (String recipeIdStr : accUnsavedIds) {
                long recipeId = Long.parseLong(recipeIdStr);
                savedRecipeRepo.deleteByAccountIdAndRecipeId(accountId, recipeId);
                redisService.removeFromCache("unsaved:" + accountId, recipeIdStr);
            }
        }
    }

    // Love
    public void toggleLoveRecipe(long accountId, long recipeId) {
        if (isUserLovedRecipe(accountId, recipeId)) {
            redisService.unloveRecipe(accountId, recipeId);
            interactionProducer.sendUnloveEvent(recipeId, accountId);
        } else {
            redisService.loveRecipe(accountId, recipeId);
            interactionProducer.sendLoveEvent(recipeId, accountId);
        }
    }

    // Chỉ khi không loved trong cả cache với db mới false
    public boolean isUserLovedRecipe(long accountId, long recipeId) {
        if (redisService.isLovedRecipe(accountId, recipeId)) {
            return true;
        } else {
            if (lovedRecipeRepo.existsByAccountIdAndRecipeId(accountId, recipeId))
                return true;
            return false;
        }
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void reloadLovedList() {
        Set<String> lovedKeys = redisService.getKeysByPattern("loved:account:*");
        Set<String> unlovedKeys = redisService.getKeysByPattern("unloved:account:*");

        Set<String> allAccountKeys = new HashSet<>();
        allAccountKeys.addAll(lovedKeys);
        allAccountKeys.addAll(unlovedKeys);

        for (String key : allAccountKeys) {
            String idStr = key.split(":")[2];
            if (idStr.equals("all"))
                continue;

            long accountId = Long.parseLong(idStr);
            Optional<Account> accountOpt = accountRepo.findById(accountId);
            if (accountOpt.isEmpty())
                continue;

            Account account = accountOpt.get();
            Set<String> lovedIds = redisService.getLovedRecipeIds(accountId);
            Set<String> flushedIds = redisService.getFlushedRecipeIds(accountId); // lấy danh sách recipe đã flush rồi
                                                                                  // của account
            Set<String> unlovedIds = redisService.getUnlovedRecipeIds(accountId);

            // merged = (loved - unloved) - flushed
            Set<Long> merged = lovedIds.stream().map(Long::parseLong).collect(Collectors.toSet());
            merged.removeAll(unlovedIds.stream().map(Long::parseLong).collect(Collectors.toSet()));// Xóa các recipeId
                                                                                                   // đã unlove
            merged.removeAll(flushedIds.stream().map(Long::parseLong).collect(Collectors.toSet()));// xóa các recipeId
                                                                                                   // đã flush

            for (Long recipeId : merged) {
                if (!lovedRecipeRepo.existsByAccountIdAndRecipeId(accountId, recipeId)) {
                    lovedRecipeRepo.save(LovedRecipe.builder()
                            .account(account)
                            .recipeId(recipeId)
                            .lovedAt(Timestamp.from(Instant.now()))
                            .build());

                    // Thêm vào flushed
                    redisService.addFlushedRecipe(accountId, recipeId);
                }
            }
            // KHÔNG xoá key loved:account hoặc flushed
            // Chỉ xoá phần bị unlove
            for (String recipeIdStr : unlovedIds) {
                long recipeId = Long.parseLong(recipeIdStr);
                lovedRecipeRepo.deleteByAccountIdAndRecipeId(accountId, recipeId);
                redisService.removeFromCache("unloved:account:" + accountId, recipeIdStr);
                redisService.removeFromCache("flushed:loved:account:" + accountId, recipeIdStr); // remove phần tử
                                                                                                 // recipeId đã unlove
                                                                                                 // khỏi flushed
            }
        }
    }

    // Mỗi 5p load lại xem recipe nào được yêu thích nhiều hơn 3 lượt trong 6 tiếng
    // thì set featured true
    @Scheduled(fixedRate = 60000)
    public void flushFeaturedRecipe() {
        Set<String> loveRecipeKeys = redisService.getKeysByPattern("loved:recipe:*");
        List<Long> recipeFeatured = new ArrayList<>();
        for (String key : loveRecipeKeys) {
            String idPart = key.split(":")[2];
            if ("all".equals(idPart))
                continue; // Bỏ qua key "loved:recipe:all"

            long recipeId = Long.parseLong(idPart);
            Set<String> accountIds = redisService.getLoverAccountIds(recipeId);
            Set<String> unlovingAccounts = redisService.getUnloverAccountIds(recipeId);

            if (accountIds == null || accountIds.isEmpty())
                continue;

            Set<String> filteredAccounts = accountIds.stream()
                    .filter(id -> unlovingAccounts == null || !unlovingAccounts.contains(id))
                    .collect(Collectors.toSet());

            if (filteredAccounts.size() >= 3) {
                recipeFeatured.add(recipeId);
            }
        }

        interactionProducer.sendRequireFeaturedRecipeIds(recipeFeatured);
        if (!recipeFeatured.isEmpty()) {
            log.info("ID RECIPE YEU CAU FEATURED THU NHAT: " + recipeFeatured.get(0));
        }
    }

    public List<RecipeResponse> getAccountSavedRecipe(long accountId, String accessToken) {
        List<Long> savedRecipeIdsDb = savedRecipeRepo.findByAccountIdSortDes(accountId);

        Set<String> savedIdsInCache = redisService.getSavedRecipeIds(accountId);
        Set<String> unsavedIdsInCache = redisService.getUnsavedRecipeIds(accountId);

        Set<Long> mergedSaved = new HashSet<>(savedRecipeIdsDb);
        mergedSaved.addAll(savedIdsInCache.stream().map(Long::parseLong).collect(Collectors.toSet()));

        Set<Long> unloved = unsavedIdsInCache.stream().map(Long::parseLong).collect(Collectors.toSet());
        mergedSaved.removeAll(unloved);

        List<Long> finalIdsToSend = new ArrayList<>(mergedSaved);
        return recipeClient.getRecipesByIds(finalIdsToSend, accessToken);
    }

    public List<RecipeResponse> getAccountLovedRecipes(long accountId, String accessToken) {
        List<Long> lovedRecipesIdsFromDB = lovedRecipeRepo.findIdsByAccountIdSortDes(accountId);//lay recipeIds trong db

        Set<String> lovedIdsInCache = redisService.getLovedRecipeIds(accountId);
        Set<String> unlovedIdsInCache = redisService.getUnlovedRecipeIds(accountId);

        // Merge loved từ DB + Redis
        Set<Long> mergedLoved = new HashSet<>(lovedRecipesIdsFromDB);
        mergedLoved.addAll(
                lovedIdsInCache.stream().map(Long::parseLong).collect(Collectors.toSet()));

        // Loại bỏ những cái vừa unlove
        Set<Long> unloved = unlovedIdsInCache.stream().map(Long::parseLong).collect(Collectors.toSet());
        mergedLoved.removeAll(unloved);

        List<Long> finalIdsToSend = new ArrayList<>(mergedLoved);
        return recipeClient.getRecipesByIds(finalIdsToSend, accessToken);
    }

    public void disableAccount(long accountId) {
        Account account = accountRepo.findById(accountId).orElse(null);
        if (account == null) {
            throw new ResourceNotFoundException("Account not found");
        }
        account.setStatus("disable");
        accountRepo.save(account);
    }

    public void toggleFollow(long followeeId, long followerId) {
        Account followee = accountRepo.findById(followeeId).orElse(null); // Nguoi duoc follow
        Account follower = accountRepo.findById(followerId).orElse(null); // Nguoi follow
        if (followee == null || follower == null) {
            throw new ResourceNotFoundException("Account not found");
        }
        if (redisService.isFollowedAccount(followeeId, followerId)) {
            redisService.unfollowAnAccount(followeeId, followerId);
            redisService.markUnfollow(followeeId, followerId);
        } else {
            redisService.followAnAccount(followeeId, followerId);
            redisService.removeUnfollowMark(followeeId, followerId);
        }
    }

    @Scheduled(fixedRate = 60000)
    public void reloadFollowList() {
        Set<String> followsSet = redisService.getKeysByPattern("follows:*");
        Set<String> unfollowSet = redisService.getKeysByPattern("unfollows:*");

        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(followsSet);
        allKeys.addAll(unfollowSet);

        for (String key : allKeys) {
            String followeeStr = key.split(":")[1];
            Long followeeId;
            try {
                followeeId = Long.parseLong(followeeStr);
            } catch (NumberFormatException e) {
                continue; // skip key lỗi
            }

            Optional<Account> followeeOpt = accountRepo.findById(followeeId);
            if (!followeeOpt.isPresent())
                continue;
            Account followee = followeeOpt.get();

            processFollowers(followee);
            processUnfollowers(followee);
        }
    }

    private void processFollowers(Account followee) {
        Set<String> followerIdsStr = redisService.getFollowerIdsInCache(followee.getId());
        for (String followerIdStr : followerIdsStr) {
            long followerId;
            try {
                followerId = Long.parseLong(followerIdStr);
            } catch (NumberFormatException e) {
                continue;
            }

            Optional<Account> followerOpt = accountRepo.findById(followerId);
            if (!followerOpt.isPresent())
                continue;
            Account follower = followerOpt.get();

            if (!followRepo.existsByFolloweeAndFollower(followee, follower)) {
                Follow follow = new Follow();
                follow.setFollowedAt(Timestamp.from(Instant.now()));
                follow.setFollowee(followee);
                follow.setFollower(follower);
                followRepo.save(follow);
            }

            redisService.removeFromCache("follows:" + followee.getId(), followerIdStr);
        }
    }

    private void processUnfollowers(Account followee) {
        Set<String> unfollowerIdsStr = redisService.getUnfollowerIdsInCache(followee.getId());
        for (String unfollowerIdStr : unfollowerIdsStr) {
            long unfollowerId;
            try {
                unfollowerId = Long.parseLong(unfollowerIdStr);
            } catch (NumberFormatException e) {
                continue;
            }

            Optional<Account> unfollowerOpt = accountRepo.findById(unfollowerId);
            if (!unfollowerOpt.isPresent())
                continue;
            Account unfollower = unfollowerOpt.get();

            followRepo.deleteByFollowerAndFollowee(unfollower, followee);
            redisService.removeFromCache("unfollows:" + followee.getId(), unfollowerIdStr);
        }
    }

    // Danh sach nhung nguoi dang theo doi "me"
    public List<AccountResponse> getAllFollowers(long accountId) {
        Account account = accountRepo.findById(accountId).orElse(null);
        if (account == null) {
            throw new ResourceNotFoundException("Account not found");
        }
        List<Account> followerAccounts = followMapper.followersToAccounts(account.getFollowers());

        List<Account> followersInCache = accountRepo.findByIdIn(
                redisService.getFollowerIdsInCache(accountId).stream()
                        .map(Long::valueOf)
                        .collect(Collectors.toList()));

        List<Account> unfollowersInCache = accountRepo.findByIdIn(
                redisService.getUnfollowerIdsInCache(accountId).stream()
                        .map(Long::parseLong)
                        .collect(Collectors.toList()));

        Set<Account> all = new HashSet<>();
        all.addAll(followersInCache);
        all.addAll(followerAccounts);

        all.removeAll(unfollowersInCache);

        return accountMapper.toAccountResponseList(new ArrayList<>(all));
    }

    // Danh sach nhung nguoi "me" dang theo doi
    public List<AccountResponse> getAllFollowees(long accountId) {
        Account account = accountRepo.findById(accountId).orElse(null);
        if (account == null) {
            throw new ResourceNotFoundException("Account not found");
        }
        List<Account> followeeAccounts = followMapper.followeesToAccounts(account.getFollowing());

        List<Account> followeesInCache = accountRepo.findByIdIn(
                redisService.getFolloweeIdsInCache(accountId).stream()
                        .map(Long::valueOf)
                        .collect(Collectors.toList()));

        List<Account> unfolloweesInCache = accountRepo.findByIdIn(
                redisService.getUnfolloweeIdsInCache(accountId).stream()
                        .map(Long::parseLong)
                        .collect(Collectors.toList()));

        Set<Account> all = new HashSet<>();
        all.addAll(followeesInCache);
        all.addAll(followeeAccounts);

        all.removeAll(unfolloweesInCache);

        return accountMapper.toAccountResponseList(new ArrayList<>(all));
    }

    public List<Long> getAllFolloweeIds(long accountId) {
        Account account = accountRepo.findById(accountId).orElse(null);
        if (account == null) {
            throw new ResourceNotFoundException("Account not found");
        }
        List<Account> followingAccounts = followMapper.followeesToAccounts(account.getFollowing());
        if (followingAccounts == null) {
            return new ArrayList<>();
        }
        List<Long> followeeIds = new ArrayList<>();
        for (Account followee : followingAccounts) {
            followeeIds.add(followee.getId());
        }
        log.info("followeeIds dau tien (interaction service): {}", followeeIds.get(0));
        if (followeeIds.isEmpty())
            return new ArrayList<>();
        return followeeIds;
    }

    public void deleteRecipeInteraction(long recipeId) {
        String recipeIdStr = String.valueOf(recipeId);

        // 1. Xóa khỏi cache
        Set<String> savedKeys = redisService.getKeysByPattern("saved:*");
        for (String key : savedKeys) {
            redisService.removeFromCache(key, recipeIdStr);
        }

        Set<String> unsavedKeys = redisService.getKeysByPattern("unsaved:*");
        for (String key : unsavedKeys) {
            redisService.removeFromCache(key, recipeIdStr);
        }

        Set<String> loveAccountKeys = redisService.getKeysByPattern("loved:account:*");
        for (String key : loveAccountKeys) {
            redisService.removeFromCache(key, recipeIdStr);
        }
        redisService.deleteValue("loved:recipe:" + recipeId);

        Set<String> unloveAccountKeys = redisService.getKeysByPattern("unloved:account:*");
        for (String key : unloveAccountKeys) {
            redisService.removeFromCache(key, recipeIdStr);
        }
        redisService.deleteValue("unloved:recipe:" + recipeId);

        // 2. Luôn xóa trong DB
        lovedRecipeRepo.deleteByRecipeId(recipeId);
        savedRecipeRepo.deleteByRecipeId(recipeId);
    }
}
