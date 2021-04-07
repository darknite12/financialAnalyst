package org.diytechprojects.financialanalyst.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.diytechprojects.financialanalyst.IntegrationTest;
import org.diytechprojects.financialanalyst.domain.Transaction;
import org.diytechprojects.financialanalyst.repository.TransactionRepository;
import org.diytechprojects.financialanalyst.service.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link TransactionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class TransactionResourceIT {

    private static final String DEFAULT_ACCOUNT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_ACCOUNT_TYPE = "BBBBBBBBBB";

    private static final Instant DEFAULT_TRANSACTION_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TRANSACTION_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CHEQUE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_CHEQUE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION_1 = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION_1 = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION_2 = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION_2 = "BBBBBBBBBB";

    private static final Long DEFAULT_AMOUNT_CAD = 1L;
    private static final Long UPDATED_AMOUNT_CAD = 2L;

    private static final Long DEFAULT_AMOUNT_USD = 1L;
    private static final Long UPDATED_AMOUNT_USD = 2L;

    private static final Boolean DEFAULT_IS_TRACKED = false;
    private static final Boolean UPDATED_IS_TRACKED = true;

    private static final String ENTITY_API_URL = "/api/transactions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Transaction transaction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Transaction createEntity(EntityManager em) {
        Transaction transaction = new Transaction()
            .accountType(DEFAULT_ACCOUNT_TYPE)
            .transactionDate(DEFAULT_TRANSACTION_DATE)
            .chequeNumber(DEFAULT_CHEQUE_NUMBER)
            .description1(DEFAULT_DESCRIPTION_1)
            .description2(DEFAULT_DESCRIPTION_2)
            .amountCAD(DEFAULT_AMOUNT_CAD)
            .amountUSD(DEFAULT_AMOUNT_USD)
            .isTracked(DEFAULT_IS_TRACKED);
        return transaction;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Transaction createUpdatedEntity(EntityManager em) {
        Transaction transaction = new Transaction()
            .accountType(UPDATED_ACCOUNT_TYPE)
            .transactionDate(UPDATED_TRANSACTION_DATE)
            .chequeNumber(UPDATED_CHEQUE_NUMBER)
            .description1(UPDATED_DESCRIPTION_1)
            .description2(UPDATED_DESCRIPTION_2)
            .amountCAD(UPDATED_AMOUNT_CAD)
            .amountUSD(UPDATED_AMOUNT_USD)
            .isTracked(UPDATED_IS_TRACKED);
        return transaction;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Transaction.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        transaction = createEntity(em);
    }

    @Test
    void createTransaction() throws Exception {
        int databaseSizeBeforeCreate = transactionRepository.findAll().collectList().block().size();
        // Create the Transaction
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(transaction))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll().collectList().block();
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate + 1);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getAccountType()).isEqualTo(DEFAULT_ACCOUNT_TYPE);
        assertThat(testTransaction.getTransactionDate()).isEqualTo(DEFAULT_TRANSACTION_DATE);
        assertThat(testTransaction.getChequeNumber()).isEqualTo(DEFAULT_CHEQUE_NUMBER);
        assertThat(testTransaction.getDescription1()).isEqualTo(DEFAULT_DESCRIPTION_1);
        assertThat(testTransaction.getDescription2()).isEqualTo(DEFAULT_DESCRIPTION_2);
        assertThat(testTransaction.getAmountCAD()).isEqualTo(DEFAULT_AMOUNT_CAD);
        assertThat(testTransaction.getAmountUSD()).isEqualTo(DEFAULT_AMOUNT_USD);
        assertThat(testTransaction.getIsTracked()).isEqualTo(DEFAULT_IS_TRACKED);
    }

    @Test
    void createTransactionWithExistingId() throws Exception {
        // Create the Transaction with an existing ID
        transaction.setId(1L);

        int databaseSizeBeforeCreate = transactionRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(transaction))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll().collectList().block();
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllTransactions() {
        // Initialize the database
        transactionRepository.save(transaction).block();

        // Get all the transactionList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(transaction.getId().intValue()))
            .jsonPath("$.[*].accountType")
            .value(hasItem(DEFAULT_ACCOUNT_TYPE))
            .jsonPath("$.[*].transactionDate")
            .value(hasItem(DEFAULT_TRANSACTION_DATE.toString()))
            .jsonPath("$.[*].chequeNumber")
            .value(hasItem(DEFAULT_CHEQUE_NUMBER))
            .jsonPath("$.[*].description1")
            .value(hasItem(DEFAULT_DESCRIPTION_1))
            .jsonPath("$.[*].description2")
            .value(hasItem(DEFAULT_DESCRIPTION_2))
            .jsonPath("$.[*].amountCAD")
            .value(hasItem(DEFAULT_AMOUNT_CAD.intValue()))
            .jsonPath("$.[*].amountUSD")
            .value(hasItem(DEFAULT_AMOUNT_USD.intValue()))
            .jsonPath("$.[*].isTracked")
            .value(hasItem(DEFAULT_IS_TRACKED.booleanValue()));
    }

    @Test
    void getTransaction() {
        // Initialize the database
        transactionRepository.save(transaction).block();

        // Get the transaction
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, transaction.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(transaction.getId().intValue()))
            .jsonPath("$.accountType")
            .value(is(DEFAULT_ACCOUNT_TYPE))
            .jsonPath("$.transactionDate")
            .value(is(DEFAULT_TRANSACTION_DATE.toString()))
            .jsonPath("$.chequeNumber")
            .value(is(DEFAULT_CHEQUE_NUMBER))
            .jsonPath("$.description1")
            .value(is(DEFAULT_DESCRIPTION_1))
            .jsonPath("$.description2")
            .value(is(DEFAULT_DESCRIPTION_2))
            .jsonPath("$.amountCAD")
            .value(is(DEFAULT_AMOUNT_CAD.intValue()))
            .jsonPath("$.amountUSD")
            .value(is(DEFAULT_AMOUNT_USD.intValue()))
            .jsonPath("$.isTracked")
            .value(is(DEFAULT_IS_TRACKED.booleanValue()));
    }

    @Test
    void getNonExistingTransaction() {
        // Get the transaction
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewTransaction() throws Exception {
        // Initialize the database
        transactionRepository.save(transaction).block();

        int databaseSizeBeforeUpdate = transactionRepository.findAll().collectList().block().size();

        // Update the transaction
        Transaction updatedTransaction = transactionRepository.findById(transaction.getId()).block();
        updatedTransaction
            .accountType(UPDATED_ACCOUNT_TYPE)
            .transactionDate(UPDATED_TRANSACTION_DATE)
            .chequeNumber(UPDATED_CHEQUE_NUMBER)
            .description1(UPDATED_DESCRIPTION_1)
            .description2(UPDATED_DESCRIPTION_2)
            .amountCAD(UPDATED_AMOUNT_CAD)
            .amountUSD(UPDATED_AMOUNT_USD)
            .isTracked(UPDATED_IS_TRACKED);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedTransaction.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedTransaction))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll().collectList().block();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getAccountType()).isEqualTo(UPDATED_ACCOUNT_TYPE);
        assertThat(testTransaction.getTransactionDate()).isEqualTo(UPDATED_TRANSACTION_DATE);
        assertThat(testTransaction.getChequeNumber()).isEqualTo(UPDATED_CHEQUE_NUMBER);
        assertThat(testTransaction.getDescription1()).isEqualTo(UPDATED_DESCRIPTION_1);
        assertThat(testTransaction.getDescription2()).isEqualTo(UPDATED_DESCRIPTION_2);
        assertThat(testTransaction.getAmountCAD()).isEqualTo(UPDATED_AMOUNT_CAD);
        assertThat(testTransaction.getAmountUSD()).isEqualTo(UPDATED_AMOUNT_USD);
        assertThat(testTransaction.getIsTracked()).isEqualTo(UPDATED_IS_TRACKED);
    }

    @Test
    void putNonExistingTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().collectList().block().size();
        transaction.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, transaction.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(transaction))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll().collectList().block();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().collectList().block().size();
        transaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(transaction))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll().collectList().block();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().collectList().block().size();
        transaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(transaction))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll().collectList().block();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTransactionWithPatch() throws Exception {
        // Initialize the database
        transactionRepository.save(transaction).block();

        int databaseSizeBeforeUpdate = transactionRepository.findAll().collectList().block().size();

        // Update the transaction using partial update
        Transaction partialUpdatedTransaction = new Transaction();
        partialUpdatedTransaction.setId(transaction.getId());

        partialUpdatedTransaction.transactionDate(UPDATED_TRANSACTION_DATE).isTracked(UPDATED_IS_TRACKED);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTransaction.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTransaction))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll().collectList().block();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getAccountType()).isEqualTo(DEFAULT_ACCOUNT_TYPE);
        assertThat(testTransaction.getTransactionDate()).isEqualTo(UPDATED_TRANSACTION_DATE);
        assertThat(testTransaction.getChequeNumber()).isEqualTo(DEFAULT_CHEQUE_NUMBER);
        assertThat(testTransaction.getDescription1()).isEqualTo(DEFAULT_DESCRIPTION_1);
        assertThat(testTransaction.getDescription2()).isEqualTo(DEFAULT_DESCRIPTION_2);
        assertThat(testTransaction.getAmountCAD()).isEqualTo(DEFAULT_AMOUNT_CAD);
        assertThat(testTransaction.getAmountUSD()).isEqualTo(DEFAULT_AMOUNT_USD);
        assertThat(testTransaction.getIsTracked()).isEqualTo(UPDATED_IS_TRACKED);
    }

    @Test
    void fullUpdateTransactionWithPatch() throws Exception {
        // Initialize the database
        transactionRepository.save(transaction).block();

        int databaseSizeBeforeUpdate = transactionRepository.findAll().collectList().block().size();

        // Update the transaction using partial update
        Transaction partialUpdatedTransaction = new Transaction();
        partialUpdatedTransaction.setId(transaction.getId());

        partialUpdatedTransaction
            .accountType(UPDATED_ACCOUNT_TYPE)
            .transactionDate(UPDATED_TRANSACTION_DATE)
            .chequeNumber(UPDATED_CHEQUE_NUMBER)
            .description1(UPDATED_DESCRIPTION_1)
            .description2(UPDATED_DESCRIPTION_2)
            .amountCAD(UPDATED_AMOUNT_CAD)
            .amountUSD(UPDATED_AMOUNT_USD)
            .isTracked(UPDATED_IS_TRACKED);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTransaction.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTransaction))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll().collectList().block();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getAccountType()).isEqualTo(UPDATED_ACCOUNT_TYPE);
        assertThat(testTransaction.getTransactionDate()).isEqualTo(UPDATED_TRANSACTION_DATE);
        assertThat(testTransaction.getChequeNumber()).isEqualTo(UPDATED_CHEQUE_NUMBER);
        assertThat(testTransaction.getDescription1()).isEqualTo(UPDATED_DESCRIPTION_1);
        assertThat(testTransaction.getDescription2()).isEqualTo(UPDATED_DESCRIPTION_2);
        assertThat(testTransaction.getAmountCAD()).isEqualTo(UPDATED_AMOUNT_CAD);
        assertThat(testTransaction.getAmountUSD()).isEqualTo(UPDATED_AMOUNT_USD);
        assertThat(testTransaction.getIsTracked()).isEqualTo(UPDATED_IS_TRACKED);
    }

    @Test
    void patchNonExistingTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().collectList().block().size();
        transaction.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, transaction.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(transaction))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll().collectList().block();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().collectList().block().size();
        transaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(transaction))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll().collectList().block();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().collectList().block().size();
        transaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(transaction))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll().collectList().block();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTransaction() {
        // Initialize the database
        transactionRepository.save(transaction).block();

        int databaseSizeBeforeDelete = transactionRepository.findAll().collectList().block().size();

        // Delete the transaction
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, transaction.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Transaction> transactionList = transactionRepository.findAll().collectList().block();
        assertThat(transactionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
