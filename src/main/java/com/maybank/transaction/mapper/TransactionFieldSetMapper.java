package com.maybank.transaction.mapper;

import com.maybank.transaction.entity.Transaction;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

import java.net.BindException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static com.maybank.transaction.utils.TransactionFields.*;

public class TransactionFieldSetMapper implements FieldSetMapper<Transaction> {

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public Transaction mapFieldSet(FieldSet fieldSet) {
        Transaction transaction = new Transaction();
        transaction.setAccountNumber(fieldSet.readString(ACCOUNT_NUMBER));
        transaction.setTrxAmount(fieldSet.readBigDecimal(TRX_AMOUNT));
        transaction.setDescription(fieldSet.readString(DESCRIPTION));

        String dateStr = fieldSet.readString(TRX_DATE);
        String timeStr = fieldSet.readString(TRX_TIME);
        transaction.setTrxDate(LocalDate.parse(dateStr, dateFormatter));
        transaction.setTrxTime(LocalTime.parse(timeStr, timeFormatter));

        transaction.setCustomerId(fieldSet.readString(CUSTOMER_ID));
        return transaction;
    }
}