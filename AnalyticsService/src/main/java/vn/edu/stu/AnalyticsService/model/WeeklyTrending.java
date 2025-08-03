package vn.edu.stu.AnalyticsService.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WeeklyTrending {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private int week;

    @Column
    private int year;

    @OneToMany(mappedBy = "weeklyTrending" , cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<WeeklyAccount> weeklyAccounts;//Những account tương tác nhiều trong tuần

    @OneToMany(mappedBy = "weeklyTrending", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<WeeklyRecipe> weeklyRecipes;
}
