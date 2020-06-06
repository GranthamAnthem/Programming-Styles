import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Eight {

    public static void main(String[] args) throws IOException {
        // error if build and run from compiler
        if (args.length < 1) {
            System.exit(1);
        }
        // if build and run from terminal
        // java Main.java pride-and-prejudice.txt
        else {
            new ReadFile().call(args[0], new FilterWords());
        }
    }
}

interface iFunction {
    void call (Object arg, iFunction func) throws IOException;
}

class ReadFile implements iFunction {

    public void call(Object arg, iFunction func) throws IOException {
        List<String> fileData = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(arg.toString()));
        String line = "";
        while ((line = br.readLine()) != null) {
            fileData.add(line);
        }
        br.close();
        func.call(fileData, new StopWords());
    }
}

class FilterWords implements iFunction  {
    public void call(Object arg, iFunction func) throws IOException {
        List<String> fileData = new ArrayList<>(Collections.singleton(arg.toString()));
        List<String> words = new ArrayList<>();
        for(int i = 0; i < fileData.size(); i++) {
            String[] w = fileData.get(i).toLowerCase().split("[^a-zA-Z]");
            for(String temp: w) {
                words.add(temp);
            }
        }
        func.call(words, new RemoveStopWords());
    }
}

class StopWords implements iFunction {
    public void call(Object arg, iFunction func) throws IOException {
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
        func.call(countMap, new Sort());
    }
}

class RemoveStopWords implements iFunction {
    public void call (Object arg, iFunction func) throws IOException {
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
        func.call(countMap, new Print());
    }
}

class Sort implements iFunction {
    public void call(Object arg, iFunction func) throws IOException {
        Map<String, Integer> countMap = (Map<String, Integer>) arg;

        LinkedHashMap<String, Integer> sortHighestCount = new LinkedHashMap<>();
        countMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> sortHighestCount.put(x.getKey(), x.getValue()));
        func.call(sortHighestCount, new Done());
    }
}

class Print implements  iFunction {
    public void call(Object arg, iFunction func) throws IOException {
        Map<String, Integer> sortHighestCount = (Map<String, Integer>) arg;
        int count = 0;
        for (String key : sortHighestCount.keySet()) {
            String keys = key;
            Integer value = sortHighestCount.get(key);
            System.out.println(keys + " - " + value);
            count++;
            if (count >= 25)
                break;
        }
        func.call(null,null);
    }
}

class Done implements  iFunction {
    public void call(Object arg, iFunction func) {
        return;
    }
}
