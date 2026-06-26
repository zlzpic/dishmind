package com.bdu.dishmind.entity;

import lombok.Data;
import javax.persistence.*;


@Data
@Entity
@Table(name = "tag_category")
public class TagCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", unique = true, nullable = false, length = 20)
    private String name;
}
