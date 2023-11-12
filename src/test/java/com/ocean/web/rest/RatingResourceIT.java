package com.ocean.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ocean.IntegrationTest;
import com.ocean.domain.Like;
import com.ocean.domain.Question;
import com.ocean.domain.Rating;
import com.ocean.domain.Student;
import com.ocean.domain.Teacher;
import com.ocean.repository.RatingRepository;
import com.ocean.service.criteria.RatingCriteria;
import com.ocean.service.dto.RatingDTO;
import com.ocean.service.mapper.RatingMapper;
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
 * Integration tests for the {@link RatingResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RatingResourceIT {

    private static final Integer DEFAULT_SCORE = 1;
    private static final Integer UPDATED_SCORE = 2;
    private static final Integer SMALLER_SCORE = 1 - 1;

    private static final String DEFAULT_REVIEW = "AAAAAAAAAA";
    private static final String UPDATED_REVIEW = "BBBBBBBBBB";

    private static final String DEFAULT_LESSON_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LESSON_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/ratings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private RatingMapper ratingMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRatingMockMvc;

    private Rating rating;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Rating createEntity(EntityManager em) {
        Rating rating = new Rating()
            .score(DEFAULT_SCORE)
            .review(DEFAULT_REVIEW)
            .lessonName(DEFAULT_LESSON_NAME)
            .createdBy(DEFAULT_CREATED_BY)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE);
        return rating;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Rating createUpdatedEntity(EntityManager em) {
        Rating rating = new Rating()
            .score(UPDATED_SCORE)
            .review(UPDATED_REVIEW)
            .lessonName(UPDATED_LESSON_NAME)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        return rating;
    }

    @BeforeEach
    public void initTest() {
        rating = createEntity(em);
    }

    @Test
    @Transactional
    void createRating() throws Exception {
        int databaseSizeBeforeCreate = ratingRepository.findAll().size();
        // Create the Rating
        RatingDTO ratingDTO = ratingMapper.toDto(rating);
        restRatingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ratingDTO)))
            .andExpect(status().isCreated());

        // Validate the Rating in the database
        List<Rating> ratingList = ratingRepository.findAll();
        assertThat(ratingList).hasSize(databaseSizeBeforeCreate + 1);
        Rating testRating = ratingList.get(ratingList.size() - 1);
        assertThat(testRating.getScore()).isEqualTo(DEFAULT_SCORE);
        assertThat(testRating.getReview()).isEqualTo(DEFAULT_REVIEW);
        assertThat(testRating.getLessonName()).isEqualTo(DEFAULT_LESSON_NAME);
        assertThat(testRating.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testRating.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testRating.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
        assertThat(testRating.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void createRatingWithExistingId() throws Exception {
        // Create the Rating with an existing ID
        rating.setId(1L);
        RatingDTO ratingDTO = ratingMapper.toDto(rating);

        int databaseSizeBeforeCreate = ratingRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRatingMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ratingDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Rating in the database
        List<Rating> ratingList = ratingRepository.findAll();
        assertThat(ratingList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllRatings() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList
        restRatingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rating.getId().intValue())))
            .andExpect(jsonPath("$.[*].score").value(hasItem(DEFAULT_SCORE)))
            .andExpect(jsonPath("$.[*].review").value(hasItem(DEFAULT_REVIEW)))
            .andExpect(jsonPath("$.[*].lessonName").value(hasItem(DEFAULT_LESSON_NAME)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));
    }

    @Test
    @Transactional
    void getRating() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get the rating
        restRatingMockMvc
            .perform(get(ENTITY_API_URL_ID, rating.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(rating.getId().intValue()))
            .andExpect(jsonPath("$.score").value(DEFAULT_SCORE))
            .andExpect(jsonPath("$.review").value(DEFAULT_REVIEW))
            .andExpect(jsonPath("$.lessonName").value(DEFAULT_LESSON_NAME))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()));
    }

    @Test
    @Transactional
    void getRatingsByIdFiltering() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        Long id = rating.getId();

        defaultRatingShouldBeFound("id.equals=" + id);
        defaultRatingShouldNotBeFound("id.notEquals=" + id);

        defaultRatingShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultRatingShouldNotBeFound("id.greaterThan=" + id);

        defaultRatingShouldBeFound("id.lessThanOrEqual=" + id);
        defaultRatingShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllRatingsByScoreIsEqualToSomething() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where score equals to DEFAULT_SCORE
        defaultRatingShouldBeFound("score.equals=" + DEFAULT_SCORE);

        // Get all the ratingList where score equals to UPDATED_SCORE
        defaultRatingShouldNotBeFound("score.equals=" + UPDATED_SCORE);
    }

    @Test
    @Transactional
    void getAllRatingsByScoreIsInShouldWork() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where score in DEFAULT_SCORE or UPDATED_SCORE
        defaultRatingShouldBeFound("score.in=" + DEFAULT_SCORE + "," + UPDATED_SCORE);

        // Get all the ratingList where score equals to UPDATED_SCORE
        defaultRatingShouldNotBeFound("score.in=" + UPDATED_SCORE);
    }

    @Test
    @Transactional
    void getAllRatingsByScoreIsNullOrNotNull() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where score is not null
        defaultRatingShouldBeFound("score.specified=true");

        // Get all the ratingList where score is null
        defaultRatingShouldNotBeFound("score.specified=false");
    }

    @Test
    @Transactional
    void getAllRatingsByScoreIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where score is greater than or equal to DEFAULT_SCORE
        defaultRatingShouldBeFound("score.greaterThanOrEqual=" + DEFAULT_SCORE);

        // Get all the ratingList where score is greater than or equal to UPDATED_SCORE
        defaultRatingShouldNotBeFound("score.greaterThanOrEqual=" + UPDATED_SCORE);
    }

    @Test
    @Transactional
    void getAllRatingsByScoreIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where score is less than or equal to DEFAULT_SCORE
        defaultRatingShouldBeFound("score.lessThanOrEqual=" + DEFAULT_SCORE);

        // Get all the ratingList where score is less than or equal to SMALLER_SCORE
        defaultRatingShouldNotBeFound("score.lessThanOrEqual=" + SMALLER_SCORE);
    }

    @Test
    @Transactional
    void getAllRatingsByScoreIsLessThanSomething() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where score is less than DEFAULT_SCORE
        defaultRatingShouldNotBeFound("score.lessThan=" + DEFAULT_SCORE);

        // Get all the ratingList where score is less than UPDATED_SCORE
        defaultRatingShouldBeFound("score.lessThan=" + UPDATED_SCORE);
    }

    @Test
    @Transactional
    void getAllRatingsByScoreIsGreaterThanSomething() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where score is greater than DEFAULT_SCORE
        defaultRatingShouldNotBeFound("score.greaterThan=" + DEFAULT_SCORE);

        // Get all the ratingList where score is greater than SMALLER_SCORE
        defaultRatingShouldBeFound("score.greaterThan=" + SMALLER_SCORE);
    }

    @Test
    @Transactional
    void getAllRatingsByReviewIsEqualToSomething() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where review equals to DEFAULT_REVIEW
        defaultRatingShouldBeFound("review.equals=" + DEFAULT_REVIEW);

        // Get all the ratingList where review equals to UPDATED_REVIEW
        defaultRatingShouldNotBeFound("review.equals=" + UPDATED_REVIEW);
    }

    @Test
    @Transactional
    void getAllRatingsByReviewIsInShouldWork() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where review in DEFAULT_REVIEW or UPDATED_REVIEW
        defaultRatingShouldBeFound("review.in=" + DEFAULT_REVIEW + "," + UPDATED_REVIEW);

        // Get all the ratingList where review equals to UPDATED_REVIEW
        defaultRatingShouldNotBeFound("review.in=" + UPDATED_REVIEW);
    }

    @Test
    @Transactional
    void getAllRatingsByReviewIsNullOrNotNull() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where review is not null
        defaultRatingShouldBeFound("review.specified=true");

        // Get all the ratingList where review is null
        defaultRatingShouldNotBeFound("review.specified=false");
    }

    @Test
    @Transactional
    void getAllRatingsByReviewContainsSomething() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where review contains DEFAULT_REVIEW
        defaultRatingShouldBeFound("review.contains=" + DEFAULT_REVIEW);

        // Get all the ratingList where review contains UPDATED_REVIEW
        defaultRatingShouldNotBeFound("review.contains=" + UPDATED_REVIEW);
    }

    @Test
    @Transactional
    void getAllRatingsByReviewNotContainsSomething() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where review does not contain DEFAULT_REVIEW
        defaultRatingShouldNotBeFound("review.doesNotContain=" + DEFAULT_REVIEW);

        // Get all the ratingList where review does not contain UPDATED_REVIEW
        defaultRatingShouldBeFound("review.doesNotContain=" + UPDATED_REVIEW);
    }

    @Test
    @Transactional
    void getAllRatingsByLessonNameIsEqualToSomething() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where lessonName equals to DEFAULT_LESSON_NAME
        defaultRatingShouldBeFound("lessonName.equals=" + DEFAULT_LESSON_NAME);

        // Get all the ratingList where lessonName equals to UPDATED_LESSON_NAME
        defaultRatingShouldNotBeFound("lessonName.equals=" + UPDATED_LESSON_NAME);
    }

    @Test
    @Transactional
    void getAllRatingsByLessonNameIsInShouldWork() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where lessonName in DEFAULT_LESSON_NAME or UPDATED_LESSON_NAME
        defaultRatingShouldBeFound("lessonName.in=" + DEFAULT_LESSON_NAME + "," + UPDATED_LESSON_NAME);

        // Get all the ratingList where lessonName equals to UPDATED_LESSON_NAME
        defaultRatingShouldNotBeFound("lessonName.in=" + UPDATED_LESSON_NAME);
    }

    @Test
    @Transactional
    void getAllRatingsByLessonNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where lessonName is not null
        defaultRatingShouldBeFound("lessonName.specified=true");

        // Get all the ratingList where lessonName is null
        defaultRatingShouldNotBeFound("lessonName.specified=false");
    }

    @Test
    @Transactional
    void getAllRatingsByLessonNameContainsSomething() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where lessonName contains DEFAULT_LESSON_NAME
        defaultRatingShouldBeFound("lessonName.contains=" + DEFAULT_LESSON_NAME);

        // Get all the ratingList where lessonName contains UPDATED_LESSON_NAME
        defaultRatingShouldNotBeFound("lessonName.contains=" + UPDATED_LESSON_NAME);
    }

    @Test
    @Transactional
    void getAllRatingsByLessonNameNotContainsSomething() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where lessonName does not contain DEFAULT_LESSON_NAME
        defaultRatingShouldNotBeFound("lessonName.doesNotContain=" + DEFAULT_LESSON_NAME);

        // Get all the ratingList where lessonName does not contain UPDATED_LESSON_NAME
        defaultRatingShouldBeFound("lessonName.doesNotContain=" + UPDATED_LESSON_NAME);
    }

    @Test
    @Transactional
    void getAllRatingsByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where createdBy equals to DEFAULT_CREATED_BY
        defaultRatingShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the ratingList where createdBy equals to UPDATED_CREATED_BY
        defaultRatingShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllRatingsByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultRatingShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the ratingList where createdBy equals to UPDATED_CREATED_BY
        defaultRatingShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllRatingsByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where createdBy is not null
        defaultRatingShouldBeFound("createdBy.specified=true");

        // Get all the ratingList where createdBy is null
        defaultRatingShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllRatingsByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where createdBy contains DEFAULT_CREATED_BY
        defaultRatingShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the ratingList where createdBy contains UPDATED_CREATED_BY
        defaultRatingShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllRatingsByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where createdBy does not contain DEFAULT_CREATED_BY
        defaultRatingShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the ratingList where createdBy does not contain UPDATED_CREATED_BY
        defaultRatingShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllRatingsByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where createdDate equals to DEFAULT_CREATED_DATE
        defaultRatingShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the ratingList where createdDate equals to UPDATED_CREATED_DATE
        defaultRatingShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllRatingsByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultRatingShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the ratingList where createdDate equals to UPDATED_CREATED_DATE
        defaultRatingShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllRatingsByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where createdDate is not null
        defaultRatingShouldBeFound("createdDate.specified=true");

        // Get all the ratingList where createdDate is null
        defaultRatingShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    void getAllRatingsByLastModifiedByIsEqualToSomething() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where lastModifiedBy equals to DEFAULT_LAST_MODIFIED_BY
        defaultRatingShouldBeFound("lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the ratingList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultRatingShouldNotBeFound("lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllRatingsByLastModifiedByIsInShouldWork() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where lastModifiedBy in DEFAULT_LAST_MODIFIED_BY or UPDATED_LAST_MODIFIED_BY
        defaultRatingShouldBeFound("lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY);

        // Get all the ratingList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultRatingShouldNotBeFound("lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllRatingsByLastModifiedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where lastModifiedBy is not null
        defaultRatingShouldBeFound("lastModifiedBy.specified=true");

        // Get all the ratingList where lastModifiedBy is null
        defaultRatingShouldNotBeFound("lastModifiedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllRatingsByLastModifiedByContainsSomething() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where lastModifiedBy contains DEFAULT_LAST_MODIFIED_BY
        defaultRatingShouldBeFound("lastModifiedBy.contains=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the ratingList where lastModifiedBy contains UPDATED_LAST_MODIFIED_BY
        defaultRatingShouldNotBeFound("lastModifiedBy.contains=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllRatingsByLastModifiedByNotContainsSomething() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where lastModifiedBy does not contain DEFAULT_LAST_MODIFIED_BY
        defaultRatingShouldNotBeFound("lastModifiedBy.doesNotContain=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the ratingList where lastModifiedBy does not contain UPDATED_LAST_MODIFIED_BY
        defaultRatingShouldBeFound("lastModifiedBy.doesNotContain=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllRatingsByLastModifiedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where lastModifiedDate equals to DEFAULT_LAST_MODIFIED_DATE
        defaultRatingShouldBeFound("lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the ratingList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultRatingShouldNotBeFound("lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllRatingsByLastModifiedDateIsInShouldWork() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where lastModifiedDate in DEFAULT_LAST_MODIFIED_DATE or UPDATED_LAST_MODIFIED_DATE
        defaultRatingShouldBeFound("lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE);

        // Get all the ratingList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultRatingShouldNotBeFound("lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllRatingsByLastModifiedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratingList where lastModifiedDate is not null
        defaultRatingShouldBeFound("lastModifiedDate.specified=true");

        // Get all the ratingList where lastModifiedDate is null
        defaultRatingShouldNotBeFound("lastModifiedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllRatingsByLikeIsEqualToSomething() throws Exception {
        Like like;
        if (TestUtil.findAll(em, Like.class).isEmpty()) {
            ratingRepository.saveAndFlush(rating);
            like = LikeResourceIT.createEntity(em);
        } else {
            like = TestUtil.findAll(em, Like.class).get(0);
        }
        em.persist(like);
        em.flush();
        rating.addLike(like);
        ratingRepository.saveAndFlush(rating);
        Long likeId = like.getId();

        // Get all the ratingList where like equals to likeId
        defaultRatingShouldBeFound("likeId.equals=" + likeId);

        // Get all the ratingList where like equals to (likeId + 1)
        defaultRatingShouldNotBeFound("likeId.equals=" + (likeId + 1));
    }

    @Test
    @Transactional
    void getAllRatingsByQuestionIsEqualToSomething() throws Exception {
        Question question;
        if (TestUtil.findAll(em, Question.class).isEmpty()) {
            ratingRepository.saveAndFlush(rating);
            question = QuestionResourceIT.createEntity(em);
        } else {
            question = TestUtil.findAll(em, Question.class).get(0);
        }
        em.persist(question);
        em.flush();
        rating.addQuestion(question);
        ratingRepository.saveAndFlush(rating);
        Long questionId = question.getId();

        // Get all the ratingList where question equals to questionId
        defaultRatingShouldBeFound("questionId.equals=" + questionId);

        // Get all the ratingList where question equals to (questionId + 1)
        defaultRatingShouldNotBeFound("questionId.equals=" + (questionId + 1));
    }

    @Test
    @Transactional
    void getAllRatingsByTeacherIsEqualToSomething() throws Exception {
        Teacher teacher;
        if (TestUtil.findAll(em, Teacher.class).isEmpty()) {
            ratingRepository.saveAndFlush(rating);
            teacher = TeacherResourceIT.createEntity(em);
        } else {
            teacher = TestUtil.findAll(em, Teacher.class).get(0);
        }
        em.persist(teacher);
        em.flush();
        rating.setTeacher(teacher);
        ratingRepository.saveAndFlush(rating);
        Long teacherId = teacher.getId();

        // Get all the ratingList where teacher equals to teacherId
        defaultRatingShouldBeFound("teacherId.equals=" + teacherId);

        // Get all the ratingList where teacher equals to (teacherId + 1)
        defaultRatingShouldNotBeFound("teacherId.equals=" + (teacherId + 1));
    }

    @Test
    @Transactional
    void getAllRatingsByStudentIsEqualToSomething() throws Exception {
        Student student;
        if (TestUtil.findAll(em, Student.class).isEmpty()) {
            ratingRepository.saveAndFlush(rating);
            student = StudentResourceIT.createEntity(em);
        } else {
            student = TestUtil.findAll(em, Student.class).get(0);
        }
        em.persist(student);
        em.flush();
        rating.setStudent(student);
        ratingRepository.saveAndFlush(rating);
        Long studentId = student.getId();

        // Get all the ratingList where student equals to studentId
        defaultRatingShouldBeFound("studentId.equals=" + studentId);

        // Get all the ratingList where student equals to (studentId + 1)
        defaultRatingShouldNotBeFound("studentId.equals=" + (studentId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRatingShouldBeFound(String filter) throws Exception {
        restRatingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rating.getId().intValue())))
            .andExpect(jsonPath("$.[*].score").value(hasItem(DEFAULT_SCORE)))
            .andExpect(jsonPath("$.[*].review").value(hasItem(DEFAULT_REVIEW)))
            .andExpect(jsonPath("$.[*].lessonName").value(hasItem(DEFAULT_LESSON_NAME)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));

        // Check, that the count call also returns 1
        restRatingMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRatingShouldNotBeFound(String filter) throws Exception {
        restRatingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRatingMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingRating() throws Exception {
        // Get the rating
        restRatingMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRating() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        int databaseSizeBeforeUpdate = ratingRepository.findAll().size();

        // Update the rating
        Rating updatedRating = ratingRepository.findById(rating.getId()).get();
        // Disconnect from session so that the updates on updatedRating are not directly saved in db
        em.detach(updatedRating);
        updatedRating
            .score(UPDATED_SCORE)
            .review(UPDATED_REVIEW)
            .lessonName(UPDATED_LESSON_NAME)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        RatingDTO ratingDTO = ratingMapper.toDto(updatedRating);

        restRatingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ratingDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ratingDTO))
            )
            .andExpect(status().isOk());

        // Validate the Rating in the database
        List<Rating> ratingList = ratingRepository.findAll();
        assertThat(ratingList).hasSize(databaseSizeBeforeUpdate);
        Rating testRating = ratingList.get(ratingList.size() - 1);
        assertThat(testRating.getScore()).isEqualTo(UPDATED_SCORE);
        assertThat(testRating.getReview()).isEqualTo(UPDATED_REVIEW);
        assertThat(testRating.getLessonName()).isEqualTo(UPDATED_LESSON_NAME);
        assertThat(testRating.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testRating.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testRating.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testRating.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void putNonExistingRating() throws Exception {
        int databaseSizeBeforeUpdate = ratingRepository.findAll().size();
        rating.setId(count.incrementAndGet());

        // Create the Rating
        RatingDTO ratingDTO = ratingMapper.toDto(rating);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRatingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ratingDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ratingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Rating in the database
        List<Rating> ratingList = ratingRepository.findAll();
        assertThat(ratingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRating() throws Exception {
        int databaseSizeBeforeUpdate = ratingRepository.findAll().size();
        rating.setId(count.incrementAndGet());

        // Create the Rating
        RatingDTO ratingDTO = ratingMapper.toDto(rating);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRatingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(ratingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Rating in the database
        List<Rating> ratingList = ratingRepository.findAll();
        assertThat(ratingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRating() throws Exception {
        int databaseSizeBeforeUpdate = ratingRepository.findAll().size();
        rating.setId(count.incrementAndGet());

        // Create the Rating
        RatingDTO ratingDTO = ratingMapper.toDto(rating);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRatingMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(ratingDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Rating in the database
        List<Rating> ratingList = ratingRepository.findAll();
        assertThat(ratingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRatingWithPatch() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        int databaseSizeBeforeUpdate = ratingRepository.findAll().size();

        // Update the rating using partial update
        Rating partialUpdatedRating = new Rating();
        partialUpdatedRating.setId(rating.getId());

        partialUpdatedRating
            .score(UPDATED_SCORE)
            .review(UPDATED_REVIEW)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);

        restRatingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRating.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRating))
            )
            .andExpect(status().isOk());

        // Validate the Rating in the database
        List<Rating> ratingList = ratingRepository.findAll();
        assertThat(ratingList).hasSize(databaseSizeBeforeUpdate);
        Rating testRating = ratingList.get(ratingList.size() - 1);
        assertThat(testRating.getScore()).isEqualTo(UPDATED_SCORE);
        assertThat(testRating.getReview()).isEqualTo(UPDATED_REVIEW);
        assertThat(testRating.getLessonName()).isEqualTo(DEFAULT_LESSON_NAME);
        assertThat(testRating.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testRating.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testRating.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testRating.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void fullUpdateRatingWithPatch() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        int databaseSizeBeforeUpdate = ratingRepository.findAll().size();

        // Update the rating using partial update
        Rating partialUpdatedRating = new Rating();
        partialUpdatedRating.setId(rating.getId());

        partialUpdatedRating
            .score(UPDATED_SCORE)
            .review(UPDATED_REVIEW)
            .lessonName(UPDATED_LESSON_NAME)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);

        restRatingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRating.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRating))
            )
            .andExpect(status().isOk());

        // Validate the Rating in the database
        List<Rating> ratingList = ratingRepository.findAll();
        assertThat(ratingList).hasSize(databaseSizeBeforeUpdate);
        Rating testRating = ratingList.get(ratingList.size() - 1);
        assertThat(testRating.getScore()).isEqualTo(UPDATED_SCORE);
        assertThat(testRating.getReview()).isEqualTo(UPDATED_REVIEW);
        assertThat(testRating.getLessonName()).isEqualTo(UPDATED_LESSON_NAME);
        assertThat(testRating.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testRating.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testRating.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testRating.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingRating() throws Exception {
        int databaseSizeBeforeUpdate = ratingRepository.findAll().size();
        rating.setId(count.incrementAndGet());

        // Create the Rating
        RatingDTO ratingDTO = ratingMapper.toDto(rating);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRatingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ratingDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ratingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Rating in the database
        List<Rating> ratingList = ratingRepository.findAll();
        assertThat(ratingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRating() throws Exception {
        int databaseSizeBeforeUpdate = ratingRepository.findAll().size();
        rating.setId(count.incrementAndGet());

        // Create the Rating
        RatingDTO ratingDTO = ratingMapper.toDto(rating);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRatingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(ratingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Rating in the database
        List<Rating> ratingList = ratingRepository.findAll();
        assertThat(ratingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRating() throws Exception {
        int databaseSizeBeforeUpdate = ratingRepository.findAll().size();
        rating.setId(count.incrementAndGet());

        // Create the Rating
        RatingDTO ratingDTO = ratingMapper.toDto(rating);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRatingMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(ratingDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Rating in the database
        List<Rating> ratingList = ratingRepository.findAll();
        assertThat(ratingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRating() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        int databaseSizeBeforeDelete = ratingRepository.findAll().size();

        // Delete the rating
        restRatingMockMvc
            .perform(delete(ENTITY_API_URL_ID, rating.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Rating> ratingList = ratingRepository.findAll();
        assertThat(ratingList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
