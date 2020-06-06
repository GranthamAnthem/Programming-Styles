import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Nine {

    public static void main(String[] args) throws IOException {
        // error if build and run from compiler
        if (args.length < 1) {
            System.exit(1);
        }
        // if build and run from terminal
        // java Main.java pride-and-prejudice.txt
        else {
            TFTheOne tfTheOne = new TFTheOne(args[0]);
            tfTheOne.bind(new ReadFile())
                    .bind(new FilterWords())
                    .bind(new StopWords())
                    .bind(new RemoveStopWords())
                    .bind(new Sort())
                    .bind(new top25())
                    .printMe();
        }
    }
}

class TFTheOne {
    private Object value;

    TFTheOne(Object v) {
        this.value = v;
    }

    public TFTheOne bind (iFunction func) throws IOException {
        value = func.call(value);
        return this;
    }

    public void printMe() {
        System.out.println(value);
    }
}

interface iFunction {
    Object call (Object arg) throws IOException;
}

class ReadFile implements iFunction {

    public Object call(Object arg) throws IOException {
        List<String> fileData = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(arg.toString()));
        String line = "";
        while ((line = br.readLine()) != null) {
            fileData.add(line);
        }
        br.close();
        return fileData;
    }
}

class FilterWords implements iFunction  {
    public Object call(Object arg) {
        List<String> fileData = new ArrayList<>(Collections.singleton(arg.toString()));
        List<String> words = new ArrayList<>();
        for(int i = 0; i < fileData.size(); i++) {
            String[] w = fileData.get(i).toLowerCase().split("[^a-zA-Z]");
            for(String temp: w) {
                words.add(temp);
            }
        }
        return words;
    }
}

class StopWords implements iFunction {
    public Object call(Object arg) {
        List<String> words = new ArrayList<>();
        words.addAll((Collection<? extends String>) arg);
        HashMap<String, Integer> countMap = new HashMap<>();
        for (String word : words) {
            if (word.length() >= 2) {
                if (!(countMap.containsKey(word)))
                    countMap.put(word, 1);
                else
                    countMap.put(word, countMap.get(word) +1);
            }
        }
        return countMap;
    }
}

class RemoveStopWords implements iFunction {
    public Object call (Object arg) throws IOException {
        Map<String, Integer> countMap = (Map<String, Integer>) arg;
        BufferedReader br = new BufferedReader(new FileReader("stop-words.txt"));
        String line = "";
        while((line = br.readLine()) != null) {
            String[] stopWords = line.split(",");
            for(String words : stopWords) {
                if (countMap.containsKey(words))
                    countMap.remove(words);
            }
        }
        return countMap;
    }
}

class Sort implements iFunction {
    public Object call(Object arg) {
        Map<String, Integer> countMap = (Map<String, Integer>) arg;

        LinkedHashMap<String, Integer> sortHighestCount = new LinkedHashMap<>();
        countMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> sortHighestCount.put(x.getKey(), x.getValue()));
        return sortHighestCount;
    }
}

class top25 implements  iFunction {
    public Object call(Object arg) {
        Map<String, Integer> sortHighestCount = (Map<String, Integer>) arg;

        StringBuilder top25Builder = new StringBuilder();

        int count = 0;
        for (String key : sortHighestCount.keySet()) {
            String keys = key;
            Integer value = sortHighestCount.get(key);
            top25Builder.append(keys + " - " + value);
            count++;
            if (count >= 25)
                break;
            top25Builder.append("\n");
        }
        return top25Builder;
    }
}
