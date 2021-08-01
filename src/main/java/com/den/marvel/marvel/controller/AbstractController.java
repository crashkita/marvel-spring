package com.den.marvel.marvel.controller;

import com.den.marvel.marvel.dto.ErrorResponse;
import com.den.marvel.marvel.entity.Character;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@Validated
public class AbstractController {

    protected Pageable getPageSort(int page, int size, String sort) {
        List<Sort.Order> orders = new ArrayList<>();

        if (sort.contains(",")) {
            for (String sortOrder : sort.split(",")) {
                orders.add(getSortOrder(sortOrder));
            }
        } else {
            orders.add(getSortOrder(sort));
        }

        return PageRequest.of(page, size, Sort.by(orders));
    }

    private Sort.Order getSortOrder(String s) {
        return new Sort.Order(s.contains("-") ? Sort.Direction.DESC : Sort.Direction.ASC, s.replace("-", ""));
    }


    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse(e), HttpStatus.BAD_REQUEST);
    }
}
