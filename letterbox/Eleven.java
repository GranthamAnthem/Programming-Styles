import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Eleven {

    public static void main(String[] args) throws IOException {
        // error if build and run from compiler
        if (args.length < 1) {
            System.exit(1);
        }
        // if build and run from terminal
        // java Main.java pride-and-prejudice.txt
        else {
            WordFrequencyController wfController = new WordFrequencyController();
            wfController.dispatch("initialize", args[0]);
            wfController.dispatch("run");
        }
    }
}

class WordFrequencyController {
    private DataStoreManager storeManager;
    private StopWordManager stopWordManager;
    private WordFrequencyManager wordFrequencyManager;

    public void dispatch(Object... message) throws IOException {
        if(message[0].equals("initialize")) {
            storeManager.dispatch(message);
            stopWordManager.dispatch(message);
        }

        else if(message[0].equals("run")) {
            run();
        }

        else
            System.out.println("Message not understood: " + message[0]);
    }

    public WordFrequencyController() {
        this.storeManager = new DataStoreManager();
        this.stopWordManager = new StopWordManager();
        this. wordFrequencyManager = new WordFrequencyManager();
    }

    private void run() throws IOException {
        for (String word: storeManager.dispatch("words")) {
            if(!stopWordManager.dispatch("../stop-words", word)) {
                wordFrequencyManager.dispatch("count-frequencies", word);
            }
        }

        LinkedHashMap<String, Integer> wordFrequencies = wordFrequencyManager.dispatch("reverse-sort");

        int count = 0;
        for (String key : wordFrequencies.keySet()) {
            String keys = key;
            Integer value = wordFrequencies.get(key);
            System.out.println(keys + " - " + value);
            count++;
            if (count >= 25)
                break;
        }
    }
}

class DataStoreManager {
    private List<String> fileData;
    private List<String> words;

    public List<String> dispatch(Object... message) throws IOException {
        if(message[0].equals("initialize")) {
            loadFileData((String) message[1]);
        }

        else if(message[0].equals("words")) {
            return getWords();
        }

        else
            System.out.println("Message not understood: " + message[0]);
        return null;
    }

    public DataStoreManager() {
        this.fileData = new ArrayList<>();
        this.words = new ArrayList<>();
    }

    private void loadFileData(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line = "";
        while ((line = br.readLine()) != null) {
            fileData.add(line);
        }
        br.close();

        for(int i = 0; i < fileData.size(); i++) {
            String[] w = fileData.get(i).toLowerCase().split("[^a-zA-Z]");
            for(String temp: w) {
                words.add(temp);
            }
        }
    }

    private List<String> getWords() {
        return words;
    }
}

class StopWordManager{
    private Set<String> stopWords;

    public boolean dispatch(Object... message) throws IOException {

        if(message[0].equals("initialize")) {
            loadStopWords();
        }

        else if(message[0].equals("stop-words")) {
            return checkStopWord((String) message[1]);
        }

        else
            System.out.println("Message not understood: " + message[0]);
        return false;
    }

    public StopWordManager() {
        stopWords = new HashSet<>();
    }

    private void loadStopWords() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("stop-words.txt"));
        String line = "";
        while((line = br.readLine()) != null) {
            String[] stopWords = line.split(",");
            for(String words : stopWords) {
                this.stopWords.add(words);
            }
        }
        br.close();
    }

    private boolean checkStopWord(String word) {
        return stopWords.contains(word);
    }
}

class WordFrequencyManager {
    private HashMap<String, Integer> countMap;
    private LinkedHashMap<String, Integer> sortHighestCount;


    public LinkedHashMap<String, Integer> dispatch(Object... message) throws IOException {

        if(message[0].equals("count-frequencies")) {
            countFrequencies((String) message[1]);
        }

        else if(message[0].equals("reverse-sort")) {
            return reverseSortTopResults();
        }
        else
            System.out.println("Message not understood: " + message[0]);
        return null;
    }

    public WordFrequencyManager() {
        this.countMap = new HashMap<>();
        this.sortHighestCount = new LinkedHashMap<>();
    }

    private void countFrequencies(String word) {
        if (word.length() >= 2) {
            if (!(countMap.containsKey(word)))
                countMap.put(word, 1);
            else
                countMap.put(word, countMap.get(word) +1);
        }
    }

    private LinkedHashMap<String, Integer> reverseSortTopResults() {
        countMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> sortHighestCount.put(x.getKey(), x.getValue()));
        return sortHighestCount;
    }
}
