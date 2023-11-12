package com.ocean.service.mapper;

import com.ocean.domain.Lesson;
import com.ocean.domain.LessonTime;
import com.ocean.domain.Room;
import com.ocean.domain.Schedule;
import com.ocean.domain.Teacher;
import com.ocean.service.dto.LessonDTO;
import com.ocean.service.dto.LessonTimeDTO;
import com.ocean.service.dto.RoomDTO;
import com.ocean.service.dto.ScheduleDTO;
import com.ocean.service.dto.TeacherDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Schedule} and its DTO {@link ScheduleDTO}.
 */
@Mapper(componentModel = "spring")
public interface ScheduleMapper extends EntityMapper<ScheduleDTO, Schedule> {
    @Mapping(target = "room", source = "room", qualifiedByName = "roomId")
    @Mapping(target = "lessonTime", source = "lessonTime", qualifiedByName = "lessonTimeId")
    @Mapping(target = "lesson", source = "lesson", qualifiedByName = "lessonId")
    @Mapping(target = "teacher", source = "teacher", qualifiedByName = "teacherId")
    ScheduleDTO toDto(Schedule s);

    @Named("roomId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RoomDTO toDtoRoomId(Room room);

    @Named("lessonTimeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    LessonTimeDTO toDtoLessonTimeId(LessonTime lessonTime);

    @Named("lessonId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    LessonDTO toDtoLessonId(Lesson lesson);

    @Named("teacherId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TeacherDTO toDtoTeacherId(Teacher teacher);
}
