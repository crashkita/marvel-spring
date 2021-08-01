package com.den.marvel.marvel.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

@Document
public class Comic {
    @Id
    public String id;
    @NotNull(message = "Title cannot be null")
    @Size(min = 3, max = 15, message = "Name should not be less than 3")
    public String title;
    @NotBlank(message = "description cannot be null")
    public String description;

    public List<Image> images;

    @DBRef
    @JsonIgnore
    private List<Character> characters;

    public Comic() {
    }

    public Comic(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getId() {
        return id;
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

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public List<com.den.marvel.marvel.entity.Character> getCharacters() {
        return characters;
    }

    public void setCharacters(List<com.den.marvel.marvel.entity.Character> character) {
        characters = character;
    }

    public void addCharacter(Character character) {
        if (characters == null) {
            characters = new ArrayList<>();
        }
        characters.add(character);
    }

    public void addImage(Image image) {
        if (images == null) {
            images = new LinkedList<>();
        }
        images.add(image);
    }
}
