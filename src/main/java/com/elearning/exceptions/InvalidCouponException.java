package com.elearning.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidCouponException extends BusinessException {

    public InvalidCouponException(String message) {
        super("INVALID_COUPON", message, HttpStatus.BAD_REQUEST);
    }
}
