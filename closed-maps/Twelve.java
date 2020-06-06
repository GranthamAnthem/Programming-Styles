import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Twelve {

    public static void main(String[] args) throws IOException {

        // error if build and run from compiler
        if (args.length < 1) {
            System.exit(1);
        }

        // build and run from terminal
        // java Main.java pride-and-prejudice.txt

        else {

            HashMap<String, Object> dataStoreObj = new HashMap<>();
                dataStoreObj.put("data", new ArrayList<>());
                dataStoreObj.put("init", (Extract) (filePath) -> {
                    try {
                        loadFileData(dataStoreObj, filePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });


            HashMap<String, Object> stopWordsObj = new HashMap<>();
            stopWordsObj.put("stopwords", new ArrayList<>());
            stopWordsObj.put("init", (StopWords)() -> {
                try {
                    loadStopWords(stopWordsObj);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            HashMap<String, Object>  wordFreqs = new HashMap<>();
            wordFreqs.put("wordfreqs", new HashMap<String, Integer>());
            wordFreqs.put("incrementCount", (WordFreqs) () -> countFrequencies(wordFreqs, dataStoreObj.get("data"), stopWordsObj.get("stopwords")));
            wordFreqs.put("sorted", (SortMap) () -> reverseSortTopResults(wordFreqs));

            ((Extract) dataStoreObj.get("init")).extract(args[0]);
            ((StopWords) stopWordsObj.get("init")).loadStopWords();
            ((WordFreqs) wordFreqs.get("incrementCount")).incrementCount();
            ((SortMap) wordFreqs.get("sorted")).sortMap();

            Map<String, Integer> printSortedFreqs = (Map<String, Integer>) wordFreqs.get("sorted");

            int count = 0;
            for (String key : printSortedFreqs.keySet()) {
                String keys = key;
                Integer value = printSortedFreqs.get(key);
                System.out.println(keys + " - " + value);
                count++;
                if (count >= 25)
                    break;
            }
        }
    }


    @FunctionalInterface
    interface Extract {
        void extract(Object filePath);
    }

    @FunctionalInterface
    interface StopWords {
        void loadStopWords();
    }

    @FunctionalInterface
    interface WordFreqs {
        void incrementCount();
    }

    @FunctionalInterface
    interface SortMap {
        void sortMap();
    }
    

    public static void loadFileData(HashMap<String, Object> dataStoreObj, Object filePath) throws IOException {
        List<String> fileData = new ArrayList<>();
        List<String> words = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader((String) filePath));
        String line = "";
        while ((line = br.readLine()) != null) {
            fileData.add(line);
        }
        br.close();

        for (int i = 0; i < fileData.size(); i++) {
            String[] w = fileData.get(i).toLowerCase().split("[^a-zA-Z]");
            for (String temp : w) {
                words.add(temp);
            }
        }
        dataStoreObj.put("data", words);
    }

    public static void loadStopWords(HashMap<String, Object> stopWordsObj) throws IOException {
        Set<String> stopWords = new HashSet<>();
        BufferedReader br = new BufferedReader(new FileReader("stop-words.txt"));
        String line = "";
        while((line = br.readLine()) != null) {
            String[] stop = line.split(",");
            for(String words : stop) {
                stopWords.add(words);
            }
        }
        br.close();
        stopWordsObj.put("stopwords",stopWords);
    }


    public static void countFrequencies(HashMap<String, Object> wordFreqs, Object data, Object stop) {

        HashMap<String, Integer> countMap = new HashMap<>();
        List<String> listDateStore = new ArrayList<>();
        Set<String> listStopWords = new HashSet<>();

        listDateStore.addAll((Collection<? extends String>) data);
        listStopWords.addAll((Collection<? extends String>) stop);

        for (String word: listDateStore) {
            if (!listStopWords.contains(word)) {
                if (word.length() >= 2) {
                    if (!(countMap.containsKey(word))){
                        countMap.put(word, 1);
                    }
                    else{
                        countMap.put(word, countMap.get(word) + 1);
                    }
                }
            }
        }
        wordFreqs.put("wordfreqs", countMap);
    }

    public static void reverseSortTopResults(HashMap<String, Object> wordFreqs) {

        Map<String, Integer> innerMap = (Map<String, Integer>) wordFreqs.get("wordfreqs");

        LinkedHashMap<String, Integer> sortHighestCount = new LinkedHashMap<>();
        innerMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> sortHighestCount.put(x.getKey(), x.getValue()));

        wordFreqs.put("sorted",sortHighestCount);
    }
}
