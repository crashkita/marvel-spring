package com.den.marvel.marvel.dto;

import com.den.marvel.marvel.validator.ImageFile;
import com.den.marvel.marvel.validator.OnUpdate;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ComicDto {
    @NotNull(message = "Title cannot be null")
    @Size(min = 3, max = 15, message = "Title should not be less than 3")
    public String title;
    @NotBlank(message = "description cannot be null")
    public String description;
    @ImageFile
    private MultipartFile[] images;
    private String[] characters;
    @NotBlank(groups = OnUpdate.class,message = "id cannot be null")
    private String id;

    public ComicDto() {
    }

    public ComicDto(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getCharacters() {
        return characters;
    }

    public void setCharacters(String[] characters) {
        this.characters = characters;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<MultipartFile> getImages() {
        return images == null ? new ArrayList<>() : Arrays.asList(images);
    }

    public void setImages(MultipartFile[] images) {
        this.images = images;
    }
}
