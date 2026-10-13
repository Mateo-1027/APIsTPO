package com._3d.marketplace.exceptions;

public class CategoryDuplicateException extends Exception {
    public CategoryDuplicateException() {
        super("Ya existe una categoría con esa descripción.");
    }
}
