package com.ocean.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ocean.domain.LessonTime} entity. This class is used
 * in {@link com.ocean.web.rest.LessonTimeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /lesson-times?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LessonTimeCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private InstantFilter startAt;

    private InstantFilter endAt;

    private Boolean distinct;

    public LessonTimeCriteria() {}

    public LessonTimeCriteria(LessonTimeCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.startAt = other.startAt == null ? null : other.startAt.copy();
        this.endAt = other.endAt == null ? null : other.endAt.copy();
        this.distinct = other.distinct;
    }

    @Override
    public LessonTimeCriteria copy() {
        return new LessonTimeCriteria(this);
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

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public InstantFilter getStartAt() {
        return startAt;
    }

    public InstantFilter startAt() {
        if (startAt == null) {
            startAt = new InstantFilter();
        }
        return startAt;
    }

    public void setStartAt(InstantFilter startAt) {
        this.startAt = startAt;
    }

    public InstantFilter getEndAt() {
        return endAt;
    }

    public InstantFilter endAt() {
        if (endAt == null) {
            endAt = new InstantFilter();
        }
        return endAt;
    }

    public void setEndAt(InstantFilter endAt) {
        this.endAt = endAt;
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
        final LessonTimeCriteria that = (LessonTimeCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(startAt, that.startAt) &&
            Objects.equals(endAt, that.endAt) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, startAt, endAt, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LessonTimeCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (startAt != null ? "startAt=" + startAt + ", " : "") +
            (endAt != null ? "endAt=" + endAt + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
