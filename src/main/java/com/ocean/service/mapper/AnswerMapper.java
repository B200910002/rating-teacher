package com.ocean.service.mapper;

import com.ocean.domain.Answer;
import com.ocean.domain.Question;
import com.ocean.service.dto.AnswerDTO;
import com.ocean.service.dto.QuestionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Answer} and its DTO {@link AnswerDTO}.
 */
@Mapper(componentModel = "spring")
public interface AnswerMapper extends EntityMapper<AnswerDTO, Answer> {
    @Mapping(target = "question", source = "question", qualifiedByName = "questionId")
    AnswerDTO toDto(Answer s);

    @Named("questionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    QuestionDTO toDtoQuestionId(Question question);
}
