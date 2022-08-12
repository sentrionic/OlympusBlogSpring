package com.github.sentrionic.olympusblog.controller;

import com.github.sentrionic.olympusblog.dto.comment.CreateCommentDto;
import com.github.sentrionic.olympusblog.service.CommentService;
import com.github.sentrionic.olympusblog.utils.Faker;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CommentsController.class)
@AutoConfigureMockMvc(addFilters = false)
class CommentsControllerTest {
    @MockBean
    private CommentService service;

    @Autowired
    private MockMvc mvc;

    @Test
    void getCommentsBySlug_ItShouldReturnA200ResponseWithAListOfComments() throws Exception {
        var article = Faker.generateArticle();
        var comment1 = Faker.generateCommentDto(article);
        var comment2 = Faker.generateCommentDto(article);

        Mockito.when(service.getCommentsBySlug(article.getSlug())).thenReturn(Arrays.asList(comment1, comment2));

        mvc.perform(get("/api/articles/" + article.getSlug() + "/comments"))
                .andExpect(status().is(200))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.size()", Matchers.is(2)))
                .andExpect(jsonPath("$[0].id", Matchers.is(comment1.getId())))
                .andExpect(jsonPath("$[0].body", Matchers.is(comment1.getBody())))
                .andExpect(jsonPath("$[1].body", Matchers.is(comment2.getBody())))
                .andExpect(jsonPath("$[1].id", Matchers.is(comment2.getId())));
    }

    @Test
    void createComment_whenCalledWithValidInput_ItShouldReturnA201ResponseWithACommentDto() throws Exception {
        var article = Faker.generateArticle();
        var user = Faker.generateUser();
        var comment = Faker.generateComment(user, article);
        var response = Faker.generateCommentDto(comment);

        var json = new JSONObject();
        json.put("body", comment.getBody());

        Mockito.when(service.createComment(article.getSlug(), new CreateCommentDto(comment.getBody()))).thenReturn(response);

        mvc.perform(post("/api/articles/" + article.getSlug() + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.toString())
                        .characterEncoding("utf-8"))
                .andExpect(status().is(201))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", Matchers.is(comment.getId())))
                .andExpect(jsonPath("$.body", Matchers.is(comment.getBody())));
    }

    @Test
    void deleteComment_whenCalledWithAValidId_ItShouldReturnA200ResponseWithACommentDto() throws Exception {
        var article = Faker.generateArticle();
        var user = Faker.generateUser();
        var comment = Faker.generateComment(user, article);
        var response = Faker.generateCommentDto(comment);

        Mockito.when(service.deleteComment(1L)).thenReturn(response);

        mvc.perform(delete("/api/articles/" + article.getSlug() + "/comments/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", Matchers.is(comment.getId())))
                .andExpect(jsonPath("$.body", Matchers.is(comment.getBody())));
    }
}
