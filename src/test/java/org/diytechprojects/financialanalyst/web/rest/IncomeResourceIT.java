package org.diytechprojects.financialanalyst.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.diytechprojects.financialanalyst.IntegrationTest;
import org.diytechprojects.financialanalyst.domain.Income;
import org.diytechprojects.financialanalyst.repository.IncomeRepository;
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
 * Integration tests for the {@link IncomeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class IncomeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SEARCH_STRING_1 = "AAAAAAAAAA";
    private static final String UPDATED_SEARCH_STRING_1 = "BBBBBBBBBB";

    private static final String DEFAULT_SEARCH_STRING_2 = "AAAAAAAAAA";
    private static final String UPDATED_SEARCH_STRING_2 = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/incomes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Income income;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Income createEntity(EntityManager em) {
        Income income = new Income().name(DEFAULT_NAME).searchString1(DEFAULT_SEARCH_STRING_1).searchString2(DEFAULT_SEARCH_STRING_2);
        return income;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Income createUpdatedEntity(EntityManager em) {
        Income income = new Income().name(UPDATED_NAME).searchString1(UPDATED_SEARCH_STRING_1).searchString2(UPDATED_SEARCH_STRING_2);
        return income;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Income.class).block();
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
        income = createEntity(em);
    }

    @Test
    void createIncome() throws Exception {
        int databaseSizeBeforeCreate = incomeRepository.findAll().collectList().block().size();
        // Create the Income
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(income))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Income in the database
        List<Income> incomeList = incomeRepository.findAll().collectList().block();
        assertThat(incomeList).hasSize(databaseSizeBeforeCreate + 1);
        Income testIncome = incomeList.get(incomeList.size() - 1);
        assertThat(testIncome.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testIncome.getSearchString1()).isEqualTo(DEFAULT_SEARCH_STRING_1);
        assertThat(testIncome.getSearchString2()).isEqualTo(DEFAULT_SEARCH_STRING_2);
    }

    @Test
    void createIncomeWithExistingId() throws Exception {
        // Create the Income with an existing ID
        income.setId(1L);

        int databaseSizeBeforeCreate = incomeRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(income))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Income in the database
        List<Income> incomeList = incomeRepository.findAll().collectList().block();
        assertThat(incomeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = incomeRepository.findAll().collectList().block().size();
        // set the field null
        income.setName(null);

        // Create the Income, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(income))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Income> incomeList = incomeRepository.findAll().collectList().block();
        assertThat(incomeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllIncomes() {
        // Initialize the database
        incomeRepository.save(income).block();

        // Get all the incomeList
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
            .value(hasItem(income.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].searchString1")
            .value(hasItem(DEFAULT_SEARCH_STRING_1))
            .jsonPath("$.[*].searchString2")
            .value(hasItem(DEFAULT_SEARCH_STRING_2));
    }

    @Test
    void getIncome() {
        // Initialize the database
        incomeRepository.save(income).block();

        // Get the income
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, income.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(income.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.searchString1")
            .value(is(DEFAULT_SEARCH_STRING_1))
            .jsonPath("$.searchString2")
            .value(is(DEFAULT_SEARCH_STRING_2));
    }

    @Test
    void getNonExistingIncome() {
        // Get the income
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewIncome() throws Exception {
        // Initialize the database
        incomeRepository.save(income).block();

        int databaseSizeBeforeUpdate = incomeRepository.findAll().collectList().block().size();

        // Update the income
        Income updatedIncome = incomeRepository.findById(income.getId()).block();
        updatedIncome.name(UPDATED_NAME).searchString1(UPDATED_SEARCH_STRING_1).searchString2(UPDATED_SEARCH_STRING_2);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedIncome.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedIncome))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Income in the database
        List<Income> incomeList = incomeRepository.findAll().collectList().block();
        assertThat(incomeList).hasSize(databaseSizeBeforeUpdate);
        Income testIncome = incomeList.get(incomeList.size() - 1);
        assertThat(testIncome.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testIncome.getSearchString1()).isEqualTo(UPDATED_SEARCH_STRING_1);
        assertThat(testIncome.getSearchString2()).isEqualTo(UPDATED_SEARCH_STRING_2);
    }

    @Test
    void putNonExistingIncome() throws Exception {
        int databaseSizeBeforeUpdate = incomeRepository.findAll().collectList().block().size();
        income.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, income.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(income))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Income in the database
        List<Income> incomeList = incomeRepository.findAll().collectList().block();
        assertThat(incomeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchIncome() throws Exception {
        int databaseSizeBeforeUpdate = incomeRepository.findAll().collectList().block().size();
        income.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(income))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Income in the database
        List<Income> incomeList = incomeRepository.findAll().collectList().block();
        assertThat(incomeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamIncome() throws Exception {
        int databaseSizeBeforeUpdate = incomeRepository.findAll().collectList().block().size();
        income.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(income))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Income in the database
        List<Income> incomeList = incomeRepository.findAll().collectList().block();
        assertThat(incomeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateIncomeWithPatch() throws Exception {
        // Initialize the database
        incomeRepository.save(income).block();

        int databaseSizeBeforeUpdate = incomeRepository.findAll().collectList().block().size();

        // Update the income using partial update
        Income partialUpdatedIncome = new Income();
        partialUpdatedIncome.setId(income.getId());

        partialUpdatedIncome.name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedIncome.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedIncome))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Income in the database
        List<Income> incomeList = incomeRepository.findAll().collectList().block();
        assertThat(incomeList).hasSize(databaseSizeBeforeUpdate);
        Income testIncome = incomeList.get(incomeList.size() - 1);
        assertThat(testIncome.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testIncome.getSearchString1()).isEqualTo(DEFAULT_SEARCH_STRING_1);
        assertThat(testIncome.getSearchString2()).isEqualTo(DEFAULT_SEARCH_STRING_2);
    }

    @Test
    void fullUpdateIncomeWithPatch() throws Exception {
        // Initialize the database
        incomeRepository.save(income).block();

        int databaseSizeBeforeUpdate = incomeRepository.findAll().collectList().block().size();

        // Update the income using partial update
        Income partialUpdatedIncome = new Income();
        partialUpdatedIncome.setId(income.getId());

        partialUpdatedIncome.name(UPDATED_NAME).searchString1(UPDATED_SEARCH_STRING_1).searchString2(UPDATED_SEARCH_STRING_2);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedIncome.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedIncome))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Income in the database
        List<Income> incomeList = incomeRepository.findAll().collectList().block();
        assertThat(incomeList).hasSize(databaseSizeBeforeUpdate);
        Income testIncome = incomeList.get(incomeList.size() - 1);
        assertThat(testIncome.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testIncome.getSearchString1()).isEqualTo(UPDATED_SEARCH_STRING_1);
        assertThat(testIncome.getSearchString2()).isEqualTo(UPDATED_SEARCH_STRING_2);
    }

    @Test
    void patchNonExistingIncome() throws Exception {
        int databaseSizeBeforeUpdate = incomeRepository.findAll().collectList().block().size();
        income.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, income.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(income))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Income in the database
        List<Income> incomeList = incomeRepository.findAll().collectList().block();
        assertThat(incomeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchIncome() throws Exception {
        int databaseSizeBeforeUpdate = incomeRepository.findAll().collectList().block().size();
        income.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(income))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Income in the database
        List<Income> incomeList = incomeRepository.findAll().collectList().block();
        assertThat(incomeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamIncome() throws Exception {
        int databaseSizeBeforeUpdate = incomeRepository.findAll().collectList().block().size();
        income.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(income))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Income in the database
        List<Income> incomeList = incomeRepository.findAll().collectList().block();
        assertThat(incomeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteIncome() {
        // Initialize the database
        incomeRepository.save(income).block();

        int databaseSizeBeforeDelete = incomeRepository.findAll().collectList().block().size();

        // Delete the income
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, income.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Income> incomeList = incomeRepository.findAll().collectList().block();
        assertThat(incomeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
