package com.ocean.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LessonTimeMapperTest {

    private LessonTimeMapper lessonTimeMapper;

    @BeforeEach
    public void setUp() {
        lessonTimeMapper = new LessonTimeMapperImpl();
    }
}
