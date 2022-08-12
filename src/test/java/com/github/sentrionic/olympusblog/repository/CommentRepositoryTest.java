package com.github.sentrionic.olympusblog.repository;

import com.github.sentrionic.olympusblog.utils.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CommentRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private CommentRepository repository;

    @Test
    void whenCommentRepositorySaveIsCalled_ItShouldReturnTheSavedComment() {
        var user = Faker.generateUser();
        var article = Faker.generateArticle(null);
        var comment = Faker.generateComment(user, article);
        var actualComment = repository.save(comment);
        assertThat(actualComment).usingRecursiveComparison()
                .ignoringFields("id").isEqualTo(comment);
    }
}
