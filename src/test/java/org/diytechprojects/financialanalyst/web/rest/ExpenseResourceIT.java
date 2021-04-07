package org.diytechprojects.financialanalyst.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.diytechprojects.financialanalyst.IntegrationTest;
import org.diytechprojects.financialanalyst.domain.Expense;
import org.diytechprojects.financialanalyst.repository.ExpenseRepository;
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
 * Integration tests for the {@link ExpenseResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class ExpenseResourceIT {

    private static final String DEFAULT_CATEGORY = "AAAAAAAAAA";
    private static final String UPDATED_CATEGORY = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SEARCH_STRING_1 = "AAAAAAAAAA";
    private static final String UPDATED_SEARCH_STRING_1 = "BBBBBBBBBB";

    private static final String DEFAULT_SEARCH_STRING_2 = "AAAAAAAAAA";
    private static final String UPDATED_SEARCH_STRING_2 = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/expenses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Expense expense;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Expense createEntity(EntityManager em) {
        Expense expense = new Expense()
            .category(DEFAULT_CATEGORY)
            .name(DEFAULT_NAME)
            .searchString1(DEFAULT_SEARCH_STRING_1)
            .searchString2(DEFAULT_SEARCH_STRING_2);
        return expense;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Expense createUpdatedEntity(EntityManager em) {
        Expense expense = new Expense()
            .category(UPDATED_CATEGORY)
            .name(UPDATED_NAME)
            .searchString1(UPDATED_SEARCH_STRING_1)
            .searchString2(UPDATED_SEARCH_STRING_2);
        return expense;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Expense.class).block();
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
        expense = createEntity(em);
    }

    @Test
    void createExpense() throws Exception {
        int databaseSizeBeforeCreate = expenseRepository.findAll().collectList().block().size();
        // Create the Expense
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(expense))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Expense in the database
        List<Expense> expenseList = expenseRepository.findAll().collectList().block();
        assertThat(expenseList).hasSize(databaseSizeBeforeCreate + 1);
        Expense testExpense = expenseList.get(expenseList.size() - 1);
        assertThat(testExpense.getCategory()).isEqualTo(DEFAULT_CATEGORY);
        assertThat(testExpense.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testExpense.getSearchString1()).isEqualTo(DEFAULT_SEARCH_STRING_1);
        assertThat(testExpense.getSearchString2()).isEqualTo(DEFAULT_SEARCH_STRING_2);
    }

    @Test
    void createExpenseWithExistingId() throws Exception {
        // Create the Expense with an existing ID
        expense.setId(1L);

        int databaseSizeBeforeCreate = expenseRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(expense))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Expense in the database
        List<Expense> expenseList = expenseRepository.findAll().collectList().block();
        assertThat(expenseList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkCategoryIsRequired() throws Exception {
        int databaseSizeBeforeTest = expenseRepository.findAll().collectList().block().size();
        // set the field null
        expense.setCategory(null);

        // Create the Expense, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(expense))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Expense> expenseList = expenseRepository.findAll().collectList().block();
        assertThat(expenseList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = expenseRepository.findAll().collectList().block().size();
        // set the field null
        expense.setName(null);

        // Create the Expense, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(expense))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Expense> expenseList = expenseRepository.findAll().collectList().block();
        assertThat(expenseList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllExpenses() {
        // Initialize the database
        expenseRepository.save(expense).block();

        // Get all the expenseList
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
            .value(hasItem(expense.getId().intValue()))
            .jsonPath("$.[*].category")
            .value(hasItem(DEFAULT_CATEGORY))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].searchString1")
            .value(hasItem(DEFAULT_SEARCH_STRING_1))
            .jsonPath("$.[*].searchString2")
            .value(hasItem(DEFAULT_SEARCH_STRING_2));
    }

    @Test
    void getExpense() {
        // Initialize the database
        expenseRepository.save(expense).block();

        // Get the expense
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, expense.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(expense.getId().intValue()))
            .jsonPath("$.category")
            .value(is(DEFAULT_CATEGORY))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.searchString1")
            .value(is(DEFAULT_SEARCH_STRING_1))
            .jsonPath("$.searchString2")
            .value(is(DEFAULT_SEARCH_STRING_2));
    }

    @Test
    void getNonExistingExpense() {
        // Get the expense
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewExpense() throws Exception {
        // Initialize the database
        expenseRepository.save(expense).block();

        int databaseSizeBeforeUpdate = expenseRepository.findAll().collectList().block().size();

        // Update the expense
        Expense updatedExpense = expenseRepository.findById(expense.getId()).block();
        updatedExpense
            .category(UPDATED_CATEGORY)
            .name(UPDATED_NAME)
            .searchString1(UPDATED_SEARCH_STRING_1)
            .searchString2(UPDATED_SEARCH_STRING_2);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedExpense.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedExpense))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Expense in the database
        List<Expense> expenseList = expenseRepository.findAll().collectList().block();
        assertThat(expenseList).hasSize(databaseSizeBeforeUpdate);
        Expense testExpense = expenseList.get(expenseList.size() - 1);
        assertThat(testExpense.getCategory()).isEqualTo(UPDATED_CATEGORY);
        assertThat(testExpense.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testExpense.getSearchString1()).isEqualTo(UPDATED_SEARCH_STRING_1);
        assertThat(testExpense.getSearchString2()).isEqualTo(UPDATED_SEARCH_STRING_2);
    }

    @Test
    void putNonExistingExpense() throws Exception {
        int databaseSizeBeforeUpdate = expenseRepository.findAll().collectList().block().size();
        expense.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, expense.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(expense))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Expense in the database
        List<Expense> expenseList = expenseRepository.findAll().collectList().block();
        assertThat(expenseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchExpense() throws Exception {
        int databaseSizeBeforeUpdate = expenseRepository.findAll().collectList().block().size();
        expense.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(expense))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Expense in the database
        List<Expense> expenseList = expenseRepository.findAll().collectList().block();
        assertThat(expenseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamExpense() throws Exception {
        int databaseSizeBeforeUpdate = expenseRepository.findAll().collectList().block().size();
        expense.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(expense))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Expense in the database
        List<Expense> expenseList = expenseRepository.findAll().collectList().block();
        assertThat(expenseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateExpenseWithPatch() throws Exception {
        // Initialize the database
        expenseRepository.save(expense).block();

        int databaseSizeBeforeUpdate = expenseRepository.findAll().collectList().block().size();

        // Update the expense using partial update
        Expense partialUpdatedExpense = new Expense();
        partialUpdatedExpense.setId(expense.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedExpense.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedExpense))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Expense in the database
        List<Expense> expenseList = expenseRepository.findAll().collectList().block();
        assertThat(expenseList).hasSize(databaseSizeBeforeUpdate);
        Expense testExpense = expenseList.get(expenseList.size() - 1);
        assertThat(testExpense.getCategory()).isEqualTo(DEFAULT_CATEGORY);
        assertThat(testExpense.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testExpense.getSearchString1()).isEqualTo(DEFAULT_SEARCH_STRING_1);
        assertThat(testExpense.getSearchString2()).isEqualTo(DEFAULT_SEARCH_STRING_2);
    }

    @Test
    void fullUpdateExpenseWithPatch() throws Exception {
        // Initialize the database
        expenseRepository.save(expense).block();

        int databaseSizeBeforeUpdate = expenseRepository.findAll().collectList().block().size();

        // Update the expense using partial update
        Expense partialUpdatedExpense = new Expense();
        partialUpdatedExpense.setId(expense.getId());

        partialUpdatedExpense
            .category(UPDATED_CATEGORY)
            .name(UPDATED_NAME)
            .searchString1(UPDATED_SEARCH_STRING_1)
            .searchString2(UPDATED_SEARCH_STRING_2);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedExpense.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedExpense))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Expense in the database
        List<Expense> expenseList = expenseRepository.findAll().collectList().block();
        assertThat(expenseList).hasSize(databaseSizeBeforeUpdate);
        Expense testExpense = expenseList.get(expenseList.size() - 1);
        assertThat(testExpense.getCategory()).isEqualTo(UPDATED_CATEGORY);
        assertThat(testExpense.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testExpense.getSearchString1()).isEqualTo(UPDATED_SEARCH_STRING_1);
        assertThat(testExpense.getSearchString2()).isEqualTo(UPDATED_SEARCH_STRING_2);
    }

    @Test
    void patchNonExistingExpense() throws Exception {
        int databaseSizeBeforeUpdate = expenseRepository.findAll().collectList().block().size();
        expense.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, expense.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(expense))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Expense in the database
        List<Expense> expenseList = expenseRepository.findAll().collectList().block();
        assertThat(expenseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchExpense() throws Exception {
        int databaseSizeBeforeUpdate = expenseRepository.findAll().collectList().block().size();
        expense.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(expense))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Expense in the database
        List<Expense> expenseList = expenseRepository.findAll().collectList().block();
        assertThat(expenseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamExpense() throws Exception {
        int databaseSizeBeforeUpdate = expenseRepository.findAll().collectList().block().size();
        expense.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(expense))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Expense in the database
        List<Expense> expenseList = expenseRepository.findAll().collectList().block();
        assertThat(expenseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteExpense() {
        // Initialize the database
        expenseRepository.save(expense).block();

        int databaseSizeBeforeDelete = expenseRepository.findAll().collectList().block().size();

        // Delete the expense
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, expense.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Expense> expenseList = expenseRepository.findAll().collectList().block();
        assertThat(expenseList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
