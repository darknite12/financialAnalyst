package org.diytechprojects.financialanalyst.service.impl;

import org.diytechprojects.financialanalyst.domain.Income;
import org.diytechprojects.financialanalyst.repository.IncomeRepository;
import org.diytechprojects.financialanalyst.service.IncomeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Income}.
 */
@Service
@Transactional
public class IncomeServiceImpl implements IncomeService {

    private final Logger log = LoggerFactory.getLogger(IncomeServiceImpl.class);

    private final IncomeRepository incomeRepository;

    public IncomeServiceImpl(IncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
    }

    @Override
    public Mono<Income> save(Income income) {
        log.debug("Request to save Income : {}", income);
        return incomeRepository.save(income);
    }

    @Override
    public Mono<Income> partialUpdate(Income income) {
        log.debug("Request to partially update Income : {}", income);

        return incomeRepository
            .findById(income.getId())
            .map(
                existingIncome -> {
                    if (income.getName() != null) {
                        existingIncome.setName(income.getName());
                    }
                    if (income.getSearchString1() != null) {
                        existingIncome.setSearchString1(income.getSearchString1());
                    }
                    if (income.getSearchString2() != null) {
                        existingIncome.setSearchString2(income.getSearchString2());
                    }

                    return existingIncome;
                }
            )
            .flatMap(incomeRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Income> findAll(Pageable pageable) {
        log.debug("Request to get all Incomes");
        return incomeRepository.findAllBy(pageable);
    }

    public Mono<Long> countAll() {
        return incomeRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Income> findOne(Long id) {
        log.debug("Request to get Income : {}", id);
        return incomeRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Income : {}", id);
        return incomeRepository.deleteById(id);
    }
}
