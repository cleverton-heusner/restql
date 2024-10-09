package io.github.cleverton.heusner.selector;

import io.github.cleverton.heusner.fixture.Comment;
import io.github.cleverton.heusner.fixture.Post;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RestQlQueryTest extends FieldsSelectorTestConfiguration  {

    private RestQlQuery restQlQuery;
    private Post post;

    @BeforeEach
    void setUp() {
        post = Instancio.create(Post.class);
        post.getComments().forEach(comment -> comment.getReplies().add(Instancio.create(Comment.class)));

        restQlQuery = new RestQlQuery();
    }

    @Test
    void when_oneLeafChildFieldSelected_then_fieldReturned() {

        // Arrange
        final var expectedSelectedPostFields = Map.of(ID, post.getId());

        // Act
        final var actualSelectedFields = restQlQuery.from(post).select(ID);

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedPostFields);
    }

    @Test
    void when_multipleLeafChildFieldsSelected_then_fieldsReturned() {

        // Arrange
        final var expectedSelectedPostFields = Map.of(ID, post.getId(),
                TEXT, post.getText(),
                DATE_PUBLISHED, post.getDatePublished()
        );

        // Act
        final var actualSelectedFields = restQlQuery.from(post).select(ID, TEXT, DATE_PUBLISHED);

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedPostFields);
    }

    @Test
    void when_oneChildWithDescendantsFieldSelected_then_fieldReturned() {

        // Arrange
        final var author = post.getAuthor();
        final var expectedSelectedFields = Map.of(AUTHOR, author);

        // Act
        final var actualSelectedFields = restQlQuery.from(post).select(AUTHOR);

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedFields);
    }

    @Test
    void when_oneLeafDescendantFieldSelected_then_fieldReturned() {

        // Arrange
        final var authorId = Map.of(ID, post.getAuthor().getId());
        final var expectedSelectedFields = Map.of(AUTHOR, authorId);

        // Act
        final var actualSelectedFields = restQlQuery.from(post).select("author.id");

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedFields);
    }

    @Test
    void when_multipleLeafDescendantFieldsSelected_then_fieldsReturned() {

        // Arrange
        final var author = Map.of(ID, post.getAuthor().getId(), EMAIL, post.getAuthor().getEmail());
        final var expectedSelectedFields = Map.of(AUTHOR, author);

        // Act
        final var actualSelectedFields = restQlQuery.from(post).select("author.id", "author.email");

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedFields);
    }

    @Test
    void when_oneDescendantFieldSelected_then_fieldReturned() {

        // Arrange
        final var authorAge = Map.of(AGE, post.getAuthor().getPet().getAge());
        final var expectedSelectedFields = Map.of(AUTHOR, Map.of(PET, authorAge));

        // Act
        final var actualSelectedFields = restQlQuery.from(post).select("author.pet.age");

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedFields);
    }

    @Test
    void when_multipleDescendantFieldsSelected_then_fieldsReturned() {

        // Arrange
        final var pet = post.getAuthor().getPet();
        final var expectedSelectedPetFields = Map.of(NAME, pet.getName(), AGE, pet.getAge());
        final var expectedSelectedFields = Map.of(AUTHOR, Map.of(PET, expectedSelectedPetFields));

        // Act
        final var actualSelectedFields = restQlQuery.from(post).select(
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
                ID, post.getId(),
                AUTHOR, Map.of(ID, post.getAuthor().getId())
        );

        // Act
        final var actualSelectedFields = restQlQuery.from(post).select(ID, "author.id");

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedFields);
    }

    @Test
    void when_fieldsWithMultipleHierarchicalLevelsSelected_then_fieldsReturned() {

        // Arrange
        final var author = post.getAuthor();
        final var pet = post.getAuthor().getPet();
        final var expectedPetSelectedFields = Map.of(NAME, pet.getName(), AGE, pet.getAge());
        final var expectedAuthorSelectedFields = Map.of(
                ID, author.getId(),
                EMAIL, author.getEmail(),
                PET, expectedPetSelectedFields
        );

        final var expectedSelectedFields = Map.of(
                ID, post.getId(),
                TEXT, post.getText(),
                AUTHOR, expectedAuthorSelectedFields
        );

        // Act
        final var actualSelectedFields = restQlQuery.from(post).select(
                ID,
                TEXT,
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
        final var expectedPetSelectedFields = Map.of(NAME, pet.getName(), AGE, pet.getAge());
        final var expectedAuthorSelectedFields = Map.of(
                ID, author.getId(),
                EMAIL, author.getEmail(),
                PET, expectedPetSelectedFields
        );

        final var expectedSelectedFields = Map.of(
                ID, post.getId(),
                TEXT, post.getText(),
                AUTHOR, expectedAuthorSelectedFields
        );

        // Act
        final var actualSelectedFields = restQlQuery.from(post).select(
                List.of(
                        ID,
                        TEXT,
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
        final var expectedPetSelectedFields = Map.of(NAME, pet.getName(), AGE, pet.getAge());
        final var expectedAuthorSelectedFields = Map.of(
                ID, author.getId(),
                EMAIL, author.getEmail(),
                PET, expectedPetSelectedFields
        );

        final var expectedSelectedFields = Map.of(
                ID, post.getId(),
                TEXT, post.getText(),
                AUTHOR, expectedAuthorSelectedFields
        );

        // Act
        final var actualSelectedFields = restQlQuery.from(post).select(
                "id,text,author.id,author.email,author.pet.age,author.pet.name"
        );

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedFields);
    }

    @Test
    void when_fieldsSeparatedByCommaAndWithBlankSpacesSelected_then_fieldsReturned() {

        // Arrange
        final var author = post.getAuthor();
        final var pet = post.getAuthor().getPet();
        final var expectedPetSelectedFields = Map.of(NAME, pet.getName(), AGE, pet.getAge());
        final var expectedAuthorSelectedFields = Map.of(
                ID, author.getId(),
                EMAIL, author.getEmail(),
                PET, expectedPetSelectedFields
        );

        final var expectedSelectedFields = Map.of(
                ID, post.getId(),
                TEXT, post.getText(),
                AUTHOR, expectedAuthorSelectedFields
        );

        // Act
        final var actualSelectedFields = restQlQuery.from(post).select(
                " id , text , author.id , author.email , author.pet.age , author.pet.name "
        );

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedFields);
    }

    @Test
    void when_nullFieldSelected_then_fieldsReturned() {

        // Arrange
        final var postWithNullField = Instancio.of(Post.class)
                .set(Select.field(TEXT), null)
                .create();
        final Map<String, Object> expectedSelectedPostFields = new HashMap<>();
        expectedSelectedPostFields.put(ID, postWithNullField.getId());
        expectedSelectedPostFields.put(TEXT, postWithNullField.getText());
        expectedSelectedPostFields.put(DATE_PUBLISHED, postWithNullField.getDatePublished());

        // Act
        final var actualSelectedFields = restQlQuery.from(postWithNullField).select(
                ID,
                TEXT,
                DATE_PUBLISHED
        );

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedPostFields);
    }

    @Test
    void when_emptyFieldSelected_then_fieldsReturned() {

        // Arrange
        final var postWithEmptyField = Instancio.of(Post.class)
                .set(Select.field(TEXT), "")
                .create();
        final Map<String, Object> expectedSelectedPostFields = new HashMap<>();
        expectedSelectedPostFields.put(ID, postWithEmptyField.getId());
        expectedSelectedPostFields.put(TEXT, postWithEmptyField.getText());
        expectedSelectedPostFields.put(DATE_PUBLISHED, postWithEmptyField.getDatePublished());

        // Act
        final var actualSelectedFields = restQlQuery.from(postWithEmptyField).select(
                ID,
                TEXT,
                DATE_PUBLISHED
        );

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedPostFields);
    }

    @Test
    void when_oneChildFieldOfListTypeSelected_then_fieldReturned() {

        // Arrange
        final var expectedSelectedPostFields = Map.of(COMMENTS, post.getComments());

        // Act
        final var actualSelectedFields = restQlQuery.from(post).select(COMMENTS);

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedPostFields);
    }

    @Test
    void when_oneChildFieldOfSetTypeSelected_then_fieldReturned() {

        // Arrange
        final var authorBooks = Map.of("books", post.getAuthor().getBooks());
        final var expectedSelectedPostFields = Map.of("author", authorBooks);

        // Act
        final var actualSelectedFields = restQlQuery.from(post).select("author.books");

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedPostFields);
    }

    @Test
    void when_oneChildFieldAsListWithJavaTypeSelected_then_fieldReturned() {

        // Arrange
        final var expectedSelectedAuthorPhoneNumbersFields = Map.of(PHONE_NUMBERS, post.getAuthor().getPhoneNumbers());
        final var expectedSelectedPostFields = Map.of(AUTHOR, expectedSelectedAuthorPhoneNumbersFields);

        // Act
        final var actualSelectedFields = restQlQuery.from(post).select("author.phoneNumbers");

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedPostFields);
    }

    @Test
    void when_oneLeafChildFieldInsideListSelected_then_fieldReturned() {

        // Arrange
        final List<Map<String, Long>> commentsIds = post.getComments().stream()
                .map(comment -> Map.of(ID, comment.getId()))
                .toList();
        final var expectedSelectedPostFields = Map.of(COMMENTS, commentsIds);

        // Act
        final var actualSelectedFields = restQlQuery.from(post).select("comments.id");

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedPostFields);
    }

    @Test
    void when_multipleLeafChildFieldsInsideListSelected_then_fieldsReturned() {

        // Arrange
        var comments = (List<?>) post.getComments().stream()
                .map(comment -> Map.of(ID, comment.getId(), TEXT, comment.getText()))
                .toList();
        final var expectedSelectedPostFields = Map.of(COMMENTS, comments);

        // Act
        final var actualSelectedFields = restQlQuery.from(post).select("comments.id", "comments.text");

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedPostFields);
    }

    @Test
    void when_oneDescendantFieldInsideListSelected_then_fieldReturned() {

        // Arrange
        var comments = (List<?>) post.getComments().stream()
                .map(comment -> Map.of(
                        AUTHOR,
                        Map.of(
                                ID,
                                comment.getAuthor().getId()
                        )
                )).toList();
        final var expectedSelectedPostFields = Map.of(COMMENTS, comments);

        // Act
        final var actualSelectedFields = restQlQuery.from(post).select("comments.author.id");

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedPostFields);
    }

    @Test
    void when_multipleDescendantFieldsInsideListSelected_then_fieldsReturned() {

        // Arrange
        var comments = (List<?>) post.getComments().stream()
                .map(comment -> Map.of(
                        AUTHOR,
                        Map.of(
                                ID,
                                comment.getAuthor().getId(),
                                NAME,
                                comment.getAuthor().getName()
                        )
                )).toList();
        final var expectedSelectedPostFields = Map.of(COMMENTS, comments);

        // Act
        final var actualSelectedFields = restQlQuery.from(post).select(
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
                        ID, comment.getId(),
                        TEXT, comment.getText(),
                        AUTHOR,
                        Map.of(
                                ID,
                                comment.getAuthor().getId(),
                                NAME,
                                comment.getAuthor().getName()
                        )
                )).toList();
        final var expectedSelectedPostFields = Map.of(COMMENTS, comments);

        // Act
        final var actualSelectedFields = restQlQuery.from(post).select(
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
        final var actualSelectedFields = restQlQuery.from(post).select("comments.replies");

        // Assert
        final List<?> actualComments = (List<?>) actualSelectedFields.get(COMMENTS);
        assertThat(actualComments).hasSize(post.getComments().size());

        for (int i = 0; i < actualComments.size(); i++) {
            final List<?> actualReplies = (List<?>) ((Map<String, Object>) actualComments.get(i)).get(REPLIES);
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
                COMMENTS, post.getComments().stream()
                        .map(comment -> Map.of(
                        REPLIES, comment.getReplies().stream()
                                        .map(reply -> Map.of(
                                                ID, reply.getId(),
                                                TEXT, reply.getText()
                        )).collect(Collectors.toList())
                )).collect(Collectors.toList())
        );

        // Act
        final var actualSelectedFields = restQlQuery.from(post).select(
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
                .set(Select.field(COMMENTS), null)
                .create();
        final Map<String, Object> expectedSelectedPostFields = new HashMap<>();
        expectedSelectedPostFields.put(ID, postWithEmptyField.getId());
        expectedSelectedPostFields.put(COMMENTS, null);

        // Act
        final var actualSelectedFields = restQlQuery.from(postWithEmptyField).select(ID, COMMENTS);

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedPostFields);
    }

    @Test
    void when_listWithDuplicatedField_then_fieldsReturned() {

        // Arrange
        final var postWithCommentDuplicated = Instancio.create(Post.class);
        postWithCommentDuplicated.getComments().add(postWithCommentDuplicated.getComments().getFirst());
        final var expectedSelectedPostFields = Map.of(COMMENTS, postWithCommentDuplicated.getComments());

        // Act
        final var actualSelectedFields = restQlQuery.from(postWithCommentDuplicated).select(
                COMMENTS
        );

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedPostFields);
    }

    @Test
    void when_fieldNameSelectedPresentInJacksonAnnotation_then_fieldReturned() {

        // Arrange
        final var authorNickName = Map.of(NICK_NAME, post.getAuthor().getNickName());
        final var expectedSelectedFields = Map.of(AUTHOR, authorNickName);

        // Act
        final var actualSelectedFields = restQlQuery.from(post).select("author.nick_name");

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedFields);
    }

    @Test
    void when_fieldNameSelectedPresentInGsonAnnotation_then_fieldReturned() {

        // Arrange
        final var petNickName = Map.of(NICK_NAME, post.getAuthor().getPet().getNickName());
        final var expectedSelectedFields = Map.of(AUTHOR, Map.of(PET, petNickName));

        // Act
        final var actualSelectedFields = restQlQuery.from(post).select("author.pet.nick_name");

        // Assert
        assertThat(actualSelectedFields).containsExactlyInAnyOrderEntriesOf(expectedSelectedFields);
    }

    @Test
    void when_nonExistingFieldsSelected_then_illegalArgumentExceptionReturned() {

        // Act
        final var illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> {
            restQlQuery.from(post).select(
                    "nonExistingField1",
                    "nonExistingField2",
                    "nonExistingField2"
            );
        });

        // Assert
        assertThat(illegalArgumentException.getMessage()).isEqualTo("Field 'nonExistingField1' not found.");
    }

    @Test
    void when_nonExistingSubfieldsSelected_then_illegalArgumentExceptionReturned() {

        // Act
        final var illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> {
            restQlQuery.from(post).select(
                    "author.nonExistingField1",
                    "author.nonExistingField2"
            );
        });

        // Assert
        assertThat(illegalArgumentException.getMessage()).isEqualTo("Field 'nonExistingField1' not found.");
    }
}