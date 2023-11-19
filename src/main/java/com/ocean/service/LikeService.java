package com.ocean.service;

import com.ocean.domain.Like;
import com.ocean.repository.LikeRepository;
import com.ocean.service.dto.LikeDTO;
import com.ocean.service.mapper.LikeMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Like}.
 */
@Service
@Transactional
public class LikeService {

    private final Logger log = LoggerFactory.getLogger(LikeService.class);

    private final LikeRepository likeRepository;

    private final LikeMapper likeMapper;

    public LikeService(LikeRepository likeRepository, LikeMapper likeMapper) {
        this.likeRepository = likeRepository;
        this.likeMapper = likeMapper;
    }

    /**
     * Save a like.
     *
     * @param likeDTO the entity to save.
     * @return the persisted entity.
     */
    public LikeDTO save(LikeDTO likeDTO) {
        log.debug("Request to save Like : {}", likeDTO);
        Like like = likeMapper.toEntity(likeDTO);
        like = likeRepository.save(like);
        return likeMapper.toDto(like);
    }

    /**
     * Update a like.
     *
     * @param likeDTO the entity to save.
     * @return the persisted entity.
     */
    public LikeDTO update(LikeDTO likeDTO) {
        log.debug("Request to update Like : {}", likeDTO);
        Like like = likeMapper.toEntity(likeDTO);
        like = likeRepository.save(like);
        return likeMapper.toDto(like);
    }

    /**
     * Partially update a like.
     *
     * @param likeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<LikeDTO> partialUpdate(LikeDTO likeDTO) {
        log.debug("Request to partially update Like : {}", likeDTO);

        return likeRepository
            .findById(likeDTO.getId())
            .map(existingLike -> {
                likeMapper.partialUpdate(existingLike, likeDTO);

                return existingLike;
            })
            .map(likeRepository::save)
            .map(likeMapper::toDto);
    }

    /**
     * Get all the likes.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Like> findAll() {
        log.debug("Request to get all Likes");
        return likeRepository.findAll();
    }

    /**
     * Get one like by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Like> findOne(Long id) {
        log.debug("Request to get Like : {}", id);
        return likeRepository.findById(id);
    }

    /**
     * Delete the like by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Like : {}", id);
        likeRepository.deleteById(id);
    }
}
