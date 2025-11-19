package com.board.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C001", "Internal server error"),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C002", "Invalid input value"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C003", "Method not allowed"),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C004", "Entity not found"),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "C005", "Invalid type value"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "C006", "Access denied"),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "User not found"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "U002", "Email already exists"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "U003", "Invalid password"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "U004", "Unauthorized"),

    // Post
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "Post not found"),
    POST_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "P002", "Post already deleted"),

    // Comment
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "M001", "Comment not found"),
    COMMENT_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "M002", "Comment already deleted"),
    INVALID_PARENT_COMMENT(HttpStatus.BAD_REQUEST, "M003", "Invalid parent comment"),

    // Category
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "T001", "Category not found"),
    DUPLICATE_CATEGORY_NAME(HttpStatus.CONFLICT, "T002", "Category name already exists"),

    // Like
    ALREADY_LIKED(HttpStatus.CONFLICT, "L001", "Already liked"),
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "L002", "Like not found");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
