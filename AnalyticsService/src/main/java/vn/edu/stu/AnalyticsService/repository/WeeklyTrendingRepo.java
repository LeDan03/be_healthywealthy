package vn.edu.stu.AnalyticsService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import vn.edu.stu.AnalyticsService.model.WeeklyTrending;

@Repository
@Transactional
public interface WeeklyTrendingRepo extends JpaRepository<WeeklyTrending, Long>{
    
    Optional<WeeklyTrending> findByWeekAndYear(int week, int year);
}
