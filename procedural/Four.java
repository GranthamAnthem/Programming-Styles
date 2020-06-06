import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class Four {
    public static List<String> fileData;
    public static List<String> words;
    public static HashMap<String, Integer> countMap;
    public static LinkedHashMap<String, Integer> sortHighestCount;


    // Read file and insert words into arraylist
    public static void readFile(String file) throws IOException {
        fileData = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = "";
        while ((line = br.readLine()) != null) {
            fileData.add(line);
        }
        br.close();
    }

    // Filter words so alphanumeric only
    public static void filterWords() {
        words = new ArrayList<>();
        for(int i = 0; i < fileData.size(); i++) {
            String[] w = fileData.get(i).toLowerCase().split("[^a-zA-Z]");
            for(String temp: w) {
                words.add(temp);
            }
        }
    }

    // Store words and their counts in HashMap
    public static void countFrequencies() throws IOException {
        countMap = new HashMap<>();
        for (String word : words) {
            if (word.length() >= 2) {
                if (!(countMap.containsKey(word)))
                    countMap.put(word, 1);
                else
                    countMap.put(word, countMap.get(word) +1);
            }
        }
    }

    // Remove all Stop Words from HashMap
    public static void removeStopWords() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("../stop-words.txt"));
        String line = "";
        while((line = br.readLine()) != null) {
            String[] stopWords = line.split(",");
            for(String words : stopWords) {
                if (countMap.containsKey(words))
                    countMap.remove(words);
            }
        }
        br.close();
    }

    // Sort words with counts by highest word-count
    public static void reverseSortTopResults() {
        sortHighestCount = new LinkedHashMap<>();
        countMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> sortHighestCount.put(x.getKey(), x.getValue()));
    }

    // print results for top 25 most occurring words
    public static void printResults() throws IOException {
        int count = 0;
        for (String key : sortHighestCount.keySet()) {
            String keys = key;
            Integer value = sortHighestCount.get(key);
            System.out.println(keys + " - " + value);
            count++;
            if (count >= 25)
                break;
        }
    }

    public static void main(String[] args) throws IOException {

        // error if build and run from compiler
        if (args.length < 1) {
            System.exit(1);
        }

        // if build and run from terminal
        // java Main.java pride-and-prejudice.txt
        else {
            readFile(args[0]);
            filterWords();
            countFrequencies();
            removeStopWords();
            reverseSortTopResults();
            printResults();
        }
    }
}
