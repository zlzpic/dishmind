package com.bdu.dishmind.repository;

import com.bdu.dishmind.entity.UserTagPreference;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface UserTagPreferenceRepository extends JpaRepository<UserTagPreference, Long> {

    Optional<UserTagPreference> findByUserIdAndTagId(Long userId, Integer tagId);

    List<UserTagPreference> findTopByUserIdOrderByScoreDesc(Long userId, Pageable pageable);

    List<UserTagPreference> findByLastInteractionBefore(LocalDateTime time);

    @Query("SELECT p FROM UserTagPreference p WHERE p.userId = :userId AND p.tagId IN :tagIds")
    List<UserTagPreference> findByUserIdAndTagIdIn(@Param("userId") Long userId, @Param("tagIds") List<Integer> tagIds);
}
