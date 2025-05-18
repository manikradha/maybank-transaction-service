package com.maybank.transaction.mapper;

import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.transform.FieldSet;
import com.maybank.transaction.entity.Transaction;

public class SafeTransactionMapper extends TransactionFieldSetMapper {

    @Override
    public Transaction mapFieldSet(FieldSet fieldSet) {
        try {
            return super.mapFieldSet(fieldSet);
        } catch (Exception ex) {
            String raw = String.join("|", fieldSet.getValues());
            throw new FlatFileParseException("Error parsing transaction record", ex, raw, -1);
        }
    }
}
