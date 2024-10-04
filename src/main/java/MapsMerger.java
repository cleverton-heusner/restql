import java.util.Map;

public class MapsMerger {

    @SuppressWarnings("unchecked")
    public static Map<String, Object> merge(final Map<String, Object> map1, final Map<String, Object> map2) {
        map2.keySet().forEach(key -> {
            if (map1.get(key) instanceof Map &&
                    map2.get(key) instanceof Map &&
                    map1.containsKey(key)) {
                merge((Map<String, Object>) map1.get(key), (Map<String, Object>) map2.get(key));
            }
            else {
                map1.put(key, map2.get(key));
            }
        });

        return map1;
    }
}
