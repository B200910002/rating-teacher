package com.ocean.web.rest;

import com.ocean.repository.TeacherRepository;
import com.ocean.service.TeacherQueryService;
import com.ocean.service.TeacherService;
import com.ocean.service.criteria.TeacherCriteria;
import com.ocean.service.dto.TeacherDTO;
import com.ocean.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.ocean.domain.Teacher}.
 */

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class TeacherResource {

    private final Logger log = LoggerFactory.getLogger(TeacherResource.class);

    private static final String ENTITY_NAME = "teacher";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TeacherService teacherService;

    private final TeacherRepository teacherRepository;

    private final TeacherQueryService teacherQueryService;

    public TeacherResource(TeacherService teacherService, TeacherRepository teacherRepository, TeacherQueryService teacherQueryService) {
        this.teacherService = teacherService;
        this.teacherRepository = teacherRepository;
        this.teacherQueryService = teacherQueryService;
    }

    /**
     * {@code POST  /teachers} : Create a new teacher.
     *
     * @param teacherDTO the teacherDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new teacherDTO, or with status {@code 400 (Bad Request)} if the teacher has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @CrossOrigin(origins = "*")
    @PostMapping("/teachers")
    public ResponseEntity<TeacherDTO> createTeacher(@RequestBody TeacherDTO teacherDTO) throws URISyntaxException {
        log.debug("REST request to save Teacher : {}", teacherDTO);
        if (teacherDTO.getId() != null) {
            throw new BadRequestAlertException("A new teacher cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TeacherDTO result = teacherService.save(teacherDTO);
        return ResponseEntity
            .created(new URI("/api/teachers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /teachers/:id} : Updates an existing teacher.
     *
     * @param id the id of the teacherDTO to save.
     * @param teacherDTO the teacherDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated teacherDTO,
     * or with status {@code 400 (Bad Request)} if the teacherDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the teacherDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @CrossOrigin(origins = "*")
    @PutMapping("/teachers/{id}")
    public ResponseEntity<TeacherDTO> updateTeacher(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TeacherDTO teacherDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Teacher : {}, {}", id, teacherDTO);
        if (teacherDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, teacherDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!teacherRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TeacherDTO result = teacherService.update(teacherDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, teacherDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /teachers/:id} : Partial updates given fields of an existing teacher, field will ignore if it is null
     *
     * @param id the id of the teacherDTO to save.
     * @param teacherDTO the teacherDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated teacherDTO,
     * or with status {@code 400 (Bad Request)} if the teacherDTO is not valid,
     * or with status {@code 404 (Not Found)} if the teacherDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the teacherDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @CrossOrigin(origins = "*")
    @PatchMapping(value = "/teachers/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TeacherDTO> partialUpdateTeacher(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TeacherDTO teacherDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Teacher partially : {}, {}", id, teacherDTO);
        if (teacherDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, teacherDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!teacherRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TeacherDTO> result = teacherService.partialUpdate(teacherDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, teacherDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /teachers} : get all the teachers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of teachers in body.
     */
    @CrossOrigin(origins = "*")
    @GetMapping("/teachers")
    public ResponseEntity<List<TeacherDTO>> getAllTeachers(TeacherCriteria criteria) {
        log.debug("REST request to get Teachers by criteria: {}", criteria);
        List<TeacherDTO> entityList = teacherQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /teachers/count} : count all the teachers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @CrossOrigin(origins = "*")
    @GetMapping("/teachers/count")
    public ResponseEntity<Long> countTeachers(TeacherCriteria criteria) {
        log.debug("REST request to count Teachers by criteria: {}", criteria);
        return ResponseEntity.ok().body(teacherQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /teachers/:id} : get the "id" teacher.
     *
     * @param id the id of the teacherDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the teacherDTO, or with status {@code 404 (Not Found)}.
     */
    @CrossOrigin(origins = "*")
    @GetMapping("/teachers/{id}")
    public ResponseEntity<TeacherDTO> getTeacher(@PathVariable Long id) {
        log.debug("REST request to get Teacher : {}", id);
        Optional<TeacherDTO> teacherDTO = teacherService.findOne(id);
        return ResponseUtil.wrapOrNotFound(teacherDTO);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/teacher-by")
    public ResponseEntity<List<TeacherDTO>> getTeacher(
        @RequestParam(name = "teacherCode", required = false) String teacherCode,
        @RequestParam(name = "teacherName", required = false) String teacherName
    ) {
        log.debug("REST request to get Teacher by Code: {} or Name: {}", teacherCode, teacherName);

        Optional<List<TeacherDTO>> teacherDTO = Optional.empty();
        if (teacherCode != null && teacherName != null) {
            teacherDTO = teacherService.findByCodeAndName(teacherCode, teacherName);
        } else if (teacherCode != null) {
            teacherDTO = teacherService.findByCode(teacherCode);
        } else if (teacherName != null) {
            teacherDTO = teacherService.findByName(teacherName);
        }

        return ResponseUtil.wrapOrNotFound(teacherDTO);
    }

    /**
     * {@code DELETE  /teachers/:id} : delete the "id" teacher.
     *
     * @param id the id of the teacherDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @CrossOrigin(origins = "*")
    @DeleteMapping("/teachers/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        log.debug("REST request to delete Teacher : {}", id);
        teacherService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
