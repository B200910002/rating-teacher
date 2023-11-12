package com.ocean.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.ocean.domain.Like} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LikeDTO implements Serializable {

    private Long id;

    private Instant timeStamp;

    private StudentDTO student;

    private RatingDTO rating;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Instant timeStamp) {
        this.timeStamp = timeStamp;
    }

    public StudentDTO getStudent() {
        return student;
    }

    public void setStudent(StudentDTO student) {
        this.student = student;
    }

    public RatingDTO getRating() {
        return rating;
    }

    public void setRating(RatingDTO rating) {
        this.rating = rating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LikeDTO)) {
            return false;
        }

        LikeDTO likeDTO = (LikeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, likeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LikeDTO{" +
            "id=" + getId() +
            ", timeStamp='" + getTimeStamp() + "'" +
            ", student=" + getStudent() +
            ", rating=" + getRating() +
            "}";
    }
}
