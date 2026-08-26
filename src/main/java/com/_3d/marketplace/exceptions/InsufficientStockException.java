package com._3d.marketplace.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "No hay suficiente stock para completar la operación")
public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException() {
        super();
    }

    public InsufficientStockException(String message) {
        super(message);
    }
}
