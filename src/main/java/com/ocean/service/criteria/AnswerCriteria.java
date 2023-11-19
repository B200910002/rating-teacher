package com.ocean.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ocean.domain.Answer} entity. This class is used
 * in {@link com.ocean.web.rest.AnswerResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /answers?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AnswerCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter answer;

    private StringFilter createdBy;

    private InstantFilter createdDate;

    private StringFilter lastModifiedBy;

    private InstantFilter lastModifiedDate;

    private LongFilter teacherId;

    private LongFilter questionId;

    private Boolean distinct;

    public AnswerCriteria() {}

    public AnswerCriteria(AnswerCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.answer = other.answer == null ? null : other.answer.copy();
        this.createdBy = other.createdBy == null ? null : other.createdBy.copy();
        this.createdDate = other.createdDate == null ? null : other.createdDate.copy();
        this.lastModifiedBy = other.lastModifiedBy == null ? null : other.lastModifiedBy.copy();
        this.lastModifiedDate = other.lastModifiedDate == null ? null : other.lastModifiedDate.copy();
        this.teacherId = other.teacherId == null ? null : other.teacherId.copy();
        this.questionId = other.questionId == null ? null : other.questionId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public AnswerCriteria copy() {
        return new AnswerCriteria(this);
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

    public StringFilter getAnswer() {
        return answer;
    }

    public StringFilter answer() {
        if (answer == null) {
            answer = new StringFilter();
        }
        return answer;
    }

    public void setAnswer(StringFilter answer) {
        this.answer = answer;
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
        final AnswerCriteria that = (AnswerCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(answer, that.answer) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(createdDate, that.createdDate) &&
            Objects.equals(lastModifiedBy, that.lastModifiedBy) &&
            Objects.equals(lastModifiedDate, that.lastModifiedDate) &&
            Objects.equals(teacherId, that.teacherId) &&
            Objects.equals(questionId, that.questionId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, answer, createdBy, createdDate, lastModifiedBy, lastModifiedDate, teacherId, questionId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AnswerCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (answer != null ? "answer=" + answer + ", " : "") +
            (createdBy != null ? "createdBy=" + createdBy + ", " : "") +
            (createdDate != null ? "createdDate=" + createdDate + ", " : "") +
            (lastModifiedBy != null ? "lastModifiedBy=" + lastModifiedBy + ", " : "") +
            (lastModifiedDate != null ? "lastModifiedDate=" + lastModifiedDate + ", " : "") +
            (teacherId != null ? "teacherId=" + teacherId + ", " : "") +
            (questionId != null ? "questionId=" + questionId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
