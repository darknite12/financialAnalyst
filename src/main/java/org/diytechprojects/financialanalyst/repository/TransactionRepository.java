package org.diytechprojects.financialanalyst.repository;

import org.diytechprojects.financialanalyst.domain.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Transaction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TransactionRepository extends R2dbcRepository<Transaction, Long>, TransactionRepositoryInternal {
    Flux<Transaction> findAllBy(Pageable pageable);

    @Query("SELECT * FROM transaction entity WHERE entity.income_id = :id")
    Flux<Transaction> findByIncome(Long id);

    @Query("SELECT * FROM transaction entity WHERE entity.income_id IS NULL")
    Flux<Transaction> findAllWhereIncomeIsNull();

    @Query("SELECT * FROM transaction entity WHERE entity.expense_id = :id")
    Flux<Transaction> findByExpense(Long id);

    @Query("SELECT * FROM transaction entity WHERE entity.expense_id IS NULL")
    Flux<Transaction> findAllWhereExpenseIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Transaction> findAll();

    @Override
    Mono<Transaction> findById(Long id);

    @Override
    <S extends Transaction> Mono<S> save(S entity);
}

interface TransactionRepositoryInternal {
    <S extends Transaction> Mono<S> insert(S entity);
    <S extends Transaction> Mono<S> save(S entity);
    Mono<Integer> update(Transaction entity);

    Flux<Transaction> findAll();
    Mono<Transaction> findById(Long id);
    Flux<Transaction> findAllBy(Pageable pageable);
    Flux<Transaction> findAllBy(Pageable pageable, Criteria criteria);
}
