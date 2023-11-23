package com.ocean.web.rest;

import com.ocean.repository.LessonRepository;
import com.ocean.service.LessonQueryService;
import com.ocean.service.LessonService;
import com.ocean.service.criteria.LessonCriteria;
import com.ocean.service.dto.LessonDTO;
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
 * REST controller for managing {@link com.ocean.domain.Lesson}.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class LessonResource {

    private final Logger log = LoggerFactory.getLogger(LessonResource.class);

    private static final String ENTITY_NAME = "lesson";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LessonService lessonService;

    private final LessonRepository lessonRepository;

    private final LessonQueryService lessonQueryService;

    public LessonResource(LessonService lessonService, LessonRepository lessonRepository, LessonQueryService lessonQueryService) {
        this.lessonService = lessonService;
        this.lessonRepository = lessonRepository;
        this.lessonQueryService = lessonQueryService;
    }

    /**
     * {@code POST  /lessons} : Create a new lesson.
     *
     * @param lessonDTO the lessonDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new lessonDTO, or with status {@code 400 (Bad Request)} if the lesson has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @CrossOrigin(origins = "*")
    @PostMapping("/lessons")
    public ResponseEntity<LessonDTO> createLesson(@RequestBody LessonDTO lessonDTO) throws URISyntaxException {
        log.debug("REST request to save Lesson : {}", lessonDTO);
        if (lessonDTO.getId() != null) {
            throw new BadRequestAlertException("A new lesson cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LessonDTO result = lessonService.save(lessonDTO);
        return ResponseEntity
            .created(new URI("/api/lessons/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /lessons/:id} : Updates an existing lesson.
     *
     * @param id the id of the lessonDTO to save.
     * @param lessonDTO the lessonDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated lessonDTO,
     * or with status {@code 400 (Bad Request)} if the lessonDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the lessonDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @CrossOrigin(origins = "*")
    @PutMapping("/lessons/{id}")
    public ResponseEntity<LessonDTO> updateLesson(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody LessonDTO lessonDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Lesson : {}, {}", id, lessonDTO);
        if (lessonDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, lessonDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!lessonRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        LessonDTO result = lessonService.update(lessonDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, lessonDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /lessons/:id} : Partial updates given fields of an existing lesson, field will ignore if it is null
     *
     * @param id the id of the lessonDTO to save.
     * @param lessonDTO the lessonDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated lessonDTO,
     * or with status {@code 400 (Bad Request)} if the lessonDTO is not valid,
     * or with status {@code 404 (Not Found)} if the lessonDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the lessonDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @CrossOrigin(origins = "*")
    @PatchMapping(value = "/lessons/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LessonDTO> partialUpdateLesson(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody LessonDTO lessonDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Lesson partially : {}, {}", id, lessonDTO);
        if (lessonDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, lessonDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!lessonRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LessonDTO> result = lessonService.partialUpdate(lessonDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, lessonDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /lessons} : get all the lessons.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of lessons in body.
     */
    @CrossOrigin(origins = "*")
    @GetMapping("/lessons")
    public ResponseEntity<List<LessonDTO>> getAllLessons(LessonCriteria criteria) {
        log.debug("REST request to get Lessons by criteria: {}", criteria);
        List<LessonDTO> entityList = lessonQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /lessons/count} : count all the lessons.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @CrossOrigin(origins = "*")
    @GetMapping("/lessons/count")
    public ResponseEntity<Long> countLessons(LessonCriteria criteria) {
        log.debug("REST request to count Lessons by criteria: {}", criteria);
        return ResponseEntity.ok().body(lessonQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /lessons/:id} : get the "id" lesson.
     *
     * @param id the id of the lessonDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the lessonDTO, or with status {@code 404 (Not Found)}.
     */
    @CrossOrigin(origins = "*")
    @GetMapping("/lessons/{id}")
    public ResponseEntity<LessonDTO> getLesson(@PathVariable Long id) {
        log.debug("REST request to get Lesson : {}", id);
        Optional<LessonDTO> lessonDTO = lessonService.findOne(id);
        return ResponseUtil.wrapOrNotFound(lessonDTO);
    }

    /**
     * {@code DELETE  /lessons/:id} : delete the "id" lesson.
     *
     * @param id the id of the lessonDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @CrossOrigin(origins = "*")
    @DeleteMapping("/lessons/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long id) {
        log.debug("REST request to delete Lesson : {}", id);
        lessonService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
