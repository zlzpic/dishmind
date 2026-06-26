package com.bdu.dishmind.repository;

import com.bdu.dishmind.entity.TagCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagCategoryRepository extends JpaRepository<TagCategory, Integer> {
    boolean existsByName(String name);
}
