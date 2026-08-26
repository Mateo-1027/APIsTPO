package com._3d.marketplace.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "El producto solicitado no existe")
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException() {
        super();
    }
    
    public ProductNotFoundException(String message) {
        super(message);
    }
}
