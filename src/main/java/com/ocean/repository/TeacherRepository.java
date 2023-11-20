package com.ocean.repository;

import com.ocean.domain.Teacher;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Teacher entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long>, JpaSpecificationExecutor<Teacher> {
    Optional<List<Teacher>> findByTeacherCode(String teacherCode);

    Optional<List<Teacher>> findByFirstName(String teacherFirstName);

    Optional<List<Teacher>> findByTeacherCodeAndFirstName(String teacherCode, String teacherFirstName);
}
