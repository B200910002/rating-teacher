package com.ocean.service.mapper;

import com.ocean.domain.Question;
import com.ocean.domain.Rating;
import com.ocean.service.dto.QuestionDTO;
import com.ocean.service.dto.RatingDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Question} and its DTO {@link QuestionDTO}.
 */
@Mapper(componentModel = "spring")
public interface QuestionMapper extends EntityMapper<QuestionDTO, Question> {
    @Mapping(target = "rating", source = "rating", qualifiedByName = "ratingId")
    QuestionDTO toDto(Question s);

    @Named("ratingId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RatingDTO toDtoRatingId(Rating rating);
}
