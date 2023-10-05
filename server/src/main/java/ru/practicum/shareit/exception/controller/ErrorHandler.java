package ru.practicum.shareit.exception.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.model.*;

/**
 * Класс предназначен для обработки исключений.
 */
@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    /**
     * Обработка исключений, связанных с отсутствием запрашиваемых сущностей.
     *
     * @param e - исключение типа NotFoundException, выбрасываемое при отсутствии в репозиториях запрашиваемой сущности.
     * @return объект ответа, содержащий сообщение об ошибке
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.info("Error: " + e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    /**
     * @param e исключение типа MissingRequestHeaderException
     * @return объект ответа, содержащий сообщение об ошибке
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingRequestHeaderException(final MissingRequestHeaderException e) {
        log.info("Error: " + e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    /**
     * Обработка исключений, связанных с нарушением логики "пользователь является владельцем вещи"
     *
     * @param e исключение типа UserIsNotOwnerException, выбрасываемое при несовпадении владельца вещи с ожидаемым
     * @return объект ответа, содержащий сообщение об ошибке
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserIsNotOwnerException(final UserIsNotItemOwnerException e) {
        log.info("Error: " + e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleItemNotAvailableException(final ItemNotAvailableException e) {
        log.info("Error: " + e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        log.info("Error: " + e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserTryBookingItsItemException(final UserTryBookingItsItemException e) {
        log.info("Error: " + e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.info("Error: " + e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleAccessDeniedException(final AccessDeniedException e) {
        log.info("Error: " + e.getMessage());
        return new ErrorResponse(e.getMessage());
    }


    /**
     * Обработка прочих непроверямых исключений.
     *
     * @param e непроверяемое исключение.
     * @return объект ответа, содержащий сообщение об ошибке.
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(final RuntimeException e) {
        log.warn("Error: " + e.getMessage());
        return new ErrorResponse(e.getMessage(), e.getStackTrace());
    }
}