package com.ocean.service;

import com.ocean.domain.*; // for static metamodels
import com.ocean.domain.Schedule;
import com.ocean.repository.ScheduleRepository;
import com.ocean.service.criteria.ScheduleCriteria;
import com.ocean.service.dto.ScheduleDTO;
import com.ocean.service.mapper.ScheduleMapper;
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
 * Service for executing complex queries for {@link Schedule} entities in the database.
 * The main input is a {@link ScheduleCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ScheduleDTO} or a {@link Page} of {@link ScheduleDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ScheduleQueryService extends QueryService<Schedule> {

    private final Logger log = LoggerFactory.getLogger(ScheduleQueryService.class);

    private final ScheduleRepository scheduleRepository;

    private final ScheduleMapper scheduleMapper;

    public ScheduleQueryService(ScheduleRepository scheduleRepository, ScheduleMapper scheduleMapper) {
        this.scheduleRepository = scheduleRepository;
        this.scheduleMapper = scheduleMapper;
    }

    /**
     * Return a {@link List} of {@link ScheduleDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ScheduleDTO> findByCriteria(ScheduleCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Schedule> specification = createSpecification(criteria);
        return scheduleMapper.toDto(scheduleRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ScheduleDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ScheduleDTO> findByCriteria(ScheduleCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Schedule> specification = createSpecification(criteria);
        return scheduleRepository.findAll(specification, page).map(scheduleMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ScheduleCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Schedule> specification = createSpecification(criteria);
        return scheduleRepository.count(specification);
    }

    /**
     * Function to convert {@link ScheduleCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Schedule> createSpecification(ScheduleCriteria criteria) {
        Specification<Schedule> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Schedule_.id));
            }
            if (criteria.getLessonType() != null) {
                specification = specification.and(buildSpecification(criteria.getLessonType(), Schedule_.lessonType));
            }
            if (criteria.getWeekDay() != null) {
                specification = specification.and(buildSpecification(criteria.getWeekDay(), Schedule_.weekDay));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), Schedule_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), Schedule_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), Schedule_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), Schedule_.lastModifiedDate));
            }
            if (criteria.getRoomId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getRoomId(), root -> root.join(Schedule_.room, JoinType.LEFT).get(Room_.id))
                    );
            }
            if (criteria.getLessonTimeId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getLessonTimeId(),
                            root -> root.join(Schedule_.lessonTime, JoinType.LEFT).get(LessonTime_.id)
                        )
                    );
            }
            if (criteria.getLessonId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getLessonId(), root -> root.join(Schedule_.lesson, JoinType.LEFT).get(Lesson_.id))
                    );
            }
            if (criteria.getTeacherId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getTeacherId(), root -> root.join(Schedule_.teacher, JoinType.LEFT).get(Teacher_.id))
                    );
            }
        }
        return specification;
    }
}
