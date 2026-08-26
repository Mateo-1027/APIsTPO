package com._3d.marketplace.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "El carrito solicitado no existe")
public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException() {
        super();
    }

    public CartNotFoundException(String message) {
        super(message);
    }
}
