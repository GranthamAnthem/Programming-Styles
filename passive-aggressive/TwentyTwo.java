import java.io.*;
import java.util.*;

public class TwentyTwo {

    // Read file and filter words into arraylist
    public static List<String> readFile(String filePath) throws IOException {
        List<String> fileData = new ArrayList<>();
        Scanner fileScanner = null;

        if(filePath.isBlank()) {
            System.err.println("File path is empty. Please enter file");
        }
        fileScanner = new Scanner(new File(filePath), "UTF-8");
        fileScanner.useDelimiter("[\\W_]+");
        while (fileScanner.hasNext()) {
            fileData.add(fileScanner.next().toLowerCase());
        }
        return fileData;
    }

    // Filter words so alphanumeric only
    public static List<String> filterWords(List<String> fileData) {
        List<String> words = new ArrayList<>();

        if(fileData.isEmpty()) {
            System.err.println("File data is empty. Not able to filter words.");
        }

        for(int i = 0; i < fileData.size(); i++) {
            String[] w = fileData.get(i).toLowerCase().split("[^a-zA-Z]");
            for(String temp: w) {
                words.add(temp);
            }
        }
        return words;
    }

    public static List<String> removeStopWords(List<String> words) throws FileNotFoundException {
        Set<String> stopWords = new HashSet<>();
        List<String> updatedWords = new ArrayList<>();

        if(words.isEmpty()) {
            System.err.println("Filtered words not found. Not able to remove stopwords.");
        }

        Scanner scan = null;
        scan = new Scanner(new File("../stop-words.txt"));
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
            System.err.println("Updated word list (filtered words - stop words) not found. Not able to count frequencies");
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
            System.err.println("Word frequencies not found. Not able to sort words.");
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
            System.err.println("Sorted frequencies not found. Not able to print words.");
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
            System.err.println("You idiot! I need an input file!");
        }

        // if build and run from terminal
        // java Main.java pride-and-prejudice.txt
        else {
            printResults(reverseSortTopResults(countFrequencies(removeStopWords(filterWords(readFile(args[0]))))));
        }
    }
}
