
import java.util.*;

public class frequencies2 implements ITop25{

    // Store words and their counts in HashMap
    public HashMap<String, Integer> top25(List<String> words) {
        HashMap<String, Integer> countMap = new HashMap<>();

        for (String word : words) {
            if (word.length() >= 2) {
                if (!(countMap.containsKey(word)))
                    countMap.put(word, 1);
                else
                    countMap.put(word, countMap.get(word) + 1);
            }
        }

        LinkedHashMap<String, Integer> sortHighestCount = new LinkedHashMap<>();
        countMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> sortHighestCount.put(x.getKey(), x.getValue()));
        return sortHighestCount;
    }
}