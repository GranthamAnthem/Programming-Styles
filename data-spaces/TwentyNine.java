import java.io.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


public class TwentyNine {

    private ArrayBlockingQueue<String> wordSpace = new ArrayBlockingQueue(200000);
    private ArrayBlockingQueue<ConcurrentHashMap<String, Integer>> freqSpace = new ArrayBlockingQueue<>(20);
    private Set<String> stopWords = Collections.synchronizedSet(new HashSet<>());

    TwentyNine(String filePath) throws IOException, InterruptedException {
        loadStopWords();
        loadFileData(filePath);
    }

    public void processWords() {
        ConcurrentHashMap<String, Integer> countMap = new ConcurrentHashMap<>();
        while(true) {
            String word = null;
            try {
                word = wordSpace.poll(1, TimeUnit.SECONDS);
                if(word == null) {
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (word.length() >= 2) {
                if (!stopWords.contains(word)) {
                    if (countMap.containsKey(word))
                        countMap.put(word, countMap.get(word) + 1);
                    else
                        countMap.put(word, 1);
                }
            }
        }
        freqSpace.offer(countMap);
    }

    private void loadStopWords() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("stop-words.txt"));
        String line = "";
        while((line = br.readLine()) != null) {
            String[] arrayStopWords = line.split(",");
            for(String words : arrayStopWords) {
                stopWords.add(words);
            }
        }
        br.close();
    }

    private void loadFileData(String filePath) throws IOException, InterruptedException {
        ArrayList<String> fileData = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line = "";
        while ((line = br.readLine()) != null) {
            fileData.add(line);
        }
        br.close();

        for(int i = 0; i < fileData.size(); i++) {
            String[] w = fileData.get(i).toLowerCase().split("[^a-zA-Z]");
            for(String temp: w) {
                wordSpace.put(temp);
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        TwentyNine twentyNine = new TwentyNine(args[0]);
        ArrayList<Thread> worker = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            worker.add(new Thread(() -> twentyNine.processWords()));
        }
        for(Thread workers: worker) {
            workers.start();
        }
        for(Thread workers: worker) {
            workers.join();
        }

        HashMap<String, Integer> wordFreqs = new HashMap<>();

        while(!twentyNine.freqSpace.isEmpty()) {
            twentyNine.freqSpace.poll()
                    .forEach(
                            (key, value) -> wordFreqs.merge(key, value, Integer::sum));
        }

        LinkedHashMap<String, Integer> sortHighestCount = new LinkedHashMap<>();
        wordFreqs.entrySet()
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
    }
}
