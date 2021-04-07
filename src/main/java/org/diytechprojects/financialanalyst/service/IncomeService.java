package org.diytechprojects.financialanalyst.service;

import org.diytechprojects.financialanalyst.domain.Income;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Income}.
 */
public interface IncomeService {
    /**
     * Save a income.
     *
     * @param income the entity to save.
     * @return the persisted entity.
     */
    Mono<Income> save(Income income);

    /**
     * Partially updates a income.
     *
     * @param income the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Income> partialUpdate(Income income);

    /**
     * Get all the incomes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<Income> findAll(Pageable pageable);

    /**
     * Returns the number of incomes available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" income.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Income> findOne(Long id);

    /**
     * Delete the "id" income.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
