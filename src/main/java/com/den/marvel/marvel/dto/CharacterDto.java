package com.den.marvel.marvel.dto;

import com.den.marvel.marvel.validator.ImageFile;
import com.den.marvel.marvel.validator.OnUpdate;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CharacterDto {
    @Parameter(description = "name of character")
    @NotNull(message = "Name cannot be null")
    @Size(min = 3, max = 15, message = "Name should not be less than 3")
    public String name;
    @NotBlank(message = "description cannot be null")
    public String description;
    @ImageFile
    private MultipartFile image;
    @NotBlank(groups = OnUpdate.class,message = "id cannot be null")
    private String id;

    public CharacterDto(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
