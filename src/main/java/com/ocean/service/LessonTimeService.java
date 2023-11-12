package com.ocean.service;

import com.ocean.domain.LessonTime;
import com.ocean.repository.LessonTimeRepository;
import com.ocean.service.dto.LessonTimeDTO;
import com.ocean.service.mapper.LessonTimeMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link LessonTime}.
 */
@Service
@Transactional
public class LessonTimeService {

    private final Logger log = LoggerFactory.getLogger(LessonTimeService.class);

    private final LessonTimeRepository lessonTimeRepository;

    private final LessonTimeMapper lessonTimeMapper;

    public LessonTimeService(LessonTimeRepository lessonTimeRepository, LessonTimeMapper lessonTimeMapper) {
        this.lessonTimeRepository = lessonTimeRepository;
        this.lessonTimeMapper = lessonTimeMapper;
    }

    /**
     * Save a lessonTime.
     *
     * @param lessonTimeDTO the entity to save.
     * @return the persisted entity.
     */
    public LessonTimeDTO save(LessonTimeDTO lessonTimeDTO) {
        log.debug("Request to save LessonTime : {}", lessonTimeDTO);
        LessonTime lessonTime = lessonTimeMapper.toEntity(lessonTimeDTO);
        lessonTime = lessonTimeRepository.save(lessonTime);
        return lessonTimeMapper.toDto(lessonTime);
    }

    /**
     * Update a lessonTime.
     *
     * @param lessonTimeDTO the entity to save.
     * @return the persisted entity.
     */
    public LessonTimeDTO update(LessonTimeDTO lessonTimeDTO) {
        log.debug("Request to update LessonTime : {}", lessonTimeDTO);
        LessonTime lessonTime = lessonTimeMapper.toEntity(lessonTimeDTO);
        lessonTime = lessonTimeRepository.save(lessonTime);
        return lessonTimeMapper.toDto(lessonTime);
    }

    /**
     * Partially update a lessonTime.
     *
     * @param lessonTimeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<LessonTimeDTO> partialUpdate(LessonTimeDTO lessonTimeDTO) {
        log.debug("Request to partially update LessonTime : {}", lessonTimeDTO);

        return lessonTimeRepository
            .findById(lessonTimeDTO.getId())
            .map(existingLessonTime -> {
                lessonTimeMapper.partialUpdate(existingLessonTime, lessonTimeDTO);

                return existingLessonTime;
            })
            .map(lessonTimeRepository::save)
            .map(lessonTimeMapper::toDto);
    }

    /**
     * Get all the lessonTimes.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<LessonTimeDTO> findAll() {
        log.debug("Request to get all LessonTimes");
        return lessonTimeRepository.findAll().stream().map(lessonTimeMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one lessonTime by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<LessonTimeDTO> findOne(Long id) {
        log.debug("Request to get LessonTime : {}", id);
        return lessonTimeRepository.findById(id).map(lessonTimeMapper::toDto);
    }

    /**
     * Delete the lessonTime by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete LessonTime : {}", id);
        lessonTimeRepository.deleteById(id);
    }
}
