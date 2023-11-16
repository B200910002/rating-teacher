package com.ocean.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * A Rating.
 */
@Entity
@Table(name = "rating")
@SuppressWarnings("common-java:DuplicatedBlocks")
@EntityListeners(AuditingEntityListener.class)
public class Rating implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Min(value = 0)
    @Max(value = 5)
    @Column(name = "score")
    private Integer score;

    @Column(name = "review")
    private String review;

    @Column(name = "lesson_name")
    private String lessonName;

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

    @OneToMany(mappedBy = "rating", fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = { "student", "rating" }, allowSetters = true)
    private Set<Like> likes = new HashSet<>();

    @OneToMany(mappedBy = "rating", fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = { "answers", "rating" }, allowSetters = true)
    private Set<Question> questions = new HashSet<>();

    @ManyToOne
    private Teacher teacher;

    @ManyToOne
    private Student student;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Rating id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getScore() {
        return this.score;
    }

    public Rating score(Integer score) {
        this.setScore(score);
        return this;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getReview() {
        return this.review;
    }

    public Rating review(String review) {
        this.setReview(review);
        return this;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getLessonName() {
        return this.lessonName;
    }

    public Rating lessonName(String lessonName) {
        this.setLessonName(lessonName);
        return this;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public Rating createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public Rating createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return this.lastModifiedBy;
    }

    public Rating lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public Rating lastModifiedDate(Instant lastModifiedDate) {
        this.setLastModifiedDate(lastModifiedDate);
        return this;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Set<Like> getLikes() {
        return this.likes;
    }

    public void setLikes(Set<Like> likes) {
        if (this.likes != null) {
            this.likes.forEach(i -> i.setRating(null));
        }
        if (likes != null) {
            likes.forEach(i -> i.setRating(this));
        }
        this.likes = likes;
    }

    public Rating likes(Set<Like> likes) {
        this.setLikes(likes);
        return this;
    }

    public Rating addLike(Like like) {
        this.likes.add(like);
        like.setRating(this);
        return this;
    }

    public Rating removeLike(Like like) {
        this.likes.remove(like);
        like.setRating(null);
        return this;
    }

    public Set<Question> getQuestions() {
        return this.questions;
    }

    public void setQuestions(Set<Question> questions) {
        if (this.questions != null) {
            this.questions.forEach(i -> i.setRating(null));
        }
        if (questions != null) {
            questions.forEach(i -> i.setRating(this));
        }
        this.questions = questions;
    }

    public Rating questions(Set<Question> questions) {
        this.setQuestions(questions);
        return this;
    }

    public Rating addQuestion(Question question) {
        this.questions.add(question);
        question.setRating(this);
        return this;
    }

    public Rating removeQuestion(Question question) {
        this.questions.remove(question);
        question.setRating(null);
        return this;
    }

    public Teacher getTeacher() {
        return this.teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Rating teacher(Teacher teacher) {
        this.setTeacher(teacher);
        return this;
    }

    public Student getStudent() {
        return this.student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Rating student(Student student) {
        this.setStudent(student);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
    // setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Rating)) {
            return false;
        }
        return id != null && id.equals(((Rating) o).id);
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
        return "Rating{" +
                "id=" + getId() +
                ", score=" + getScore() +
                ", review='" + getReview() + "'" +
                ", lessonName='" + getLessonName() + "'" +
                ", createdBy='" + getCreatedBy() + "'" +
                ", createdDate='" + getCreatedDate() + "'" +
                ", lastModifiedBy='" + getLastModifiedBy() + "'" +
                ", lastModifiedDate='" + getLastModifiedDate() + "'" +
                "}";
    }
}
