package com.github.sentrionic.olympusblog.service;

import com.github.sentrionic.olympusblog.dto.comment.CommentDto;
import com.github.sentrionic.olympusblog.dto.comment.CreateCommentDto;
import com.github.sentrionic.olympusblog.dto.user.Profile;
import com.github.sentrionic.olympusblog.exception.ArticleNotFoundException;
import com.github.sentrionic.olympusblog.exception.CommentNotFoundException;
import com.github.sentrionic.olympusblog.exception.UnauthorizedException;
import com.github.sentrionic.olympusblog.mapper.CommentMapper;
import com.github.sentrionic.olympusblog.mapper.ProfileMapper;
import com.github.sentrionic.olympusblog.model.Comment;
import com.github.sentrionic.olympusblog.model.User;
import com.github.sentrionic.olympusblog.repository.ArticleRepository;
import com.github.sentrionic.olympusblog.repository.CommentRepository;
import com.github.sentrionic.olympusblog.utils.Faker;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private ProfileMapper profileMapper;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private AuthService authService;
    @Captor
    private ArgumentCaptor<Comment> commentArgumentCaptor;

    private CommentService service;

    @BeforeEach
    public void setup() {
        service = new CommentService(commentRepository, articleRepository, profileMapper, commentMapper, authService);
    }

    @Test
    void whenGivenInput_ItShouldSuccessfullyCreateTheComment_AndReturnTheCommentDto() {
        var user = Faker.generateUser();
        var article = Faker.generateArticle(null);
        var comment = Faker.generateComment(user, article);

        var profile = new Profile();
        var response = new CommentDto();

        var request = new CreateCommentDto(comment.getBody());
        Mockito.when(articleRepository.findBySlug(article.getSlug())).thenReturn(Optional.of(article));
        Mockito.when(authService.getCurrentUser()).thenReturn(user);
        Mockito.when(profileMapper.mapEntityToDto(user, null)).thenReturn(profile);
        Mockito.lenient().when(commentMapper.mapEntityToDto(comment, profile)).thenReturn(response);

        service.createComment(article.getSlug(), request);
        Mockito.verify(commentRepository, Mockito.times(1)).save(commentArgumentCaptor.capture());

        Assertions.assertThat(commentArgumentCaptor.getValue().getBody()).isEqualTo(comment.getBody());
        Assertions.assertThat(commentArgumentCaptor.getValue().getAuthor()).isEqualTo(user);
        Assertions.assertThat(commentArgumentCaptor.getValue().getArticle()).isEqualTo(article);
    }

    @Test()
    void whenArticleNotFound_ItShouldThrowAnArticleNotFoundException() {
        var user = Faker.generateUser();
        var comment = Faker.generateComment(user, null);
        final String slug = "slug";

        var request = new CreateCommentDto(comment.getBody());
        Mockito.when(articleRepository.findBySlug(slug)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.createComment(slug, request)).isInstanceOf(ArticleNotFoundException.class);
        Mockito.verify(commentRepository, Mockito.times(0)).save(commentArgumentCaptor.capture());
    }

    @Test
    void whenGetCommentsBySlugIsCalled_WithAValidArticle_ThenItReturnsAllComments() {
        var user = Faker.generateUser();
        var article = Faker.generateArticle(null);
        List<Comment> comments = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            comments.add(Faker.generateComment(user, article));
        }

        var profile = new Profile();
        var dto = new CommentDto();

        Mockito.when(articleRepository.findBySlug(article.getSlug())).thenReturn(Optional.of(article));
        Mockito.when(commentRepository.findByArticleOrderByCreatedAtDesc(article)).thenReturn(comments);
        Mockito.when(authService.getOptionalUser()).thenReturn(user);

        Mockito.when(profileMapper.mapEntityToDto(Mockito.any(User.class), Mockito.any(User.class))).thenReturn(profile);
        Mockito.lenient().when(commentMapper.mapEntityToDto(Mockito.any(Comment.class), Mockito.any(Profile.class))).thenReturn(dto);

        var response = service.getCommentsBySlug(article.getSlug());
        Assertions.assertThat(response).hasSameSizeAs(comments);
    }

    @Test()
    void whenGetCommentsBySlugIsCalled_andTheArticleDoesNotExist_ItShouldThrowAnArticleNotFoundException() {
        final String slug = "slug";
        Mockito.when(articleRepository.findBySlug(slug)).thenReturn(Optional.empty());
        Assertions.assertThatThrownBy(() -> service.getCommentsBySlug(slug)).isInstanceOf(ArticleNotFoundException.class);
        Mockito.verifyNoInteractions(commentRepository);
    }

    @Test
    void whenDeleteCommentIsCalled_AndTheInputIsValid_ItShouldSuccessfullyDeleteAndReturnTheComment() {
        var user = Faker.generateUser();
        var article = Faker.generateArticle(null);
        var comment = Faker.generateComment(user, article);

        var profile = new Profile();
        var response = new CommentDto();

        Mockito.when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        Mockito.when(authService.getCurrentUser()).thenReturn(user);
        Mockito.when(profileMapper.mapEntityToDto(user, null)).thenReturn(profile);
        Mockito.lenient().when(commentMapper.mapEntityToDto(comment, profile)).thenReturn(response);

        service.deleteComment(comment.getId());
        Mockito.verify(commentRepository, Mockito.times(1)).delete(commentArgumentCaptor.capture());

        Assertions.assertThat(commentArgumentCaptor.getValue().getBody()).isEqualTo(comment.getBody());
        Assertions.assertThat(commentArgumentCaptor.getValue().getAuthor()).isEqualTo(user);
        Assertions.assertThat(commentArgumentCaptor.getValue().getArticle()).isEqualTo(article);
    }

    @Test
    void whenDeleteCommentIsCalled_AndNoCommentIsFound_ItShouldThrowACommentNotFoundException() {
        var user = Faker.generateUser();

        Mockito.when(commentRepository.findById(2L)).thenReturn(Optional.empty());
        Mockito.when(authService.getCurrentUser()).thenReturn(user);

        Assertions.assertThatThrownBy(() -> service.deleteComment(2L)).isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    void whenDeleteCommentIsCalled_AndTheUserIsNotTheAuthor_ItShouldThrowAnUnauthorizedException() {
        var user = Faker.generateUser();
        var comment = Faker.generateComment(Faker.generateUser(), null);

        Mockito.when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        Mockito.when(authService.getCurrentUser()).thenReturn(user);

        Assertions.assertThatThrownBy(() -> service.deleteComment(comment.getId())).isInstanceOf(UnauthorizedException.class);
    }
}
