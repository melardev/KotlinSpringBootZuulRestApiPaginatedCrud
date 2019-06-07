package com.melardev.spring.paginatedrestcrud.dtos.responses

import java.util.*

abstract class AppResponse {
    private var success: Boolean = false
    private var fullMessages: MutableList<String>? = null

    val isSuccess: Boolean
        get() = success

    fun getFullMessages(): List<String>? {
        return fullMessages
    }

    fun setFullMessages(fullMessages: MutableList<String>) {
        this.fullMessages = fullMessages
    }

    constructor() {
        println("Created AppResponse")
    }

    protected constructor(success: Boolean) {
        this.success = success
        fullMessages = ArrayList()
    }


    protected fun addFullMessage(message: String?) {
        if (message == null)
            return
        if (fullMessages == null)
            fullMessages = ArrayList()

        fullMessages!!.add(message)
    }

}