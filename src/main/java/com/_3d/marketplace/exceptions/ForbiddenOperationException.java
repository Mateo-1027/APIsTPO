package com._3d.marketplace.exceptions;

/**
 * Se lanza cuando un usuario intenta modificar un recurso que no le pertenece
 * (por ejemplo, editar o borrar un producto publicado por otro vendedor).
 */
public class ForbiddenOperationException extends RuntimeException {
    public ForbiddenOperationException(String message) {
        super(message);
    }
}
