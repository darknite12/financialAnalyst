package org.diytechprojects.financialanalyst.service;

import org.diytechprojects.financialanalyst.domain.Expense;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Expense}.
 */
public interface ExpenseService {
    /**
     * Save a expense.
     *
     * @param expense the entity to save.
     * @return the persisted entity.
     */
    Mono<Expense> save(Expense expense);

    /**
     * Partially updates a expense.
     *
     * @param expense the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Expense> partialUpdate(Expense expense);

    /**
     * Get all the expenses.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<Expense> findAll(Pageable pageable);

    /**
     * Returns the number of expenses available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" expense.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Expense> findOne(Long id);

    /**
     * Delete the "id" expense.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
