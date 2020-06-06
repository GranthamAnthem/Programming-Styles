
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class words2 implements IExtractWords {

    public List<String> extractWords(String filePath) {
        List<String> fileData = new ArrayList<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line = "";
        while (true) {
            try {
                if (!((line = br.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            fileData.add(line);
        }
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> normalizedWords = new ArrayList<>();
        for (int i = 0; i < fileData.size(); i++) {
            String[] w = fileData.get(i).toLowerCase().split("[^a-zA-Z]");
            for (String temp : w) {
                normalizedWords.add(temp);
            }
        }

        Set<String> stopWords = new HashSet<>();
        BufferedReader brStop = null;
        try {
            brStop = new BufferedReader(new FileReader("/Users/Nicolas/Desktop/programming-styles/Week7/src/framework/stop-words.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String lineStop = "";
        while (true) {
            try {
                if (!((lineStop = brStop.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            String[] arrayStopWords = lineStop.split(",");
            for (String words : arrayStopWords) {
                stopWords.add(words);
            }
        }
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> updatedWords = new ArrayList<>();
        for (String word : normalizedWords) {
            if (!stopWords.contains(word)) {
                updatedWords.add(word);
            }
        }
        return updatedWords;
    }
}
