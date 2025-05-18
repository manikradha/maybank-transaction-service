package com.maybank.transaction.config;

import com.maybank.transaction.entity.Transaction;
import com.maybank.transaction.mapper.SafeTransactionMapper;
import com.maybank.transaction.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.lang.Nullable;
import org.springframework.transaction.PlatformTransactionManager;

import static com.maybank.transaction.utils.TransactionFields.*;

@Slf4j
@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final TransactionRepository transactionRepository;
    private final PlatformTransactionManager transactionManager;

    public BatchConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager, TransactionRepository transactionRepository) {
        this.jobBuilderFactory = new JobBuilderFactory(jobRepository);
        this.stepBuilderFactory = new StepBuilderFactory(jobRepository);
        this.transactionManager = transactionManager;
        this.transactionRepository = transactionRepository;
    }

    @Bean
    public Job importTransactionsJob() {
        return jobBuilderFactory.get("importTransactionsJob")
                .incrementer(new RunIdIncrementer())
                .listener(jobExecutionListener())
                .start(transactionStep())
                .build();
    }

    @Bean
    public Step transactionStep() {
        return stepBuilderFactory.get("transactionStep")
                .<Transaction, Transaction>chunk(100)
                .reader(transactionItemReader(null))
                .processor(transactionProcessor())
                .writer(transactionWriter())
                .transactionManager(transactionManager)
                .faultTolerant()
                .skipLimit(100)
                .skip(FlatFileParseException.class)
                .skip(DataIntegrityViolationException.class)
                .retryLimit(3)
                .retry(Exception.class)
                .listener(itemReadListener())
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Transaction> transactionItemReader(@Value("#{jobParameters['inputFile']}") String filePath) {

        FlatFileItemReader<Transaction> reader = new FlatFileItemReader<>();
        reader.setName("transactionReader");
        reader.setResource(new FileSystemResource(filePath));
        reader.setLinesToSkip(1);

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer("|");
        tokenizer.setNames(ACCOUNT_NUMBER, TRX_AMOUNT, DESCRIPTION, TRX_DATE, TRX_TIME, CUSTOMER_ID);

        DefaultLineMapper<Transaction> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(new SafeTransactionMapper());

        reader.setLineMapper(lineMapper);
        return reader;
    }


    @Bean
    public ItemProcessor<Transaction, Transaction> transactionProcessor() {
        return (@Nullable Transaction trx) -> trx;
    }

    @Bean
    public ItemWriter<Transaction> transactionWriter() {
        return items -> {
            log.info("Persisting {} transactions", items.size());
            transactionRepository.saveAll(items);
        };
    }

    @Bean
    public JobExecutionListener jobExecutionListener() {
        return new JobExecutionListener() {
            @Override
            public void beforeJob(JobExecution je) {
                log.info("Job parameters: {}", je.getJobParameters());
            }

            @Override
            public void afterJob(JobExecution je) {
                if (je.getStatus() == BatchStatus.FAILED) {
                    log.error("Job failed with {} exception(s):", je.getAllFailureExceptions().size());
                    je.getAllFailureExceptions().forEach(ex -> log.error("   • ", ex));
                }
            }
        };
    }

    @Bean
    public ItemReadListener<Transaction> itemReadListener() {
        return new ItemReadListener<>() {
            @Override
            public void beforeRead() {
                log.debug("Start read transaction");
            }

            @Override
            public void afterRead(Transaction transaction) {
                log.debug("Read transaction {}", transaction);
            }

            @Override
            public void onReadError(Exception ex) {
                log.error("Read transaction error: {}", ex.getMessage());
            }
        };
    }

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor("batch-");
    }

    @Bean
    public JobLauncher jobLauncher(JobRepository repo) throws Exception {
        SimpleJobLauncher launcher = new SimpleJobLauncher();
        launcher.setJobRepository(repo);
        launcher.afterPropertiesSet();
        return launcher;
    }
}
