package vn.edu.stu.WebBlogNauAn.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"followerId", "followeeId"}))
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followerId")
    private Account follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followeeId")
    private Account followee;

    @Column
    private Timestamp followedAt;
}
