
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class words1 implements IExtractWords {

    public List<String> extractWords(String filePath) {
        List<String> fileData = new ArrayList<>();
        Scanner fileScanner = null;

        try {
            fileScanner = new Scanner(new File(filePath), "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        fileScanner.useDelimiter("[\\W_]+");
        while (fileScanner.hasNext()) {
            fileData.add(fileScanner.next().toLowerCase());
        }

        fileScanner.close();

        List<String> words = new ArrayList<>();

        for(int i = 0; i < fileData.size(); i++) {
            String[] w = fileData.get(i).toLowerCase().split("[^a-zA-Z]");
            for(String temp: w) {
                words.add(temp);
            }
        }

        Set<String> stopWords = new HashSet<>();
        List<String> updatedWords = new ArrayList<>();

        Scanner scan = null;

        try {
            scan = new Scanner(new File("/Users/Nicolas/Desktop/programming-styles/Week7/src/framework/stop-words.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
}
