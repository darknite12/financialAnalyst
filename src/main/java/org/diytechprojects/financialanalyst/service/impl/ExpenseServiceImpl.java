package org.diytechprojects.financialanalyst.service.impl;

import org.diytechprojects.financialanalyst.domain.Expense;
import org.diytechprojects.financialanalyst.repository.ExpenseRepository;
import org.diytechprojects.financialanalyst.service.ExpenseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Expense}.
 */
@Service
@Transactional
public class ExpenseServiceImpl implements ExpenseService {

    private final Logger log = LoggerFactory.getLogger(ExpenseServiceImpl.class);

    private final ExpenseRepository expenseRepository;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Override
    public Mono<Expense> save(Expense expense) {
        log.debug("Request to save Expense : {}", expense);
        return expenseRepository.save(expense);
    }

    @Override
    public Mono<Expense> partialUpdate(Expense expense) {
        log.debug("Request to partially update Expense : {}", expense);

        return expenseRepository
            .findById(expense.getId())
            .map(
                existingExpense -> {
                    if (expense.getCategory() != null) {
                        existingExpense.setCategory(expense.getCategory());
                    }
                    if (expense.getName() != null) {
                        existingExpense.setName(expense.getName());
                    }
                    if (expense.getSearchString1() != null) {
                        existingExpense.setSearchString1(expense.getSearchString1());
                    }
                    if (expense.getSearchString2() != null) {
                        existingExpense.setSearchString2(expense.getSearchString2());
                    }

                    return existingExpense;
                }
            )
            .flatMap(expenseRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Expense> findAll(Pageable pageable) {
        log.debug("Request to get all Expenses");
        return expenseRepository.findAllBy(pageable);
    }

    public Mono<Long> countAll() {
        return expenseRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Expense> findOne(Long id) {
        log.debug("Request to get Expense : {}", id);
        return expenseRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Expense : {}", id);
        return expenseRepository.deleteById(id);
    }
}
