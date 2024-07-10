package net.javaguides.springboot.service;

import java.util.List;
import net.javaguides.springboot.model.Post;
import net.javaguides.springboot.model.User;

public interface PostService {
    List<Post> getAllPosts();
    void savePost(Post post);
    void deletePostById(Long id);
    Post findPostById(Long id);
}
