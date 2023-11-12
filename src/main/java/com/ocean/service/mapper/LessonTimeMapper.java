package com.ocean.service.mapper;

import com.ocean.domain.LessonTime;
import com.ocean.service.dto.LessonTimeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link LessonTime} and its DTO {@link LessonTimeDTO}.
 */
@Mapper(componentModel = "spring")
public interface LessonTimeMapper extends EntityMapper<LessonTimeDTO, LessonTime> {}
