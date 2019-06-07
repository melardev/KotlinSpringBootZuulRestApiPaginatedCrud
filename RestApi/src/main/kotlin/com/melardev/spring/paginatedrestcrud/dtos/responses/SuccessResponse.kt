package com.melardev.spring.paginatedrestcrud.dtos.responses

open class SuccessResponse @JvmOverloads constructor(message: String? = null) : AppResponse(true) {

    init {
        addFullMessage(message)
    }
}
