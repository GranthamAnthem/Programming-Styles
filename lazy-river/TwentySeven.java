import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

public class TwentySeven {

    // Read file and filter words into arraylist
    public static Stream<String> allWords(String fileName) throws IOException {

        return Files.lines(Paths.get(fileName))
                .map(String::toLowerCase)
                .flatMap(Pattern.compile("[^a-zA-Z]")::splitAsStream)
                .filter(word -> word.length() >= 2);
    }

    public static Stream<String> nonStopWords(String fileName) throws IOException {

        Set<String> stopWords = Files.lines(Paths.get("../stop-words.txt"))
                .flatMap(Pattern.compile("\\s*,\\s*")::splitAsStream)
                .collect(Collectors.toSet());

        return allWords(fileName).filter(word -> !stopWords.contains(word));
    }

    // Sort words with counts by highest word-count
    public static Stream<Map.Entry<String, Integer>> countAndSort(String fileName) throws IOException {

        return nonStopWords(fileName)
                .collect(groupingBy(Function.identity(), summingInt(entry -> 1)))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()));
    }

    public static void main(String[] args) throws IOException {

        // error if build and run from compiler
        if (args.length < 1) {
            System.exit(1);
        }
        // if build and run from terminal
        // java Main.java pride-and-prejudice.txt
        else {
            countAndSort(args[0]).limit(25).forEach(entries -> System.out.println(entries.getKey() + " - " + entries.getValue()));
        }
    }
}
