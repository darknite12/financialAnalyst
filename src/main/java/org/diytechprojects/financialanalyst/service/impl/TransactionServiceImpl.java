package org.diytechprojects.financialanalyst.service.impl;

import org.diytechprojects.financialanalyst.domain.Transaction;
import org.diytechprojects.financialanalyst.repository.TransactionRepository;
import org.diytechprojects.financialanalyst.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Transaction}.
 */
@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Mono<Transaction> save(Transaction transaction) {
        log.debug("Request to save Transaction : {}", transaction);
        return transactionRepository.save(transaction);
    }

    @Override
    public Mono<Transaction> partialUpdate(Transaction transaction) {
        log.debug("Request to partially update Transaction : {}", transaction);

        return transactionRepository
            .findById(transaction.getId())
            .map(
                existingTransaction -> {
                    if (transaction.getAccountType() != null) {
                        existingTransaction.setAccountType(transaction.getAccountType());
                    }
                    if (transaction.getTransactionDate() != null) {
                        existingTransaction.setTransactionDate(transaction.getTransactionDate());
                    }
                    if (transaction.getChequeNumber() != null) {
                        existingTransaction.setChequeNumber(transaction.getChequeNumber());
                    }
                    if (transaction.getDescription1() != null) {
                        existingTransaction.setDescription1(transaction.getDescription1());
                    }
                    if (transaction.getDescription2() != null) {
                        existingTransaction.setDescription2(transaction.getDescription2());
                    }
                    if (transaction.getAmountCAD() != null) {
                        existingTransaction.setAmountCAD(transaction.getAmountCAD());
                    }
                    if (transaction.getAmountUSD() != null) {
                        existingTransaction.setAmountUSD(transaction.getAmountUSD());
                    }
                    if (transaction.getIsTracked() != null) {
                        existingTransaction.setIsTracked(transaction.getIsTracked());
                    }

                    return existingTransaction;
                }
            )
            .flatMap(transactionRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Transaction> findAll(Pageable pageable) {
        log.debug("Request to get all Transactions");
        return transactionRepository.findAllBy(pageable);
    }

    public Mono<Long> countAll() {
        return transactionRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Transaction> findOne(Long id) {
        log.debug("Request to get Transaction : {}", id);
        return transactionRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Transaction : {}", id);
        return transactionRepository.deleteById(id);
    }
}
