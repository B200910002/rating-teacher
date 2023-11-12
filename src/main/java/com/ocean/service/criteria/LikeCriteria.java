package com.ocean.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ocean.domain.Like} entity. This class is used
 * in {@link com.ocean.web.rest.LikeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /likes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LikeCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter timeStamp;

    private LongFilter studentId;

    private LongFilter ratingId;

    private Boolean distinct;

    public LikeCriteria() {}

    public LikeCriteria(LikeCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.timeStamp = other.timeStamp == null ? null : other.timeStamp.copy();
        this.studentId = other.studentId == null ? null : other.studentId.copy();
        this.ratingId = other.ratingId == null ? null : other.ratingId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public LikeCriteria copy() {
        return new LikeCriteria(this);
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

    public InstantFilter getTimeStamp() {
        return timeStamp;
    }

    public InstantFilter timeStamp() {
        if (timeStamp == null) {
            timeStamp = new InstantFilter();
        }
        return timeStamp;
    }

    public void setTimeStamp(InstantFilter timeStamp) {
        this.timeStamp = timeStamp;
    }

    public LongFilter getStudentId() {
        return studentId;
    }

    public LongFilter studentId() {
        if (studentId == null) {
            studentId = new LongFilter();
        }
        return studentId;
    }

    public void setStudentId(LongFilter studentId) {
        this.studentId = studentId;
    }

    public LongFilter getRatingId() {
        return ratingId;
    }

    public LongFilter ratingId() {
        if (ratingId == null) {
            ratingId = new LongFilter();
        }
        return ratingId;
    }

    public void setRatingId(LongFilter ratingId) {
        this.ratingId = ratingId;
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
        final LikeCriteria that = (LikeCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(timeStamp, that.timeStamp) &&
            Objects.equals(studentId, that.studentId) &&
            Objects.equals(ratingId, that.ratingId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timeStamp, studentId, ratingId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LikeCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (timeStamp != null ? "timeStamp=" + timeStamp + ", " : "") +
            (studentId != null ? "studentId=" + studentId + ", " : "") +
            (ratingId != null ? "ratingId=" + ratingId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
