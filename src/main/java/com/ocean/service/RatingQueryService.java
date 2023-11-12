package com.ocean.service;

import com.ocean.domain.*; // for static metamodels
import com.ocean.domain.Rating;
import com.ocean.repository.RatingRepository;
import com.ocean.service.criteria.RatingCriteria;
import com.ocean.service.dto.RatingDTO;
import com.ocean.service.mapper.RatingMapper;
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
 * Service for executing complex queries for {@link Rating} entities in the database.
 * The main input is a {@link RatingCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link RatingDTO} or a {@link Page} of {@link RatingDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RatingQueryService extends QueryService<Rating> {

    private final Logger log = LoggerFactory.getLogger(RatingQueryService.class);

    private final RatingRepository ratingRepository;

    private final RatingMapper ratingMapper;

    public RatingQueryService(RatingRepository ratingRepository, RatingMapper ratingMapper) {
        this.ratingRepository = ratingRepository;
        this.ratingMapper = ratingMapper;
    }

    /**
     * Return a {@link List} of {@link RatingDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<RatingDTO> findByCriteria(RatingCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Rating> specification = createSpecification(criteria);
        return ratingMapper.toDto(ratingRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link RatingDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<RatingDTO> findByCriteria(RatingCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Rating> specification = createSpecification(criteria);
        return ratingRepository.findAll(specification, page).map(ratingMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RatingCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Rating> specification = createSpecification(criteria);
        return ratingRepository.count(specification);
    }

    /**
     * Function to convert {@link RatingCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Rating> createSpecification(RatingCriteria criteria) {
        Specification<Rating> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Rating_.id));
            }
            if (criteria.getScore() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getScore(), Rating_.score));
            }
            if (criteria.getReview() != null) {
                specification = specification.and(buildStringSpecification(criteria.getReview(), Rating_.review));
            }
            if (criteria.getLessonName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLessonName(), Rating_.lessonName));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), Rating_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), Rating_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), Rating_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), Rating_.lastModifiedDate));
            }
            if (criteria.getLikeId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getLikeId(), root -> root.join(Rating_.likes, JoinType.LEFT).get(Like_.id))
                    );
            }
            if (criteria.getQuestionId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getQuestionId(), root -> root.join(Rating_.questions, JoinType.LEFT).get(Question_.id))
                    );
            }
            if (criteria.getTeacherId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getTeacherId(), root -> root.join(Rating_.teacher, JoinType.LEFT).get(Teacher_.id))
                    );
            }
            if (criteria.getStudentId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getStudentId(), root -> root.join(Rating_.student, JoinType.LEFT).get(Student_.id))
                    );
            }
        }
        return specification;
    }
}
