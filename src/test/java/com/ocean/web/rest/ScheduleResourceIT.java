package com.ocean.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ocean.IntegrationTest;
import com.ocean.domain.Lesson;
import com.ocean.domain.LessonTime;
import com.ocean.domain.Room;
import com.ocean.domain.Schedule;
import com.ocean.domain.Teacher;
import com.ocean.domain.enumeration.LessonType;
import com.ocean.domain.enumeration.WeekDay;
import com.ocean.repository.ScheduleRepository;
import com.ocean.service.criteria.ScheduleCriteria;
import com.ocean.service.dto.ScheduleDTO;
import com.ocean.service.mapper.ScheduleMapper;
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
 * Integration tests for the {@link ScheduleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ScheduleResourceIT {

    private static final LessonType DEFAULT_LESSON_TYPE = LessonType.LECTURE;
    private static final LessonType UPDATED_LESSON_TYPE = LessonType.SEMINAR;

    private static final WeekDay DEFAULT_WEEK_DAY = WeekDay.MONDAY;
    private static final WeekDay UPDATED_WEEK_DAY = WeekDay.TUESDAY;

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/schedules";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restScheduleMockMvc;

    private Schedule schedule;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Schedule createEntity(EntityManager em) {
        Schedule schedule = new Schedule()
            .lessonType(DEFAULT_LESSON_TYPE)
            .weekDay(DEFAULT_WEEK_DAY)
            .createdBy(DEFAULT_CREATED_BY)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE);
        return schedule;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Schedule createUpdatedEntity(EntityManager em) {
        Schedule schedule = new Schedule()
            .lessonType(UPDATED_LESSON_TYPE)
            .weekDay(UPDATED_WEEK_DAY)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        return schedule;
    }

    @BeforeEach
    public void initTest() {
        schedule = createEntity(em);
    }

    @Test
    @Transactional
    void createSchedule() throws Exception {
        int databaseSizeBeforeCreate = scheduleRepository.findAll().size();
        // Create the Schedule
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);
        restScheduleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(scheduleDTO)))
            .andExpect(status().isCreated());

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll();
        assertThat(scheduleList).hasSize(databaseSizeBeforeCreate + 1);
        Schedule testSchedule = scheduleList.get(scheduleList.size() - 1);
        assertThat(testSchedule.getLessonType()).isEqualTo(DEFAULT_LESSON_TYPE);
        assertThat(testSchedule.getWeekDay()).isEqualTo(DEFAULT_WEEK_DAY);
        assertThat(testSchedule.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testSchedule.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testSchedule.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
        assertThat(testSchedule.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void createScheduleWithExistingId() throws Exception {
        // Create the Schedule with an existing ID
        schedule.setId(1L);
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        int databaseSizeBeforeCreate = scheduleRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restScheduleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(scheduleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll();
        assertThat(scheduleList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSchedules() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList
        restScheduleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(schedule.getId().intValue())))
            .andExpect(jsonPath("$.[*].lessonType").value(hasItem(DEFAULT_LESSON_TYPE.toString())))
            .andExpect(jsonPath("$.[*].weekDay").value(hasItem(DEFAULT_WEEK_DAY.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));
    }

    @Test
    @Transactional
    void getSchedule() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        // Get the schedule
        restScheduleMockMvc
            .perform(get(ENTITY_API_URL_ID, schedule.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(schedule.getId().intValue()))
            .andExpect(jsonPath("$.lessonType").value(DEFAULT_LESSON_TYPE.toString()))
            .andExpect(jsonPath("$.weekDay").value(DEFAULT_WEEK_DAY.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()));
    }

    @Test
    @Transactional
    void getSchedulesByIdFiltering() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        Long id = schedule.getId();

        defaultScheduleShouldBeFound("id.equals=" + id);
        defaultScheduleShouldNotBeFound("id.notEquals=" + id);

        defaultScheduleShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultScheduleShouldNotBeFound("id.greaterThan=" + id);

        defaultScheduleShouldBeFound("id.lessThanOrEqual=" + id);
        defaultScheduleShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSchedulesByLessonTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where lessonType equals to DEFAULT_LESSON_TYPE
        defaultScheduleShouldBeFound("lessonType.equals=" + DEFAULT_LESSON_TYPE);

        // Get all the scheduleList where lessonType equals to UPDATED_LESSON_TYPE
        defaultScheduleShouldNotBeFound("lessonType.equals=" + UPDATED_LESSON_TYPE);
    }

    @Test
    @Transactional
    void getAllSchedulesByLessonTypeIsInShouldWork() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where lessonType in DEFAULT_LESSON_TYPE or UPDATED_LESSON_TYPE
        defaultScheduleShouldBeFound("lessonType.in=" + DEFAULT_LESSON_TYPE + "," + UPDATED_LESSON_TYPE);

        // Get all the scheduleList where lessonType equals to UPDATED_LESSON_TYPE
        defaultScheduleShouldNotBeFound("lessonType.in=" + UPDATED_LESSON_TYPE);
    }

    @Test
    @Transactional
    void getAllSchedulesByLessonTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where lessonType is not null
        defaultScheduleShouldBeFound("lessonType.specified=true");

        // Get all the scheduleList where lessonType is null
        defaultScheduleShouldNotBeFound("lessonType.specified=false");
    }

    @Test
    @Transactional
    void getAllSchedulesByWeekDayIsEqualToSomething() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where weekDay equals to DEFAULT_WEEK_DAY
        defaultScheduleShouldBeFound("weekDay.equals=" + DEFAULT_WEEK_DAY);

        // Get all the scheduleList where weekDay equals to UPDATED_WEEK_DAY
        defaultScheduleShouldNotBeFound("weekDay.equals=" + UPDATED_WEEK_DAY);
    }

    @Test
    @Transactional
    void getAllSchedulesByWeekDayIsInShouldWork() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where weekDay in DEFAULT_WEEK_DAY or UPDATED_WEEK_DAY
        defaultScheduleShouldBeFound("weekDay.in=" + DEFAULT_WEEK_DAY + "," + UPDATED_WEEK_DAY);

        // Get all the scheduleList where weekDay equals to UPDATED_WEEK_DAY
        defaultScheduleShouldNotBeFound("weekDay.in=" + UPDATED_WEEK_DAY);
    }

    @Test
    @Transactional
    void getAllSchedulesByWeekDayIsNullOrNotNull() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where weekDay is not null
        defaultScheduleShouldBeFound("weekDay.specified=true");

        // Get all the scheduleList where weekDay is null
        defaultScheduleShouldNotBeFound("weekDay.specified=false");
    }

    @Test
    @Transactional
    void getAllSchedulesByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where createdBy equals to DEFAULT_CREATED_BY
        defaultScheduleShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the scheduleList where createdBy equals to UPDATED_CREATED_BY
        defaultScheduleShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllSchedulesByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultScheduleShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the scheduleList where createdBy equals to UPDATED_CREATED_BY
        defaultScheduleShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllSchedulesByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where createdBy is not null
        defaultScheduleShouldBeFound("createdBy.specified=true");

        // Get all the scheduleList where createdBy is null
        defaultScheduleShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllSchedulesByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where createdBy contains DEFAULT_CREATED_BY
        defaultScheduleShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the scheduleList where createdBy contains UPDATED_CREATED_BY
        defaultScheduleShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllSchedulesByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where createdBy does not contain DEFAULT_CREATED_BY
        defaultScheduleShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the scheduleList where createdBy does not contain UPDATED_CREATED_BY
        defaultScheduleShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllSchedulesByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where createdDate equals to DEFAULT_CREATED_DATE
        defaultScheduleShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the scheduleList where createdDate equals to UPDATED_CREATED_DATE
        defaultScheduleShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllSchedulesByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultScheduleShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the scheduleList where createdDate equals to UPDATED_CREATED_DATE
        defaultScheduleShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllSchedulesByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where createdDate is not null
        defaultScheduleShouldBeFound("createdDate.specified=true");

        // Get all the scheduleList where createdDate is null
        defaultScheduleShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    void getAllSchedulesByLastModifiedByIsEqualToSomething() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where lastModifiedBy equals to DEFAULT_LAST_MODIFIED_BY
        defaultScheduleShouldBeFound("lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the scheduleList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultScheduleShouldNotBeFound("lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllSchedulesByLastModifiedByIsInShouldWork() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where lastModifiedBy in DEFAULT_LAST_MODIFIED_BY or UPDATED_LAST_MODIFIED_BY
        defaultScheduleShouldBeFound("lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY);

        // Get all the scheduleList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultScheduleShouldNotBeFound("lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllSchedulesByLastModifiedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where lastModifiedBy is not null
        defaultScheduleShouldBeFound("lastModifiedBy.specified=true");

        // Get all the scheduleList where lastModifiedBy is null
        defaultScheduleShouldNotBeFound("lastModifiedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllSchedulesByLastModifiedByContainsSomething() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where lastModifiedBy contains DEFAULT_LAST_MODIFIED_BY
        defaultScheduleShouldBeFound("lastModifiedBy.contains=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the scheduleList where lastModifiedBy contains UPDATED_LAST_MODIFIED_BY
        defaultScheduleShouldNotBeFound("lastModifiedBy.contains=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllSchedulesByLastModifiedByNotContainsSomething() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where lastModifiedBy does not contain DEFAULT_LAST_MODIFIED_BY
        defaultScheduleShouldNotBeFound("lastModifiedBy.doesNotContain=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the scheduleList where lastModifiedBy does not contain UPDATED_LAST_MODIFIED_BY
        defaultScheduleShouldBeFound("lastModifiedBy.doesNotContain=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllSchedulesByLastModifiedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where lastModifiedDate equals to DEFAULT_LAST_MODIFIED_DATE
        defaultScheduleShouldBeFound("lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the scheduleList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultScheduleShouldNotBeFound("lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllSchedulesByLastModifiedDateIsInShouldWork() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where lastModifiedDate in DEFAULT_LAST_MODIFIED_DATE or UPDATED_LAST_MODIFIED_DATE
        defaultScheduleShouldBeFound("lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE);

        // Get all the scheduleList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultScheduleShouldNotBeFound("lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllSchedulesByLastModifiedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        // Get all the scheduleList where lastModifiedDate is not null
        defaultScheduleShouldBeFound("lastModifiedDate.specified=true");

        // Get all the scheduleList where lastModifiedDate is null
        defaultScheduleShouldNotBeFound("lastModifiedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllSchedulesByRoomIsEqualToSomething() throws Exception {
        Room room;
        if (TestUtil.findAll(em, Room.class).isEmpty()) {
            scheduleRepository.saveAndFlush(schedule);
            room = RoomResourceIT.createEntity(em);
        } else {
            room = TestUtil.findAll(em, Room.class).get(0);
        }
        em.persist(room);
        em.flush();
        schedule.setRoom(room);
        scheduleRepository.saveAndFlush(schedule);
        Long roomId = room.getId();

        // Get all the scheduleList where room equals to roomId
        defaultScheduleShouldBeFound("roomId.equals=" + roomId);

        // Get all the scheduleList where room equals to (roomId + 1)
        defaultScheduleShouldNotBeFound("roomId.equals=" + (roomId + 1));
    }

    @Test
    @Transactional
    void getAllSchedulesByLessonTimeIsEqualToSomething() throws Exception {
        LessonTime lessonTime;
        if (TestUtil.findAll(em, LessonTime.class).isEmpty()) {
            scheduleRepository.saveAndFlush(schedule);
            lessonTime = LessonTimeResourceIT.createEntity(em);
        } else {
            lessonTime = TestUtil.findAll(em, LessonTime.class).get(0);
        }
        em.persist(lessonTime);
        em.flush();
        schedule.setLessonTime(lessonTime);
        scheduleRepository.saveAndFlush(schedule);
        Long lessonTimeId = lessonTime.getId();

        // Get all the scheduleList where lessonTime equals to lessonTimeId
        defaultScheduleShouldBeFound("lessonTimeId.equals=" + lessonTimeId);

        // Get all the scheduleList where lessonTime equals to (lessonTimeId + 1)
        defaultScheduleShouldNotBeFound("lessonTimeId.equals=" + (lessonTimeId + 1));
    }

    @Test
    @Transactional
    void getAllSchedulesByLessonIsEqualToSomething() throws Exception {
        Lesson lesson;
        if (TestUtil.findAll(em, Lesson.class).isEmpty()) {
            scheduleRepository.saveAndFlush(schedule);
            lesson = LessonResourceIT.createEntity(em);
        } else {
            lesson = TestUtil.findAll(em, Lesson.class).get(0);
        }
        em.persist(lesson);
        em.flush();
        schedule.setLesson(lesson);
        scheduleRepository.saveAndFlush(schedule);
        Long lessonId = lesson.getId();

        // Get all the scheduleList where lesson equals to lessonId
        defaultScheduleShouldBeFound("lessonId.equals=" + lessonId);

        // Get all the scheduleList where lesson equals to (lessonId + 1)
        defaultScheduleShouldNotBeFound("lessonId.equals=" + (lessonId + 1));
    }

    @Test
    @Transactional
    void getAllSchedulesByTeacherIsEqualToSomething() throws Exception {
        Teacher teacher;
        if (TestUtil.findAll(em, Teacher.class).isEmpty()) {
            scheduleRepository.saveAndFlush(schedule);
            teacher = TeacherResourceIT.createEntity(em);
        } else {
            teacher = TestUtil.findAll(em, Teacher.class).get(0);
        }
        em.persist(teacher);
        em.flush();
        schedule.setTeacher(teacher);
        scheduleRepository.saveAndFlush(schedule);
        Long teacherId = teacher.getId();

        // Get all the scheduleList where teacher equals to teacherId
        defaultScheduleShouldBeFound("teacherId.equals=" + teacherId);

        // Get all the scheduleList where teacher equals to (teacherId + 1)
        defaultScheduleShouldNotBeFound("teacherId.equals=" + (teacherId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultScheduleShouldBeFound(String filter) throws Exception {
        restScheduleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(schedule.getId().intValue())))
            .andExpect(jsonPath("$.[*].lessonType").value(hasItem(DEFAULT_LESSON_TYPE.toString())))
            .andExpect(jsonPath("$.[*].weekDay").value(hasItem(DEFAULT_WEEK_DAY.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));

        // Check, that the count call also returns 1
        restScheduleMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultScheduleShouldNotBeFound(String filter) throws Exception {
        restScheduleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restScheduleMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSchedule() throws Exception {
        // Get the schedule
        restScheduleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSchedule() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        int databaseSizeBeforeUpdate = scheduleRepository.findAll().size();

        // Update the schedule
        Schedule updatedSchedule = scheduleRepository.findById(schedule.getId()).get();
        // Disconnect from session so that the updates on updatedSchedule are not directly saved in db
        em.detach(updatedSchedule);
        updatedSchedule
            .lessonType(UPDATED_LESSON_TYPE)
            .weekDay(UPDATED_WEEK_DAY)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(updatedSchedule);

        restScheduleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, scheduleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(scheduleDTO))
            )
            .andExpect(status().isOk());

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll();
        assertThat(scheduleList).hasSize(databaseSizeBeforeUpdate);
        Schedule testSchedule = scheduleList.get(scheduleList.size() - 1);
        assertThat(testSchedule.getLessonType()).isEqualTo(UPDATED_LESSON_TYPE);
        assertThat(testSchedule.getWeekDay()).isEqualTo(UPDATED_WEEK_DAY);
        assertThat(testSchedule.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testSchedule.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testSchedule.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testSchedule.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void putNonExistingSchedule() throws Exception {
        int databaseSizeBeforeUpdate = scheduleRepository.findAll().size();
        schedule.setId(count.incrementAndGet());

        // Create the Schedule
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restScheduleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, scheduleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(scheduleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll();
        assertThat(scheduleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSchedule() throws Exception {
        int databaseSizeBeforeUpdate = scheduleRepository.findAll().size();
        schedule.setId(count.incrementAndGet());

        // Create the Schedule
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScheduleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(scheduleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll();
        assertThat(scheduleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSchedule() throws Exception {
        int databaseSizeBeforeUpdate = scheduleRepository.findAll().size();
        schedule.setId(count.incrementAndGet());

        // Create the Schedule
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScheduleMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(scheduleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll();
        assertThat(scheduleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateScheduleWithPatch() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        int databaseSizeBeforeUpdate = scheduleRepository.findAll().size();

        // Update the schedule using partial update
        Schedule partialUpdatedSchedule = new Schedule();
        partialUpdatedSchedule.setId(schedule.getId());

        partialUpdatedSchedule.lessonType(UPDATED_LESSON_TYPE).createdDate(UPDATED_CREATED_DATE).lastModifiedBy(UPDATED_LAST_MODIFIED_BY);

        restScheduleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSchedule.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSchedule))
            )
            .andExpect(status().isOk());

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll();
        assertThat(scheduleList).hasSize(databaseSizeBeforeUpdate);
        Schedule testSchedule = scheduleList.get(scheduleList.size() - 1);
        assertThat(testSchedule.getLessonType()).isEqualTo(UPDATED_LESSON_TYPE);
        assertThat(testSchedule.getWeekDay()).isEqualTo(DEFAULT_WEEK_DAY);
        assertThat(testSchedule.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testSchedule.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testSchedule.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testSchedule.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void fullUpdateScheduleWithPatch() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        int databaseSizeBeforeUpdate = scheduleRepository.findAll().size();

        // Update the schedule using partial update
        Schedule partialUpdatedSchedule = new Schedule();
        partialUpdatedSchedule.setId(schedule.getId());

        partialUpdatedSchedule
            .lessonType(UPDATED_LESSON_TYPE)
            .weekDay(UPDATED_WEEK_DAY)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);

        restScheduleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSchedule.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSchedule))
            )
            .andExpect(status().isOk());

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll();
        assertThat(scheduleList).hasSize(databaseSizeBeforeUpdate);
        Schedule testSchedule = scheduleList.get(scheduleList.size() - 1);
        assertThat(testSchedule.getLessonType()).isEqualTo(UPDATED_LESSON_TYPE);
        assertThat(testSchedule.getWeekDay()).isEqualTo(UPDATED_WEEK_DAY);
        assertThat(testSchedule.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testSchedule.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testSchedule.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testSchedule.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingSchedule() throws Exception {
        int databaseSizeBeforeUpdate = scheduleRepository.findAll().size();
        schedule.setId(count.incrementAndGet());

        // Create the Schedule
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restScheduleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, scheduleDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(scheduleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll();
        assertThat(scheduleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSchedule() throws Exception {
        int databaseSizeBeforeUpdate = scheduleRepository.findAll().size();
        schedule.setId(count.incrementAndGet());

        // Create the Schedule
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScheduleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(scheduleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll();
        assertThat(scheduleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSchedule() throws Exception {
        int databaseSizeBeforeUpdate = scheduleRepository.findAll().size();
        schedule.setId(count.incrementAndGet());

        // Create the Schedule
        ScheduleDTO scheduleDTO = scheduleMapper.toDto(schedule);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScheduleMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(scheduleDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll();
        assertThat(scheduleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSchedule() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        int databaseSizeBeforeDelete = scheduleRepository.findAll().size();

        // Delete the schedule
        restScheduleMockMvc
            .perform(delete(ENTITY_API_URL_ID, schedule.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Schedule> scheduleList = scheduleRepository.findAll();
        assertThat(scheduleList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
