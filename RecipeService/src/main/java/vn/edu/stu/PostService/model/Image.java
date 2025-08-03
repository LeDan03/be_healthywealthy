package vn.edu.stu.PostService.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String url;

    @Column
    private String publicId;

    public Image(String url) {
        this.url = url;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recipeId", nullable = false, referencedColumnName = "id")
    @JsonBackReference
    private Recipe recipe;
}
