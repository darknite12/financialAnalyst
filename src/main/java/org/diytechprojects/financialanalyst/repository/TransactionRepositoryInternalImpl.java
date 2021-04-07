package org.diytechprojects.financialanalyst.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.diytechprojects.financialanalyst.domain.Transaction;
import org.diytechprojects.financialanalyst.repository.rowmapper.ExpenseRowMapper;
import org.diytechprojects.financialanalyst.repository.rowmapper.IncomeRowMapper;
import org.diytechprojects.financialanalyst.repository.rowmapper.TransactionRowMapper;
import org.diytechprojects.financialanalyst.service.EntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the Transaction entity.
 */
@SuppressWarnings("unused")
class TransactionRepositoryInternalImpl implements TransactionRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final IncomeRowMapper incomeMapper;
    private final ExpenseRowMapper expenseMapper;
    private final TransactionRowMapper transactionMapper;

    private static final Table entityTable = Table.aliased("transaction", EntityManager.ENTITY_ALIAS);
    private static final Table incomeTable = Table.aliased("income", "income");
    private static final Table expenseTable = Table.aliased("expense", "expense");

    public TransactionRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        IncomeRowMapper incomeMapper,
        ExpenseRowMapper expenseMapper,
        TransactionRowMapper transactionMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.incomeMapper = incomeMapper;
        this.expenseMapper = expenseMapper;
        this.transactionMapper = transactionMapper;
    }

    @Override
    public Flux<Transaction> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Transaction> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Transaction> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = TransactionSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(IncomeSqlHelper.getColumns(incomeTable, "income"));
        columns.addAll(ExpenseSqlHelper.getColumns(expenseTable, "expense"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(incomeTable)
            .on(Column.create("income_id", entityTable))
            .equals(Column.create("id", incomeTable))
            .leftOuterJoin(expenseTable)
            .on(Column.create("expense_id", entityTable))
            .equals(Column.create("id", expenseTable));

        String select = entityManager.createSelect(selectFrom, Transaction.class, pageable, criteria);
        String alias = entityTable.getReferenceName().getReference();
        String selectWhere = Optional
            .ofNullable(criteria)
            .map(
                crit ->
                    new StringBuilder(select)
                        .append(" ")
                        .append("WHERE")
                        .append(" ")
                        .append(alias)
                        .append(".")
                        .append(crit.toString())
                        .toString()
            )
            .orElse(select); // TODO remove once https://github.com/spring-projects/spring-data-jdbc/issues/907 will be fixed
        return db.sql(selectWhere).map(this::process);
    }

    @Override
    public Flux<Transaction> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Transaction> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private Transaction process(Row row, RowMetadata metadata) {
        Transaction entity = transactionMapper.apply(row, "e");
        entity.setIncome(incomeMapper.apply(row, "income"));
        entity.setExpense(expenseMapper.apply(row, "expense"));
        return entity;
    }

    @Override
    public <S extends Transaction> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends Transaction> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update Transaction with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(Transaction entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class TransactionSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("account_type", table, columnPrefix + "_account_type"));
        columns.add(Column.aliased("transaction_date", table, columnPrefix + "_transaction_date"));
        columns.add(Column.aliased("cheque_number", table, columnPrefix + "_cheque_number"));
        columns.add(Column.aliased("description_1", table, columnPrefix + "_description_1"));
        columns.add(Column.aliased("description_2", table, columnPrefix + "_description_2"));
        columns.add(Column.aliased("amount_cad", table, columnPrefix + "_amount_cad"));
        columns.add(Column.aliased("amount_usd", table, columnPrefix + "_amount_usd"));
        columns.add(Column.aliased("is_tracked", table, columnPrefix + "_is_tracked"));

        columns.add(Column.aliased("income_id", table, columnPrefix + "_income_id"));
        columns.add(Column.aliased("expense_id", table, columnPrefix + "_expense_id"));
        return columns;
    }
}
