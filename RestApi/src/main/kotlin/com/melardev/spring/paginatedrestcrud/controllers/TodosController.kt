package com.melardev.spring.paginatedrestcrud.controllers


import com.melardev.spring.paginatedrestcrud.dtos.responses.*
import com.melardev.spring.paginatedrestcrud.entities.Todo
import com.melardev.spring.paginatedrestcrud.repositories.TodosRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.ZoneId
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@CrossOrigin
@RestController
@RequestMapping("/todos")
class TodosController {

    @Autowired
    private val todosRepository: TodosRepository? = null

    @GetMapping
    fun index(@RequestParam(value = "page", defaultValue = "1") page: Int,
              @RequestParam(value = "page_size", defaultValue = "10") pageSize: Int,
              request: HttpServletRequest): AppResponse {

        val pageable = getPageable(page, pageSize)
        val todos = this.todosRepository!!.findAll(pageable)
        val todoDtos = buildTodoDtos(todos)
        return TodoListResponse(PageMeta.build(todos, request.requestURI), todoDtos)
    }

    @GetMapping("simple")
    fun indexSimple(@RequestParam(value = "page", defaultValue = "1") page: Int,
                    @RequestParam(value = "page_size", defaultValue = "10") pageSize: Int,
                    request: HttpServletRequest): List<Todo> {

        val pageable = getPageable(page - 1, pageSize)
        val todos = this.todosRepository!!.findAll(pageable)
        return todos.content
    }

    @GetMapping("/{id}")
    operator fun get(@PathVariable("id") id: Long): ResponseEntity<AppResponse> {
        val todo = this.todosRepository!!.findById(id)

        /*
        return if (todo.isPresent)
            ResponseEntity(TodoDetailsResponse(todo.get()), HttpStatus.OK)
        else
            ResponseEntity(ErrorResponse("Todo not found"), HttpStatus.NOT_FOUND)
         */
        return todo.map { t ->
            ResponseEntity<AppResponse>(TodoDetailsResponse(t), HttpStatus.OK)
        }.orElseGet {
            ResponseEntity(ErrorResponse("Todo not found"), HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping("/pending")
    fun getNotCompletedTodos(@RequestParam(value = "page", defaultValue = "1") page: Int,
                             @RequestParam(value = "page_size", defaultValue = "10") pageSize: Int,
                             request: HttpServletRequest): AppResponse {
        val pageable = getPageable(page - 1, pageSize)
        val todos = this.todosRepository!!.findByIsCompletedFalse(pageable)
        return TodoListResponse(PageMeta.build(todos, request.requestURI), buildTodoDtos(todos))
    }

    @GetMapping("/completed")
    fun getCompletedTodos(@RequestParam(value = "page", defaultValue = "1") page: Int,
                          @RequestParam(value = "page_size", defaultValue = "10") pageSize: Int,
                          request: HttpServletRequest): AppResponse {
        val todosPage = todosRepository!!.findByIsCompletedIsTrue(getPageable(page, pageSize))
        return TodoListResponse(PageMeta.build(todosPage, request.requestURI), buildTodoDtos(todosPage))
    }

    @PostMapping
    fun create(@Valid @RequestBody todo: Todo): ResponseEntity<AppResponse> {
        return ResponseEntity(TodoDetailsResponse(todosRepository!!.save(todo), "Todo created successfully"), HttpStatus.CREATED)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable("id") id: Long,
               @RequestBody todoInput: Todo): ResponseEntity<AppResponse> {
        val optionalTodo = todosRepository!!.findById(id)
        return if (optionalTodo.isPresent) {
            val todo = optionalTodo.get()
            todo.title = todoInput.title

            val description = todoInput.description
            if (description != null) {
                todo.description = (description)
            }

            todo.isCompleted = todoInput.isCompleted
            ResponseEntity.ok<AppResponse>(TodoDetailsResponse(todosRepository.save(optionalTodo.get()),
                    "Todo updated successfully"))
        } else {
            ResponseEntity(ErrorResponse("Todo does not exist"), HttpStatus.NOT_FOUND)
        }
    }


    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: Long): ResponseEntity<AppResponse> {
        val todo = todosRepository!!.findById(id)
        return if (todo.isPresent) {
            todosRepository.delete(todo.get())
            ResponseEntity.ok<AppResponse>(SuccessResponse("You have successfully deleted the article"))
        } else {
            ResponseEntity(ErrorResponse("This todo does not exist"), HttpStatus.NOT_FOUND)
        }
    }

    @DeleteMapping
    fun deleteAll(): AppResponse {
        todosRepository!!.deleteAll()
        return SuccessResponse("Deleted all todos successfully")
    }

    @GetMapping(value = ["/after/{date}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getByDateAfter(@PathVariable("date") @DateTimeFormat(pattern = "dd-MM-yyyy") date: Date): List<Todo> {
        val articlesIterable = todosRepository!!.findByCreatedAtAfter(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
        val articleList = ArrayList<Todo>()
        articlesIterable.forEach(Consumer<Todo> { articleList.add(it) })
        return articleList
    }

    @GetMapping(value = ["/before/{date}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getByDateBefore(@PathVariable("date") @DateTimeFormat(pattern = "dd-MM-yyyy") date: Date): List<Todo> {
        val articlesIterable = todosRepository!!.findByCreatedAtBefore(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
        val articleList = ArrayList<Todo>()
        articlesIterable.forEach(Consumer<Todo> { articleList.add(it) })
        return articleList
    }

    private fun getPageable(page: Int, pageSize: Int): Pageable {
        var page = page
        var pageSize = pageSize
        if (page <= 0)
            page = 1

        if (pageSize <= 0)
            pageSize = 5

        return PageRequest.of(page - 1, pageSize, Sort.Direction.DESC, "createdAt")
    }

    private fun buildTodoDtos(todos: Page<Todo>): List<TodoSummaryDto> {
        return todos.content.stream()
                .map { TodoSummaryDto.build(it) }
                .collect(Collectors.toList())
    }
}
