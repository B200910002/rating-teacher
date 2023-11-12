package com.ocean.repository;

import com.ocean.domain.LessonTime;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the LessonTime entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LessonTimeRepository extends JpaRepository<LessonTime, Long>, JpaSpecificationExecutor<LessonTime> {}
