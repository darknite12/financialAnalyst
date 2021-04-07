package org.diytechprojects.financialanalyst.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.diytechprojects.financialanalyst.domain.Income;
import org.diytechprojects.financialanalyst.service.ColumnConverter;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Income}, with proper type conversions.
 */
@Service
public class IncomeRowMapper implements BiFunction<Row, String, Income> {

    private final ColumnConverter converter;

    public IncomeRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Income} stored in the database.
     */
    @Override
    public Income apply(Row row, String prefix) {
        Income entity = new Income();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setSearchString1(converter.fromRow(row, prefix + "_search_string_1", String.class));
        entity.setSearchString2(converter.fromRow(row, prefix + "_search_string_2", String.class));
        return entity;
    }
}
