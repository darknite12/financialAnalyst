package org.diytechprojects.financialanalyst.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.diytechprojects.financialanalyst.domain.Expense;
import org.diytechprojects.financialanalyst.service.ColumnConverter;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Expense}, with proper type conversions.
 */
@Service
public class ExpenseRowMapper implements BiFunction<Row, String, Expense> {

    private final ColumnConverter converter;

    public ExpenseRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Expense} stored in the database.
     */
    @Override
    public Expense apply(Row row, String prefix) {
        Expense entity = new Expense();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setCategory(converter.fromRow(row, prefix + "_category", String.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setSearchString1(converter.fromRow(row, prefix + "_search_string_1", String.class));
        entity.setSearchString2(converter.fromRow(row, prefix + "_search_string_2", String.class));
        return entity;
    }
}
