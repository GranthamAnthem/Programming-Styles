import java.io.*;
import java.util.*;

public class Twenty {

    // Read file and filter words into arraylist
    public static List<String> readFile(String filePath) {
        List<String> fileData = new ArrayList<>();
        Scanner fileScanner = null;

        if(filePath.isBlank()) {
            return  fileData;
        }
        try {
            fileScanner = new Scanner(new File(filePath), "UTF-8");
        } catch (FileNotFoundException e) {
            System.out.println("Error when opening file: " + e.getMessage());
            return fileData;
        }
        try {
            fileScanner.useDelimiter("[\\W_]+");
            while (fileScanner.hasNext()) {
                fileData.add(fileScanner.next().toLowerCase());
            }
        } finally {
            fileScanner.close();
        }
        return fileData;
    }

    // Filter words so alphanumeric only
    public static List<String> filterWords(List<String> fileData) {
        List<String> words = new ArrayList<>();

        if(fileData.isEmpty()) {
            return words;
        }

        for(int i = 0; i < fileData.size(); i++) {
            String[] w = fileData.get(i).toLowerCase().split("[^a-zA-Z]");
            for(String temp: w) {
                words.add(temp);
            }
        }
        return words;
    }

    public static List<String> removeStopWords(List<String> words) {
        Set<String> stopWords = new HashSet<>();
        List<String> updatedWords = new ArrayList<>();

        if(words.isEmpty()) {
            return updatedWords;
        }

        Scanner scan = null;
        try {
            scan = new Scanner(new File("stop-words.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("Error when opening file: " + e.getMessage());
            return updatedWords;
        }
        scan.useDelimiter(",");
        while(scan.hasNext()) {
            stopWords.add(scan.next());
        }

        for(String word: words) {
            if(!stopWords.contains(word)) {
                updatedWords.add(word);
            }
        }
        return updatedWords;
    }

    // Store words and their counts in HashMap
    public static HashMap<String, Integer> countFrequencies(List<String> updatedWords) {
        HashMap<String, Integer> countMap = new HashMap<>();

        if(updatedWords.isEmpty()) {
            return countMap;
        }

        for(String word: updatedWords) {
            if (word.length() >= 2) {
                if (!(countMap.containsKey(word)))
                    countMap.put(word, 1);
                else
                    countMap.put(word, countMap.get(word) + 1);
            }
        }
        return countMap;
    }

    // Sort words with counts by highest word-count
    public static LinkedHashMap<String, Integer> reverseSortTopResults(HashMap<String, Integer> countMap) {
        LinkedHashMap<String, Integer> sortHighestCount = new LinkedHashMap<>();

        if(countMap.isEmpty()) {
            return sortHighestCount;
        }

        countMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> sortHighestCount.put(x.getKey(), x.getValue()));
        return sortHighestCount;
    }

    // print results for top 25 most occurring words
    public static void printResults(LinkedHashMap<String, Integer> sortHighestCount) {

        if(sortHighestCount.isEmpty()) {
            return;
        }

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
            printResults(reverseSortTopResults(countFrequencies(removeStopWords(filterWords(readFile("great-expectations.txt"))))));
            System.out.println("No file found. Here are the results of Great Expectations.");
        }

        // if build and run from terminal
        // java Main.java pride-and-prejudice.txt
        else {
            printResults(reverseSortTopResults(countFrequencies(removeStopWords(filterWords(readFile(args[0]))))));
        }
    }
}
