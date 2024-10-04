import java.lang.reflect.Field;
import java.util.*;

public class FieldsSelector {

    private static final String COMMA = ",";
    private static final String DOT = "\\.";
    private Object rootField;

    public FieldsSelector from(final Object entity) {
        this.rootField = entity;
        return this;
    }

    public Map<String, Object> select(final String...fields) {
        return selectedFields(Arrays.asList(fields), new HashMap<>());
    }

    public Map<String, Object> select(final List<String> fields) {
        return selectedFields(fields, new HashMap<>());
    }

    public Map<String, Object> select(final String fieldsSeparatedByComma) {
        return selectedFields(Arrays.asList(fieldsSeparatedByComma.split(COMMA)), new HashMap<>());
    }

    private Map<String, Object> selectedFields(final List<String> fields,
                                               final Map<String, Object> previousSelectedSubfields) {

        final var currentSelectedSubfields = selectSubfields(extractSubfields(fields), rootField);
        final List<String> remainingFields = removeFirst(fields);

        @SuppressWarnings("unchecked")
        final var selectedSubfields = MapsMerger.merge(
                previousSelectedSubfields,
                (Map<String, Object>) currentSelectedSubfields
        );

        return inferSubfields(selectedSubfields, remainingFields);
    }

    private Map<String, Object> inferSubfields(final Map<String, Object> selectedSubfields,
                                               final List<String> remainingFields) {
        return remainingFields.isEmpty() ?
                selectedSubfields :
                selectedFields(remainingFields, selectedSubfields);
    }

    private List<String> extractSubfields(final List<String> fields) {
        return Arrays.asList(fields.getFirst().split(DOT));
    }

    private Object selectSubfields(final List<String> subFields, final Object field) {
        try {
            if (isCollection(field)) {
                return selectSubfieldsInCollection(field, subFields);
            }

            final Field firstFieldMetadata = retrieveFirstFieldMetadata(field, subFields);
            final Object firstField = firstFieldMetadata.get(field);
            final List<String> remainingSubfields = removeFirst(subFields);

            return new HashMap<>() {{
                put(firstFieldMetadata.getName(), inferField(firstField, remainingSubfields));
            }};
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    private Object inferField(final Object firstField, final List<String> remainingSubfields) {
        return remainingSubfields.isEmpty() ?
                firstField :
                selectSubfields(remainingSubfields, firstField);
    }

    private Field retrieveFirstFieldMetadata(final Object field, final List<String> subFields) {
        if (isCollection(field)) {
            return retrieveFirstFieldMetadata(retrieveFirstItemFromCollection(field), subFields);
        }

        try {
            final Field firstFieldMetadata = field.getClass().getDeclaredField(subFields.getFirst());
            firstFieldMetadata.setAccessible(true);
            return firstFieldMetadata;
        } catch (final NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isCollection(final Object field) {
        return Collection.class.isAssignableFrom(field.getClass());
    }

    private List<Object> selectSubfieldsInCollection(final Object field, final List<String> subFields) {
        return ((Collection<?>) field).stream()
                .map(f -> selectSubfields(subFields, f))
                .toList();
    }

    private Object retrieveFirstItemFromCollection(final Object collection) {
        return ((Collection<?>) collection).iterator().next();
    }

    private List<String> removeFirst(final List<String> list) {
        return list.subList(1, list.size());
    }
}