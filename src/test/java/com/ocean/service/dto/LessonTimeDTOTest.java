package com.ocean.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ocean.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LessonTimeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(LessonTimeDTO.class);
        LessonTimeDTO lessonTimeDTO1 = new LessonTimeDTO();
        lessonTimeDTO1.setId(1L);
        LessonTimeDTO lessonTimeDTO2 = new LessonTimeDTO();
        assertThat(lessonTimeDTO1).isNotEqualTo(lessonTimeDTO2);
        lessonTimeDTO2.setId(lessonTimeDTO1.getId());
        assertThat(lessonTimeDTO1).isEqualTo(lessonTimeDTO2);
        lessonTimeDTO2.setId(2L);
        assertThat(lessonTimeDTO1).isNotEqualTo(lessonTimeDTO2);
        lessonTimeDTO1.setId(null);
        assertThat(lessonTimeDTO1).isNotEqualTo(lessonTimeDTO2);
    }
}
