import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        final List<String> remainingFields = removeFirstField(fields);
        final var selectedSubfields = MapsMerger.merge(previousSelectedSubfields, currentSelectedSubfields);

        return remainingFields.isEmpty() ?
                selectedSubfields :
                selectedFields(remainingFields, selectedSubfields);
    }

    private List<String> extractSubfields(final List<String> fields) {
        return Arrays.asList(fields.getFirst().split(DOT));
    }

    private Map<String, Object> selectSubfields(final List<String> subFields,
                                                final Object field) {

        final Map<String, Object> selectedField = new HashMap<>();

        try {
            final Field firstDeclaredField = retrieveFirstDeclaredField(field, subFields);
            final Object currentField = firstDeclaredField.get(field);
            final List<String> remainingSubfields = removeFirstField(subFields);
            final Object fieldValue = remainingSubfields.isEmpty() ?
                    currentField :
                    selectSubfields(remainingSubfields, currentField);
            selectedField.put(firstDeclaredField.getName(), fieldValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return selectedField;
    }

    private Field retrieveFirstDeclaredField(final Object field, final List<String> subFields) {
        final Field declaredField;

        try {
            declaredField = field.getClass().getDeclaredField(subFields.getFirst());
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        declaredField.setAccessible(true);
        return declaredField;
    }

    private List<String> removeFirstField(final List<String> list) {
        return list.subList(1, list.size());
    }
}