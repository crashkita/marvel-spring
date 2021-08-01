package com.den.marvel.marvel.service;

import com.den.marvel.marvel.dto.ComicDto;
import com.den.marvel.marvel.entity.Comic;
import com.den.marvel.marvel.exception.ComicNotFoundException;
import com.den.marvel.marvel.repository.CharacterRepository;
import com.den.marvel.marvel.repository.ComicRepository;
import com.den.marvel.marvel.validator.OnCreate;
import com.den.marvel.marvel.validator.OnUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.Validator;

@Service
public class ComicService {
    private final ComicRepository comicRepository;
    private FileStorageService fileStorageService;
    private CharacterRepository characterRepository;
    @Autowired(required=true)
    private Validator validator;


    public ComicService(@Autowired FileStorageService fileStorageService,
                            @Autowired ComicRepository comicRepository,
                            @Autowired CharacterRepository characterRepository) {
        this.fileStorageService = fileStorageService;
        this.comicRepository = comicRepository;
        this.characterRepository = characterRepository;
    }

    @Validated(OnCreate.class)
    public Comic create(@Valid ComicDto comicDto) {
        return save(comicDto, new Comic());
    }

    @Validated(OnUpdate.class)
    public Comic update(@Valid ComicDto comicDto) {
        Comic comic = comicRepository.findById(comicDto.getId()).orElseThrow(() -> new ComicNotFoundException(comicDto.getId()));
        return save(comicDto, comic);
    }

    protected Comic save(ComicDto comicDto, Comic comic) {
        comic.setDescription(comicDto.getDescription());
        comic.setTitle(comicDto.getTitle());
        comicDto.getImages()
                .stream()
                .forEach(file -> comic.addImage(fileStorageService.storeFileAsImage(file)));

        if (comicDto.getCharacters() != null) {
            for (String comicId : comicDto.getCharacters()) {
                characterRepository.findById(comicId).ifPresent(comic::addCharacter);
            }
        }

        comicRepository.save(comic);

        return comic;
    }
}
