package com.bdu.dishmind.entity;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "recipe_tag")
public class RecipeTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recipe_id", nullable = false)
    private Long recipeId;

    @Column(name = "tag_id", nullable = false)
    private Integer tagId;

    @Column(name = "weight", precision = 3, scale = 2)
    private BigDecimal weight;
}
