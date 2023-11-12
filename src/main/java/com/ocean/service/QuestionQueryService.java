package com.ocean.service;

import com.ocean.domain.*; // for static metamodels
import com.ocean.domain.Question;
import com.ocean.repository.QuestionRepository;
import com.ocean.service.criteria.QuestionCriteria;
import com.ocean.service.dto.QuestionDTO;
import com.ocean.service.mapper.QuestionMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Question} entities in the database.
 * The main input is a {@link QuestionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link QuestionDTO} or a {@link Page} of {@link QuestionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class QuestionQueryService extends QueryService<Question> {

    private final Logger log = LoggerFactory.getLogger(QuestionQueryService.class);

    private final QuestionRepository questionRepository;

    private final QuestionMapper questionMapper;

    public QuestionQueryService(QuestionRepository questionRepository, QuestionMapper questionMapper) {
        this.questionRepository = questionRepository;
        this.questionMapper = questionMapper;
    }

    /**
     * Return a {@link List} of {@link QuestionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<QuestionDTO> findByCriteria(QuestionCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Question> specification = createSpecification(criteria);
        return questionMapper.toDto(questionRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link QuestionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<QuestionDTO> findByCriteria(QuestionCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Question> specification = createSpecification(criteria);
        return questionRepository.findAll(specification, page).map(questionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(QuestionCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Question> specification = createSpecification(criteria);
        return questionRepository.count(specification);
    }

    /**
     * Function to convert {@link QuestionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Question> createSpecification(QuestionCriteria criteria) {
        Specification<Question> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Question_.id));
            }
            if (criteria.getQuestion() != null) {
                specification = specification.and(buildStringSpecification(criteria.getQuestion(), Question_.question));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), Question_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), Question_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), Question_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), Question_.lastModifiedDate));
            }
            if (criteria.getAnswerId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getAnswerId(), root -> root.join(Question_.answers, JoinType.LEFT).get(Answer_.id))
                    );
            }
            if (criteria.getRatingId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getRatingId(), root -> root.join(Question_.rating, JoinType.LEFT).get(Rating_.id))
                    );
            }
        }
        return specification;
    }
}
