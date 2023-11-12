package com.ocean.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.ocean.domain.Room} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RoomDTO implements Serializable {

    private Long id;

    private String roomCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RoomDTO)) {
            return false;
        }

        RoomDTO roomDTO = (RoomDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, roomDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RoomDTO{" +
            "id=" + getId() +
            ", roomCode='" + getRoomCode() + "'" +
            "}";
    }
}
