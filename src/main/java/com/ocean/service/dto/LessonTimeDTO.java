package com.ocean.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.ocean.domain.LessonTime} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LessonTimeDTO implements Serializable {

    private Long id;

    private String name;

    private Instant startAt;

    private Instant endAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getStartAt() {
        return startAt;
    }

    public void setStartAt(Instant startAt) {
        this.startAt = startAt;
    }

    public Instant getEndAt() {
        return endAt;
    }

    public void setEndAt(Instant endAt) {
        this.endAt = endAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LessonTimeDTO)) {
            return false;
        }

        LessonTimeDTO lessonTimeDTO = (LessonTimeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, lessonTimeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LessonTimeDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", startAt='" + getStartAt() + "'" +
            ", endAt='" + getEndAt() + "'" +
            "}";
    }
}
