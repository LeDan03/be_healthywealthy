package vn.edu.stu.WebBlogNauAn.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true) //gọi equals, hash của thằng cha
public class CommunityMessage extends Message {
    
    @Column
    private Boolean pinned;
}
