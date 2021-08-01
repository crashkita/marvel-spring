package com.den.marvel.marvel.service;

import com.den.marvel.marvel.dto.CharacterDto;
import com.den.marvel.marvel.entity.Character;
import com.den.marvel.marvel.entity.Comic;
import com.den.marvel.marvel.exception.CharacterNotFoundException;
import com.den.marvel.marvel.repository.CharacterRepository;
import com.den.marvel.marvel.repository.ComicRepository;
import com.den.marvel.marvel.validator.OnCreate;
import com.den.marvel.marvel.validator.OnUpdate;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
@Validated
public class CharacterService {
    private final ComicRepository comicRepository;
    private FileStorageService fileStorageService;
    private CharacterRepository characterRepository;
    @Resource
    private MongoTemplate mongoTemplate;
    @Autowired(required=true)
    private Validator validator;


    public CharacterService(@Autowired FileStorageService fileStorageService,
                            @Autowired CharacterRepository characterRepository,
                            @Autowired ComicRepository comicRepository) {
        this.fileStorageService = fileStorageService;
        this.characterRepository = characterRepository;
        this.comicRepository = comicRepository;
    }

    @Validated(OnCreate.class)
    public Character create(@Valid CharacterDto characterDto) {
        return save(characterDto, new Character());
    }

    @Validated(OnUpdate.class)
    public Character update(@Valid CharacterDto characterDto) {
        Character character = characterRepository.findById(characterDto.getId()).orElseThrow(() -> new CharacterNotFoundException(characterDto.getId()));
        return save(characterDto, character);
    }

    protected Character save(CharacterDto characterDto, Character character) {
        character.setDescription(characterDto.getDescription());
        character.setName(characterDto.getName());
        if (characterDto.getImage() != null) {
            character.setImage(fileStorageService.storeFileAsImage(characterDto.getImage()));
        }

        characterRepository.save(character);

        return character;
    }

    public List<Comic> getAllComics(Character character) {
        Query query = new Query();
        query.addCriteria(Criteria.where("characters.$id").is(new ObjectId(character.getId())));
        List<Comic> comments = mongoTemplate.find(query, Comic.class);
        return comments;
    }
}
