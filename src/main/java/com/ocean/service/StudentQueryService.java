package com.ocean.service;

import com.ocean.domain.*; // for static metamodels
import com.ocean.domain.Student;
import com.ocean.repository.StudentRepository;
import com.ocean.service.criteria.StudentCriteria;
import com.ocean.service.dto.StudentDTO;
import com.ocean.service.mapper.StudentMapper;
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
 * Service for executing complex queries for {@link Student} entities in the database.
 * The main input is a {@link StudentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link StudentDTO} or a {@link Page} of {@link StudentDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class StudentQueryService extends QueryService<Student> {

    private final Logger log = LoggerFactory.getLogger(StudentQueryService.class);

    private final StudentRepository studentRepository;

    private final StudentMapper studentMapper;

    public StudentQueryService(StudentRepository studentRepository, StudentMapper studentMapper) {
        this.studentRepository = studentRepository;
        this.studentMapper = studentMapper;
    }

    /**
     * Return a {@link List} of {@link StudentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<StudentDTO> findByCriteria(StudentCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Student> specification = createSpecification(criteria);
        return studentMapper.toDto(studentRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link StudentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<StudentDTO> findByCriteria(StudentCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Student> specification = createSpecification(criteria);
        return studentRepository.findAll(specification, page).map(studentMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(StudentCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Student> specification = createSpecification(criteria);
        return studentRepository.count(specification);
    }

    /**
     * Function to convert {@link StudentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Student> createSpecification(StudentCriteria criteria) {
        Specification<Student> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Student_.id));
            }
            if (criteria.getFirstName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFirstName(), Student_.firstName));
            }
            if (criteria.getLastName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastName(), Student_.lastName));
            }
            if (criteria.getStudentCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getStudentCode(), Student_.studentCode));
            }
            if (criteria.getEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmail(), Student_.email));
            }
            if (criteria.getPhone() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhone(), Student_.phone));
            }
            if (criteria.getLoginType() != null) {
                specification = specification.and(buildSpecification(criteria.getLoginType(), Student_.loginType));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), Student_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), Student_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), Student_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), Student_.lastModifiedDate));
            }
        }
        return specification;
    }
}
