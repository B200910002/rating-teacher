package com.ocean.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ocean.domain.Rating} entity. This class is used
 * in {@link com.ocean.web.rest.RatingResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /ratings?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RatingCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter score;

    private StringFilter review;

    private StringFilter lessonName;

    private StringFilter createdBy;

    private InstantFilter createdDate;

    private StringFilter lastModifiedBy;

    private InstantFilter lastModifiedDate;

    private LongFilter likeId;

    private LongFilter questionId;

    private LongFilter teacherId;

    private LongFilter studentId;

    private Boolean distinct;

    public RatingCriteria() {}

    public RatingCriteria(RatingCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.score = other.score == null ? null : other.score.copy();
        this.review = other.review == null ? null : other.review.copy();
        this.lessonName = other.lessonName == null ? null : other.lessonName.copy();
        this.createdBy = other.createdBy == null ? null : other.createdBy.copy();
        this.createdDate = other.createdDate == null ? null : other.createdDate.copy();
        this.lastModifiedBy = other.lastModifiedBy == null ? null : other.lastModifiedBy.copy();
        this.lastModifiedDate = other.lastModifiedDate == null ? null : other.lastModifiedDate.copy();
        this.likeId = other.likeId == null ? null : other.likeId.copy();
        this.questionId = other.questionId == null ? null : other.questionId.copy();
        this.teacherId = other.teacherId == null ? null : other.teacherId.copy();
        this.studentId = other.studentId == null ? null : other.studentId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public RatingCriteria copy() {
        return new RatingCriteria(this);
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

    public IntegerFilter getScore() {
        return score;
    }

    public IntegerFilter score() {
        if (score == null) {
            score = new IntegerFilter();
        }
        return score;
    }

    public void setScore(IntegerFilter score) {
        this.score = score;
    }

    public StringFilter getReview() {
        return review;
    }

    public StringFilter review() {
        if (review == null) {
            review = new StringFilter();
        }
        return review;
    }

    public void setReview(StringFilter review) {
        this.review = review;
    }

    public StringFilter getLessonName() {
        return lessonName;
    }

    public StringFilter lessonName() {
        if (lessonName == null) {
            lessonName = new StringFilter();
        }
        return lessonName;
    }

    public void setLessonName(StringFilter lessonName) {
        this.lessonName = lessonName;
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

    public LongFilter getLikeId() {
        return likeId;
    }

    public LongFilter likeId() {
        if (likeId == null) {
            likeId = new LongFilter();
        }
        return likeId;
    }

    public void setLikeId(LongFilter likeId) {
        this.likeId = likeId;
    }

    public LongFilter getQuestionId() {
        return questionId;
    }

    public LongFilter questionId() {
        if (questionId == null) {
            questionId = new LongFilter();
        }
        return questionId;
    }

    public void setQuestionId(LongFilter questionId) {
        this.questionId = questionId;
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
        final RatingCriteria that = (RatingCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(score, that.score) &&
            Objects.equals(review, that.review) &&
            Objects.equals(lessonName, that.lessonName) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(createdDate, that.createdDate) &&
            Objects.equals(lastModifiedBy, that.lastModifiedBy) &&
            Objects.equals(lastModifiedDate, that.lastModifiedDate) &&
            Objects.equals(likeId, that.likeId) &&
            Objects.equals(questionId, that.questionId) &&
            Objects.equals(teacherId, that.teacherId) &&
            Objects.equals(studentId, that.studentId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            score,
            review,
            lessonName,
            createdBy,
            createdDate,
            lastModifiedBy,
            lastModifiedDate,
            likeId,
            questionId,
            teacherId,
            studentId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RatingCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (score != null ? "score=" + score + ", " : "") +
            (review != null ? "review=" + review + ", " : "") +
            (lessonName != null ? "lessonName=" + lessonName + ", " : "") +
            (createdBy != null ? "createdBy=" + createdBy + ", " : "") +
            (createdDate != null ? "createdDate=" + createdDate + ", " : "") +
            (lastModifiedBy != null ? "lastModifiedBy=" + lastModifiedBy + ", " : "") +
            (lastModifiedDate != null ? "lastModifiedDate=" + lastModifiedDate + ", " : "") +
            (likeId != null ? "likeId=" + likeId + ", " : "") +
            (questionId != null ? "questionId=" + questionId + ", " : "") +
            (teacherId != null ? "teacherId=" + teacherId + ", " : "") +
            (studentId != null ? "studentId=" + studentId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
