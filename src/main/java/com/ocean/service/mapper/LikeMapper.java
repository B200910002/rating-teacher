package com.ocean.service.mapper;

import com.ocean.domain.Like;
import com.ocean.domain.Rating;
import com.ocean.domain.Student;
import com.ocean.service.dto.LikeDTO;
import com.ocean.service.dto.RatingDTO;
import com.ocean.service.dto.StudentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Like} and its DTO {@link LikeDTO}.
 */
@Mapper(componentModel = "spring")
public interface LikeMapper extends EntityMapper<LikeDTO, Like> {
    @Mapping(target = "student", source = "student", qualifiedByName = "studentId")
    @Mapping(target = "rating", source = "rating", qualifiedByName = "ratingId")
    LikeDTO toDto(Like s);

    @Named("studentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    StudentDTO toDtoStudentId(Student student);

    @Named("ratingId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RatingDTO toDtoRatingId(Rating rating);
}
