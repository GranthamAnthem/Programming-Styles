import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class TwentyEight {

    public static void main(String[] args) throws InterruptedException, IOException {
        // error if build and run from compiler
        if (args.length < 1) {
            System.exit(1);
        }
        // if build and run from terminal
        // java Main.java pride-and-prejudice.txt
        else {

            WordFrequencyManager wordFrequencyManager = new WordFrequencyManager();

            StopWordManager stopWordManager = new StopWordManager();
            stopWordManager.send(stopWordManager, new Message("initialize", wordFrequencyManager));

            DataStoreManager dataStoreManager = new DataStoreManager();
            dataStoreManager.send(dataStoreManager, new Message("init", stopWordManager));
            dataStoreManager.send(dataStoreManager,  new Message("initialize", args[0]));

            WordFrequencyController wfController = new WordFrequencyController();
            wfController.send(wfController, new Message("run", dataStoreManager));


            ActiveWTFObject[] threads = new ActiveWTFObject[]{wordFrequencyManager, stopWordManager, dataStoreManager, wfController};
            for (ActiveWTFObject thread : threads) {
                thread.join();
            }

        }
    }
}

abstract class ActiveWTFObject extends Thread{

    ArrayBlockingQueue<Message> queue;
    Boolean flag = true;

    ActiveWTFObject() {
        queue = new ArrayBlockingQueue<Message>(5);
        flag = true;
        start();
    }

    @Override
    public void run() {
        while(flag) {
            Message message = null;
            try {
                message = queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                dispatch(message);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (message.getName().equals("die")) {
                    flag = false;
                }
        }
    }

    public abstract void dispatch(Message message) throws IOException, InterruptedException;

    public void send(ActiveWTFObject receiver, Message message) throws InterruptedException, IOException {
        receiver.queue.put(message);
    }
}

class Message {

    String name;
    Object data;

    public Message(String name, Object data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public Object getData() {
        return data;
    }
}


class DataStoreManager extends ActiveWTFObject{
    private List<String> fileData;
    private List<String> words;
    private StopWordManager stopWordManager;

    public void dispatch(Message message) throws IOException, InterruptedException {

        if(message.getName().equals("init")) {
            load(message);
        }

        else if(message.getName().equals("initialize")) {
            loadFileData(message);
        }

        else if(message.getName().equals("sendWordFreqs")) {
            processWords(message);
        }
        else
            send(stopWordManager, message);
    }

    public DataStoreManager() {
        this.fileData = Collections.synchronizedList(new ArrayList<String>());
        this.words = Collections.synchronizedList(new ArrayList<String>());
    }

    private void load(Message message) {
        stopWordManager = (StopWordManager) message.getData();
    }


    private void loadFileData(Message message) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(message.getData().toString()));
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

    private void processWords(Message message) throws IOException, InterruptedException {
        Object recipient = message.getData();

        for(String word: words) {
            send(stopWordManager, new Message("filter", word));
        }
        send(stopWordManager, new Message("top25", recipient));
    }
}

class StopWordManager extends ActiveWTFObject{
    private Set<String> stopWords;
    private WordFrequencyManager wordFrequencyManager;

    public void dispatch(Message message) throws IOException, InterruptedException {

        if(message.getName().equals("initialize")) {
            loadStopWords(message);
        }

        else if(message.getName().equals("filter")) {
            filter(message);
        }
        else
            send(wordFrequencyManager, message);
    }

    public StopWordManager() {
        stopWords = Collections.synchronizedSet(new HashSet<>());
    }

    private void loadStopWords(Message message) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("stop-words.txt"));
        String line = "";
        while((line = br.readLine()) != null) {
            String[] stopWords = line.split(",");
            for(String words : stopWords) {
                this.stopWords.add(words);
            }
        }
        br.close();
        wordFrequencyManager = (WordFrequencyManager) message.getData();
    }

    private void filter(Message message) throws InterruptedException, IOException {
        if(!stopWords.contains(message.getData().toString())) {
            send(wordFrequencyManager, new Message("word", message.getData().toString()));
        }
    }
}

class WordFrequencyManager extends ActiveWTFObject{
    private ConcurrentHashMap<String, Integer> countMap;
    private LinkedHashMap<String, Integer> sortHighestCount;
    private Object lock;

    public void dispatch(Message message) throws InterruptedException, IOException {

        if(message.getName().equals("word")) {
            countFrequencies(message);
        }

        else if(message.getName().equals("top25")) {
            top25(message);
        }
    }

    public WordFrequencyManager() {
        this.sortHighestCount = new LinkedHashMap<>();
        this.countMap = new ConcurrentHashMap<>();
        this.lock = new Object();
    }

    private void countFrequencies(Message message) {
        synchronized (lock) {
            if (message.getData().toString().length() >= 2) {
                if (!(countMap.containsKey(message.getData().toString())))
                    countMap.put(message.getData().toString(), 1);
                else
                    countMap.put(message.getData().toString(), countMap.get(message.getData().toString()) + 1);
            }
        }
    }

    private void top25(Message message) throws InterruptedException, IOException {
        Object recipient = message.getData();

        countMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> sortHighestCount.put(x.getKey(), x.getValue()));

        send((WordFrequencyController) recipient, new Message("top25", sortHighestCount));
    }
}

class WordFrequencyController extends ActiveWTFObject {
    private DataStoreManager storeManager;

    public void dispatch(Message message) throws IOException, InterruptedException {

        if(message.getName().equals("run")) {
            run(message);
        }
        else if (message.getName().equals("top25")) {
            display(message);
        }
        else
            System.out.println("Message not understood: " + message.getName());
    }

    private void run(Message message) throws InterruptedException, IOException {
        storeManager = (DataStoreManager) message.getData();
        send(storeManager, new Message("sendWordFreqs", this));
    }

    private void display(Message message) throws InterruptedException, IOException {
        Map<String, Integer> sortHighestCount = (Map<String, Integer>) message.getData();
        int count = 0;
        for (String key : sortHighestCount.keySet()) {
            String keys = key;
            Integer value = sortHighestCount.get(key);
            System.out.println(keys + " - " + value);
            count++;
            if (count >= 25)
                break;
        }
        send(storeManager, new Message("die", null));
        flag = false;
    }
}
