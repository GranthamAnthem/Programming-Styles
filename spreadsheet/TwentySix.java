import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TwentySix {

    public static Object[] allWords = new Object[] {new ArrayList<String>(), "None"};
    public static Object[] stopWords = new Object[] {new HashSet<String>(), "None"};
    public static Object[] nonStopWords = new Object[] {new ArrayList<String>(), (ColumnFunction) () -> loadNonStopWords()};
    public static Object[] uniqueWords = new Object[] {new HashSet<String>(), (ColumnFunction) () -> loadUniqueWords()};
    public static Object[] counts = new Object[] {new HashSet<String>(), (ColumnFunction) () -> loadCounts()};
    public static Object[] sortedData = new Object[] {new HashSet<String>(), (ColumnFunction) () -> loadSort()};
    public static Object[] allColumns = new Object[] {allWords, stopWords, nonStopWords, uniqueWords, counts, sortedData};

    @FunctionalInterface
    interface ColumnFunction {
        Object columnFunction();
    }


    public static List<String> loadNonStopWords() {
        List<String> words = new ArrayList<>();
        for(String word: (ArrayList<String>) allWords[0]) {
            if( ! ((Set<String>)stopWords[0]).contains(word)) {
                words.add(word);
            }
        }
        return words;
    }


    public static Set<String> loadUniqueWords() {
        Set<String> uniqueSet = new HashSet<>();

        for(String word: (ArrayList<String>) nonStopWords[0]) {
            uniqueSet.add(word);
        }
        return uniqueSet;
    }


    public static HashMap<String, Integer> loadCounts() {
        HashMap<String, Integer> countMap = new HashMap<>();

        for(String word: (ArrayList<String>) nonStopWords[0]) {
            if (!(countMap.containsKey(word))){
                countMap.put(word, 1);
            }
            else{
                countMap.put(word, countMap.get(word) + 1);
            }
        }
        return countMap;
    }


    public static LinkedHashMap<String, Integer> loadSort() {

        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();

        ((HashMap<String, Integer>) counts[0]).entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));

        return sortedMap;
    }


    public static void update() {

        for (int i = 0; i < allColumns.length; i++) {
            Object[] column = (Object[]) allColumns[i];

            if(!column[1].equals("None"))
                column[0] =  ((ColumnFunction) column[1]).columnFunction();
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

            allWords[0] = Files.lines(Paths.get(args[0]))
                    .map(String::toLowerCase)
                    .flatMap(Pattern.compile("[^a-zA-Z]")::splitAsStream)
                    .filter(word -> word.length() >= 2)
                    .collect(Collectors.toList());

            stopWords[0] = Files.lines(Paths.get("stop-words.txt"))
                    .flatMap(Pattern.compile("\\s*,\\s*")::splitAsStream)
                    .collect(Collectors.toSet());

            update();

            ((LinkedHashMap<String, Integer>) sortedData[0]).entrySet().stream().limit(25).forEach(entries -> System.out.println(entries.getKey() + " - " + entries.getValue()));

            List<String> tempStoreWords = (ArrayList<String>) allWords[0];
            Scanner scan = new Scanner(System.in);
            System.out.println("\nEnter another book or 'q' to quit:\nadventures\nfrankenstein\nmetamorphosis");
            String flag = scan.next().toLowerCase();
            while(!flag.equals("q")) {

                tempStoreWords.addAll(Files.lines(Paths.get(flag +".txt"))
                        .map(String::toLowerCase)
                        .flatMap(Pattern.compile("[^a-zA-Z]")::splitAsStream)
                        .filter(word -> word.length() >= 2)
                        .collect(Collectors.toList()));

                allWords[0] = tempStoreWords;

                update();

                System.out.println("\nAdding " + flag + " frequencies\n");
                ((LinkedHashMap<String, Integer>) sortedData[0]).entrySet().stream().limit(25).forEach(entries -> System.out.println(entries.getKey() + " - " + entries.getValue()));

                System.out.println("\nEnter another book or 'q' to quit:\nadventures\nfrankenstein\nmetamorphosis");
                flag = scan.next().toLowerCase();
            }
        }
    }
}
