package hu.juranyi.zsolt.heritrixremote.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Little tool for counting objects in a Map.
 *
 * @author Zsolt Jurányi
 */
public class ObjectCounter<T> {

    protected Map<T, Integer> map = new HashMap<T, Integer>();

    public Map<T, Integer> getMap() {
        return map;
    }

    public void add(T key) {
        if (map.containsKey(key)) {
            map.put(key, map.get(key) + 1);
        } else {
            map.put(key, 1);
        }
    }

    public int get(T key) {
        if (map.containsKey(key)) {
            return map.get(key);
        } else {
            return 0;
        }
    }
}
