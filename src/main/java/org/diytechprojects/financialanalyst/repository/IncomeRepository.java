package org.diytechprojects.financialanalyst.repository;

import org.diytechprojects.financialanalyst.domain.Income;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Income entity.
 */
@SuppressWarnings("unused")
@Repository
public interface IncomeRepository extends R2dbcRepository<Income, Long>, IncomeRepositoryInternal {
    Flux<Income> findAllBy(Pageable pageable);

    // just to avoid having unambigous methods
    @Override
    Flux<Income> findAll();

    @Override
    Mono<Income> findById(Long id);

    @Override
    <S extends Income> Mono<S> save(S entity);
}

interface IncomeRepositoryInternal {
    <S extends Income> Mono<S> insert(S entity);
    <S extends Income> Mono<S> save(S entity);
    Mono<Integer> update(Income entity);

    Flux<Income> findAll();
    Mono<Income> findById(Long id);
    Flux<Income> findAllBy(Pageable pageable);
    Flux<Income> findAllBy(Pageable pageable, Criteria criteria);
}
