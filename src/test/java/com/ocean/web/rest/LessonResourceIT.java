package com.ocean.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ocean.IntegrationTest;
import com.ocean.domain.Lesson;
import com.ocean.repository.LessonRepository;
import com.ocean.service.criteria.LessonCriteria;
import com.ocean.service.dto.LessonDTO;
import com.ocean.service.mapper.LessonMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link LessonResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LessonResourceIT {

    private static final String DEFAULT_LESSON_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LESSON_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LESSON_CODE = "AAAAAAAAAA";
    private static final String UPDATED_LESSON_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/lessons";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private LessonMapper lessonMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLessonMockMvc;

    private Lesson lesson;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Lesson createEntity(EntityManager em) {
        Lesson lesson = new Lesson()
            .lessonName(DEFAULT_LESSON_NAME)
            .lessonCode(DEFAULT_LESSON_CODE)
            .createdBy(DEFAULT_CREATED_BY)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE);
        return lesson;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Lesson createUpdatedEntity(EntityManager em) {
        Lesson lesson = new Lesson()
            .lessonName(UPDATED_LESSON_NAME)
            .lessonCode(UPDATED_LESSON_CODE)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        return lesson;
    }

    @BeforeEach
    public void initTest() {
        lesson = createEntity(em);
    }

    @Test
    @Transactional
    void createLesson() throws Exception {
        int databaseSizeBeforeCreate = lessonRepository.findAll().size();
        // Create the Lesson
        LessonDTO lessonDTO = lessonMapper.toDto(lesson);
        restLessonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(lessonDTO)))
            .andExpect(status().isCreated());

        // Validate the Lesson in the database
        List<Lesson> lessonList = lessonRepository.findAll();
        assertThat(lessonList).hasSize(databaseSizeBeforeCreate + 1);
        Lesson testLesson = lessonList.get(lessonList.size() - 1);
        assertThat(testLesson.getLessonName()).isEqualTo(DEFAULT_LESSON_NAME);
        assertThat(testLesson.getLessonCode()).isEqualTo(DEFAULT_LESSON_CODE);
        assertThat(testLesson.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testLesson.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testLesson.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
        assertThat(testLesson.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void createLessonWithExistingId() throws Exception {
        // Create the Lesson with an existing ID
        lesson.setId(1L);
        LessonDTO lessonDTO = lessonMapper.toDto(lesson);

        int databaseSizeBeforeCreate = lessonRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLessonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(lessonDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Lesson in the database
        List<Lesson> lessonList = lessonRepository.findAll();
        assertThat(lessonList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllLessons() throws Exception {
        // Initialize the database
        lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList
        restLessonMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lesson.getId().intValue())))
            .andExpect(jsonPath("$.[*].lessonName").value(hasItem(DEFAULT_LESSON_NAME)))
            .andExpect(jsonPath("$.[*].lessonCode").value(hasItem(DEFAULT_LESSON_CODE)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));
    }

    @Test
    @Transactional
    void getLesson() throws Exception {
        // Initialize the database
        lessonRepository.saveAndFlush(lesson);

        // Get the lesson
        restLessonMockMvc
            .perform(get(ENTITY_API_URL_ID, lesson.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(lesson.getId().intValue()))
            .andExpect(jsonPath("$.lessonName").value(DEFAULT_LESSON_NAME))
            .andExpect(jsonPath("$.lessonCode").value(DEFAULT_LESSON_CODE))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()));
    }

    @Test
    @Transactional
    void getLessonsByIdFiltering() throws Exception {
        // Initialize the database
        lessonRepository.saveAndFlush(lesson);

        Long id = lesson.getId();

        defaultLessonShouldBeFound("id.equals=" + id);
        defaultLessonShouldNotBeFound("id.notEquals=" + id);

        defaultLessonShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultLessonShouldNotBeFound("id.greaterThan=" + id);

        defaultLessonShouldBeFound("id.lessThanOrEqual=" + id);
        defaultLessonShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllLessonsByLessonNameIsEqualToSomething() throws Exception {
        // Initialize the database
        lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where lessonName equals to DEFAULT_LESSON_NAME
        defaultLessonShouldBeFound("lessonName.equals=" + DEFAULT_LESSON_NAME);

        // Get all the lessonList where lessonName equals to UPDATED_LESSON_NAME
        defaultLessonShouldNotBeFound("lessonName.equals=" + UPDATED_LESSON_NAME);
    }

    @Test
    @Transactional
    void getAllLessonsByLessonNameIsInShouldWork() throws Exception {
        // Initialize the database
        lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where lessonName in DEFAULT_LESSON_NAME or UPDATED_LESSON_NAME
        defaultLessonShouldBeFound("lessonName.in=" + DEFAULT_LESSON_NAME + "," + UPDATED_LESSON_NAME);

        // Get all the lessonList where lessonName equals to UPDATED_LESSON_NAME
        defaultLessonShouldNotBeFound("lessonName.in=" + UPDATED_LESSON_NAME);
    }

    @Test
    @Transactional
    void getAllLessonsByLessonNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where lessonName is not null
        defaultLessonShouldBeFound("lessonName.specified=true");

        // Get all the lessonList where lessonName is null
        defaultLessonShouldNotBeFound("lessonName.specified=false");
    }

    @Test
    @Transactional
    void getAllLessonsByLessonNameContainsSomething() throws Exception {
        // Initialize the database
        lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where lessonName contains DEFAULT_LESSON_NAME
        defaultLessonShouldBeFound("lessonName.contains=" + DEFAULT_LESSON_NAME);

        // Get all the lessonList where lessonName contains UPDATED_LESSON_NAME
        defaultLessonShouldNotBeFound("lessonName.contains=" + UPDATED_LESSON_NAME);
    }

    @Test
    @Transactional
    void getAllLessonsByLessonNameNotContainsSomething() throws Exception {
        // Initialize the database
        lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where lessonName does not contain DEFAULT_LESSON_NAME
        defaultLessonShouldNotBeFound("lessonName.doesNotContain=" + DEFAULT_LESSON_NAME);

        // Get all the lessonList where lessonName does not contain UPDATED_LESSON_NAME
        defaultLessonShouldBeFound("lessonName.doesNotContain=" + UPDATED_LESSON_NAME);
    }

    @Test
    @Transactional
    void getAllLessonsByLessonCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where lessonCode equals to DEFAULT_LESSON_CODE
        defaultLessonShouldBeFound("lessonCode.equals=" + DEFAULT_LESSON_CODE);

        // Get all the lessonList where lessonCode equals to UPDATED_LESSON_CODE
        defaultLessonShouldNotBeFound("lessonCode.equals=" + UPDATED_LESSON_CODE);
    }

    @Test
    @Transactional
    void getAllLessonsByLessonCodeIsInShouldWork() throws Exception {
        // Initialize the database
        lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where lessonCode in DEFAULT_LESSON_CODE or UPDATED_LESSON_CODE
        defaultLessonShouldBeFound("lessonCode.in=" + DEFAULT_LESSON_CODE + "," + UPDATED_LESSON_CODE);

        // Get all the lessonList where lessonCode equals to UPDATED_LESSON_CODE
        defaultLessonShouldNotBeFound("lessonCode.in=" + UPDATED_LESSON_CODE);
    }

    @Test
    @Transactional
    void getAllLessonsByLessonCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where lessonCode is not null
        defaultLessonShouldBeFound("lessonCode.specified=true");

        // Get all the lessonList where lessonCode is null
        defaultLessonShouldNotBeFound("lessonCode.specified=false");
    }

    @Test
    @Transactional
    void getAllLessonsByLessonCodeContainsSomething() throws Exception {
        // Initialize the database
        lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where lessonCode contains DEFAULT_LESSON_CODE
        defaultLessonShouldBeFound("lessonCode.contains=" + DEFAULT_LESSON_CODE);

        // Get all the lessonList where lessonCode contains UPDATED_LESSON_CODE
        defaultLessonShouldNotBeFound("lessonCode.contains=" + UPDATED_LESSON_CODE);
    }

    @Test
    @Transactional
    void getAllLessonsByLessonCodeNotContainsSomething() throws Exception {
        // Initialize the database
        lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where lessonCode does not contain DEFAULT_LESSON_CODE
        defaultLessonShouldNotBeFound("lessonCode.doesNotContain=" + DEFAULT_LESSON_CODE);

        // Get all the lessonList where lessonCode does not contain UPDATED_LESSON_CODE
        defaultLessonShouldBeFound("lessonCode.doesNotContain=" + UPDATED_LESSON_CODE);
    }

    @Test
    @Transactional
    void getAllLessonsByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where createdBy equals to DEFAULT_CREATED_BY
        defaultLessonShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the lessonList where createdBy equals to UPDATED_CREATED_BY
        defaultLessonShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllLessonsByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultLessonShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the lessonList where createdBy equals to UPDATED_CREATED_BY
        defaultLessonShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllLessonsByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where createdBy is not null
        defaultLessonShouldBeFound("createdBy.specified=true");

        // Get all the lessonList where createdBy is null
        defaultLessonShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllLessonsByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where createdBy contains DEFAULT_CREATED_BY
        defaultLessonShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the lessonList where createdBy contains UPDATED_CREATED_BY
        defaultLessonShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllLessonsByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where createdBy does not contain DEFAULT_CREATED_BY
        defaultLessonShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the lessonList where createdBy does not contain UPDATED_CREATED_BY
        defaultLessonShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllLessonsByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where createdDate equals to DEFAULT_CREATED_DATE
        defaultLessonShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the lessonList where createdDate equals to UPDATED_CREATED_DATE
        defaultLessonShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllLessonsByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultLessonShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the lessonList where createdDate equals to UPDATED_CREATED_DATE
        defaultLessonShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllLessonsByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where createdDate is not null
        defaultLessonShouldBeFound("createdDate.specified=true");

        // Get all the lessonList where createdDate is null
        defaultLessonShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    void getAllLessonsByLastModifiedByIsEqualToSomething() throws Exception {
        // Initialize the database
        lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where lastModifiedBy equals to DEFAULT_LAST_MODIFIED_BY
        defaultLessonShouldBeFound("lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the lessonList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultLessonShouldNotBeFound("lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllLessonsByLastModifiedByIsInShouldWork() throws Exception {
        // Initialize the database
        lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where lastModifiedBy in DEFAULT_LAST_MODIFIED_BY or UPDATED_LAST_MODIFIED_BY
        defaultLessonShouldBeFound("lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY);

        // Get all the lessonList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultLessonShouldNotBeFound("lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllLessonsByLastModifiedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where lastModifiedBy is not null
        defaultLessonShouldBeFound("lastModifiedBy.specified=true");

        // Get all the lessonList where lastModifiedBy is null
        defaultLessonShouldNotBeFound("lastModifiedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllLessonsByLastModifiedByContainsSomething() throws Exception {
        // Initialize the database
        lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where lastModifiedBy contains DEFAULT_LAST_MODIFIED_BY
        defaultLessonShouldBeFound("lastModifiedBy.contains=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the lessonList where lastModifiedBy contains UPDATED_LAST_MODIFIED_BY
        defaultLessonShouldNotBeFound("lastModifiedBy.contains=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllLessonsByLastModifiedByNotContainsSomething() throws Exception {
        // Initialize the database
        lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where lastModifiedBy does not contain DEFAULT_LAST_MODIFIED_BY
        defaultLessonShouldNotBeFound("lastModifiedBy.doesNotContain=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the lessonList where lastModifiedBy does not contain UPDATED_LAST_MODIFIED_BY
        defaultLessonShouldBeFound("lastModifiedBy.doesNotContain=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllLessonsByLastModifiedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where lastModifiedDate equals to DEFAULT_LAST_MODIFIED_DATE
        defaultLessonShouldBeFound("lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the lessonList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultLessonShouldNotBeFound("lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllLessonsByLastModifiedDateIsInShouldWork() throws Exception {
        // Initialize the database
        lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where lastModifiedDate in DEFAULT_LAST_MODIFIED_DATE or UPDATED_LAST_MODIFIED_DATE
        defaultLessonShouldBeFound("lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE);

        // Get all the lessonList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultLessonShouldNotBeFound("lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllLessonsByLastModifiedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        lessonRepository.saveAndFlush(lesson);

        // Get all the lessonList where lastModifiedDate is not null
        defaultLessonShouldBeFound("lastModifiedDate.specified=true");

        // Get all the lessonList where lastModifiedDate is null
        defaultLessonShouldNotBeFound("lastModifiedDate.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultLessonShouldBeFound(String filter) throws Exception {
        restLessonMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lesson.getId().intValue())))
            .andExpect(jsonPath("$.[*].lessonName").value(hasItem(DEFAULT_LESSON_NAME)))
            .andExpect(jsonPath("$.[*].lessonCode").value(hasItem(DEFAULT_LESSON_CODE)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));

        // Check, that the count call also returns 1
        restLessonMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultLessonShouldNotBeFound(String filter) throws Exception {
        restLessonMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLessonMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingLesson() throws Exception {
        // Get the lesson
        restLessonMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLesson() throws Exception {
        // Initialize the database
        lessonRepository.saveAndFlush(lesson);

        int databaseSizeBeforeUpdate = lessonRepository.findAll().size();

        // Update the lesson
        Lesson updatedLesson = lessonRepository.findById(lesson.getId()).get();
        // Disconnect from session so that the updates on updatedLesson are not directly saved in db
        em.detach(updatedLesson);
        updatedLesson
            .lessonName(UPDATED_LESSON_NAME)
            .lessonCode(UPDATED_LESSON_CODE)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        LessonDTO lessonDTO = lessonMapper.toDto(updatedLesson);

        restLessonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, lessonDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(lessonDTO))
            )
            .andExpect(status().isOk());

        // Validate the Lesson in the database
        List<Lesson> lessonList = lessonRepository.findAll();
        assertThat(lessonList).hasSize(databaseSizeBeforeUpdate);
        Lesson testLesson = lessonList.get(lessonList.size() - 1);
        assertThat(testLesson.getLessonName()).isEqualTo(UPDATED_LESSON_NAME);
        assertThat(testLesson.getLessonCode()).isEqualTo(UPDATED_LESSON_CODE);
        assertThat(testLesson.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testLesson.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testLesson.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testLesson.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void putNonExistingLesson() throws Exception {
        int databaseSizeBeforeUpdate = lessonRepository.findAll().size();
        lesson.setId(count.incrementAndGet());

        // Create the Lesson
        LessonDTO lessonDTO = lessonMapper.toDto(lesson);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLessonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, lessonDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(lessonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Lesson in the database
        List<Lesson> lessonList = lessonRepository.findAll();
        assertThat(lessonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLesson() throws Exception {
        int databaseSizeBeforeUpdate = lessonRepository.findAll().size();
        lesson.setId(count.incrementAndGet());

        // Create the Lesson
        LessonDTO lessonDTO = lessonMapper.toDto(lesson);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLessonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(lessonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Lesson in the database
        List<Lesson> lessonList = lessonRepository.findAll();
        assertThat(lessonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLesson() throws Exception {
        int databaseSizeBeforeUpdate = lessonRepository.findAll().size();
        lesson.setId(count.incrementAndGet());

        // Create the Lesson
        LessonDTO lessonDTO = lessonMapper.toDto(lesson);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLessonMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(lessonDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Lesson in the database
        List<Lesson> lessonList = lessonRepository.findAll();
        assertThat(lessonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLessonWithPatch() throws Exception {
        // Initialize the database
        lessonRepository.saveAndFlush(lesson);

        int databaseSizeBeforeUpdate = lessonRepository.findAll().size();

        // Update the lesson using partial update
        Lesson partialUpdatedLesson = new Lesson();
        partialUpdatedLesson.setId(lesson.getId());

        partialUpdatedLesson.lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);

        restLessonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLesson.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLesson))
            )
            .andExpect(status().isOk());

        // Validate the Lesson in the database
        List<Lesson> lessonList = lessonRepository.findAll();
        assertThat(lessonList).hasSize(databaseSizeBeforeUpdate);
        Lesson testLesson = lessonList.get(lessonList.size() - 1);
        assertThat(testLesson.getLessonName()).isEqualTo(DEFAULT_LESSON_NAME);
        assertThat(testLesson.getLessonCode()).isEqualTo(DEFAULT_LESSON_CODE);
        assertThat(testLesson.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testLesson.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testLesson.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
        assertThat(testLesson.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void fullUpdateLessonWithPatch() throws Exception {
        // Initialize the database
        lessonRepository.saveAndFlush(lesson);

        int databaseSizeBeforeUpdate = lessonRepository.findAll().size();

        // Update the lesson using partial update
        Lesson partialUpdatedLesson = new Lesson();
        partialUpdatedLesson.setId(lesson.getId());

        partialUpdatedLesson
            .lessonName(UPDATED_LESSON_NAME)
            .lessonCode(UPDATED_LESSON_CODE)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);

        restLessonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLesson.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLesson))
            )
            .andExpect(status().isOk());

        // Validate the Lesson in the database
        List<Lesson> lessonList = lessonRepository.findAll();
        assertThat(lessonList).hasSize(databaseSizeBeforeUpdate);
        Lesson testLesson = lessonList.get(lessonList.size() - 1);
        assertThat(testLesson.getLessonName()).isEqualTo(UPDATED_LESSON_NAME);
        assertThat(testLesson.getLessonCode()).isEqualTo(UPDATED_LESSON_CODE);
        assertThat(testLesson.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testLesson.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testLesson.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testLesson.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingLesson() throws Exception {
        int databaseSizeBeforeUpdate = lessonRepository.findAll().size();
        lesson.setId(count.incrementAndGet());

        // Create the Lesson
        LessonDTO lessonDTO = lessonMapper.toDto(lesson);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLessonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, lessonDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(lessonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Lesson in the database
        List<Lesson> lessonList = lessonRepository.findAll();
        assertThat(lessonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLesson() throws Exception {
        int databaseSizeBeforeUpdate = lessonRepository.findAll().size();
        lesson.setId(count.incrementAndGet());

        // Create the Lesson
        LessonDTO lessonDTO = lessonMapper.toDto(lesson);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLessonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(lessonDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Lesson in the database
        List<Lesson> lessonList = lessonRepository.findAll();
        assertThat(lessonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLesson() throws Exception {
        int databaseSizeBeforeUpdate = lessonRepository.findAll().size();
        lesson.setId(count.incrementAndGet());

        // Create the Lesson
        LessonDTO lessonDTO = lessonMapper.toDto(lesson);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLessonMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(lessonDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Lesson in the database
        List<Lesson> lessonList = lessonRepository.findAll();
        assertThat(lessonList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLesson() throws Exception {
        // Initialize the database
        lessonRepository.saveAndFlush(lesson);

        int databaseSizeBeforeDelete = lessonRepository.findAll().size();

        // Delete the lesson
        restLessonMockMvc
            .perform(delete(ENTITY_API_URL_ID, lesson.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Lesson> lessonList = lessonRepository.findAll();
        assertThat(lessonList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
