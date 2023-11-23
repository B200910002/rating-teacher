package com.ocean.web.rest;

import com.ocean.domain.Like;
import com.ocean.repository.LikeRepository;
import com.ocean.security.SecurityUtils;
import com.ocean.service.LikeQueryService;
import com.ocean.service.LikeService;
import com.ocean.service.StudentService;
import com.ocean.service.criteria.LikeCriteria;
import com.ocean.service.dto.LikeDTO;
import com.ocean.service.dto.StudentDTO;
import com.ocean.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.ocean.domain.Like}.
 */

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class LikeResource {

    private final Logger log = LoggerFactory.getLogger(LikeResource.class);

    private static final String ENTITY_NAME = "like";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LikeService likeService;

    private final LikeRepository likeRepository;

    private final LikeQueryService likeQueryService;

    @Autowired
    private StudentService studentService;

    public LikeResource(LikeService likeService, LikeRepository likeRepository, LikeQueryService likeQueryService) {
        this.likeService = likeService;
        this.likeRepository = likeRepository;
        this.likeQueryService = likeQueryService;
    }

    /**
     * {@code POST  /likes} : Create a new like.
     *
     * @param likeDTO the likeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new likeDTO, or with status {@code 400 (Bad Request)} if the like has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @CrossOrigin(origins = "*")
    @PostMapping("/likes")
    public ResponseEntity<LikeDTO> createLike(@RequestBody LikeDTO likeDTO) throws URISyntaxException {
        log.debug("REST request to save Like : {}", likeDTO);
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isPresent()) {
            Optional<StudentDTO> student = studentService.findOneByStudentCode(currentUserLogin.get());
            if (student.isPresent()) {
                likeDTO.setStudent(student.get());
            }
        }

        if (likeDTO.getId() != null) {
            throw new BadRequestAlertException("A new like cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LikeDTO result = likeService.save(likeDTO);
        return ResponseEntity
            .created(new URI("/api/likes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /likes/:id} : Updates an existing like.
     *
     * @param id the id of the likeDTO to save.
     * @param likeDTO the likeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated likeDTO,
     * or with status {@code 400 (Bad Request)} if the likeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the likeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @CrossOrigin(origins = "*")
    @PutMapping("/likes/{id}")
    public ResponseEntity<LikeDTO> updateLike(@PathVariable(value = "id", required = false) final Long id, @RequestBody LikeDTO likeDTO)
        throws URISyntaxException {
        log.debug("REST request to update Like : {}, {}", id, likeDTO);
        if (likeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, likeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!likeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        LikeDTO result = likeService.update(likeDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, likeDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /likes/:id} : Partial updates given fields of an existing like, field will ignore if it is null
     *
     * @param id the id of the likeDTO to save.
     * @param likeDTO the likeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated likeDTO,
     * or with status {@code 400 (Bad Request)} if the likeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the likeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the likeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @CrossOrigin(origins = "*")
    @PatchMapping(value = "/likes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LikeDTO> partialUpdateLike(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody LikeDTO likeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Like partially : {}, {}", id, likeDTO);
        if (likeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, likeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!likeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LikeDTO> result = likeService.partialUpdate(likeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, likeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /likes} : get all the likes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of likes in body.
     */
    @CrossOrigin(origins = "*")
    @GetMapping("/likes")
    public ResponseEntity<List<Like>> getAllLikes(LikeCriteria criteria) {
        log.debug("REST request to get Likes by criteria: {}", criteria);
        List<Like> entityList = likeService.findAll();
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /likes/count} : count all the likes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @CrossOrigin(origins = "*")
    @GetMapping("/likes/count")
    public ResponseEntity<Long> countLikes(LikeCriteria criteria) {
        log.debug("REST request to count Likes by criteria: {}", criteria);
        return ResponseEntity.ok().body(likeQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /likes/:id} : get the "id" like.
     *
     * @param id the id of the likeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the likeDTO, or with status {@code 404 (Not Found)}.
     */
    @CrossOrigin(origins = "*")
    @GetMapping("/likes/{id}")
    public ResponseEntity<Like> getLike(@PathVariable Long id) {
        log.debug("REST request to get Like : {}", id);
        Optional<Like> like = likeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(like);
    }

    /**
     * {@code DELETE  /likes/:id} : delete the "id" like.
     *
     * @param id the id of the likeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @CrossOrigin(origins = "*")
    @DeleteMapping("/likes/{id}")
    public ResponseEntity<Void> deleteLike(@PathVariable Long id) {
        log.debug("REST request to delete Like : {}", id);
        likeService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
