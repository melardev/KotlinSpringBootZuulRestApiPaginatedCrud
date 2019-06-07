package com.melardev.spring.paginatedrestcrud.dtos.responses

class TodoListResponse(val pageMeta: PageMeta, val todos: Collection<TodoSummaryDto>) : SuccessResponse()
