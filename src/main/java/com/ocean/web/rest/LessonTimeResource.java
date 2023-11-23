package com.ocean.web.rest;

import com.ocean.repository.LessonTimeRepository;
import com.ocean.service.LessonTimeQueryService;
import com.ocean.service.LessonTimeService;
import com.ocean.service.criteria.LessonTimeCriteria;
import com.ocean.service.dto.LessonTimeDTO;
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
 * REST controller for managing {@link com.ocean.domain.LessonTime}.
 */

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class LessonTimeResource {

    private final Logger log = LoggerFactory.getLogger(LessonTimeResource.class);

    private static final String ENTITY_NAME = "lessonTime";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LessonTimeService lessonTimeService;

    private final LessonTimeRepository lessonTimeRepository;

    private final LessonTimeQueryService lessonTimeQueryService;

    public LessonTimeResource(
        LessonTimeService lessonTimeService,
        LessonTimeRepository lessonTimeRepository,
        LessonTimeQueryService lessonTimeQueryService
    ) {
        this.lessonTimeService = lessonTimeService;
        this.lessonTimeRepository = lessonTimeRepository;
        this.lessonTimeQueryService = lessonTimeQueryService;
    }

    /**
     * {@code POST  /lesson-times} : Create a new lessonTime.
     *
     * @param lessonTimeDTO the lessonTimeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new lessonTimeDTO, or with status {@code 400 (Bad Request)} if the lessonTime has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/lesson-times")
    public ResponseEntity<LessonTimeDTO> createLessonTime(@RequestBody LessonTimeDTO lessonTimeDTO) throws URISyntaxException {
        log.debug("REST request to save LessonTime : {}", lessonTimeDTO);
        if (lessonTimeDTO.getId() != null) {
            throw new BadRequestAlertException("A new lessonTime cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LessonTimeDTO result = lessonTimeService.save(lessonTimeDTO);
        return ResponseEntity
            .created(new URI("/api/lesson-times/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /lesson-times/:id} : Updates an existing lessonTime.
     *
     * @param id the id of the lessonTimeDTO to save.
     * @param lessonTimeDTO the lessonTimeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated lessonTimeDTO,
     * or with status {@code 400 (Bad Request)} if the lessonTimeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the lessonTimeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/lesson-times/{id}")
    public ResponseEntity<LessonTimeDTO> updateLessonTime(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody LessonTimeDTO lessonTimeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update LessonTime : {}, {}", id, lessonTimeDTO);
        if (lessonTimeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, lessonTimeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!lessonTimeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        LessonTimeDTO result = lessonTimeService.update(lessonTimeDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, lessonTimeDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /lesson-times/:id} : Partial updates given fields of an existing lessonTime, field will ignore if it is null
     *
     * @param id the id of the lessonTimeDTO to save.
     * @param lessonTimeDTO the lessonTimeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated lessonTimeDTO,
     * or with status {@code 400 (Bad Request)} if the lessonTimeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the lessonTimeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the lessonTimeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/lesson-times/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LessonTimeDTO> partialUpdateLessonTime(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody LessonTimeDTO lessonTimeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update LessonTime partially : {}, {}", id, lessonTimeDTO);
        if (lessonTimeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, lessonTimeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!lessonTimeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LessonTimeDTO> result = lessonTimeService.partialUpdate(lessonTimeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, lessonTimeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /lesson-times} : get all the lessonTimes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of lessonTimes in body.
     */
    @GetMapping("/lesson-times")
    public ResponseEntity<List<LessonTimeDTO>> getAllLessonTimes(LessonTimeCriteria criteria) {
        log.debug("REST request to get LessonTimes by criteria: {}", criteria);
        List<LessonTimeDTO> entityList = lessonTimeQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /lesson-times/count} : count all the lessonTimes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/lesson-times/count")
    public ResponseEntity<Long> countLessonTimes(LessonTimeCriteria criteria) {
        log.debug("REST request to count LessonTimes by criteria: {}", criteria);
        return ResponseEntity.ok().body(lessonTimeQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /lesson-times/:id} : get the "id" lessonTime.
     *
     * @param id the id of the lessonTimeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the lessonTimeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/lesson-times/{id}")
    public ResponseEntity<LessonTimeDTO> getLessonTime(@PathVariable Long id) {
        log.debug("REST request to get LessonTime : {}", id);
        Optional<LessonTimeDTO> lessonTimeDTO = lessonTimeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(lessonTimeDTO);
    }

    /**
     * {@code DELETE  /lesson-times/:id} : delete the "id" lessonTime.
     *
     * @param id the id of the lessonTimeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/lesson-times/{id}")
    public ResponseEntity<Void> deleteLessonTime(@PathVariable Long id) {
        log.debug("REST request to delete LessonTime : {}", id);
        lessonTimeService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
