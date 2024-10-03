import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FieldsSelector {

    private Object entity;

    public FieldsSelector from(final Object entity) {
        this.entity = entity;
        return this;
    }

    public Map<String, Object> select(final String...fields) {
        return selectedFields(fields, new HashMap<>());
    }

    private Map<String, Object> selectedFields(final String[] fields, final Map<String, Object> previousSelectedSubfields) {
        final String[] subFields = fields[0].split("\\.");
        final var currentSelectedSubfields = selectSubfields(subFields, entity);
        final String[] remainingFields = removeFirstElementFromArray(fields);
        final var selectedSubfields = MapsMerger.merge(previousSelectedSubfields, currentSelectedSubfields);

        if (remainingFields.length == 0) {
            return selectedSubfields;
        }

        return selectedFields(remainingFields, selectedSubfields);
    }

    private Map<String, Object> selectSubfields(final String[] subFields,
                                                final Object entity) {

        final Map<String, Object> selectedField = new HashMap<>();

        try {
            final Field declaredField = entity.getClass().getDeclaredField(subFields[0]);
            declaredField.setAccessible(true);
            final Object childEntity = declaredField.get(entity);
            final String[] remainingSubfields = removeFirstElementFromArray(subFields);
            final Object value = remainingSubfields.length > 0 ?
                    selectSubfields(remainingSubfields, childEntity) :
                    childEntity;
            selectedField.put(declaredField.getName(), value);
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return selectedField;
    }

    private String[] removeFirstElementFromArray(final String[] arr) {
        return Arrays.copyOfRange(arr, 1, arr.length);
    }
}