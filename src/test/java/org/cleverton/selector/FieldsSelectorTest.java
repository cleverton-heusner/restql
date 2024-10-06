package org.cleverton.selector;

import org.cleverton.fixture.Comment;
import org.cleverton.fixture.Post;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class FieldsSelectorTest {

    private FieldsSelector fieldsSelector;
    private Post post;

    @BeforeEach
    void setUp() {
        post = Instancio.create(Post.class);
        post.getComments().forEach(comment -> comment.getReplies().add(Instancio.create(Comment.class)));

        fieldsSelector = new FieldsSelector();
    }

    @Test
    void when_oneLeafChildFieldSelected_then_fieldReturned() {

        // Arrange
        final var expectedSelectedPostFields = Map.of("id", post.getId());

        // Act
        final var actualSelectedFields = fieldsSelector.from(post).select("id");

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedPostFields);
    }

    @Test
    void when_multipleLeafChildFieldsSelected_then_fieldsReturned() {

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
    void when_oneChildWithDescendantsFieldSelected_then_fieldReturned() {

        // Arrange
        final var author = post.getAuthor();
        final var expectedSelectedFields = Map.of("author", author);

        // Act
        final var actualSelectedFields = fieldsSelector.from(post).select("author");

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedFields);
    }

    @Test
    void when_oneLeafDescendantFieldSelected_then_fieldReturned() {

        // Arrange
        final var authorId = Map.of("id", post.getAuthor().getId());
        final var expectedSelectedFields = Map.of("author", authorId);

        // Act
        final var actualSelectedFields = fieldsSelector.from(post).select("author.id");

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedFields);
    }

    @Test
    void when_multipleLeafDescendantFieldsSelected_then_fieldsReturned() {

        // Arrange
        final var author = Map.of("id", post.getAuthor().getId(), "email", post.getAuthor().getEmail());
        final var expectedSelectedFields = Map.of("author", author);

        // Act
        final var actualSelectedFields = fieldsSelector.from(post).select("author.id", "author.email");

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedFields);
    }

    @Test
    void when_oneDescendantFieldSelected_then_fieldReturned() {

        // Arrange
        final var authorAge = Map.of("age", post.getAuthor().getPet().getAge());
        final var expectedSelectedFields = Map.of("author", Map.of("pet", authorAge));

        // Act
        final var actualSelectedFields = fieldsSelector.from(post).select("author.pet.age");

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedFields);
    }

    @Test
    void when_multipleDescendantFieldsSelected_then_fieldsReturned() {

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
    void when_oneLeafChildFieldAndOneLeafDescendantFieldSelected_then_fieldReturned() {

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
    void when_fieldsWithMultipleHierarchicalLevelsSelected_then_fieldsReturned() {

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

    @Test
    void when_fieldsSelectedFromList_then_fieldsReturned() {

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
                List.of(
                        "id",
                        "text",
                        "author.id",
                        "author.email",
                        "author.pet.age",
                        "author.pet.name"
                )
        );

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedFields);
    }

    @Test
    void when_fieldsSeparatedByCommaSelected_then_fieldsReturned() {

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
                "id,text,author.id,author.email,author.pet.age,author.pet.name"
        );

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedFields);
    }

    @Test
    void when_nullFieldSelected_then_fieldsReturned() {

        // Arrange
        final var postWithNullField = Instancio.of(Post.class)
                .set(Select.field("text"), null)
                .create();
        final Map<String, Object> expectedSelectedPostFields = new HashMap<>();
        expectedSelectedPostFields.put("id", postWithNullField.getId());
        expectedSelectedPostFields.put("text", postWithNullField.getText());
        expectedSelectedPostFields.put("datePublished", postWithNullField.getDatePublished());

        // Act
        final var actualSelectedFields = fieldsSelector.from(postWithNullField).select(
                "id",
                "text",
                "datePublished"
        );

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedPostFields);
    }

    @Test
    void when_emptyFieldSelected_then_fieldsReturned() {

        // Arrange
        final var postWithEmptyField = Instancio.of(Post.class)
                .set(Select.field("text"), "")
                .create();
        final Map<String, Object> expectedSelectedPostFields = new HashMap<>();
        expectedSelectedPostFields.put("id", postWithEmptyField.getId());
        expectedSelectedPostFields.put("text", postWithEmptyField.getText());
        expectedSelectedPostFields.put("datePublished", postWithEmptyField.getDatePublished());

        // Act
        final var actualSelectedFields = fieldsSelector.from(postWithEmptyField).select(
                "id",
                "text",
                "datePublished"
        );

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedPostFields);
    }

    @Test
    void when_oneChildFieldOfListTypeSelected_then_fieldReturned() {

        // Arrange
        final var expectedSelectedPostFields = Map.of("comments", post.getComments());

        // Act
        final var actualSelectedFields = fieldsSelector.from(post).select("comments");

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedPostFields);
    }

    @Test
    void when_oneChildFieldAsListWithJavaTypeSelected_then_fieldReturned() {

        // Arrange
        final var expectedSelectedAuthorPhoneNumbersFields = Map.of(
                "phoneNumbers",
                post.getAuthor().getPhoneNumbers()
        );
        final var expectedSelectedPostFields = Map.of("author", expectedSelectedAuthorPhoneNumbersFields);

        // Act
        final var actualSelectedFields = fieldsSelector.from(post).select("author.phoneNumbers");

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedPostFields);
    }

    @Test
    void when_oneLeafChildFieldInsideListSelected_then_fieldReturned() {

        // Arrange
        final List<Map<String, Long>> commentsIds = post.getComments().stream()
                .map(comment -> Map.of("id", comment.getId()))
                .toList();
        final var expectedSelectedPostFields = Map.of("comments", commentsIds);

        // Act
        final var actualSelectedFields = fieldsSelector.from(post).select("comments.id");

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedPostFields);
    }

    @Test
    void when_multipleLeafChildFieldsInsideListSelected_then_fieldsReturned() {

        // Arrange
        var comments = (List<?>) post.getComments().stream()
                .map(comment -> Map.of("id", comment.getId(), "text", comment.getText()))
                .toList();
        final var expectedSelectedPostFields = Map.of("comments", comments);

        // Act
        final var actualSelectedFields = fieldsSelector.from(post).select("comments.id", "comments.text");

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedPostFields);
    }

    @Test
    void when_oneDescendantFieldInsideListSelected_then_fieldReturned() {

        // Arrange
        var comments = (List<?>) post.getComments().stream()
                .map(comment -> Map.of(
                        "author",
                        Map.of(
                                "id",
                                comment.getAuthor().getId()
                        )
                )).toList();
        final var expectedSelectedPostFields = Map.of("comments", comments);

        // Act
        final var actualSelectedFields = fieldsSelector.from(post).select("comments.author.id");

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedPostFields);
    }

    @Test
    void when_multipleDescendantFieldsInsideListSelected_then_fieldsReturned() {

        // Arrange
        var comments = (List<?>) post.getComments().stream()
                .map(comment -> Map.of(
                        "author",
                        Map.of(
                                "id",
                                comment.getAuthor().getId(),
                                "name",
                                comment.getAuthor().getName()
                        )
                )).toList();
        final var expectedSelectedPostFields = Map.of("comments", comments);

        // Act
        final var actualSelectedFields = fieldsSelector.from(post).select(
                "comments.author.id",
                "comments.author.name"
        );

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedPostFields);
    }

    @Test
    void when_multipleLeafAndDescendantFieldsInsideListSelected_then_fieldsReturned() {

        // Arrange
        var comments = (List<?>) post.getComments().stream()
                .map(comment -> Map.of(
                        "id", comment.getId(),
                        "text", comment.getText(),
                        "author",
                        Map.of(
                                "id",
                                comment.getAuthor().getId(),
                                "name",
                                comment.getAuthor().getName()
                        )
                )).toList();
        final var expectedSelectedPostFields = Map.of("comments", comments);

        // Act
        final var actualSelectedFields = fieldsSelector.from(post).select(
                "comments.id",
                "comments.text",
                "comments.author.id",
                "comments.author.name"
        );

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedPostFields);
    }

    @Test
    @SuppressWarnings("unchecked")
    void when_nestedLists_then_fieldsReturned() {

        // Act
        final var actualSelectedFields = fieldsSelector.from(post).select("comments.replies");

        // Assert
        final List<?> actualComments = (List<?>) actualSelectedFields.get("comments");
        assertThat(actualComments).hasSize(post.getComments().size());

        for (int i = 0; i < actualComments.size(); i++) {
            final List<?> actualReplies = (List<?>) ((Map<String, Object>) actualComments.get(i)).get("replies");
            assertThat(actualReplies).hasSize(post.getComments().get(i).getReplies().size());

            for (int j = 0; j < actualReplies.size(); j++) {
                final Comment actualReply = (Comment) actualReplies.get(j);
                final Comment expectedReply = post.getComments().get(i).getReplies().get(j);
                assertThat(actualReply).isEqualTo(expectedReply);
            }
        }
    }

    @Test
    void when_leafFieldsSelectedInNestedLists_then_fieldsReturned() {

        var expectedSelectedPostFields = Map.of(
                "comments", post.getComments().stream()
                        .map(comment -> Map.of(
                        "replies", comment.getReplies().stream()
                                        .map(reply -> Map.of(
                                                "id", reply.getId(),
                                                "text", reply.getText()
                        )).collect(Collectors.toList())
                )).collect(Collectors.toList())
        );

        // Act
        final var actualSelectedFields = fieldsSelector.from(post).select(
                "comments.replies.id",
                "comments.replies.text"
        );

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedPostFields);
    }

    @Test
    void when_childFieldOfListTypeAsNullSelected_then_fieldsReturned() {

        // Arrange
        final var postWithEmptyField = Instancio.of(Post.class)
                .set(Select.field("comments"), null)
                .create();
        final Map<String, Object> expectedSelectedPostFields = new HashMap<>();
        expectedSelectedPostFields.put("id", postWithEmptyField.getId());
        expectedSelectedPostFields.put("comments", null);

        // Act
        final var actualSelectedFields = fieldsSelector.from(postWithEmptyField).select("id", "comments");

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedPostFields);
    }

    @Test
    void when_fieldNameSelectedPresentInJacksonAnnotation_then_fieldReturned() {

        // Arrange
        final var authorNickName = Map.of("nick_name", post.getAuthor().getNickName());
        final var expectedSelectedFields = Map.of("author", authorNickName);

        // Act
        final var actualSelectedFields = fieldsSelector.from(post).select("author.nick_name");

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedFields);
    }

    @Test
    void when_fieldNameSelectedPresentInGsonAnnotation_then_fieldReturned() {

        // Arrange
        final var petNickName = Map.of("nick_name", post.getAuthor().getPet().getNickName());
        final var expectedSelectedFields = Map.of("author", Map.of("pet", petNickName));

        // Act
        final var actualSelectedFields = fieldsSelector.from(post).select("author.pet.nick_name");

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedFields);
    }
}