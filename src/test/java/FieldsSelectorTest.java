import org.example.Post;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class FieldsSelectorTest {

    private FieldsSelector fieldsSelector;
    private Post post;

    @BeforeEach
    void setUp() {
        post = Instancio.create(Post.class);
        fieldsSelector = new FieldsSelector();
    }

    @Test
    void when_oneFieldSelected_then_oneFieldReturned() {

        // Arrange
        final var expectedSelectedPostFields = Map.of("id", post.getId());

        // Act
        final var actualSelectedFields = fieldsSelector.from(post).select("id");

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedPostFields);
    }

    @Test
    void when_moreThanOneFieldSelected_then_moreThanOneFieldReturned() {

        // Arrange
        final var expectedSelectedPostFields = Map.of("id", post.getId(),
                "text", post.getText(),
                "datePublished", post.getDatePublished()
        );

        // Act
        final var actualSelectedFields = fieldsSelector.from(post).select("id", "text", "datePublished");

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedPostFields);
    }

    @Test
    void when_oneChildFieldSelected_then_oneChildFieldReturned() {

        // Arrange
        final var expectedSelectedFields = Map.of(
                "author", Map.of("id", post.getAuthor().getId())
        );

        // Act
        final var actualSelectedFields = fieldsSelector.from(post).select("author.id");

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedFields);
    }

    @Test
    void when_moreThanOneChildFieldSelected_then_moreThanOneChildFieldReturned() {

        // Arrange
        final var expectedSelectedFields = Map.of(
                "author", Map.of(
                        "id", post.getAuthor().getId(),
                        "email", post.getAuthor().getEmail()
                )
        );

        // Act
        final var actualSelectedFields = fieldsSelector.from(post).select("author.id", "author.email");

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedFields);
    }

    @Test
    void when_oneDescendantFieldSelected_then_oneDescendantFieldReturned() {

        // Arrange
        final var expectedPetSelectedFields = Map.of("age", post.getAuthor().getPet().getAge());
        final var expectedSelectedFields = Map.of(
                "author", Map.of("pet", expectedPetSelectedFields)
        );

        // Act
        final var actualSelectedFields = fieldsSelector.from(post).select("author.pet.age");

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedFields);
    }

    @Test
    void when_moreThanOneDescendantFieldSelected_then_moreThanOneDescendantFieldReturned() {

        // Arrange
        final var pet = post.getAuthor().getPet();
        final var expectedSelectedPetFields = Map.of("name", pet.getName(), "age", pet.getAge());
        final var expectedSelectedFields = Map.of("author", Map.of("pet", expectedSelectedPetFields));

        // Act
        final var actualSelectedFields = fieldsSelector.from(post).select(
                "author.pet.age",
                "author.pet.name"
        );

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedFields);
    }

    @Test
    void when_oneChildFieldAndOneDescendantFieldSelected_then_oneChildFieldAndOneDescendantFieldReturned() {

        // Arrange
        final var expectedSelectedFields = Map.of(
                "id", post.getId(),
                "author", Map.of("id", post.getAuthor().getId())
        );

        // Act
        final var actualSelectedFields = fieldsSelector.from(post).select("id", "author.id");

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedFields);
    }

    @Test
    void when_moreThanOneChildFieldAndMoreThanOneDescendantFieldSelected_then_moreThanOneChildFieldAndMoreThanOneDescendantFieldReturned() {

        // Arrange
        final var author = post.getAuthor();
        final var pet = post.getAuthor().getPet();
        final var expectedPetSelectedFields = Map.of("name", pet.getName(), "age", pet.getAge());
        final var expectedAuthorSelectedFields = Map.of(
                "id", author.getId(),
                "email", author.getEmail(),
                "pet", expectedPetSelectedFields
        );

        final var expectedSelectedFields = Map.of(
                "id", post.getId(),
                "text", post.getText(),
                "author", expectedAuthorSelectedFields
        );

        // Act
        final var actualSelectedFields = fieldsSelector.from(post).select(
                "id",
                "text",
                "author.id",
                "author.email",
                "author.pet.age",
                "author.pet.name"
        );

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedFields);
    }
}