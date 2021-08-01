package com.den.marvel.marvel.controller;

import com.den.marvel.marvel.dto.CharacterDto;
import com.den.marvel.marvel.dto.ComicDto;
import com.den.marvel.marvel.dto.ListResponse;
import com.den.marvel.marvel.entity.Character;
import com.den.marvel.marvel.entity.Comic;
import com.den.marvel.marvel.exception.CharacterNotFoundException;
import com.den.marvel.marvel.exception.ComicNotFoundException;
import com.den.marvel.marvel.repository.ComicRepository;
import com.den.marvel.marvel.service.ComicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import java.util.List;

@RestController
@Tag(name = "Comics", description = "The Comics API")
@RequestMapping("/v1/public/comics")
public class ComicController extends AbstractController{

    @Autowired
    private ComicRepository comicRepository;

    @Autowired
    private ComicService comicService;

    @Operation(summary = "Get all comics", tags = "Comics")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "list of comics",
                    content = {
                            @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Comic.class))
                            ) })
            }
    )
    @GetMapping
    public ResponseEntity<ListResponse<Comic>> getAllPage(
            @Parameter(description = "part of name for filters") @RequestParam(required = false) String name,
            @Parameter(description = "page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "page size")  @RequestParam(defaultValue = "3") int size,
            @Parameter(description = "sort fields concat coma. Minus before field desc order(-id), field without minus - asc order(id) . ") @RequestParam(defaultValue = "-id") String sort) {

        try {
            Pageable pagingSort = this.getPageSort(page, size, sort);


            Page<Comic> pageComics;
            if (name == null)
                pageComics = comicRepository.findAll(pagingSort);
            else
                pageComics = comicRepository.findByTitleContaining(name, pagingSort);

            List<Comic> comics = pageComics.getContent();

            if (comics.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(new ListResponse<Comic>(pageComics), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Create comic",
            description = "Create new comic.", tags = "Comics")
    @ApiResponse(responseCode = "200", description = "comic create")
    @ApiResponse(responseCode = "400", description = "Invalid comic parameters")
    @PostMapping(value = "/", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE} )
    public ResponseEntity create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Created comic object", required = true,
                    content = @Content(
                            schema = @Schema(implementation = CharacterDto.class)
                    )) @ModelAttribute ComicDto comicDto) {
        try {
            return new ResponseEntity(comicService.create(comicDto), HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return handleConstraintViolationException(e);
        }
    }

    @Operation(summary = "Update comic", description = "Update comic", tags = "Comics")
    @ApiResponse(responseCode = "200", description = "Comic create")
    @ApiResponse(responseCode = "400", description = "Invalid comic parameters")
    @ApiResponse(responseCode = "404", description = "Comic not found by id")
    @PutMapping(value = "/{comicId}",
            consumes={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE} )
    public ResponseEntity update(
            @Parameter(description = "Comic Id") @PathVariable String comicId,
                                 @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                         description = "Update comic object", required = true,
                                         content = @Content(
                                                 schema = @Schema(implementation = CharacterDto.class)
                                         )) @RequestBody ComicDto comicDto) {
        try {
            comicDto.setId(comicId);
            return new ResponseEntity(comicService.update(comicDto), HttpStatus.OK);
        } catch (ConstraintViolationException e) {
            return handleConstraintViolationException(e);
        }
    }

    @Operation(summary = "Delete Comic",
            description = "Delete Comic by id.", tags = "Comics")
    @ApiResponse(responseCode = "200", description = "Comic deteled")
    @ApiResponse(responseCode = "404", description = "Comic not found")
    @DeleteMapping(value = "/{comicId}")
    public ResponseEntity delete(@Parameter(description = "Comic Id") @PathVariable String comicId) {
        Comic comic = comicRepository.findById(comicId).orElseThrow(() -> new ComicNotFoundException(comicId));
        comicRepository.delete(comic);
        return ResponseEntity.ok("success");
    }

    @Operation(summary = "Show Comic",
            description = "Show Comic by id.", tags = "Comics")
    @ApiResponse(responseCode = "200", description = "Comic find and show")
    @ApiResponse(responseCode = "404", description = "Comic not found")
    @GetMapping("/{id}")
    Comic one(@Parameter(description = "Comic Id") @PathVariable String id) {
        return comicRepository.findById(id)
                .orElseThrow(() -> new ComicNotFoundException(id));
    }

    @Operation(summary = "Get all characters in comic", tags = "Comics")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "list of character",
                    content = {
                            @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Character.class))
                            ) }),
            @ApiResponse(responseCode = "404", description = "comic not found")
    }
    )
    @GetMapping("/{id}/characters")
    public ResponseEntity<ListResponse<Character>>  characters(
                @Parameter(description = "Comic Id")  @PathVariable String id,
                @Parameter(description = "page number")  @RequestParam(defaultValue = "0") int page,
                @Parameter(description = "page number")  @RequestParam(defaultValue = "3") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Comic comic = comicRepository.findById(id)
                .orElseThrow(() -> new ComicNotFoundException(id));
        List<Character> characters = comic.getCharacters();

        if (characters.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        final int start = (int)pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), characters.size());
        final Page<Character> pageCharacters = new PageImpl<>(characters.subList(start, end), pageable, characters.size());

        return new ResponseEntity<>(new ListResponse<Character>(pageCharacters), HttpStatus.OK);
    }

    @ResponseBody
    @ExceptionHandler(ComicNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String comicNotFoundHandler(ComicNotFoundException ex) {
        return ex.getMessage();
    }
}
