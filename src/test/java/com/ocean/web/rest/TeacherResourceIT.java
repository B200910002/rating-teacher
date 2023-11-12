package com.ocean.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ocean.IntegrationTest;
import com.ocean.domain.Teacher;
import com.ocean.domain.enumeration.JobPosition;
import com.ocean.repository.TeacherRepository;
import com.ocean.service.criteria.TeacherCriteria;
import com.ocean.service.dto.TeacherDTO;
import com.ocean.service.mapper.TeacherMapper;
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
 * Integration tests for the {@link TeacherResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TeacherResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_TEACHER_CODE = "AAAAAAAAAA";
    private static final String UPDATED_TEACHER_CODE = "BBBBBBBBBB";

    private static final JobPosition DEFAULT_JOB_POSITION = JobPosition.TEACHER;
    private static final JobPosition UPDATED_JOB_POSITION = JobPosition.SENIORTEACHER;

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/teachers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTeacherMockMvc;

    private Teacher teacher;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Teacher createEntity(EntityManager em) {
        Teacher teacher = new Teacher()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .teacherCode(DEFAULT_TEACHER_CODE)
            .jobPosition(DEFAULT_JOB_POSITION)
            .email(DEFAULT_EMAIL)
            .phone(DEFAULT_PHONE)
            .createdBy(DEFAULT_CREATED_BY)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE);
        return teacher;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Teacher createUpdatedEntity(EntityManager em) {
        Teacher teacher = new Teacher()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .teacherCode(UPDATED_TEACHER_CODE)
            .jobPosition(UPDATED_JOB_POSITION)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        return teacher;
    }

    @BeforeEach
    public void initTest() {
        teacher = createEntity(em);
    }

    @Test
    @Transactional
    void createTeacher() throws Exception {
        int databaseSizeBeforeCreate = teacherRepository.findAll().size();
        // Create the Teacher
        TeacherDTO teacherDTO = teacherMapper.toDto(teacher);
        restTeacherMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(teacherDTO)))
            .andExpect(status().isCreated());

        // Validate the Teacher in the database
        List<Teacher> teacherList = teacherRepository.findAll();
        assertThat(teacherList).hasSize(databaseSizeBeforeCreate + 1);
        Teacher testTeacher = teacherList.get(teacherList.size() - 1);
        assertThat(testTeacher.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testTeacher.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testTeacher.getTeacherCode()).isEqualTo(DEFAULT_TEACHER_CODE);
        assertThat(testTeacher.getJobPosition()).isEqualTo(DEFAULT_JOB_POSITION);
        assertThat(testTeacher.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testTeacher.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testTeacher.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testTeacher.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testTeacher.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
        assertThat(testTeacher.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void createTeacherWithExistingId() throws Exception {
        // Create the Teacher with an existing ID
        teacher.setId(1L);
        TeacherDTO teacherDTO = teacherMapper.toDto(teacher);

        int databaseSizeBeforeCreate = teacherRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTeacherMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(teacherDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Teacher in the database
        List<Teacher> teacherList = teacherRepository.findAll();
        assertThat(teacherList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTeachers() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList
        restTeacherMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(teacher.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].teacherCode").value(hasItem(DEFAULT_TEACHER_CODE)))
            .andExpect(jsonPath("$.[*].jobPosition").value(hasItem(DEFAULT_JOB_POSITION.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));
    }

    @Test
    @Transactional
    void getTeacher() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get the teacher
        restTeacherMockMvc
            .perform(get(ENTITY_API_URL_ID, teacher.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(teacher.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.teacherCode").value(DEFAULT_TEACHER_CODE))
            .andExpect(jsonPath("$.jobPosition").value(DEFAULT_JOB_POSITION.toString()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()));
    }

    @Test
    @Transactional
    void getTeachersByIdFiltering() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        Long id = teacher.getId();

        defaultTeacherShouldBeFound("id.equals=" + id);
        defaultTeacherShouldNotBeFound("id.notEquals=" + id);

        defaultTeacherShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTeacherShouldNotBeFound("id.greaterThan=" + id);

        defaultTeacherShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTeacherShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTeachersByFirstNameIsEqualToSomething() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where firstName equals to DEFAULT_FIRST_NAME
        defaultTeacherShouldBeFound("firstName.equals=" + DEFAULT_FIRST_NAME);

        // Get all the teacherList where firstName equals to UPDATED_FIRST_NAME
        defaultTeacherShouldNotBeFound("firstName.equals=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllTeachersByFirstNameIsInShouldWork() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where firstName in DEFAULT_FIRST_NAME or UPDATED_FIRST_NAME
        defaultTeacherShouldBeFound("firstName.in=" + DEFAULT_FIRST_NAME + "," + UPDATED_FIRST_NAME);

        // Get all the teacherList where firstName equals to UPDATED_FIRST_NAME
        defaultTeacherShouldNotBeFound("firstName.in=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllTeachersByFirstNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where firstName is not null
        defaultTeacherShouldBeFound("firstName.specified=true");

        // Get all the teacherList where firstName is null
        defaultTeacherShouldNotBeFound("firstName.specified=false");
    }

    @Test
    @Transactional
    void getAllTeachersByFirstNameContainsSomething() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where firstName contains DEFAULT_FIRST_NAME
        defaultTeacherShouldBeFound("firstName.contains=" + DEFAULT_FIRST_NAME);

        // Get all the teacherList where firstName contains UPDATED_FIRST_NAME
        defaultTeacherShouldNotBeFound("firstName.contains=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllTeachersByFirstNameNotContainsSomething() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where firstName does not contain DEFAULT_FIRST_NAME
        defaultTeacherShouldNotBeFound("firstName.doesNotContain=" + DEFAULT_FIRST_NAME);

        // Get all the teacherList where firstName does not contain UPDATED_FIRST_NAME
        defaultTeacherShouldBeFound("firstName.doesNotContain=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllTeachersByLastNameIsEqualToSomething() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where lastName equals to DEFAULT_LAST_NAME
        defaultTeacherShouldBeFound("lastName.equals=" + DEFAULT_LAST_NAME);

        // Get all the teacherList where lastName equals to UPDATED_LAST_NAME
        defaultTeacherShouldNotBeFound("lastName.equals=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllTeachersByLastNameIsInShouldWork() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where lastName in DEFAULT_LAST_NAME or UPDATED_LAST_NAME
        defaultTeacherShouldBeFound("lastName.in=" + DEFAULT_LAST_NAME + "," + UPDATED_LAST_NAME);

        // Get all the teacherList where lastName equals to UPDATED_LAST_NAME
        defaultTeacherShouldNotBeFound("lastName.in=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllTeachersByLastNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where lastName is not null
        defaultTeacherShouldBeFound("lastName.specified=true");

        // Get all the teacherList where lastName is null
        defaultTeacherShouldNotBeFound("lastName.specified=false");
    }

    @Test
    @Transactional
    void getAllTeachersByLastNameContainsSomething() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where lastName contains DEFAULT_LAST_NAME
        defaultTeacherShouldBeFound("lastName.contains=" + DEFAULT_LAST_NAME);

        // Get all the teacherList where lastName contains UPDATED_LAST_NAME
        defaultTeacherShouldNotBeFound("lastName.contains=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllTeachersByLastNameNotContainsSomething() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where lastName does not contain DEFAULT_LAST_NAME
        defaultTeacherShouldNotBeFound("lastName.doesNotContain=" + DEFAULT_LAST_NAME);

        // Get all the teacherList where lastName does not contain UPDATED_LAST_NAME
        defaultTeacherShouldBeFound("lastName.doesNotContain=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllTeachersByTeacherCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where teacherCode equals to DEFAULT_TEACHER_CODE
        defaultTeacherShouldBeFound("teacherCode.equals=" + DEFAULT_TEACHER_CODE);

        // Get all the teacherList where teacherCode equals to UPDATED_TEACHER_CODE
        defaultTeacherShouldNotBeFound("teacherCode.equals=" + UPDATED_TEACHER_CODE);
    }

    @Test
    @Transactional
    void getAllTeachersByTeacherCodeIsInShouldWork() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where teacherCode in DEFAULT_TEACHER_CODE or UPDATED_TEACHER_CODE
        defaultTeacherShouldBeFound("teacherCode.in=" + DEFAULT_TEACHER_CODE + "," + UPDATED_TEACHER_CODE);

        // Get all the teacherList where teacherCode equals to UPDATED_TEACHER_CODE
        defaultTeacherShouldNotBeFound("teacherCode.in=" + UPDATED_TEACHER_CODE);
    }

    @Test
    @Transactional
    void getAllTeachersByTeacherCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where teacherCode is not null
        defaultTeacherShouldBeFound("teacherCode.specified=true");

        // Get all the teacherList where teacherCode is null
        defaultTeacherShouldNotBeFound("teacherCode.specified=false");
    }

    @Test
    @Transactional
    void getAllTeachersByTeacherCodeContainsSomething() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where teacherCode contains DEFAULT_TEACHER_CODE
        defaultTeacherShouldBeFound("teacherCode.contains=" + DEFAULT_TEACHER_CODE);

        // Get all the teacherList where teacherCode contains UPDATED_TEACHER_CODE
        defaultTeacherShouldNotBeFound("teacherCode.contains=" + UPDATED_TEACHER_CODE);
    }

    @Test
    @Transactional
    void getAllTeachersByTeacherCodeNotContainsSomething() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where teacherCode does not contain DEFAULT_TEACHER_CODE
        defaultTeacherShouldNotBeFound("teacherCode.doesNotContain=" + DEFAULT_TEACHER_CODE);

        // Get all the teacherList where teacherCode does not contain UPDATED_TEACHER_CODE
        defaultTeacherShouldBeFound("teacherCode.doesNotContain=" + UPDATED_TEACHER_CODE);
    }

    @Test
    @Transactional
    void getAllTeachersByJobPositionIsEqualToSomething() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where jobPosition equals to DEFAULT_JOB_POSITION
        defaultTeacherShouldBeFound("jobPosition.equals=" + DEFAULT_JOB_POSITION);

        // Get all the teacherList where jobPosition equals to UPDATED_JOB_POSITION
        defaultTeacherShouldNotBeFound("jobPosition.equals=" + UPDATED_JOB_POSITION);
    }

    @Test
    @Transactional
    void getAllTeachersByJobPositionIsInShouldWork() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where jobPosition in DEFAULT_JOB_POSITION or UPDATED_JOB_POSITION
        defaultTeacherShouldBeFound("jobPosition.in=" + DEFAULT_JOB_POSITION + "," + UPDATED_JOB_POSITION);

        // Get all the teacherList where jobPosition equals to UPDATED_JOB_POSITION
        defaultTeacherShouldNotBeFound("jobPosition.in=" + UPDATED_JOB_POSITION);
    }

    @Test
    @Transactional
    void getAllTeachersByJobPositionIsNullOrNotNull() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where jobPosition is not null
        defaultTeacherShouldBeFound("jobPosition.specified=true");

        // Get all the teacherList where jobPosition is null
        defaultTeacherShouldNotBeFound("jobPosition.specified=false");
    }

    @Test
    @Transactional
    void getAllTeachersByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where email equals to DEFAULT_EMAIL
        defaultTeacherShouldBeFound("email.equals=" + DEFAULT_EMAIL);

        // Get all the teacherList where email equals to UPDATED_EMAIL
        defaultTeacherShouldNotBeFound("email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllTeachersByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where email in DEFAULT_EMAIL or UPDATED_EMAIL
        defaultTeacherShouldBeFound("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL);

        // Get all the teacherList where email equals to UPDATED_EMAIL
        defaultTeacherShouldNotBeFound("email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllTeachersByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where email is not null
        defaultTeacherShouldBeFound("email.specified=true");

        // Get all the teacherList where email is null
        defaultTeacherShouldNotBeFound("email.specified=false");
    }

    @Test
    @Transactional
    void getAllTeachersByEmailContainsSomething() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where email contains DEFAULT_EMAIL
        defaultTeacherShouldBeFound("email.contains=" + DEFAULT_EMAIL);

        // Get all the teacherList where email contains UPDATED_EMAIL
        defaultTeacherShouldNotBeFound("email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllTeachersByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where email does not contain DEFAULT_EMAIL
        defaultTeacherShouldNotBeFound("email.doesNotContain=" + DEFAULT_EMAIL);

        // Get all the teacherList where email does not contain UPDATED_EMAIL
        defaultTeacherShouldBeFound("email.doesNotContain=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllTeachersByPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where phone equals to DEFAULT_PHONE
        defaultTeacherShouldBeFound("phone.equals=" + DEFAULT_PHONE);

        // Get all the teacherList where phone equals to UPDATED_PHONE
        defaultTeacherShouldNotBeFound("phone.equals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllTeachersByPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where phone in DEFAULT_PHONE or UPDATED_PHONE
        defaultTeacherShouldBeFound("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE);

        // Get all the teacherList where phone equals to UPDATED_PHONE
        defaultTeacherShouldNotBeFound("phone.in=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllTeachersByPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where phone is not null
        defaultTeacherShouldBeFound("phone.specified=true");

        // Get all the teacherList where phone is null
        defaultTeacherShouldNotBeFound("phone.specified=false");
    }

    @Test
    @Transactional
    void getAllTeachersByPhoneContainsSomething() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where phone contains DEFAULT_PHONE
        defaultTeacherShouldBeFound("phone.contains=" + DEFAULT_PHONE);

        // Get all the teacherList where phone contains UPDATED_PHONE
        defaultTeacherShouldNotBeFound("phone.contains=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllTeachersByPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where phone does not contain DEFAULT_PHONE
        defaultTeacherShouldNotBeFound("phone.doesNotContain=" + DEFAULT_PHONE);

        // Get all the teacherList where phone does not contain UPDATED_PHONE
        defaultTeacherShouldBeFound("phone.doesNotContain=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllTeachersByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where createdBy equals to DEFAULT_CREATED_BY
        defaultTeacherShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the teacherList where createdBy equals to UPDATED_CREATED_BY
        defaultTeacherShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTeachersByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultTeacherShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the teacherList where createdBy equals to UPDATED_CREATED_BY
        defaultTeacherShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTeachersByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where createdBy is not null
        defaultTeacherShouldBeFound("createdBy.specified=true");

        // Get all the teacherList where createdBy is null
        defaultTeacherShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllTeachersByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where createdBy contains DEFAULT_CREATED_BY
        defaultTeacherShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the teacherList where createdBy contains UPDATED_CREATED_BY
        defaultTeacherShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTeachersByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where createdBy does not contain DEFAULT_CREATED_BY
        defaultTeacherShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the teacherList where createdBy does not contain UPDATED_CREATED_BY
        defaultTeacherShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTeachersByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where createdDate equals to DEFAULT_CREATED_DATE
        defaultTeacherShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the teacherList where createdDate equals to UPDATED_CREATED_DATE
        defaultTeacherShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllTeachersByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultTeacherShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the teacherList where createdDate equals to UPDATED_CREATED_DATE
        defaultTeacherShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllTeachersByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where createdDate is not null
        defaultTeacherShouldBeFound("createdDate.specified=true");

        // Get all the teacherList where createdDate is null
        defaultTeacherShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    void getAllTeachersByLastModifiedByIsEqualToSomething() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where lastModifiedBy equals to DEFAULT_LAST_MODIFIED_BY
        defaultTeacherShouldBeFound("lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the teacherList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultTeacherShouldNotBeFound("lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllTeachersByLastModifiedByIsInShouldWork() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where lastModifiedBy in DEFAULT_LAST_MODIFIED_BY or UPDATED_LAST_MODIFIED_BY
        defaultTeacherShouldBeFound("lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY);

        // Get all the teacherList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultTeacherShouldNotBeFound("lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllTeachersByLastModifiedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where lastModifiedBy is not null
        defaultTeacherShouldBeFound("lastModifiedBy.specified=true");

        // Get all the teacherList where lastModifiedBy is null
        defaultTeacherShouldNotBeFound("lastModifiedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllTeachersByLastModifiedByContainsSomething() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where lastModifiedBy contains DEFAULT_LAST_MODIFIED_BY
        defaultTeacherShouldBeFound("lastModifiedBy.contains=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the teacherList where lastModifiedBy contains UPDATED_LAST_MODIFIED_BY
        defaultTeacherShouldNotBeFound("lastModifiedBy.contains=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllTeachersByLastModifiedByNotContainsSomething() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where lastModifiedBy does not contain DEFAULT_LAST_MODIFIED_BY
        defaultTeacherShouldNotBeFound("lastModifiedBy.doesNotContain=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the teacherList where lastModifiedBy does not contain UPDATED_LAST_MODIFIED_BY
        defaultTeacherShouldBeFound("lastModifiedBy.doesNotContain=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllTeachersByLastModifiedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where lastModifiedDate equals to DEFAULT_LAST_MODIFIED_DATE
        defaultTeacherShouldBeFound("lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the teacherList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultTeacherShouldNotBeFound("lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllTeachersByLastModifiedDateIsInShouldWork() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where lastModifiedDate in DEFAULT_LAST_MODIFIED_DATE or UPDATED_LAST_MODIFIED_DATE
        defaultTeacherShouldBeFound("lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE);

        // Get all the teacherList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultTeacherShouldNotBeFound("lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllTeachersByLastModifiedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        // Get all the teacherList where lastModifiedDate is not null
        defaultTeacherShouldBeFound("lastModifiedDate.specified=true");

        // Get all the teacherList where lastModifiedDate is null
        defaultTeacherShouldNotBeFound("lastModifiedDate.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTeacherShouldBeFound(String filter) throws Exception {
        restTeacherMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(teacher.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].teacherCode").value(hasItem(DEFAULT_TEACHER_CODE)))
            .andExpect(jsonPath("$.[*].jobPosition").value(hasItem(DEFAULT_JOB_POSITION.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));

        // Check, that the count call also returns 1
        restTeacherMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTeacherShouldNotBeFound(String filter) throws Exception {
        restTeacherMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTeacherMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTeacher() throws Exception {
        // Get the teacher
        restTeacherMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTeacher() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        int databaseSizeBeforeUpdate = teacherRepository.findAll().size();

        // Update the teacher
        Teacher updatedTeacher = teacherRepository.findById(teacher.getId()).get();
        // Disconnect from session so that the updates on updatedTeacher are not directly saved in db
        em.detach(updatedTeacher);
        updatedTeacher
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .teacherCode(UPDATED_TEACHER_CODE)
            .jobPosition(UPDATED_JOB_POSITION)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        TeacherDTO teacherDTO = teacherMapper.toDto(updatedTeacher);

        restTeacherMockMvc
            .perform(
                put(ENTITY_API_URL_ID, teacherDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(teacherDTO))
            )
            .andExpect(status().isOk());

        // Validate the Teacher in the database
        List<Teacher> teacherList = teacherRepository.findAll();
        assertThat(teacherList).hasSize(databaseSizeBeforeUpdate);
        Teacher testTeacher = teacherList.get(teacherList.size() - 1);
        assertThat(testTeacher.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testTeacher.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testTeacher.getTeacherCode()).isEqualTo(UPDATED_TEACHER_CODE);
        assertThat(testTeacher.getJobPosition()).isEqualTo(UPDATED_JOB_POSITION);
        assertThat(testTeacher.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testTeacher.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testTeacher.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testTeacher.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testTeacher.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testTeacher.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void putNonExistingTeacher() throws Exception {
        int databaseSizeBeforeUpdate = teacherRepository.findAll().size();
        teacher.setId(count.incrementAndGet());

        // Create the Teacher
        TeacherDTO teacherDTO = teacherMapper.toDto(teacher);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTeacherMockMvc
            .perform(
                put(ENTITY_API_URL_ID, teacherDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(teacherDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Teacher in the database
        List<Teacher> teacherList = teacherRepository.findAll();
        assertThat(teacherList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTeacher() throws Exception {
        int databaseSizeBeforeUpdate = teacherRepository.findAll().size();
        teacher.setId(count.incrementAndGet());

        // Create the Teacher
        TeacherDTO teacherDTO = teacherMapper.toDto(teacher);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeacherMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(teacherDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Teacher in the database
        List<Teacher> teacherList = teacherRepository.findAll();
        assertThat(teacherList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTeacher() throws Exception {
        int databaseSizeBeforeUpdate = teacherRepository.findAll().size();
        teacher.setId(count.incrementAndGet());

        // Create the Teacher
        TeacherDTO teacherDTO = teacherMapper.toDto(teacher);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeacherMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(teacherDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Teacher in the database
        List<Teacher> teacherList = teacherRepository.findAll();
        assertThat(teacherList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTeacherWithPatch() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        int databaseSizeBeforeUpdate = teacherRepository.findAll().size();

        // Update the teacher using partial update
        Teacher partialUpdatedTeacher = new Teacher();
        partialUpdatedTeacher.setId(teacher.getId());

        partialUpdatedTeacher
            .firstName(UPDATED_FIRST_NAME)
            .teacherCode(UPDATED_TEACHER_CODE)
            .jobPosition(UPDATED_JOB_POSITION)
            .phone(UPDATED_PHONE)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE);

        restTeacherMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTeacher.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTeacher))
            )
            .andExpect(status().isOk());

        // Validate the Teacher in the database
        List<Teacher> teacherList = teacherRepository.findAll();
        assertThat(teacherList).hasSize(databaseSizeBeforeUpdate);
        Teacher testTeacher = teacherList.get(teacherList.size() - 1);
        assertThat(testTeacher.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testTeacher.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testTeacher.getTeacherCode()).isEqualTo(UPDATED_TEACHER_CODE);
        assertThat(testTeacher.getJobPosition()).isEqualTo(UPDATED_JOB_POSITION);
        assertThat(testTeacher.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testTeacher.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testTeacher.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testTeacher.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testTeacher.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
        assertThat(testTeacher.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void fullUpdateTeacherWithPatch() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        int databaseSizeBeforeUpdate = teacherRepository.findAll().size();

        // Update the teacher using partial update
        Teacher partialUpdatedTeacher = new Teacher();
        partialUpdatedTeacher.setId(teacher.getId());

        partialUpdatedTeacher
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .teacherCode(UPDATED_TEACHER_CODE)
            .jobPosition(UPDATED_JOB_POSITION)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);

        restTeacherMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTeacher.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTeacher))
            )
            .andExpect(status().isOk());

        // Validate the Teacher in the database
        List<Teacher> teacherList = teacherRepository.findAll();
        assertThat(teacherList).hasSize(databaseSizeBeforeUpdate);
        Teacher testTeacher = teacherList.get(teacherList.size() - 1);
        assertThat(testTeacher.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testTeacher.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testTeacher.getTeacherCode()).isEqualTo(UPDATED_TEACHER_CODE);
        assertThat(testTeacher.getJobPosition()).isEqualTo(UPDATED_JOB_POSITION);
        assertThat(testTeacher.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testTeacher.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testTeacher.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testTeacher.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testTeacher.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testTeacher.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingTeacher() throws Exception {
        int databaseSizeBeforeUpdate = teacherRepository.findAll().size();
        teacher.setId(count.incrementAndGet());

        // Create the Teacher
        TeacherDTO teacherDTO = teacherMapper.toDto(teacher);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTeacherMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, teacherDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(teacherDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Teacher in the database
        List<Teacher> teacherList = teacherRepository.findAll();
        assertThat(teacherList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTeacher() throws Exception {
        int databaseSizeBeforeUpdate = teacherRepository.findAll().size();
        teacher.setId(count.incrementAndGet());

        // Create the Teacher
        TeacherDTO teacherDTO = teacherMapper.toDto(teacher);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeacherMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(teacherDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Teacher in the database
        List<Teacher> teacherList = teacherRepository.findAll();
        assertThat(teacherList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTeacher() throws Exception {
        int databaseSizeBeforeUpdate = teacherRepository.findAll().size();
        teacher.setId(count.incrementAndGet());

        // Create the Teacher
        TeacherDTO teacherDTO = teacherMapper.toDto(teacher);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeacherMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(teacherDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Teacher in the database
        List<Teacher> teacherList = teacherRepository.findAll();
        assertThat(teacherList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTeacher() throws Exception {
        // Initialize the database
        teacherRepository.saveAndFlush(teacher);

        int databaseSizeBeforeDelete = teacherRepository.findAll().size();

        // Delete the teacher
        restTeacherMockMvc
            .perform(delete(ENTITY_API_URL_ID, teacher.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Teacher> teacherList = teacherRepository.findAll();
        assertThat(teacherList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
