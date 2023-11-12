package com.ocean.service.criteria;

import com.ocean.domain.enumeration.LessonType;
import com.ocean.domain.enumeration.WeekDay;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ocean.domain.Schedule} entity. This class is used
 * in {@link com.ocean.web.rest.ScheduleResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /schedules?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ScheduleCriteria implements Serializable, Criteria {

    /**
     * Class for filtering LessonType
     */
    public static class LessonTypeFilter extends Filter<LessonType> {

        public LessonTypeFilter() {}

        public LessonTypeFilter(LessonTypeFilter filter) {
            super(filter);
        }

        @Override
        public LessonTypeFilter copy() {
            return new LessonTypeFilter(this);
        }
    }

    /**
     * Class for filtering WeekDay
     */
    public static class WeekDayFilter extends Filter<WeekDay> {

        public WeekDayFilter() {}

        public WeekDayFilter(WeekDayFilter filter) {
            super(filter);
        }

        @Override
        public WeekDayFilter copy() {
            return new WeekDayFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LessonTypeFilter lessonType;

    private WeekDayFilter weekDay;

    private StringFilter createdBy;

    private InstantFilter createdDate;

    private StringFilter lastModifiedBy;

    private InstantFilter lastModifiedDate;

    private LongFilter roomId;

    private LongFilter lessonTimeId;

    private LongFilter lessonId;

    private LongFilter teacherId;

    private Boolean distinct;

    public ScheduleCriteria() {}

    public ScheduleCriteria(ScheduleCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.lessonType = other.lessonType == null ? null : other.lessonType.copy();
        this.weekDay = other.weekDay == null ? null : other.weekDay.copy();
        this.createdBy = other.createdBy == null ? null : other.createdBy.copy();
        this.createdDate = other.createdDate == null ? null : other.createdDate.copy();
        this.lastModifiedBy = other.lastModifiedBy == null ? null : other.lastModifiedBy.copy();
        this.lastModifiedDate = other.lastModifiedDate == null ? null : other.lastModifiedDate.copy();
        this.roomId = other.roomId == null ? null : other.roomId.copy();
        this.lessonTimeId = other.lessonTimeId == null ? null : other.lessonTimeId.copy();
        this.lessonId = other.lessonId == null ? null : other.lessonId.copy();
        this.teacherId = other.teacherId == null ? null : other.teacherId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ScheduleCriteria copy() {
        return new ScheduleCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LessonTypeFilter getLessonType() {
        return lessonType;
    }

    public LessonTypeFilter lessonType() {
        if (lessonType == null) {
            lessonType = new LessonTypeFilter();
        }
        return lessonType;
    }

    public void setLessonType(LessonTypeFilter lessonType) {
        this.lessonType = lessonType;
    }

    public WeekDayFilter getWeekDay() {
        return weekDay;
    }

    public WeekDayFilter weekDay() {
        if (weekDay == null) {
            weekDay = new WeekDayFilter();
        }
        return weekDay;
    }

    public void setWeekDay(WeekDayFilter weekDay) {
        this.weekDay = weekDay;
    }

    public StringFilter getCreatedBy() {
        return createdBy;
    }

    public StringFilter createdBy() {
        if (createdBy == null) {
            createdBy = new StringFilter();
        }
        return createdBy;
    }

    public void setCreatedBy(StringFilter createdBy) {
        this.createdBy = createdBy;
    }

    public InstantFilter getCreatedDate() {
        return createdDate;
    }

    public InstantFilter createdDate() {
        if (createdDate == null) {
            createdDate = new InstantFilter();
        }
        return createdDate;
    }

    public void setCreatedDate(InstantFilter createdDate) {
        this.createdDate = createdDate;
    }

    public StringFilter getLastModifiedBy() {
        return lastModifiedBy;
    }

    public StringFilter lastModifiedBy() {
        if (lastModifiedBy == null) {
            lastModifiedBy = new StringFilter();
        }
        return lastModifiedBy;
    }

    public void setLastModifiedBy(StringFilter lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public InstantFilter getLastModifiedDate() {
        return lastModifiedDate;
    }

    public InstantFilter lastModifiedDate() {
        if (lastModifiedDate == null) {
            lastModifiedDate = new InstantFilter();
        }
        return lastModifiedDate;
    }

    public void setLastModifiedDate(InstantFilter lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public LongFilter getRoomId() {
        return roomId;
    }

    public LongFilter roomId() {
        if (roomId == null) {
            roomId = new LongFilter();
        }
        return roomId;
    }

    public void setRoomId(LongFilter roomId) {
        this.roomId = roomId;
    }

    public LongFilter getLessonTimeId() {
        return lessonTimeId;
    }

    public LongFilter lessonTimeId() {
        if (lessonTimeId == null) {
            lessonTimeId = new LongFilter();
        }
        return lessonTimeId;
    }

    public void setLessonTimeId(LongFilter lessonTimeId) {
        this.lessonTimeId = lessonTimeId;
    }

    public LongFilter getLessonId() {
        return lessonId;
    }

    public LongFilter lessonId() {
        if (lessonId == null) {
            lessonId = new LongFilter();
        }
        return lessonId;
    }

    public void setLessonId(LongFilter lessonId) {
        this.lessonId = lessonId;
    }

    public LongFilter getTeacherId() {
        return teacherId;
    }

    public LongFilter teacherId() {
        if (teacherId == null) {
            teacherId = new LongFilter();
        }
        return teacherId;
    }

    public void setTeacherId(LongFilter teacherId) {
        this.teacherId = teacherId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ScheduleCriteria that = (ScheduleCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(lessonType, that.lessonType) &&
            Objects.equals(weekDay, that.weekDay) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(createdDate, that.createdDate) &&
            Objects.equals(lastModifiedBy, that.lastModifiedBy) &&
            Objects.equals(lastModifiedDate, that.lastModifiedDate) &&
            Objects.equals(roomId, that.roomId) &&
            Objects.equals(lessonTimeId, that.lessonTimeId) &&
            Objects.equals(lessonId, that.lessonId) &&
            Objects.equals(teacherId, that.teacherId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            lessonType,
            weekDay,
            createdBy,
            createdDate,
            lastModifiedBy,
            lastModifiedDate,
            roomId,
            lessonTimeId,
            lessonId,
            teacherId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ScheduleCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (lessonType != null ? "lessonType=" + lessonType + ", " : "") +
            (weekDay != null ? "weekDay=" + weekDay + ", " : "") +
            (createdBy != null ? "createdBy=" + createdBy + ", " : "") +
            (createdDate != null ? "createdDate=" + createdDate + ", " : "") +
            (lastModifiedBy != null ? "lastModifiedBy=" + lastModifiedBy + ", " : "") +
            (lastModifiedDate != null ? "lastModifiedDate=" + lastModifiedDate + ", " : "") +
            (roomId != null ? "roomId=" + roomId + ", " : "") +
            (lessonTimeId != null ? "lessonTimeId=" + lessonTimeId + ", " : "") +
            (lessonId != null ? "lessonId=" + lessonId + ", " : "") +
            (teacherId != null ? "teacherId=" + teacherId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
