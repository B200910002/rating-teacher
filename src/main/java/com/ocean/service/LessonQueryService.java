package com.ocean.service;

import com.ocean.domain.*; // for static metamodels
import com.ocean.domain.Lesson;
import com.ocean.repository.LessonRepository;
import com.ocean.service.criteria.LessonCriteria;
import com.ocean.service.dto.LessonDTO;
import com.ocean.service.mapper.LessonMapper;
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
 * Service for executing complex queries for {@link Lesson} entities in the database.
 * The main input is a {@link LessonCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link LessonDTO} or a {@link Page} of {@link LessonDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LessonQueryService extends QueryService<Lesson> {

    private final Logger log = LoggerFactory.getLogger(LessonQueryService.class);

    private final LessonRepository lessonRepository;

    private final LessonMapper lessonMapper;

    public LessonQueryService(LessonRepository lessonRepository, LessonMapper lessonMapper) {
        this.lessonRepository = lessonRepository;
        this.lessonMapper = lessonMapper;
    }

    /**
     * Return a {@link List} of {@link LessonDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<LessonDTO> findByCriteria(LessonCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Lesson> specification = createSpecification(criteria);
        return lessonMapper.toDto(lessonRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link LessonDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<LessonDTO> findByCriteria(LessonCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Lesson> specification = createSpecification(criteria);
        return lessonRepository.findAll(specification, page).map(lessonMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(LessonCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Lesson> specification = createSpecification(criteria);
        return lessonRepository.count(specification);
    }

    /**
     * Function to convert {@link LessonCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Lesson> createSpecification(LessonCriteria criteria) {
        Specification<Lesson> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Lesson_.id));
            }
            if (criteria.getLessonName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLessonName(), Lesson_.lessonName));
            }
            if (criteria.getLessonCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLessonCode(), Lesson_.lessonCode));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), Lesson_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), Lesson_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), Lesson_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), Lesson_.lastModifiedDate));
            }
        }
        return specification;
    }
}
