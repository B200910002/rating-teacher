package com.ocean.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ocean.IntegrationTest;
import com.ocean.domain.LessonTime;
import com.ocean.repository.LessonTimeRepository;
import com.ocean.service.criteria.LessonTimeCriteria;
import com.ocean.service.dto.LessonTimeDTO;
import com.ocean.service.mapper.LessonTimeMapper;
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
 * Integration tests for the {@link LessonTimeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LessonTimeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Instant DEFAULT_START_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/lesson-times";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LessonTimeRepository lessonTimeRepository;

    @Autowired
    private LessonTimeMapper lessonTimeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLessonTimeMockMvc;

    private LessonTime lessonTime;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LessonTime createEntity(EntityManager em) {
        LessonTime lessonTime = new LessonTime().name(DEFAULT_NAME).startAt(DEFAULT_START_AT).endAt(DEFAULT_END_AT);
        return lessonTime;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LessonTime createUpdatedEntity(EntityManager em) {
        LessonTime lessonTime = new LessonTime().name(UPDATED_NAME).startAt(UPDATED_START_AT).endAt(UPDATED_END_AT);
        return lessonTime;
    }

    @BeforeEach
    public void initTest() {
        lessonTime = createEntity(em);
    }

    @Test
    @Transactional
    void createLessonTime() throws Exception {
        int databaseSizeBeforeCreate = lessonTimeRepository.findAll().size();
        // Create the LessonTime
        LessonTimeDTO lessonTimeDTO = lessonTimeMapper.toDto(lessonTime);
        restLessonTimeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(lessonTimeDTO)))
            .andExpect(status().isCreated());

        // Validate the LessonTime in the database
        List<LessonTime> lessonTimeList = lessonTimeRepository.findAll();
        assertThat(lessonTimeList).hasSize(databaseSizeBeforeCreate + 1);
        LessonTime testLessonTime = lessonTimeList.get(lessonTimeList.size() - 1);
        assertThat(testLessonTime.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testLessonTime.getStartAt()).isEqualTo(DEFAULT_START_AT);
        assertThat(testLessonTime.getEndAt()).isEqualTo(DEFAULT_END_AT);
    }

    @Test
    @Transactional
    void createLessonTimeWithExistingId() throws Exception {
        // Create the LessonTime with an existing ID
        lessonTime.setId(1L);
        LessonTimeDTO lessonTimeDTO = lessonTimeMapper.toDto(lessonTime);

        int databaseSizeBeforeCreate = lessonTimeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLessonTimeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(lessonTimeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the LessonTime in the database
        List<LessonTime> lessonTimeList = lessonTimeRepository.findAll();
        assertThat(lessonTimeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllLessonTimes() throws Exception {
        // Initialize the database
        lessonTimeRepository.saveAndFlush(lessonTime);

        // Get all the lessonTimeList
        restLessonTimeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lessonTime.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].startAt").value(hasItem(DEFAULT_START_AT.toString())))
            .andExpect(jsonPath("$.[*].endAt").value(hasItem(DEFAULT_END_AT.toString())));
    }

    @Test
    @Transactional
    void getLessonTime() throws Exception {
        // Initialize the database
        lessonTimeRepository.saveAndFlush(lessonTime);

        // Get the lessonTime
        restLessonTimeMockMvc
            .perform(get(ENTITY_API_URL_ID, lessonTime.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(lessonTime.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.startAt").value(DEFAULT_START_AT.toString()))
            .andExpect(jsonPath("$.endAt").value(DEFAULT_END_AT.toString()));
    }

    @Test
    @Transactional
    void getLessonTimesByIdFiltering() throws Exception {
        // Initialize the database
        lessonTimeRepository.saveAndFlush(lessonTime);

        Long id = lessonTime.getId();

        defaultLessonTimeShouldBeFound("id.equals=" + id);
        defaultLessonTimeShouldNotBeFound("id.notEquals=" + id);

        defaultLessonTimeShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultLessonTimeShouldNotBeFound("id.greaterThan=" + id);

        defaultLessonTimeShouldBeFound("id.lessThanOrEqual=" + id);
        defaultLessonTimeShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllLessonTimesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        lessonTimeRepository.saveAndFlush(lessonTime);

        // Get all the lessonTimeList where name equals to DEFAULT_NAME
        defaultLessonTimeShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the lessonTimeList where name equals to UPDATED_NAME
        defaultLessonTimeShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllLessonTimesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        lessonTimeRepository.saveAndFlush(lessonTime);

        // Get all the lessonTimeList where name in DEFAULT_NAME or UPDATED_NAME
        defaultLessonTimeShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the lessonTimeList where name equals to UPDATED_NAME
        defaultLessonTimeShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllLessonTimesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        lessonTimeRepository.saveAndFlush(lessonTime);

        // Get all the lessonTimeList where name is not null
        defaultLessonTimeShouldBeFound("name.specified=true");

        // Get all the lessonTimeList where name is null
        defaultLessonTimeShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllLessonTimesByNameContainsSomething() throws Exception {
        // Initialize the database
        lessonTimeRepository.saveAndFlush(lessonTime);

        // Get all the lessonTimeList where name contains DEFAULT_NAME
        defaultLessonTimeShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the lessonTimeList where name contains UPDATED_NAME
        defaultLessonTimeShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllLessonTimesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        lessonTimeRepository.saveAndFlush(lessonTime);

        // Get all the lessonTimeList where name does not contain DEFAULT_NAME
        defaultLessonTimeShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the lessonTimeList where name does not contain UPDATED_NAME
        defaultLessonTimeShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllLessonTimesByStartAtIsEqualToSomething() throws Exception {
        // Initialize the database
        lessonTimeRepository.saveAndFlush(lessonTime);

        // Get all the lessonTimeList where startAt equals to DEFAULT_START_AT
        defaultLessonTimeShouldBeFound("startAt.equals=" + DEFAULT_START_AT);

        // Get all the lessonTimeList where startAt equals to UPDATED_START_AT
        defaultLessonTimeShouldNotBeFound("startAt.equals=" + UPDATED_START_AT);
    }

    @Test
    @Transactional
    void getAllLessonTimesByStartAtIsInShouldWork() throws Exception {
        // Initialize the database
        lessonTimeRepository.saveAndFlush(lessonTime);

        // Get all the lessonTimeList where startAt in DEFAULT_START_AT or UPDATED_START_AT
        defaultLessonTimeShouldBeFound("startAt.in=" + DEFAULT_START_AT + "," + UPDATED_START_AT);

        // Get all the lessonTimeList where startAt equals to UPDATED_START_AT
        defaultLessonTimeShouldNotBeFound("startAt.in=" + UPDATED_START_AT);
    }

    @Test
    @Transactional
    void getAllLessonTimesByStartAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        lessonTimeRepository.saveAndFlush(lessonTime);

        // Get all the lessonTimeList where startAt is not null
        defaultLessonTimeShouldBeFound("startAt.specified=true");

        // Get all the lessonTimeList where startAt is null
        defaultLessonTimeShouldNotBeFound("startAt.specified=false");
    }

    @Test
    @Transactional
    void getAllLessonTimesByEndAtIsEqualToSomething() throws Exception {
        // Initialize the database
        lessonTimeRepository.saveAndFlush(lessonTime);

        // Get all the lessonTimeList where endAt equals to DEFAULT_END_AT
        defaultLessonTimeShouldBeFound("endAt.equals=" + DEFAULT_END_AT);

        // Get all the lessonTimeList where endAt equals to UPDATED_END_AT
        defaultLessonTimeShouldNotBeFound("endAt.equals=" + UPDATED_END_AT);
    }

    @Test
    @Transactional
    void getAllLessonTimesByEndAtIsInShouldWork() throws Exception {
        // Initialize the database
        lessonTimeRepository.saveAndFlush(lessonTime);

        // Get all the lessonTimeList where endAt in DEFAULT_END_AT or UPDATED_END_AT
        defaultLessonTimeShouldBeFound("endAt.in=" + DEFAULT_END_AT + "," + UPDATED_END_AT);

        // Get all the lessonTimeList where endAt equals to UPDATED_END_AT
        defaultLessonTimeShouldNotBeFound("endAt.in=" + UPDATED_END_AT);
    }

    @Test
    @Transactional
    void getAllLessonTimesByEndAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        lessonTimeRepository.saveAndFlush(lessonTime);

        // Get all the lessonTimeList where endAt is not null
        defaultLessonTimeShouldBeFound("endAt.specified=true");

        // Get all the lessonTimeList where endAt is null
        defaultLessonTimeShouldNotBeFound("endAt.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultLessonTimeShouldBeFound(String filter) throws Exception {
        restLessonTimeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lessonTime.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].startAt").value(hasItem(DEFAULT_START_AT.toString())))
            .andExpect(jsonPath("$.[*].endAt").value(hasItem(DEFAULT_END_AT.toString())));

        // Check, that the count call also returns 1
        restLessonTimeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultLessonTimeShouldNotBeFound(String filter) throws Exception {
        restLessonTimeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLessonTimeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingLessonTime() throws Exception {
        // Get the lessonTime
        restLessonTimeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLessonTime() throws Exception {
        // Initialize the database
        lessonTimeRepository.saveAndFlush(lessonTime);

        int databaseSizeBeforeUpdate = lessonTimeRepository.findAll().size();

        // Update the lessonTime
        LessonTime updatedLessonTime = lessonTimeRepository.findById(lessonTime.getId()).get();
        // Disconnect from session so that the updates on updatedLessonTime are not directly saved in db
        em.detach(updatedLessonTime);
        updatedLessonTime.name(UPDATED_NAME).startAt(UPDATED_START_AT).endAt(UPDATED_END_AT);
        LessonTimeDTO lessonTimeDTO = lessonTimeMapper.toDto(updatedLessonTime);

        restLessonTimeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, lessonTimeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(lessonTimeDTO))
            )
            .andExpect(status().isOk());

        // Validate the LessonTime in the database
        List<LessonTime> lessonTimeList = lessonTimeRepository.findAll();
        assertThat(lessonTimeList).hasSize(databaseSizeBeforeUpdate);
        LessonTime testLessonTime = lessonTimeList.get(lessonTimeList.size() - 1);
        assertThat(testLessonTime.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testLessonTime.getStartAt()).isEqualTo(UPDATED_START_AT);
        assertThat(testLessonTime.getEndAt()).isEqualTo(UPDATED_END_AT);
    }

    @Test
    @Transactional
    void putNonExistingLessonTime() throws Exception {
        int databaseSizeBeforeUpdate = lessonTimeRepository.findAll().size();
        lessonTime.setId(count.incrementAndGet());

        // Create the LessonTime
        LessonTimeDTO lessonTimeDTO = lessonTimeMapper.toDto(lessonTime);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLessonTimeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, lessonTimeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(lessonTimeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LessonTime in the database
        List<LessonTime> lessonTimeList = lessonTimeRepository.findAll();
        assertThat(lessonTimeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLessonTime() throws Exception {
        int databaseSizeBeforeUpdate = lessonTimeRepository.findAll().size();
        lessonTime.setId(count.incrementAndGet());

        // Create the LessonTime
        LessonTimeDTO lessonTimeDTO = lessonTimeMapper.toDto(lessonTime);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLessonTimeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(lessonTimeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LessonTime in the database
        List<LessonTime> lessonTimeList = lessonTimeRepository.findAll();
        assertThat(lessonTimeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLessonTime() throws Exception {
        int databaseSizeBeforeUpdate = lessonTimeRepository.findAll().size();
        lessonTime.setId(count.incrementAndGet());

        // Create the LessonTime
        LessonTimeDTO lessonTimeDTO = lessonTimeMapper.toDto(lessonTime);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLessonTimeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(lessonTimeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LessonTime in the database
        List<LessonTime> lessonTimeList = lessonTimeRepository.findAll();
        assertThat(lessonTimeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLessonTimeWithPatch() throws Exception {
        // Initialize the database
        lessonTimeRepository.saveAndFlush(lessonTime);

        int databaseSizeBeforeUpdate = lessonTimeRepository.findAll().size();

        // Update the lessonTime using partial update
        LessonTime partialUpdatedLessonTime = new LessonTime();
        partialUpdatedLessonTime.setId(lessonTime.getId());

        partialUpdatedLessonTime.name(UPDATED_NAME).startAt(UPDATED_START_AT).endAt(UPDATED_END_AT);

        restLessonTimeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLessonTime.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLessonTime))
            )
            .andExpect(status().isOk());

        // Validate the LessonTime in the database
        List<LessonTime> lessonTimeList = lessonTimeRepository.findAll();
        assertThat(lessonTimeList).hasSize(databaseSizeBeforeUpdate);
        LessonTime testLessonTime = lessonTimeList.get(lessonTimeList.size() - 1);
        assertThat(testLessonTime.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testLessonTime.getStartAt()).isEqualTo(UPDATED_START_AT);
        assertThat(testLessonTime.getEndAt()).isEqualTo(UPDATED_END_AT);
    }

    @Test
    @Transactional
    void fullUpdateLessonTimeWithPatch() throws Exception {
        // Initialize the database
        lessonTimeRepository.saveAndFlush(lessonTime);

        int databaseSizeBeforeUpdate = lessonTimeRepository.findAll().size();

        // Update the lessonTime using partial update
        LessonTime partialUpdatedLessonTime = new LessonTime();
        partialUpdatedLessonTime.setId(lessonTime.getId());

        partialUpdatedLessonTime.name(UPDATED_NAME).startAt(UPDATED_START_AT).endAt(UPDATED_END_AT);

        restLessonTimeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLessonTime.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLessonTime))
            )
            .andExpect(status().isOk());

        // Validate the LessonTime in the database
        List<LessonTime> lessonTimeList = lessonTimeRepository.findAll();
        assertThat(lessonTimeList).hasSize(databaseSizeBeforeUpdate);
        LessonTime testLessonTime = lessonTimeList.get(lessonTimeList.size() - 1);
        assertThat(testLessonTime.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testLessonTime.getStartAt()).isEqualTo(UPDATED_START_AT);
        assertThat(testLessonTime.getEndAt()).isEqualTo(UPDATED_END_AT);
    }

    @Test
    @Transactional
    void patchNonExistingLessonTime() throws Exception {
        int databaseSizeBeforeUpdate = lessonTimeRepository.findAll().size();
        lessonTime.setId(count.incrementAndGet());

        // Create the LessonTime
        LessonTimeDTO lessonTimeDTO = lessonTimeMapper.toDto(lessonTime);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLessonTimeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, lessonTimeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(lessonTimeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LessonTime in the database
        List<LessonTime> lessonTimeList = lessonTimeRepository.findAll();
        assertThat(lessonTimeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLessonTime() throws Exception {
        int databaseSizeBeforeUpdate = lessonTimeRepository.findAll().size();
        lessonTime.setId(count.incrementAndGet());

        // Create the LessonTime
        LessonTimeDTO lessonTimeDTO = lessonTimeMapper.toDto(lessonTime);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLessonTimeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(lessonTimeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LessonTime in the database
        List<LessonTime> lessonTimeList = lessonTimeRepository.findAll();
        assertThat(lessonTimeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLessonTime() throws Exception {
        int databaseSizeBeforeUpdate = lessonTimeRepository.findAll().size();
        lessonTime.setId(count.incrementAndGet());

        // Create the LessonTime
        LessonTimeDTO lessonTimeDTO = lessonTimeMapper.toDto(lessonTime);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLessonTimeMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(lessonTimeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the LessonTime in the database
        List<LessonTime> lessonTimeList = lessonTimeRepository.findAll();
        assertThat(lessonTimeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLessonTime() throws Exception {
        // Initialize the database
        lessonTimeRepository.saveAndFlush(lessonTime);

        int databaseSizeBeforeDelete = lessonTimeRepository.findAll().size();

        // Delete the lessonTime
        restLessonTimeMockMvc
            .perform(delete(ENTITY_API_URL_ID, lessonTime.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<LessonTime> lessonTimeList = lessonTimeRepository.findAll();
        assertThat(lessonTimeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
