package com.ocean.service.mapper;

import com.ocean.domain.Rating;
import com.ocean.domain.Student;
import com.ocean.domain.Teacher;
import com.ocean.service.dto.RatingDTO;
import com.ocean.service.dto.StudentDTO;
import com.ocean.service.dto.TeacherDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Rating} and its DTO {@link RatingDTO}.
 */
@Mapper(componentModel = "spring")
public interface RatingMapper extends EntityMapper<RatingDTO, Rating> {
    @Mapping(target = "teacher", source = "teacher", qualifiedByName = "teacherId")
    @Mapping(target = "student", source = "student", qualifiedByName = "studentId")
    RatingDTO toDto(Rating s);

    @Named("teacherId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TeacherDTO toDtoTeacherId(Teacher teacher);

    @Named("studentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    StudentDTO toDtoStudentId(Student student);
}
