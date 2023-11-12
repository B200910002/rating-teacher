package com.ocean.service;

import com.ocean.domain.Answer;
import com.ocean.repository.AnswerRepository;
import com.ocean.service.dto.AnswerDTO;
import com.ocean.service.mapper.AnswerMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Answer}.
 */
@Service
@Transactional
public class AnswerService {

    private final Logger log = LoggerFactory.getLogger(AnswerService.class);

    private final AnswerRepository answerRepository;

    private final AnswerMapper answerMapper;

    public AnswerService(AnswerRepository answerRepository, AnswerMapper answerMapper) {
        this.answerRepository = answerRepository;
        this.answerMapper = answerMapper;
    }

    /**
     * Save a answer.
     *
     * @param answerDTO the entity to save.
     * @return the persisted entity.
     */
    public AnswerDTO save(AnswerDTO answerDTO) {
        log.debug("Request to save Answer : {}", answerDTO);
        Answer answer = answerMapper.toEntity(answerDTO);
        answer = answerRepository.save(answer);
        return answerMapper.toDto(answer);
    }

    /**
     * Update a answer.
     *
     * @param answerDTO the entity to save.
     * @return the persisted entity.
     */
    public AnswerDTO update(AnswerDTO answerDTO) {
        log.debug("Request to update Answer : {}", answerDTO);
        Answer answer = answerMapper.toEntity(answerDTO);
        answer = answerRepository.save(answer);
        return answerMapper.toDto(answer);
    }

    /**
     * Partially update a answer.
     *
     * @param answerDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AnswerDTO> partialUpdate(AnswerDTO answerDTO) {
        log.debug("Request to partially update Answer : {}", answerDTO);

        return answerRepository
            .findById(answerDTO.getId())
            .map(existingAnswer -> {
                answerMapper.partialUpdate(existingAnswer, answerDTO);

                return existingAnswer;
            })
            .map(answerRepository::save)
            .map(answerMapper::toDto);
    }

    /**
     * Get all the answers.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<AnswerDTO> findAll() {
        log.debug("Request to get all Answers");
        return answerRepository.findAll().stream().map(answerMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one answer by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AnswerDTO> findOne(Long id) {
        log.debug("Request to get Answer : {}", id);
        return answerRepository.findById(id).map(answerMapper::toDto);
    }

    /**
     * Delete the answer by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Answer : {}", id);
        answerRepository.deleteById(id);
    }
}
