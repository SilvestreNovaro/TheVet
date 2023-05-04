package com.example.veterinaria.exception;

public class NotFoundExceptionLong extends RuntimeException{

    public NotFoundExceptionLong(Long id) {
        super("No se encontró el producto con el ID: " + id);
    }
}

