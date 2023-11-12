package com.ocean.service;

import com.ocean.domain.*; // for static metamodels
import com.ocean.domain.LessonTime;
import com.ocean.repository.LessonTimeRepository;
import com.ocean.service.criteria.LessonTimeCriteria;
import com.ocean.service.dto.LessonTimeDTO;
import com.ocean.service.mapper.LessonTimeMapper;
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
 * Service for executing complex queries for {@link LessonTime} entities in the database.
 * The main input is a {@link LessonTimeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link LessonTimeDTO} or a {@link Page} of {@link LessonTimeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LessonTimeQueryService extends QueryService<LessonTime> {

    private final Logger log = LoggerFactory.getLogger(LessonTimeQueryService.class);

    private final LessonTimeRepository lessonTimeRepository;

    private final LessonTimeMapper lessonTimeMapper;

    public LessonTimeQueryService(LessonTimeRepository lessonTimeRepository, LessonTimeMapper lessonTimeMapper) {
        this.lessonTimeRepository = lessonTimeRepository;
        this.lessonTimeMapper = lessonTimeMapper;
    }

    /**
     * Return a {@link List} of {@link LessonTimeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<LessonTimeDTO> findByCriteria(LessonTimeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<LessonTime> specification = createSpecification(criteria);
        return lessonTimeMapper.toDto(lessonTimeRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link LessonTimeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<LessonTimeDTO> findByCriteria(LessonTimeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<LessonTime> specification = createSpecification(criteria);
        return lessonTimeRepository.findAll(specification, page).map(lessonTimeMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(LessonTimeCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<LessonTime> specification = createSpecification(criteria);
        return lessonTimeRepository.count(specification);
    }

    /**
     * Function to convert {@link LessonTimeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<LessonTime> createSpecification(LessonTimeCriteria criteria) {
        Specification<LessonTime> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), LessonTime_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), LessonTime_.name));
            }
            if (criteria.getStartAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStartAt(), LessonTime_.startAt));
            }
            if (criteria.getEndAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEndAt(), LessonTime_.endAt));
            }
        }
        return specification;
    }
}
