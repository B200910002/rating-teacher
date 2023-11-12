package com.ocean.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ocean.IntegrationTest;
import com.ocean.domain.Answer;
import com.ocean.domain.Question;
import com.ocean.repository.AnswerRepository;
import com.ocean.service.criteria.AnswerCriteria;
import com.ocean.service.dto.AnswerDTO;
import com.ocean.service.mapper.AnswerMapper;
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
 * Integration tests for the {@link AnswerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AnswerResourceIT {

    private static final String DEFAULT_ANSWER = "AAAAAAAAAA";
    private static final String UPDATED_ANSWER = "BBBBBBBBBB";

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/answers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private AnswerMapper answerMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAnswerMockMvc;

    private Answer answer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Answer createEntity(EntityManager em) {
        Answer answer = new Answer()
            .answer(DEFAULT_ANSWER)
            .createdBy(DEFAULT_CREATED_BY)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE);
        return answer;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Answer createUpdatedEntity(EntityManager em) {
        Answer answer = new Answer()
            .answer(UPDATED_ANSWER)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        return answer;
    }

    @BeforeEach
    public void initTest() {
        answer = createEntity(em);
    }

    @Test
    @Transactional
    void createAnswer() throws Exception {
        int databaseSizeBeforeCreate = answerRepository.findAll().size();
        // Create the Answer
        AnswerDTO answerDTO = answerMapper.toDto(answer);
        restAnswerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(answerDTO)))
            .andExpect(status().isCreated());

        // Validate the Answer in the database
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeCreate + 1);
        Answer testAnswer = answerList.get(answerList.size() - 1);
        assertThat(testAnswer.getAnswer()).isEqualTo(DEFAULT_ANSWER);
        assertThat(testAnswer.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testAnswer.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testAnswer.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
        assertThat(testAnswer.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void createAnswerWithExistingId() throws Exception {
        // Create the Answer with an existing ID
        answer.setId(1L);
        AnswerDTO answerDTO = answerMapper.toDto(answer);

        int databaseSizeBeforeCreate = answerRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAnswerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(answerDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Answer in the database
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllAnswers() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);

        // Get all the answerList
        restAnswerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(answer.getId().intValue())))
            .andExpect(jsonPath("$.[*].answer").value(hasItem(DEFAULT_ANSWER)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));
    }

    @Test
    @Transactional
    void getAnswer() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);

        // Get the answer
        restAnswerMockMvc
            .perform(get(ENTITY_API_URL_ID, answer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(answer.getId().intValue()))
            .andExpect(jsonPath("$.answer").value(DEFAULT_ANSWER))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()));
    }

    @Test
    @Transactional
    void getAnswersByIdFiltering() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);

        Long id = answer.getId();

        defaultAnswerShouldBeFound("id.equals=" + id);
        defaultAnswerShouldNotBeFound("id.notEquals=" + id);

        defaultAnswerShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultAnswerShouldNotBeFound("id.greaterThan=" + id);

        defaultAnswerShouldBeFound("id.lessThanOrEqual=" + id);
        defaultAnswerShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAnswersByAnswerIsEqualToSomething() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);

        // Get all the answerList where answer equals to DEFAULT_ANSWER
        defaultAnswerShouldBeFound("answer.equals=" + DEFAULT_ANSWER);

        // Get all the answerList where answer equals to UPDATED_ANSWER
        defaultAnswerShouldNotBeFound("answer.equals=" + UPDATED_ANSWER);
    }

    @Test
    @Transactional
    void getAllAnswersByAnswerIsInShouldWork() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);

        // Get all the answerList where answer in DEFAULT_ANSWER or UPDATED_ANSWER
        defaultAnswerShouldBeFound("answer.in=" + DEFAULT_ANSWER + "," + UPDATED_ANSWER);

        // Get all the answerList where answer equals to UPDATED_ANSWER
        defaultAnswerShouldNotBeFound("answer.in=" + UPDATED_ANSWER);
    }

    @Test
    @Transactional
    void getAllAnswersByAnswerIsNullOrNotNull() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);

        // Get all the answerList where answer is not null
        defaultAnswerShouldBeFound("answer.specified=true");

        // Get all the answerList where answer is null
        defaultAnswerShouldNotBeFound("answer.specified=false");
    }

    @Test
    @Transactional
    void getAllAnswersByAnswerContainsSomething() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);

        // Get all the answerList where answer contains DEFAULT_ANSWER
        defaultAnswerShouldBeFound("answer.contains=" + DEFAULT_ANSWER);

        // Get all the answerList where answer contains UPDATED_ANSWER
        defaultAnswerShouldNotBeFound("answer.contains=" + UPDATED_ANSWER);
    }

    @Test
    @Transactional
    void getAllAnswersByAnswerNotContainsSomething() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);

        // Get all the answerList where answer does not contain DEFAULT_ANSWER
        defaultAnswerShouldNotBeFound("answer.doesNotContain=" + DEFAULT_ANSWER);

        // Get all the answerList where answer does not contain UPDATED_ANSWER
        defaultAnswerShouldBeFound("answer.doesNotContain=" + UPDATED_ANSWER);
    }

    @Test
    @Transactional
    void getAllAnswersByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);

        // Get all the answerList where createdBy equals to DEFAULT_CREATED_BY
        defaultAnswerShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the answerList where createdBy equals to UPDATED_CREATED_BY
        defaultAnswerShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllAnswersByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);

        // Get all the answerList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultAnswerShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the answerList where createdBy equals to UPDATED_CREATED_BY
        defaultAnswerShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllAnswersByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);

        // Get all the answerList where createdBy is not null
        defaultAnswerShouldBeFound("createdBy.specified=true");

        // Get all the answerList where createdBy is null
        defaultAnswerShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllAnswersByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);

        // Get all the answerList where createdBy contains DEFAULT_CREATED_BY
        defaultAnswerShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the answerList where createdBy contains UPDATED_CREATED_BY
        defaultAnswerShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllAnswersByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);

        // Get all the answerList where createdBy does not contain DEFAULT_CREATED_BY
        defaultAnswerShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the answerList where createdBy does not contain UPDATED_CREATED_BY
        defaultAnswerShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllAnswersByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);

        // Get all the answerList where createdDate equals to DEFAULT_CREATED_DATE
        defaultAnswerShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the answerList where createdDate equals to UPDATED_CREATED_DATE
        defaultAnswerShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllAnswersByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);

        // Get all the answerList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultAnswerShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the answerList where createdDate equals to UPDATED_CREATED_DATE
        defaultAnswerShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllAnswersByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);

        // Get all the answerList where createdDate is not null
        defaultAnswerShouldBeFound("createdDate.specified=true");

        // Get all the answerList where createdDate is null
        defaultAnswerShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    void getAllAnswersByLastModifiedByIsEqualToSomething() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);

        // Get all the answerList where lastModifiedBy equals to DEFAULT_LAST_MODIFIED_BY
        defaultAnswerShouldBeFound("lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the answerList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultAnswerShouldNotBeFound("lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllAnswersByLastModifiedByIsInShouldWork() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);

        // Get all the answerList where lastModifiedBy in DEFAULT_LAST_MODIFIED_BY or UPDATED_LAST_MODIFIED_BY
        defaultAnswerShouldBeFound("lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY);

        // Get all the answerList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultAnswerShouldNotBeFound("lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllAnswersByLastModifiedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);

        // Get all the answerList where lastModifiedBy is not null
        defaultAnswerShouldBeFound("lastModifiedBy.specified=true");

        // Get all the answerList where lastModifiedBy is null
        defaultAnswerShouldNotBeFound("lastModifiedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllAnswersByLastModifiedByContainsSomething() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);

        // Get all the answerList where lastModifiedBy contains DEFAULT_LAST_MODIFIED_BY
        defaultAnswerShouldBeFound("lastModifiedBy.contains=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the answerList where lastModifiedBy contains UPDATED_LAST_MODIFIED_BY
        defaultAnswerShouldNotBeFound("lastModifiedBy.contains=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllAnswersByLastModifiedByNotContainsSomething() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);

        // Get all the answerList where lastModifiedBy does not contain DEFAULT_LAST_MODIFIED_BY
        defaultAnswerShouldNotBeFound("lastModifiedBy.doesNotContain=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the answerList where lastModifiedBy does not contain UPDATED_LAST_MODIFIED_BY
        defaultAnswerShouldBeFound("lastModifiedBy.doesNotContain=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllAnswersByLastModifiedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);

        // Get all the answerList where lastModifiedDate equals to DEFAULT_LAST_MODIFIED_DATE
        defaultAnswerShouldBeFound("lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the answerList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultAnswerShouldNotBeFound("lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllAnswersByLastModifiedDateIsInShouldWork() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);

        // Get all the answerList where lastModifiedDate in DEFAULT_LAST_MODIFIED_DATE or UPDATED_LAST_MODIFIED_DATE
        defaultAnswerShouldBeFound("lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE);

        // Get all the answerList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultAnswerShouldNotBeFound("lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllAnswersByLastModifiedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);

        // Get all the answerList where lastModifiedDate is not null
        defaultAnswerShouldBeFound("lastModifiedDate.specified=true");

        // Get all the answerList where lastModifiedDate is null
        defaultAnswerShouldNotBeFound("lastModifiedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllAnswersByQuestionIsEqualToSomething() throws Exception {
        Question question;
        if (TestUtil.findAll(em, Question.class).isEmpty()) {
            answerRepository.saveAndFlush(answer);
            question = QuestionResourceIT.createEntity(em);
        } else {
            question = TestUtil.findAll(em, Question.class).get(0);
        }
        em.persist(question);
        em.flush();
        answer.setQuestion(question);
        answerRepository.saveAndFlush(answer);
        Long questionId = question.getId();

        // Get all the answerList where question equals to questionId
        defaultAnswerShouldBeFound("questionId.equals=" + questionId);

        // Get all the answerList where question equals to (questionId + 1)
        defaultAnswerShouldNotBeFound("questionId.equals=" + (questionId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAnswerShouldBeFound(String filter) throws Exception {
        restAnswerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(answer.getId().intValue())))
            .andExpect(jsonPath("$.[*].answer").value(hasItem(DEFAULT_ANSWER)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));

        // Check, that the count call also returns 1
        restAnswerMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAnswerShouldNotBeFound(String filter) throws Exception {
        restAnswerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAnswerMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAnswer() throws Exception {
        // Get the answer
        restAnswerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAnswer() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);

        int databaseSizeBeforeUpdate = answerRepository.findAll().size();

        // Update the answer
        Answer updatedAnswer = answerRepository.findById(answer.getId()).get();
        // Disconnect from session so that the updates on updatedAnswer are not directly saved in db
        em.detach(updatedAnswer);
        updatedAnswer
            .answer(UPDATED_ANSWER)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        AnswerDTO answerDTO = answerMapper.toDto(updatedAnswer);

        restAnswerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, answerDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(answerDTO))
            )
            .andExpect(status().isOk());

        // Validate the Answer in the database
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeUpdate);
        Answer testAnswer = answerList.get(answerList.size() - 1);
        assertThat(testAnswer.getAnswer()).isEqualTo(UPDATED_ANSWER);
        assertThat(testAnswer.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testAnswer.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testAnswer.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testAnswer.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void putNonExistingAnswer() throws Exception {
        int databaseSizeBeforeUpdate = answerRepository.findAll().size();
        answer.setId(count.incrementAndGet());

        // Create the Answer
        AnswerDTO answerDTO = answerMapper.toDto(answer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAnswerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, answerDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(answerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Answer in the database
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAnswer() throws Exception {
        int databaseSizeBeforeUpdate = answerRepository.findAll().size();
        answer.setId(count.incrementAndGet());

        // Create the Answer
        AnswerDTO answerDTO = answerMapper.toDto(answer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAnswerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(answerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Answer in the database
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAnswer() throws Exception {
        int databaseSizeBeforeUpdate = answerRepository.findAll().size();
        answer.setId(count.incrementAndGet());

        // Create the Answer
        AnswerDTO answerDTO = answerMapper.toDto(answer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAnswerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(answerDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Answer in the database
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAnswerWithPatch() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);

        int databaseSizeBeforeUpdate = answerRepository.findAll().size();

        // Update the answer using partial update
        Answer partialUpdatedAnswer = new Answer();
        partialUpdatedAnswer.setId(answer.getId());

        partialUpdatedAnswer.answer(UPDATED_ANSWER).createdBy(UPDATED_CREATED_BY).lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);

        restAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAnswer.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAnswer))
            )
            .andExpect(status().isOk());

        // Validate the Answer in the database
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeUpdate);
        Answer testAnswer = answerList.get(answerList.size() - 1);
        assertThat(testAnswer.getAnswer()).isEqualTo(UPDATED_ANSWER);
        assertThat(testAnswer.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testAnswer.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testAnswer.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
        assertThat(testAnswer.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void fullUpdateAnswerWithPatch() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);

        int databaseSizeBeforeUpdate = answerRepository.findAll().size();

        // Update the answer using partial update
        Answer partialUpdatedAnswer = new Answer();
        partialUpdatedAnswer.setId(answer.getId());

        partialUpdatedAnswer
            .answer(UPDATED_ANSWER)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);

        restAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAnswer.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAnswer))
            )
            .andExpect(status().isOk());

        // Validate the Answer in the database
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeUpdate);
        Answer testAnswer = answerList.get(answerList.size() - 1);
        assertThat(testAnswer.getAnswer()).isEqualTo(UPDATED_ANSWER);
        assertThat(testAnswer.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testAnswer.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testAnswer.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testAnswer.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingAnswer() throws Exception {
        int databaseSizeBeforeUpdate = answerRepository.findAll().size();
        answer.setId(count.incrementAndGet());

        // Create the Answer
        AnswerDTO answerDTO = answerMapper.toDto(answer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, answerDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(answerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Answer in the database
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAnswer() throws Exception {
        int databaseSizeBeforeUpdate = answerRepository.findAll().size();
        answer.setId(count.incrementAndGet());

        // Create the Answer
        AnswerDTO answerDTO = answerMapper.toDto(answer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(answerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Answer in the database
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAnswer() throws Exception {
        int databaseSizeBeforeUpdate = answerRepository.findAll().size();
        answer.setId(count.incrementAndGet());

        // Create the Answer
        AnswerDTO answerDTO = answerMapper.toDto(answer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(answerDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Answer in the database
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAnswer() throws Exception {
        // Initialize the database
        answerRepository.saveAndFlush(answer);

        int databaseSizeBeforeDelete = answerRepository.findAll().size();

        // Delete the answer
        restAnswerMockMvc
            .perform(delete(ENTITY_API_URL_ID, answer.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Answer> answerList = answerRepository.findAll();
        assertThat(answerList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
