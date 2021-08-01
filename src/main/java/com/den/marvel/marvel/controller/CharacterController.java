package com.den.marvel.marvel.controller;

import com.den.marvel.marvel.dto.CharacterDto;
import com.den.marvel.marvel.dto.ListResponse;
import com.den.marvel.marvel.entity.Character;
import com.den.marvel.marvel.entity.Comic;
import com.den.marvel.marvel.exception.CharacterNotFoundException;
import com.den.marvel.marvel.repository.CharacterRepository;
import com.den.marvel.marvel.repository.ComicRepository;
import com.den.marvel.marvel.service.CharacterService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import java.util.*;

@RestController()
@Tag(name = "Characters", description = "The characters API")
@RequestMapping("/v1/public/characters")
public class CharacterController extends AbstractController{

    @Autowired
    private CharacterRepository characterRepository;

    @Autowired
    private ComicRepository comicRepository;

    @Autowired
    private CharacterService characterService;

    @Operation(summary = "Get all characters", tags = "Characters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "list of characters",
                    content = {
                    @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Character.class))
                    ) })
            }
    )
    @GetMapping
    public ResponseEntity<ListResponse<Character>> getAllPage(
            @Parameter(description = "part of name for filters")  @RequestParam(required = false) String name,
            @Parameter(description = "page number")  @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "page size")  @RequestParam(defaultValue = "3") int size,
            @Parameter(description = "sort fields concat coma. Minus before field desc order(-id), field without minus - asc order(id) . ") @RequestParam(defaultValue = "-id") String sort) {

        try {
            Pageable pagingSort = this.getPageSort(page, size, sort);


            Page<Character> pageCharacters;
            if (name == null)
                pageCharacters = characterRepository.findAll(pagingSort);
            else
                pageCharacters = characterRepository.findByNameContaining(name, pagingSort);

            List<Character> characters = pageCharacters.getContent();

            if (characters.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(new ListResponse<Character>(pageCharacters), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Create character",
            description = "Create new character.", tags = "Characters")
    @ApiResponse(responseCode = "200", description = "character create")
    @ApiResponse(responseCode = "400", description = "Invalid character parameters")
    @PostMapping(value = "/")
    public ResponseEntity create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Created character object", required = true,
            content = @Content(
                    schema = @Schema(implementation = CharacterDto.class)
            )) @ModelAttribute CharacterDto characterDto) {
        try {
            return new ResponseEntity(characterService.create(characterDto), HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return handleConstraintViolationException(e);
        }
    }

    @Operation(summary = "Update character", description = "Update character", tags = "Characters")
    @ApiResponse(responseCode = "200", description = "character create")
    @ApiResponse(responseCode = "400", description = "Invalid character parameters")
    @ApiResponse(responseCode = "404", description = "Character not found by id")
    @PutMapping(value = "/{characterId}",
        consumes={MediaType.APPLICATION_JSON_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE} )
    public ResponseEntity update(
            @Parameter(description = "Character Id") @PathVariable String characterId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Update character object", required = true,
                    content = @Content(
                            schema = @Schema(implementation = CharacterDto.class)
                    )) @RequestBody CharacterDto characterDto) {
        try {
            characterDto.setId(characterId);
            return new ResponseEntity(characterService.update(characterDto), HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return handleConstraintViolationException(e);
        }
    }

    @Operation(summary = "Delete character",
            description = "Delete character by id.", tags = "Characters")
    @ApiResponse(responseCode = "200", description = "character deteled")
    @ApiResponse(responseCode = "404", description = "character not found")
    @DeleteMapping(value = "/{characterId}")
    public ResponseEntity delete(
            @Parameter(description = "Character Id")  @PathVariable String characterId) {
        Character character = characterRepository.findById(characterId).orElseThrow(() -> new CharacterNotFoundException(characterId));
        characterRepository.delete(character);
        return ResponseEntity.ok("success");
    }

    @Operation(summary = "Show character",
            description = "Show character by id.", tags = "Characters")
    @ApiResponse(responseCode = "200", description = "character find and show")
    @ApiResponse(responseCode = "404", description = "character not found")
    @GetMapping("/{id}")
    Character one(
            @Parameter(description = "Character Id") @PathVariable String id) {

        return characterRepository.findById(id)
                .orElseThrow(() -> new CharacterNotFoundException(id));
    }

    @Operation(summary = "Get all comics where character exist", tags = "Characters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "list of characters",
                    content = {
                            @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Comic.class))
                            ) }),
            @ApiResponse(responseCode = "404", description = "character not found")
            }
    )
    @GetMapping(value = "/{characterId}/comics")
    public ResponseEntity<ListResponse<Comic>> getAllComics(
            @Parameter(description = "Character Id") @PathVariable(required = true) String characterId,
            @Parameter(description = "page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "page size") @RequestParam(defaultValue = "3") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);

            Character character = characterRepository.findById(characterId)
                    .orElseThrow(() -> new CharacterNotFoundException(characterId));
            List<Comic> comics = characterService.getAllComics(character);

            final int start = (int)pageable.getOffset();
            final int end = Math.min((start + pageable.getPageSize()), comics.size());
            final Page<Comic> pageComics = new PageImpl<>(comics.subList(start, end), pageable, comics.size());
            if (comics.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(new ListResponse<Comic>(pageComics), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ResponseBody
    @ExceptionHandler(CharacterNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String characterNotFoundHandler(CharacterNotFoundException ex) {
        return ex.getMessage();
    }
}
