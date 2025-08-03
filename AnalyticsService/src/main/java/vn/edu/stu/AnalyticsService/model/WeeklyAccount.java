package vn.edu.stu.AnalyticsService.model;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class WeeklyAccount {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private long accountId;

    @Column
    private int totalLoveAction; //tất cả lượt yêu thích account thực hiện

    @Column
    private int rank;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "weeklyTrendingId")
    private WeeklyTrending weeklyTrending;
}
