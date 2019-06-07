package com.melardev.spring.paginatedrestcrud.repositories

import com.melardev.spring.paginatedrestcrud.entities.Todo
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

import java.time.LocalDateTime

@Repository
interface TodosRepository : CrudRepository<Todo, Long> {

    fun findByIsCompleted(done: Boolean): List<Todo>

    fun findByIsCompleted(pageable: Pageable, done: Boolean): Page<Todo>

    override fun findAll(): List<Todo>

    fun findAll(pageRequest: Pageable): Page<Todo>

    fun findByIsCompletedTrue(): List<Todo>

    fun findByIsCompletedFalse(): List<Todo>

    fun findByIsCompletedIsTrue(): List<Todo>

    fun findByIsCompletedIsTrue(pageable: Pageable): Page<Todo>

    fun findByIsCompletedIsFalse(): List<Todo>

    fun findByTitleContains(title: String): List<Todo>

    fun findByDescriptionContains(description: String): List<Todo>

    fun findByIsCompletedTrue(pageRequest: PageRequest): Page<Todo>

    fun findByIsCompletedFalse(pageRequest: Pageable): Page<Todo>

    @Query("select t from Todo t where t.isCompleted = :completed")
    fun findByHqlCompletedIs(pageRequest: Pageable, completed: Boolean): Page<Todo>

    @Query("select t from Todo t where t.title like %:word%")
    fun findByHqlTitleLike(pageRequest: Pageable, word: String): Page<Todo>


    @Query("SELECT t FROM Todo t WHERE title = :title and description  = :description")
    fun findByHqlTitleAndDescription(title: String, description: String): List<Todo>

    @Query("select t FROM Todo t WHERE title = ?0 and description  = ?1")
    fun findByTHqlTitleAndDescription(title: String, description: String): List<Todo>

    fun findByCreatedAtAfter(date: LocalDateTime): List<Todo>

    fun findByCreatedAtBefore(date: LocalDateTime): List<Todo>
    /*
    // for deferred execution
    Flux<Todo> findByDescription(Mono<String> description);

    Mono<Todo> findByTitleAndDescription(Mono<String> title, String description);
    */
}