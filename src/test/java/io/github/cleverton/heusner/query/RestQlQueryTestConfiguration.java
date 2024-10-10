package io.github.cleverton.heusner.query;

public class RestQlQueryTestConfiguration {

    protected static final String COMMA = ",";
    protected static final String DOT = ".";

    protected static final String NON_EXISTING_FIELD_1 = "nonExistingField1";
    protected static final String NON_EXISTING_FIELD_2 = "nonExistingField2";
    protected static final String NON_EXISTING_FIELD_3 = "nonExistingField3";

    protected static final String AGE = "age";
    protected static final String AUTHOR = "author";
    protected static final String BOOKS = "books";
    protected static final String COMMENTS = "comments";
    protected static final String DATE_PUBLISHED = "datePublished";
    protected static final String EMAIL = "email";
    protected static final String ID = "id";
    protected static final String NAME = "name";
    protected static final String NICK_NAME = "nick_name";
    protected static final String PET = "pet";
    protected static final String PHONE_NUMBERS = "phoneNumbers";
    protected static final String REPLIES = "replies";
    protected static final String TEXT = "text";

    protected static final String AUTHOR_DOT_ID = AUTHOR + DOT + ID;
    protected static final String AUTHOR_DOT_EMAIL = AUTHOR + DOT + EMAIL;
    protected static final String AUTHOR_DOT_NICK_NAME = AUTHOR + DOT + NICK_NAME;
    protected static final String AUTHOR_DOT_PET_DOT_AGE = AUTHOR + DOT + PET + DOT + AGE;
    protected static final String AUTHOR_DOT_PET_DOT_NAME = AUTHOR + DOT + PET + DOT + NAME;
    protected static final String AUTHOR_DOT_PET_DOT_NICK_NAME = AUTHOR + DOT + PET + DOT + NICK_NAME;
    protected static final String AUTHOR_DOT_BOOKS = AUTHOR + DOT + BOOKS;
    protected static final String AUTHOR_DOT_PHONE_NUMBERS = AUTHOR + DOT + PHONE_NUMBERS;
    protected static final String AUTHOR_DOT_NON_EXISTING_FIELD_1 = AUTHOR + DOT + NON_EXISTING_FIELD_1;
    protected static final String AUTHOR_DOT_NON_EXISTING_FIELD_2 = AUTHOR + DOT + NON_EXISTING_FIELD_2;

    protected static final String COMMENTS_DOT_AUTHOR_DOT_ID = COMMENTS + DOT + AUTHOR + DOT + ID;
    protected static final String COMMENTS_DOT_AUTHOR_DOT_NAME = COMMENTS + DOT + AUTHOR + DOT + NAME;
    protected static final String COMMENTS_DOT_ID = COMMENTS + DOT + ID;
    protected static final String COMMENTS_DOT_TEXT = COMMENTS + DOT + TEXT;
    protected static final String COMMENTS_DOT_REPLIES = COMMENTS + DOT + REPLIES;
    protected static final String COMMENTS_DOT_REPLIES_DOT_ID = COMMENTS + DOT + REPLIES + DOT + ID;
    protected static final String COMMENTS_DOT_REPLIES_DOT_TEXT = COMMENTS + DOT + REPLIES + DOT+ TEXT;

    protected String getFieldNotFoundMessage() {
        return String.format("Field '%s' not found.", NON_EXISTING_FIELD_1);
    }
}