package net.javaguides.springboot.web;

import net.javaguides.springboot.model.User;
import net.javaguides.springboot.model.Post;
import net.javaguides.springboot.service.PostService;
import net.javaguides.springboot.service.UserService;
import net.javaguides.springboot.web.dto.UserRegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String showPostsPage(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        List<Post> posts = postService.getAllPosts();
        posts.sort(Comparator.comparing(Post::getCreatedAt).reversed()); // Sort posts in reverse order by creation date

        for (Post post : posts) {
            if (currentUser != null && currentUser.getUsername().equals(post.getUser().getEmail())) {
                post.setCanDelete(true);
            } else {
                post.setCanDelete(false);
            }
        }
        model.addAttribute("posts", posts);
        model.addAttribute("newPost", new Post());

        if (currentUser != null) {
            User userAuth = userService.findUserByEmail(currentUser.getUsername());
            model.addAttribute("userAuth", userAuth);
        }

        List<UserRegistrationDto> users = userService.findAllUsers();
        model.addAttribute("users", users);

        return "index";
    }

    @PostMapping
    public String savePost(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("content") String postContent) {
        User user = userService.findUserByEmail(userDetails.getUsername());

        Post post = new Post();
        post.setUser(user);
        post.setContent(postContent);
        postService.savePost(post);

        return "redirect:/";
    }

    @GetMapping("/delete/{postId}")
    public String deletePost(@PathVariable("postId") Long postId, @AuthenticationPrincipal UserDetails currentUser) {
        Post post = postService.findPostById(postId);
        if (post != null && post.getUser().getEmail().equals(currentUser.getUsername())) {
            postService.deletePostById(postId);
        }
        return "redirect:/";
    }

    @GetMapping("/editUser")
    public String editUser(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        if (currentUser != null) {
            User user = userService.findUserByEmail(currentUser.getUsername());
            UserRegistrationDto userDto = new UserRegistrationDto();
            userDto.setEmail(user.getEmail());
            userDto.setUsername(user.getUsername());
            userDto.setPassword(user.getPassword());
            model.addAttribute("editProfile", userDto);
        }
        return "editProfile";
    }

    @PostMapping("/editUser")
    public String updateUser(@ModelAttribute("editProfile") UserRegistrationDto userDto) {
        userService.updateUser(userDto);
        return "redirect:/";
    }

    @GetMapping("/editPost/{postId}")
    public String showEditPostForm(@PathVariable("postId") Long postId, Model model, @AuthenticationPrincipal UserDetails currentUser) {
        Post post = postService.findPostById(postId);
        if (post != null && post.getUser().getEmail().equals(currentUser.getUsername())) {
            model.addAttribute("post", post);
            return "editPost";
        }
        return "redirect:/";
    }

    @PostMapping("/editPost/{postId}")
    public String updatePost(@PathVariable("postId") Long postId, @RequestParam("content") String content, @AuthenticationPrincipal UserDetails currentUser) {
        Post post = postService.findPostById(postId);
        if (post != null && post.getUser().getEmail().equals(currentUser.getUsername())) {
            post.setContent(content);
            postService.savePost(post);
        }
        return "redirect:/";
    }
}
