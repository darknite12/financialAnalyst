package org.diytechprojects.financialanalyst.repository;

import org.diytechprojects.financialanalyst.domain.Expense;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Expense entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ExpenseRepository extends R2dbcRepository<Expense, Long>, ExpenseRepositoryInternal {
    Flux<Expense> findAllBy(Pageable pageable);

    // just to avoid having unambigous methods
    @Override
    Flux<Expense> findAll();

    @Override
    Mono<Expense> findById(Long id);

    @Override
    <S extends Expense> Mono<S> save(S entity);
}

interface ExpenseRepositoryInternal {
    <S extends Expense> Mono<S> insert(S entity);
    <S extends Expense> Mono<S> save(S entity);
    Mono<Integer> update(Expense entity);

    Flux<Expense> findAll();
    Mono<Expense> findById(Long id);
    Flux<Expense> findAllBy(Pageable pageable);
    Flux<Expense> findAllBy(Pageable pageable, Criteria criteria);
}
