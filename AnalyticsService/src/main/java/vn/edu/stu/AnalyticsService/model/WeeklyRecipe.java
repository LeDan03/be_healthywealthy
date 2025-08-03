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
public class WeeklyRecipe {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private long recipeId;

    @Column
    private int loveCount;

    @Column
    private int rank;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "weeklyTrendingId")
    private WeeklyTrending weeklyTrending;

}
