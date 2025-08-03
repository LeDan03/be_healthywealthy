package vn.edu.stu.PostService.model;

import jakarta.persistence.*;
import lombok.*;
import vn.edu.stu.PostService.customSerilizer.RecipeIdOnlySerializer;

import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column
    private String imageUrl;

    @Column
    private String imagePublicId;

    @Column
    private Timestamp createdAt;

    @Column
    private long accountId;

    @ManyToOne
    @JoinColumn(name = "parentId")
    @JsonBackReference
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Comment> replies;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "recipeId", nullable = false, referencedColumnName = "id")
    @JsonSerialize(using = RecipeIdOnlySerializer.class)
    private Recipe recipe;
}
