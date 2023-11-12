package com.ocean.service.mapper;

import com.ocean.domain.Lesson;
import com.ocean.service.dto.LessonDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Lesson} and its DTO {@link LessonDTO}.
 */
@Mapper(componentModel = "spring")
public interface LessonMapper extends EntityMapper<LessonDTO, Lesson> {}
