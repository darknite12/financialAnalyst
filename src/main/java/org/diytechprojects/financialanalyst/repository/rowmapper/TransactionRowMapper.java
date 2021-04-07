package org.diytechprojects.financialanalyst.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.diytechprojects.financialanalyst.domain.Transaction;
import org.diytechprojects.financialanalyst.service.ColumnConverter;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Transaction}, with proper type conversions.
 */
@Service
public class TransactionRowMapper implements BiFunction<Row, String, Transaction> {

    private final ColumnConverter converter;

    public TransactionRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Transaction} stored in the database.
     */
    @Override
    public Transaction apply(Row row, String prefix) {
        Transaction entity = new Transaction();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setAccountType(converter.fromRow(row, prefix + "_account_type", String.class));
        entity.setTransactionDate(converter.fromRow(row, prefix + "_transaction_date", Instant.class));
        entity.setChequeNumber(converter.fromRow(row, prefix + "_cheque_number", String.class));
        entity.setDescription1(converter.fromRow(row, prefix + "_description_1", String.class));
        entity.setDescription2(converter.fromRow(row, prefix + "_description_2", String.class));
        entity.setAmountCAD(converter.fromRow(row, prefix + "_amount_cad", Long.class));
        entity.setAmountUSD(converter.fromRow(row, prefix + "_amount_usd", Long.class));
        entity.setIsTracked(converter.fromRow(row, prefix + "_is_tracked", Boolean.class));
        entity.setIncomeId(converter.fromRow(row, prefix + "_income_id", Long.class));
        entity.setExpenseId(converter.fromRow(row, prefix + "_expense_id", Long.class));
        return entity;
    }
}
