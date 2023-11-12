package com.ocean.domain;

import com.ocean.domain.enumeration.LessonType;
import com.ocean.domain.enumeration.WeekDay;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * A Schedule.
 */
@Entity
@Table(name = "schedule")
@SuppressWarnings("common-java:DuplicatedBlocks")
@EntityListeners(AuditingEntityListener.class)
public class Schedule implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "lesson_type")
    private LessonType lessonType;

    @Enumerated(EnumType.STRING)
    @Column(name = "week_day")
    private WeekDay weekDay;

    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;

    @Column(name = "created_date")
    @CreatedDate
    private Instant createdDate;

    @Column(name = "last_modified_by")
    @LastModifiedBy
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    @LastModifiedDate
    private Instant lastModifiedDate;

    @ManyToOne
    private Room room;

    @ManyToOne
    private LessonTime lessonTime;

    @ManyToOne
    private Lesson lesson;

    @ManyToOne
    private Teacher teacher;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Schedule id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LessonType getLessonType() {
        return this.lessonType;
    }

    public Schedule lessonType(LessonType lessonType) {
        this.setLessonType(lessonType);
        return this;
    }

    public void setLessonType(LessonType lessonType) {
        this.lessonType = lessonType;
    }

    public WeekDay getWeekDay() {
        return this.weekDay;
    }

    public Schedule weekDay(WeekDay weekDay) {
        this.setWeekDay(weekDay);
        return this;
    }

    public void setWeekDay(WeekDay weekDay) {
        this.weekDay = weekDay;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public Schedule createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public Schedule createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return this.lastModifiedBy;
    }

    public Schedule lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public Schedule lastModifiedDate(Instant lastModifiedDate) {
        this.setLastModifiedDate(lastModifiedDate);
        return this;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Room getRoom() {
        return this.room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Schedule room(Room room) {
        this.setRoom(room);
        return this;
    }

    public LessonTime getLessonTime() {
        return this.lessonTime;
    }

    public void setLessonTime(LessonTime lessonTime) {
        this.lessonTime = lessonTime;
    }

    public Schedule lessonTime(LessonTime lessonTime) {
        this.setLessonTime(lessonTime);
        return this;
    }

    public Lesson getLesson() {
        return this.lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public Schedule lesson(Lesson lesson) {
        this.setLesson(lesson);
        return this;
    }

    public Teacher getTeacher() {
        return this.teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Schedule teacher(Teacher teacher) {
        this.setTeacher(teacher);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
    // setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Schedule)) {
            return false;
        }
        return id != null && id.equals(((Schedule) o).id);
    }

    @Override
    public int hashCode() {
        // see
        // https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Schedule{" +
                "id=" + getId() +
                ", lessonType='" + getLessonType() + "'" +
                ", weekDay='" + getWeekDay() + "'" +
                ", createdBy='" + getCreatedBy() + "'" +
                ", createdDate='" + getCreatedDate() + "'" +
                ", lastModifiedBy='" + getLastModifiedBy() + "'" +
                ", lastModifiedDate='" + getLastModifiedDate() + "'" +
                "}";
    }
}
