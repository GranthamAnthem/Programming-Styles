import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class Fourteen {

    public static void main(String[] args) throws IOException {
        // error if build and run from compiler
        if (args.length < 1) {
            System.exit(1);
        }
        // if build and run from terminal
        // java Main.java pride-and-prejudice.txt
        else {
            WordFrequencyFramework wfapp = new WordFrequencyFramework();
            StopWordFilter stopWordFilter = new StopWordFilter(wfapp);
            DataStorage dataStorage = new DataStorage(wfapp, stopWordFilter);
            WordFrequencyCounter wordFrequencyCounter = new WordFrequencyCounter(wfapp, dataStorage);
            PrintUniqueZ printUniqueZ = new PrintUniqueZ(wfapp, dataStorage);
            wfapp.run(args[0]);
        }
    }
}

class WordFrequencyFramework {
    private List<Function> loadEventHandlers = new ArrayList<>();
    private List<Function> doWorkEventHandlers = new ArrayList<>();
    private List<Function> endEventHandlers = new ArrayList<>();

    public void registerForLoadEvent(Function handler) {
        loadEventHandlers.add(handler);
    }

    public void registerForDoWorkEvent(Function handler) {
        doWorkEventHandlers.add(handler);
    }

    public void registerForEndWorkEvent(Function handler) {
        endEventHandlers.add(handler);
    }

    public void run(String pathToFile) {
        for (Function handler: loadEventHandlers) {
            handler.apply(pathToFile);
        }

        for (Function handler: doWorkEventHandlers) {
            handler.apply(handler.toString());
        }

        for (Function handler: endEventHandlers) {
            handler.apply(handler.toString());
        }
    }
}


class DataStorage {
    private List<String> fileData;
    private List<String> words;
    private List<Function> wordEventHandlers = new ArrayList<>();
    StopWordFilter stopWordFilter;


    DataStorage (WordFrequencyFramework wfapp, StopWordFilter stopWordFilter) throws IOException {
        this.stopWordFilter = stopWordFilter;
        wfapp.registerForLoadEvent(load);
        wfapp.registerForDoWorkEvent(produceWords);
    }

    Function<String, Object> load = (pathToFile) -> {
        fileData = new ArrayList<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(pathToFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line = "";
        while (true) {
            try {
                if (!((line = br.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            fileData.add(line);
        }
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        words = new ArrayList<>();
        for(int i = 0; i < fileData.size(); i++) {
            String[] w = fileData.get(i).toLowerCase().split("[^a-zA-Z]");
            for(String temp: w) {
                words.add(temp);
            }
        }
        return null;
    };

    Function<String, Object> produceWords = word -> {
        for (String temp: words) {
            word = temp;
            if(! stopWordFilter.isStopWord.apply(word)) {
                for (Function handler: wordEventHandlers) {
                    handler.apply(word);
                }
            }
        }
        return null;
    };

    public void registerForWordEvent(Function handler) {
        wordEventHandlers.add(handler);
    }
}

class StopWordFilter {

    private Set<String> stopWords;

    StopWordFilter(WordFrequencyFramework wfapp) throws IOException {
        wfapp.registerForLoadEvent(load);
    }

    Function<String, Object> load = (filepath) -> {
        stopWords = new HashSet<>();
        filepath = "stop-words.txt";

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filepath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line = "";
        while(true) {
            try {
                if (!((line = br.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            String[] stopWords = line.split(",");
            for(String words : stopWords) {
                this.stopWords.add(words);
            }
        }
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    };

    Function<String, Boolean> isStopWord = (word) -> stopWords.contains(word);
}

class WordFrequencyCounter {
    private HashMap<String, Integer> countMap = new HashMap<>();
    private LinkedHashMap<String, Integer> sortHighestCount = new LinkedHashMap<>();

    WordFrequencyCounter(WordFrequencyFramework wfapp, DataStorage dataStorage) {
        dataStorage.registerForWordEvent(incrementCount);
        wfapp.registerForEndWorkEvent(printFrequencues);

    }

    Function<String, Object> incrementCount = word -> {
        if (word.length() >= 2) {
            if (!(countMap.containsKey(word)))
                countMap.put(word, 1);
            else
                countMap.put(word, countMap.get(word) +1);
        }
        return null;
    };

    Function<String, Object> printFrequencues = word -> {
        countMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> sortHighestCount.put(x.getKey(), x.getValue()));

        int count = 0;
        for (String key : sortHighestCount.keySet()) {
            String keys = key;
            Integer value = sortHighestCount.get(key);
            System.out.println(keys + " - " + value);
            count++;
            if (count >= 25)
                break;
        }
        return null;
    };
}

class PrintUniqueZ {

    DataStorage dataStorage;
    Set<String> wordsWithZ = new HashSet<>();

    PrintUniqueZ(WordFrequencyFramework wfapp, DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        dataStorage.registerForWordEvent(filterWordsWithZ);
        wfapp.registerForEndWorkEvent(printResults);
    }

    Function<String, Object> filterWordsWithZ = word -> {

        if(word.contains("z") || word.contains("z")) {
            wordsWithZ.add(word);
        }
        return null;
    };

    Function<String, Object> printResults = word -> {

        System.out.println("Unique Words With Z: " + wordsWithZ.size());
        return  null;
    };
}
