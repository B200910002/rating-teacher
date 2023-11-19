package com.ocean.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * A Like.
 */
@Entity
@Table(name = "jhi_like")
@SuppressWarnings("common-java:DuplicatedBlocks")
@EntityListeners(AuditingEntityListener.class)
public class Like implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "time_stamp")
    @CreatedDate
    private Instant timeStamp;

    @ManyToOne
    private Student student;

    @ManyToOne
    @JsonIgnoreProperties(value = { "likes", "questions", "teacher", "student" }, allowSetters = true)
    private Rating rating;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Like id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getTimeStamp() {
        return this.timeStamp;
    }

    public Like timeStamp(Instant timeStamp) {
        this.setTimeStamp(timeStamp);
        return this;
    }

    public void setTimeStamp(Instant timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Student getStudent() {
        return this.student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Like student(Student student) {
        this.setStudent(student);
        return this;
    }

    public Rating getRating() {
        return this.rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public Like rating(Rating rating) {
        this.setRating(rating);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Like)) {
            return false;
        }
        return id != null && id.equals(((Like) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Like{" +
            "id=" + getId() +
            ", timeStamp='" + getTimeStamp() + "'" +
            "}";
    }
}
