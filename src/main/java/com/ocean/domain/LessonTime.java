package com.ocean.domain;

import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;

/**
 * A LessonTime.
 */
@Entity
@Table(name = "lesson_time")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LessonTime implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "start_at")
    private Instant startAt;

    @Column(name = "end_at")
    private Instant endAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public LessonTime id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public LessonTime name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getStartAt() {
        return this.startAt;
    }

    public LessonTime startAt(Instant startAt) {
        this.setStartAt(startAt);
        return this;
    }

    public void setStartAt(Instant startAt) {
        this.startAt = startAt;
    }

    public Instant getEndAt() {
        return this.endAt;
    }

    public LessonTime endAt(Instant endAt) {
        this.setEndAt(endAt);
        return this;
    }

    public void setEndAt(Instant endAt) {
        this.endAt = endAt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LessonTime)) {
            return false;
        }
        return id != null && id.equals(((LessonTime) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LessonTime{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", startAt='" + getStartAt() + "'" +
            ", endAt='" + getEndAt() + "'" +
            "}";
    }
}
