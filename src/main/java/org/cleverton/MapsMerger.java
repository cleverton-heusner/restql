package org.cleverton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class MapsMerger {

    @SuppressWarnings("unchecked")
    public static Map<String, Object> merge(final Map<String, Object> map1, final Map<String, Object> map2) {
        map2.keySet().forEach(key -> {
            final Object value1 = map1.get(key);
            final Object value2 = map2.get(key);

            if (value1 == null) {
                map1.put(key, value2);
            }
            else if (value2 == null) {
                map2.put(key, value1);
            }
            else if (value1 instanceof Map && value2 instanceof Map && map1.containsKey(key)) {
                merge((Map<String, Object>) value1, (Map<String, Object>) value2);
            }
            else {
                map1.merge(key, value2, (oldValue, newValue) -> mergeLists(
                        (List<Map<String, Object>>) oldValue,
                        (List<Map<String, Object>>) newValue)
                );
            }
        });

        return map1;
    }

    private static List<Map<String, Object>> mergeLists(final List<Map<String, Object>> list1,
                                                        final List<Map<String, Object>> list2) {
        return IntStream.range(0, list1.size())
                .mapToObj(i -> merge(new HashMap<>(list1.get(i)), new HashMap<>(list2.get(i))))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
}