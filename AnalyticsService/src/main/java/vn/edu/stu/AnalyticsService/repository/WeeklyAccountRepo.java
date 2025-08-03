package vn.edu.stu.AnalyticsService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import vn.edu.stu.AnalyticsService.model.WeeklyAccount;

@Repository
@Transactional
public interface WeeklyAccountRepo extends JpaRepository<WeeklyAccount, Long> {
    
}
