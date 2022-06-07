package com.example.batchprocessing;

import org.openapitools.client.ApiClient;
import org.openapitools.client.api.PostsApi;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public PostsApi postsApi() {
        PostsApi postsApi = new PostsApi();
        postsApi.setApiClient(new ApiClient().setBasePath("https://jsonplaceholder.typicode.com/"));
        return postsApi;
    }

    @Bean
    public FlatFileItemReader<Person> reader() {
        return new FlatFileItemReaderBuilder<Person>()
                .name("personItemReader")
                .resource(new ClassPathResource("sample-data.csv"))
                .delimited()
                .names(new String[]{"firstName", "lastName"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {
                    {
                        setTargetType(Person.class);
                    }
                })
                .build();
    }

    @Bean
    public RestJsonPlaceholderReader jsonPlaceholderReader() {
        return new RestJsonPlaceholderReader();
    }

    @Bean
    public PersonItemProcessor processor() {
        return new PersonItemProcessor();
    }

    @Bean
    public PersonItemProcessor2 processor2() {
        return new PersonItemProcessor2();
    }

    @Bean
    public PostItemProcessor processor3() {
        return new PostItemProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Person> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Person>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Post> postWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Post>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO post (title, completed) VALUES (:title, :completed)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener, @Autowired @Qualifier("step1") Step step1,
                             @Autowired @Qualifier("step2") Step step2,
                             @Autowired @Qualifier("step3") Step step3
    ) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .next(step2)
                .next(step3)

                .end()
                .build();
    }

    @Bean("step1")
    public Step step1(JdbcBatchItemWriter<Person> writer) {
        return stepBuilderFactory.get("step1")
                .<Person, Person>chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer)
                .build();
    }

    @Bean("step2")
    public Step step2(JdbcBatchItemWriter<Person> writer) {
        return stepBuilderFactory.get("step2")
                .<Person, Person>chunk(10)
                .reader(reader())
                .processor(processor2())
                .writer(writer)
                .build();
    }

    @Bean("step3")
    public Step step3(JdbcBatchItemWriter<Post> writer) {
        return stepBuilderFactory.get("step3")
                .<org.openapitools.client.model.Post, Post>chunk(10)
                .reader(jsonPlaceholderReader())
                .processor(processor3())
                .writer(writer)
                .build();
    }
}