package com.ocean.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ocean.IntegrationTest;
import com.ocean.domain.Student;
import com.ocean.domain.enumeration.LoginType;
import com.ocean.repository.StudentRepository;
import com.ocean.service.criteria.StudentCriteria;
import com.ocean.service.dto.StudentDTO;
import com.ocean.service.mapper.StudentMapper;
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
 * Integration tests for the {@link StudentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StudentResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_STUDENT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_STUDENT_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final LoginType DEFAULT_LOGIN_TYPE = LoginType.MUST;
    private static final LoginType UPDATED_LOGIN_TYPE = LoginType.GOOGLE;

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/students";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStudentMockMvc;

    private Student student;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Student createEntity(EntityManager em) {
        Student student = new Student()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .studentCode(DEFAULT_STUDENT_CODE)
            .email(DEFAULT_EMAIL)
            .phone(DEFAULT_PHONE)
            .loginType(DEFAULT_LOGIN_TYPE)
            .createdBy(DEFAULT_CREATED_BY)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE);
        return student;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Student createUpdatedEntity(EntityManager em) {
        Student student = new Student()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .studentCode(UPDATED_STUDENT_CODE)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .loginType(UPDATED_LOGIN_TYPE)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        return student;
    }

    @BeforeEach
    public void initTest() {
        student = createEntity(em);
    }

    @Test
    @Transactional
    void createStudent() throws Exception {
        int databaseSizeBeforeCreate = studentRepository.findAll().size();
        // Create the Student
        StudentDTO studentDTO = studentMapper.toDto(student);
        restStudentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(studentDTO)))
            .andExpect(status().isCreated());

        // Validate the Student in the database
        List<Student> studentList = studentRepository.findAll();
        assertThat(studentList).hasSize(databaseSizeBeforeCreate + 1);
        Student testStudent = studentList.get(studentList.size() - 1);
        assertThat(testStudent.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testStudent.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testStudent.getStudentCode()).isEqualTo(DEFAULT_STUDENT_CODE);
        assertThat(testStudent.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testStudent.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testStudent.getLoginType()).isEqualTo(DEFAULT_LOGIN_TYPE);
        assertThat(testStudent.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testStudent.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testStudent.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
        assertThat(testStudent.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void createStudentWithExistingId() throws Exception {
        // Create the Student with an existing ID
        student.setId(1L);
        StudentDTO studentDTO = studentMapper.toDto(student);

        int databaseSizeBeforeCreate = studentRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStudentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(studentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Student in the database
        List<Student> studentList = studentRepository.findAll();
        assertThat(studentList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllStudents() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList
        restStudentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(student.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].studentCode").value(hasItem(DEFAULT_STUDENT_CODE)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].loginType").value(hasItem(DEFAULT_LOGIN_TYPE.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));
    }

    @Test
    @Transactional
    void getStudent() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get the student
        restStudentMockMvc
            .perform(get(ENTITY_API_URL_ID, student.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(student.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.studentCode").value(DEFAULT_STUDENT_CODE))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.loginType").value(DEFAULT_LOGIN_TYPE.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()));
    }

    @Test
    @Transactional
    void getStudentsByIdFiltering() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        Long id = student.getId();

        defaultStudentShouldBeFound("id.equals=" + id);
        defaultStudentShouldNotBeFound("id.notEquals=" + id);

        defaultStudentShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultStudentShouldNotBeFound("id.greaterThan=" + id);

        defaultStudentShouldBeFound("id.lessThanOrEqual=" + id);
        defaultStudentShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllStudentsByFirstNameIsEqualToSomething() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where firstName equals to DEFAULT_FIRST_NAME
        defaultStudentShouldBeFound("firstName.equals=" + DEFAULT_FIRST_NAME);

        // Get all the studentList where firstName equals to UPDATED_FIRST_NAME
        defaultStudentShouldNotBeFound("firstName.equals=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllStudentsByFirstNameIsInShouldWork() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where firstName in DEFAULT_FIRST_NAME or UPDATED_FIRST_NAME
        defaultStudentShouldBeFound("firstName.in=" + DEFAULT_FIRST_NAME + "," + UPDATED_FIRST_NAME);

        // Get all the studentList where firstName equals to UPDATED_FIRST_NAME
        defaultStudentShouldNotBeFound("firstName.in=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllStudentsByFirstNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where firstName is not null
        defaultStudentShouldBeFound("firstName.specified=true");

        // Get all the studentList where firstName is null
        defaultStudentShouldNotBeFound("firstName.specified=false");
    }

    @Test
    @Transactional
    void getAllStudentsByFirstNameContainsSomething() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where firstName contains DEFAULT_FIRST_NAME
        defaultStudentShouldBeFound("firstName.contains=" + DEFAULT_FIRST_NAME);

        // Get all the studentList where firstName contains UPDATED_FIRST_NAME
        defaultStudentShouldNotBeFound("firstName.contains=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllStudentsByFirstNameNotContainsSomething() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where firstName does not contain DEFAULT_FIRST_NAME
        defaultStudentShouldNotBeFound("firstName.doesNotContain=" + DEFAULT_FIRST_NAME);

        // Get all the studentList where firstName does not contain UPDATED_FIRST_NAME
        defaultStudentShouldBeFound("firstName.doesNotContain=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllStudentsByLastNameIsEqualToSomething() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where lastName equals to DEFAULT_LAST_NAME
        defaultStudentShouldBeFound("lastName.equals=" + DEFAULT_LAST_NAME);

        // Get all the studentList where lastName equals to UPDATED_LAST_NAME
        defaultStudentShouldNotBeFound("lastName.equals=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllStudentsByLastNameIsInShouldWork() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where lastName in DEFAULT_LAST_NAME or UPDATED_LAST_NAME
        defaultStudentShouldBeFound("lastName.in=" + DEFAULT_LAST_NAME + "," + UPDATED_LAST_NAME);

        // Get all the studentList where lastName equals to UPDATED_LAST_NAME
        defaultStudentShouldNotBeFound("lastName.in=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllStudentsByLastNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where lastName is not null
        defaultStudentShouldBeFound("lastName.specified=true");

        // Get all the studentList where lastName is null
        defaultStudentShouldNotBeFound("lastName.specified=false");
    }

    @Test
    @Transactional
    void getAllStudentsByLastNameContainsSomething() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where lastName contains DEFAULT_LAST_NAME
        defaultStudentShouldBeFound("lastName.contains=" + DEFAULT_LAST_NAME);

        // Get all the studentList where lastName contains UPDATED_LAST_NAME
        defaultStudentShouldNotBeFound("lastName.contains=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllStudentsByLastNameNotContainsSomething() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where lastName does not contain DEFAULT_LAST_NAME
        defaultStudentShouldNotBeFound("lastName.doesNotContain=" + DEFAULT_LAST_NAME);

        // Get all the studentList where lastName does not contain UPDATED_LAST_NAME
        defaultStudentShouldBeFound("lastName.doesNotContain=" + UPDATED_LAST_NAME);
    }

    @Test
    @Transactional
    void getAllStudentsByStudentCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where studentCode equals to DEFAULT_STUDENT_CODE
        defaultStudentShouldBeFound("studentCode.equals=" + DEFAULT_STUDENT_CODE);

        // Get all the studentList where studentCode equals to UPDATED_STUDENT_CODE
        defaultStudentShouldNotBeFound("studentCode.equals=" + UPDATED_STUDENT_CODE);
    }

    @Test
    @Transactional
    void getAllStudentsByStudentCodeIsInShouldWork() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where studentCode in DEFAULT_STUDENT_CODE or UPDATED_STUDENT_CODE
        defaultStudentShouldBeFound("studentCode.in=" + DEFAULT_STUDENT_CODE + "," + UPDATED_STUDENT_CODE);

        // Get all the studentList where studentCode equals to UPDATED_STUDENT_CODE
        defaultStudentShouldNotBeFound("studentCode.in=" + UPDATED_STUDENT_CODE);
    }

    @Test
    @Transactional
    void getAllStudentsByStudentCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where studentCode is not null
        defaultStudentShouldBeFound("studentCode.specified=true");

        // Get all the studentList where studentCode is null
        defaultStudentShouldNotBeFound("studentCode.specified=false");
    }

    @Test
    @Transactional
    void getAllStudentsByStudentCodeContainsSomething() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where studentCode contains DEFAULT_STUDENT_CODE
        defaultStudentShouldBeFound("studentCode.contains=" + DEFAULT_STUDENT_CODE);

        // Get all the studentList where studentCode contains UPDATED_STUDENT_CODE
        defaultStudentShouldNotBeFound("studentCode.contains=" + UPDATED_STUDENT_CODE);
    }

    @Test
    @Transactional
    void getAllStudentsByStudentCodeNotContainsSomething() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where studentCode does not contain DEFAULT_STUDENT_CODE
        defaultStudentShouldNotBeFound("studentCode.doesNotContain=" + DEFAULT_STUDENT_CODE);

        // Get all the studentList where studentCode does not contain UPDATED_STUDENT_CODE
        defaultStudentShouldBeFound("studentCode.doesNotContain=" + UPDATED_STUDENT_CODE);
    }

    @Test
    @Transactional
    void getAllStudentsByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where email equals to DEFAULT_EMAIL
        defaultStudentShouldBeFound("email.equals=" + DEFAULT_EMAIL);

        // Get all the studentList where email equals to UPDATED_EMAIL
        defaultStudentShouldNotBeFound("email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllStudentsByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where email in DEFAULT_EMAIL or UPDATED_EMAIL
        defaultStudentShouldBeFound("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL);

        // Get all the studentList where email equals to UPDATED_EMAIL
        defaultStudentShouldNotBeFound("email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllStudentsByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where email is not null
        defaultStudentShouldBeFound("email.specified=true");

        // Get all the studentList where email is null
        defaultStudentShouldNotBeFound("email.specified=false");
    }

    @Test
    @Transactional
    void getAllStudentsByEmailContainsSomething() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where email contains DEFAULT_EMAIL
        defaultStudentShouldBeFound("email.contains=" + DEFAULT_EMAIL);

        // Get all the studentList where email contains UPDATED_EMAIL
        defaultStudentShouldNotBeFound("email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllStudentsByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where email does not contain DEFAULT_EMAIL
        defaultStudentShouldNotBeFound("email.doesNotContain=" + DEFAULT_EMAIL);

        // Get all the studentList where email does not contain UPDATED_EMAIL
        defaultStudentShouldBeFound("email.doesNotContain=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllStudentsByPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where phone equals to DEFAULT_PHONE
        defaultStudentShouldBeFound("phone.equals=" + DEFAULT_PHONE);

        // Get all the studentList where phone equals to UPDATED_PHONE
        defaultStudentShouldNotBeFound("phone.equals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllStudentsByPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where phone in DEFAULT_PHONE or UPDATED_PHONE
        defaultStudentShouldBeFound("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE);

        // Get all the studentList where phone equals to UPDATED_PHONE
        defaultStudentShouldNotBeFound("phone.in=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllStudentsByPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where phone is not null
        defaultStudentShouldBeFound("phone.specified=true");

        // Get all the studentList where phone is null
        defaultStudentShouldNotBeFound("phone.specified=false");
    }

    @Test
    @Transactional
    void getAllStudentsByPhoneContainsSomething() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where phone contains DEFAULT_PHONE
        defaultStudentShouldBeFound("phone.contains=" + DEFAULT_PHONE);

        // Get all the studentList where phone contains UPDATED_PHONE
        defaultStudentShouldNotBeFound("phone.contains=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllStudentsByPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where phone does not contain DEFAULT_PHONE
        defaultStudentShouldNotBeFound("phone.doesNotContain=" + DEFAULT_PHONE);

        // Get all the studentList where phone does not contain UPDATED_PHONE
        defaultStudentShouldBeFound("phone.doesNotContain=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllStudentsByLoginTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where loginType equals to DEFAULT_LOGIN_TYPE
        defaultStudentShouldBeFound("loginType.equals=" + DEFAULT_LOGIN_TYPE);

        // Get all the studentList where loginType equals to UPDATED_LOGIN_TYPE
        defaultStudentShouldNotBeFound("loginType.equals=" + UPDATED_LOGIN_TYPE);
    }

    @Test
    @Transactional
    void getAllStudentsByLoginTypeIsInShouldWork() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where loginType in DEFAULT_LOGIN_TYPE or UPDATED_LOGIN_TYPE
        defaultStudentShouldBeFound("loginType.in=" + DEFAULT_LOGIN_TYPE + "," + UPDATED_LOGIN_TYPE);

        // Get all the studentList where loginType equals to UPDATED_LOGIN_TYPE
        defaultStudentShouldNotBeFound("loginType.in=" + UPDATED_LOGIN_TYPE);
    }

    @Test
    @Transactional
    void getAllStudentsByLoginTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where loginType is not null
        defaultStudentShouldBeFound("loginType.specified=true");

        // Get all the studentList where loginType is null
        defaultStudentShouldNotBeFound("loginType.specified=false");
    }

    @Test
    @Transactional
    void getAllStudentsByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where createdBy equals to DEFAULT_CREATED_BY
        defaultStudentShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the studentList where createdBy equals to UPDATED_CREATED_BY
        defaultStudentShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllStudentsByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultStudentShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the studentList where createdBy equals to UPDATED_CREATED_BY
        defaultStudentShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllStudentsByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where createdBy is not null
        defaultStudentShouldBeFound("createdBy.specified=true");

        // Get all the studentList where createdBy is null
        defaultStudentShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllStudentsByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where createdBy contains DEFAULT_CREATED_BY
        defaultStudentShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the studentList where createdBy contains UPDATED_CREATED_BY
        defaultStudentShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllStudentsByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where createdBy does not contain DEFAULT_CREATED_BY
        defaultStudentShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the studentList where createdBy does not contain UPDATED_CREATED_BY
        defaultStudentShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllStudentsByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where createdDate equals to DEFAULT_CREATED_DATE
        defaultStudentShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the studentList where createdDate equals to UPDATED_CREATED_DATE
        defaultStudentShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllStudentsByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultStudentShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the studentList where createdDate equals to UPDATED_CREATED_DATE
        defaultStudentShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllStudentsByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where createdDate is not null
        defaultStudentShouldBeFound("createdDate.specified=true");

        // Get all the studentList where createdDate is null
        defaultStudentShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    void getAllStudentsByLastModifiedByIsEqualToSomething() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where lastModifiedBy equals to DEFAULT_LAST_MODIFIED_BY
        defaultStudentShouldBeFound("lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the studentList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultStudentShouldNotBeFound("lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllStudentsByLastModifiedByIsInShouldWork() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where lastModifiedBy in DEFAULT_LAST_MODIFIED_BY or UPDATED_LAST_MODIFIED_BY
        defaultStudentShouldBeFound("lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY);

        // Get all the studentList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultStudentShouldNotBeFound("lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllStudentsByLastModifiedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where lastModifiedBy is not null
        defaultStudentShouldBeFound("lastModifiedBy.specified=true");

        // Get all the studentList where lastModifiedBy is null
        defaultStudentShouldNotBeFound("lastModifiedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllStudentsByLastModifiedByContainsSomething() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where lastModifiedBy contains DEFAULT_LAST_MODIFIED_BY
        defaultStudentShouldBeFound("lastModifiedBy.contains=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the studentList where lastModifiedBy contains UPDATED_LAST_MODIFIED_BY
        defaultStudentShouldNotBeFound("lastModifiedBy.contains=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllStudentsByLastModifiedByNotContainsSomething() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where lastModifiedBy does not contain DEFAULT_LAST_MODIFIED_BY
        defaultStudentShouldNotBeFound("lastModifiedBy.doesNotContain=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the studentList where lastModifiedBy does not contain UPDATED_LAST_MODIFIED_BY
        defaultStudentShouldBeFound("lastModifiedBy.doesNotContain=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllStudentsByLastModifiedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where lastModifiedDate equals to DEFAULT_LAST_MODIFIED_DATE
        defaultStudentShouldBeFound("lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the studentList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultStudentShouldNotBeFound("lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllStudentsByLastModifiedDateIsInShouldWork() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where lastModifiedDate in DEFAULT_LAST_MODIFIED_DATE or UPDATED_LAST_MODIFIED_DATE
        defaultStudentShouldBeFound("lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE);

        // Get all the studentList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultStudentShouldNotBeFound("lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllStudentsByLastModifiedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        // Get all the studentList where lastModifiedDate is not null
        defaultStudentShouldBeFound("lastModifiedDate.specified=true");

        // Get all the studentList where lastModifiedDate is null
        defaultStudentShouldNotBeFound("lastModifiedDate.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultStudentShouldBeFound(String filter) throws Exception {
        restStudentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(student.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].studentCode").value(hasItem(DEFAULT_STUDENT_CODE)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].loginType").value(hasItem(DEFAULT_LOGIN_TYPE.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));

        // Check, that the count call also returns 1
        restStudentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultStudentShouldNotBeFound(String filter) throws Exception {
        restStudentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restStudentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingStudent() throws Exception {
        // Get the student
        restStudentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStudent() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        int databaseSizeBeforeUpdate = studentRepository.findAll().size();

        // Update the student
        Student updatedStudent = studentRepository.findById(student.getId()).get();
        // Disconnect from session so that the updates on updatedStudent are not directly saved in db
        em.detach(updatedStudent);
        updatedStudent
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .studentCode(UPDATED_STUDENT_CODE)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .loginType(UPDATED_LOGIN_TYPE)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        StudentDTO studentDTO = studentMapper.toDto(updatedStudent);

        restStudentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, studentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(studentDTO))
            )
            .andExpect(status().isOk());

        // Validate the Student in the database
        List<Student> studentList = studentRepository.findAll();
        assertThat(studentList).hasSize(databaseSizeBeforeUpdate);
        Student testStudent = studentList.get(studentList.size() - 1);
        assertThat(testStudent.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testStudent.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testStudent.getStudentCode()).isEqualTo(UPDATED_STUDENT_CODE);
        assertThat(testStudent.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testStudent.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testStudent.getLoginType()).isEqualTo(UPDATED_LOGIN_TYPE);
        assertThat(testStudent.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testStudent.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testStudent.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testStudent.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void putNonExistingStudent() throws Exception {
        int databaseSizeBeforeUpdate = studentRepository.findAll().size();
        student.setId(count.incrementAndGet());

        // Create the Student
        StudentDTO studentDTO = studentMapper.toDto(student);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStudentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, studentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(studentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Student in the database
        List<Student> studentList = studentRepository.findAll();
        assertThat(studentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStudent() throws Exception {
        int databaseSizeBeforeUpdate = studentRepository.findAll().size();
        student.setId(count.incrementAndGet());

        // Create the Student
        StudentDTO studentDTO = studentMapper.toDto(student);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStudentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(studentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Student in the database
        List<Student> studentList = studentRepository.findAll();
        assertThat(studentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStudent() throws Exception {
        int databaseSizeBeforeUpdate = studentRepository.findAll().size();
        student.setId(count.incrementAndGet());

        // Create the Student
        StudentDTO studentDTO = studentMapper.toDto(student);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStudentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(studentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Student in the database
        List<Student> studentList = studentRepository.findAll();
        assertThat(studentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStudentWithPatch() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        int databaseSizeBeforeUpdate = studentRepository.findAll().size();

        // Update the student using partial update
        Student partialUpdatedStudent = new Student();
        partialUpdatedStudent.setId(student.getId());

        partialUpdatedStudent
            .studentCode(UPDATED_STUDENT_CODE)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);

        restStudentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStudent.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStudent))
            )
            .andExpect(status().isOk());

        // Validate the Student in the database
        List<Student> studentList = studentRepository.findAll();
        assertThat(studentList).hasSize(databaseSizeBeforeUpdate);
        Student testStudent = studentList.get(studentList.size() - 1);
        assertThat(testStudent.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testStudent.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testStudent.getStudentCode()).isEqualTo(UPDATED_STUDENT_CODE);
        assertThat(testStudent.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testStudent.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testStudent.getLoginType()).isEqualTo(DEFAULT_LOGIN_TYPE);
        assertThat(testStudent.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testStudent.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testStudent.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testStudent.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void fullUpdateStudentWithPatch() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        int databaseSizeBeforeUpdate = studentRepository.findAll().size();

        // Update the student using partial update
        Student partialUpdatedStudent = new Student();
        partialUpdatedStudent.setId(student.getId());

        partialUpdatedStudent
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .studentCode(UPDATED_STUDENT_CODE)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .loginType(UPDATED_LOGIN_TYPE)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);

        restStudentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStudent.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStudent))
            )
            .andExpect(status().isOk());

        // Validate the Student in the database
        List<Student> studentList = studentRepository.findAll();
        assertThat(studentList).hasSize(databaseSizeBeforeUpdate);
        Student testStudent = studentList.get(studentList.size() - 1);
        assertThat(testStudent.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testStudent.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testStudent.getStudentCode()).isEqualTo(UPDATED_STUDENT_CODE);
        assertThat(testStudent.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testStudent.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testStudent.getLoginType()).isEqualTo(UPDATED_LOGIN_TYPE);
        assertThat(testStudent.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testStudent.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testStudent.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testStudent.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingStudent() throws Exception {
        int databaseSizeBeforeUpdate = studentRepository.findAll().size();
        student.setId(count.incrementAndGet());

        // Create the Student
        StudentDTO studentDTO = studentMapper.toDto(student);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStudentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, studentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(studentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Student in the database
        List<Student> studentList = studentRepository.findAll();
        assertThat(studentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStudent() throws Exception {
        int databaseSizeBeforeUpdate = studentRepository.findAll().size();
        student.setId(count.incrementAndGet());

        // Create the Student
        StudentDTO studentDTO = studentMapper.toDto(student);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStudentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(studentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Student in the database
        List<Student> studentList = studentRepository.findAll();
        assertThat(studentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStudent() throws Exception {
        int databaseSizeBeforeUpdate = studentRepository.findAll().size();
        student.setId(count.incrementAndGet());

        // Create the Student
        StudentDTO studentDTO = studentMapper.toDto(student);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStudentMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(studentDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Student in the database
        List<Student> studentList = studentRepository.findAll();
        assertThat(studentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStudent() throws Exception {
        // Initialize the database
        studentRepository.saveAndFlush(student);

        int databaseSizeBeforeDelete = studentRepository.findAll().size();

        // Delete the student
        restStudentMockMvc
            .perform(delete(ENTITY_API_URL_ID, student.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Student> studentList = studentRepository.findAll();
        assertThat(studentList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
