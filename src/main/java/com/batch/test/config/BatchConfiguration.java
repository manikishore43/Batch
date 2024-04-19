package com.batch.test.config;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.batch.test.Listener.MyStepExecutionListner;
import com.batch.test.domain.Product;
import com.batch.test.domain.ProductFieldSetMapper;
import com.batch.test.domain.ProductItemPreparedSetter;
import com.batch.test.processor.FilterDataItemProcessor;
import com.batch.test.processor.MyProductItemProcessor;
import com.batch.test.reader.ProductNameReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.ItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class BatchConfiguration {

    @Autowired
    private DataSource dataSource;

    @Bean
    public ItemReader<String> itemReader(){
        List<String> prodcutList=new ArrayList<>();
        prodcutList.add("Product 1");
        prodcutList.add("Product 2");
        prodcutList.add("Product 3");
        prodcutList.add("Product 4");
        prodcutList.add("Product 5");
        prodcutList.add("Product 6");
        prodcutList.add("Product 7");
        return new ProductNameReader(prodcutList);
    }

    @Bean
    public ItemReader<Product> flatFileitemReader(){
        FlatFileItemReader<Product> itemReader= new FlatFileItemReader<>();
        // Line to skip Header
        itemReader.setLinesToSkip(1);
        // let item reader know where the file is
        itemReader.setResource(new ClassPathResource("/data/Product_Details.csv"));
        // How to map the Line to
        DefaultLineMapper<Product> lineMapper= new DefaultLineMapper<>();
        //how to read based on delimiter default is , separated
        DelimitedLineTokenizer lineTokenizer= new DelimitedLineTokenizer();
        //set Names
        lineTokenizer.setNames("product_id","product_name","product_category","product_price");

        lineMapper.setLineTokenizer(lineTokenizer);
        //how to map from CSV to product
        lineMapper.setFieldSetMapper(new ProductFieldSetMapper());
        itemReader.setLineMapper(lineMapper);
        return itemReader;
    }


    @Bean
    public JdbcBatchItemWriter<Product> jdbcBatchItemWriter(){
        JdbcBatchItemWriter<Product> itemWriter= new JdbcBatchItemWriter<>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("insert into PRODUCT_DETAILS_OUTPUT values (:productId,:productName,:productCategory,:productPrice)");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider());
        return itemWriter;
    }

    @Bean
    public MyStepExecutionListner myStepExecutionListner(){
        return new MyStepExecutionListner();
    }


    @Bean
    public ItemProcessor<Product,Product> itemProcessor(){
        return new MyProductItemProcessor();
    }

    public ItemProcessor<Product,Product> filterItemProcessor(){
        return new FilterDataItemProcessor();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("step1",jobRepository).<Product,Product>chunk(3,platformTransactionManager)
                .reader(flatFileitemReader())
                .processor(filterItemProcessor())
                .writer(jdbcBatchItemWriter()).build();
    }
    @Bean
    public Job FirstJob(JobRepository jobRepository,PlatformTransactionManager platformTransactionManager){
        return new JobBuilder("job1",jobRepository).
                 start(step1(jobRepository,platformTransactionManager))
                .build();
    }
}
