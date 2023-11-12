package com.ocean.service;

import com.ocean.domain.*; // for static metamodels
import com.ocean.domain.Like;
import com.ocean.repository.LikeRepository;
import com.ocean.service.criteria.LikeCriteria;
import com.ocean.service.dto.LikeDTO;
import com.ocean.service.mapper.LikeMapper;
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
 * Service for executing complex queries for {@link Like} entities in the database.
 * The main input is a {@link LikeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link LikeDTO} or a {@link Page} of {@link LikeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LikeQueryService extends QueryService<Like> {

    private final Logger log = LoggerFactory.getLogger(LikeQueryService.class);

    private final LikeRepository likeRepository;

    private final LikeMapper likeMapper;

    public LikeQueryService(LikeRepository likeRepository, LikeMapper likeMapper) {
        this.likeRepository = likeRepository;
        this.likeMapper = likeMapper;
    }

    /**
     * Return a {@link List} of {@link LikeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<LikeDTO> findByCriteria(LikeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Like> specification = createSpecification(criteria);
        return likeMapper.toDto(likeRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link LikeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<LikeDTO> findByCriteria(LikeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Like> specification = createSpecification(criteria);
        return likeRepository.findAll(specification, page).map(likeMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(LikeCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Like> specification = createSpecification(criteria);
        return likeRepository.count(specification);
    }

    /**
     * Function to convert {@link LikeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Like> createSpecification(LikeCriteria criteria) {
        Specification<Like> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Like_.id));
            }
            if (criteria.getTimeStamp() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTimeStamp(), Like_.timeStamp));
            }
            if (criteria.getStudentId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getStudentId(), root -> root.join(Like_.student, JoinType.LEFT).get(Student_.id))
                    );
            }
            if (criteria.getRatingId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getRatingId(), root -> root.join(Like_.rating, JoinType.LEFT).get(Rating_.id))
                    );
            }
        }
        return specification;
    }
}
