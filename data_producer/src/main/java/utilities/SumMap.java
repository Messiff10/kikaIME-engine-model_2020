package utilities;

import java.util.*;

/**
 * Created by gaoxin on 17-1-20.
 */
public class SumMap<T> {
    private class MutableDouble {
        private double value = 0;

        public MutableDouble() {
            value = 0;
        }

        public MutableDouble(double times) {
            value = times;
        }

        public void add(double valueIn) {
            value += valueIn;
        }

        public double get() {
            return value;
        }
    }

    private Map<T, MutableDouble> countMap = new HashMap<>();

    private double sum = 0;

    public void add(T key, double value) {
        MutableDouble count = countMap.get(key);
        if (count == null) {
            countMap.put(key, new MutableDouble(value));
        } else {
            count.add(value);
        }
        sum += value;
    }

    public double get(T key) {
        MutableDouble res = countMap.get(key);
        if (res == null) {
            return 0;
        } else {
            return res.get();
        }
    }

    public Set<T> keySet() {
        return countMap.keySet();
    }

    public double sum() {
        return sum;
    }

    public int size() {
        return countMap.size();
    }

    public Map<T, Double> sort() {
        ArrayList<Map.Entry<T, MutableDouble>> list = new ArrayList<>(countMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<T, MutableDouble>>() {
            @Override
            public int compare(Map.Entry<T, MutableDouble> arg0, Map.Entry<T, MutableDouble> arg1) {
                if (arg1.getValue().get() > arg0.getValue().get()) {
                    return 1;
                } else if (arg1.getValue().get() < arg0.getValue().get()){
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        Map<T, Double> newMap = new LinkedHashMap<>();
        for (int i = 0; i < list.size(); i++) {
            newMap.put(list.get(i).getKey(), list.get(i).getValue().get());
        }
        return newMap;
    }
}
