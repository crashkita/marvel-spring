package com.den.marvel.marvel.repository;

import com.den.marvel.marvel.entity.Character;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

public interface CharacterRepository extends MongoRepository<Character, String> {
    public Character findByName(@Param("name") String name);
    Page<Character> findByNameContaining(@Param("name") String name, Pageable pageable);
}
