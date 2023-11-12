package com.ocean.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ocean.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LessonTimeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LessonTime.class);
        LessonTime lessonTime1 = new LessonTime();
        lessonTime1.setId(1L);
        LessonTime lessonTime2 = new LessonTime();
        lessonTime2.setId(lessonTime1.getId());
        assertThat(lessonTime1).isEqualTo(lessonTime2);
        lessonTime2.setId(2L);
        assertThat(lessonTime1).isNotEqualTo(lessonTime2);
        lessonTime1.setId(null);
        assertThat(lessonTime1).isNotEqualTo(lessonTime2);
    }
}
