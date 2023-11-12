package com.ocean.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ocean.domain.Room} entity. This class is used
 * in {@link com.ocean.web.rest.RoomResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /rooms?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RoomCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter roomCode;

    private Boolean distinct;

    public RoomCriteria() {}

    public RoomCriteria(RoomCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.roomCode = other.roomCode == null ? null : other.roomCode.copy();
        this.distinct = other.distinct;
    }

    @Override
    public RoomCriteria copy() {
        return new RoomCriteria(this);
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

    public StringFilter getRoomCode() {
        return roomCode;
    }

    public StringFilter roomCode() {
        if (roomCode == null) {
            roomCode = new StringFilter();
        }
        return roomCode;
    }

    public void setRoomCode(StringFilter roomCode) {
        this.roomCode = roomCode;
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
        final RoomCriteria that = (RoomCriteria) o;
        return Objects.equals(id, that.id) && Objects.equals(roomCode, that.roomCode) && Objects.equals(distinct, that.distinct);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, roomCode, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RoomCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (roomCode != null ? "roomCode=" + roomCode + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
