
import java.io.FileInputStream;
import java.io.IOException;

import java.net.URL;
import java.net.URLClassLoader;

import java.util.Map;
import java.util.Properties;


class Main {

        public static void main(String[] args) throws IOException {

            Properties properties = new Properties();
            properties.load(new FileInputStream("/Users/Nicolas/Desktop/programming-styles/Week7/src/config.properties"));

            String config = (String) properties.get("program1");
            String wordsPlugin = (String) properties.get("app1Words");
            String frequenciesPlugin = (String) properties.get("app1Frequencies");

            Class cls = null;
            Class cls2 = null;
            URL classUrl = null;
            try {
                // Find classes in the given jar file
                classUrl = new URL(config);
            } catch (Exception e) {
                e.printStackTrace();
            }
            URL[] classUrls = {classUrl};
            URLClassLoader cloader = new URLClassLoader(classUrls);
            try {
                cls = cloader.loadClass(wordsPlugin);
                cls2 = cloader.loadClass(frequenciesPlugin);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (cls != null && cls2 != null) {
                try {
                    IExtractWords words = (IExtractWords) cls.newInstance();
                    ITop25 freqs = (ITop25) cls2.newInstance();

                    Map<String, Integer> wordFreqs = freqs.top25(words.extractWords(args[0]));

                    int count = 0;
                    for (String key : wordFreqs.keySet()) {
                        String keys = key;
                        Integer value = wordFreqs.get(key);
                        System.out.println(keys + " - " + value);
                        count++;
                        if (count >= 25)
                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
}