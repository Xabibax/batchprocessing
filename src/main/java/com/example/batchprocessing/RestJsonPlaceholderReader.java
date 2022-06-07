package com.example.batchprocessing;

import lombok.Getter;
import lombok.Setter;
import org.openapitools.client.api.PostsApi;
import org.openapitools.client.model.Post;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Getter
@Setter
public class RestJsonPlaceholderReader implements ItemReader<Post> {

    @Autowired
    PostsApi postsApi;
    private List<Post> lstPost;
    int nextPostIndex;

    RestJsonPlaceholderReader() {
        nextPostIndex = 0;
    }

    @Override
    public Post read() {
        if (lstPostIsNotInitializes())
            lstPost = fetchPostDataFromAPI();
        Post nextPost = null;
        if (nextPostIndex < lstPost.size()) {
            nextPost = lstPost.get(nextPostIndex);
            nextPostIndex++;
        } else {
            nextPostIndex = 0;
            lstPost = null;
        }
        return nextPost;
    }

    private boolean lstPostIsNotInitializes() {
        return this.lstPost == null;
    }

    private List<Post> fetchPostDataFromAPI() {
        return postsApi.getPosts().collectList().block();
    }
}