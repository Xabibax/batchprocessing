package com.example.batchprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;


public class PostItemProcessor implements ItemProcessor<org.openapitools.client.model.Post, Post> {

    private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor2.class);

    @Override
    public Post process(final  org.openapitools.client.model.Post post) {

        final Post transformedPost = new Post(post.getId(), post.getUserId(), post.getTitle(), post.getCompleted());

        log.info("Converting 2 (" + post + ") into (" + transformedPost + ")");

        return transformedPost;
    }

}