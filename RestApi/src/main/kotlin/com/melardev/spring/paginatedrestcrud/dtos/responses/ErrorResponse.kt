package com.melardev.spring.paginatedrestcrud.dtos.responses

class ErrorResponse(errorMessage: String) : AppResponse(false) {

    init {
        addFullMessage(errorMessage)
    }

}
