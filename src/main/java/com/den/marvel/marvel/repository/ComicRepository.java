package com.den.marvel.marvel.repository;

import com.den.marvel.marvel.entity.Comic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ComicRepository extends MongoRepository<Comic, String> {
    Page<Comic> findByTitleContaining(String title, Pageable pagingSort);

    @Query("{'characters' :{'$ref' : 'character' , '$id' : ?0}}")
    List<Comic> findByCharacterId(String characterId);
}
