import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResultNewForm {

    public class ResWord {
        public String word = "";
        public double probability = 0;

        @Override
        public String toString() {
            return word + ":" + probability;
        }
    }

    public class ResSentence {
        public String lineString = "";
        public List<String> inputContextsList = new ArrayList<>();
        public List<String> inputLettersList = new ArrayList<>();
        public List<String> outputWordsList = new ArrayList<>();
        public List<List<ResWord>> resWordsList = new ArrayList<>();
    }

    public List<ResSentence> resSentencesList = new ArrayList<>();

    private List<ResWord> splitTopnResPc(String resString) {
        String[] resWordsStrings = resString.split("\\|", -1);
        List<ResWord> topnWordsList = new ArrayList<>();
        for (String resWordsString : resWordsStrings) {
            ResWord resWord = new ResWord();
            String word = resWordsString.trim();
            int idx = word.lastIndexOf(":");
            if (idx > 0) {
                resWord.word = word.substring(0, idx);
                resWord.probability = Double.parseDouble(word.substring(idx + 1, word.length()));
            } else {
                resWord.word = word;
            }
            topnWordsList.add(resWord);
        }
        return topnWordsList;
    }

    public void parseResultPcNewForm(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
            String lineString = "";
            while ((lineString = reader.readLine()) != null) {
                String[] lineStrings = lineString.split("\\|#\\|", -1);
                if (lineStrings.length == 4) {
                    ResSentence resSentence = new ResSentence();
                    resSentence.lineString = lineString;
                    if (lineStrings[0].trim().length() > 0) {
                        resSentence.inputContextsList.addAll(Arrays.asList(lineStrings[0].trim().split("\\t", -1)));
                    }
                    if (lineStrings[1].trim().length() > 0) {
                        resSentence.inputLettersList.addAll(Arrays.asList(lineStrings[1].trim().split("\\s", -1)));
                    }
                    resSentence.outputWordsList.addAll(Arrays.asList(lineStrings[2].trim().split("\\t", -1)));
                    String resString = lineStrings[3].trim();
                    String[] resStrings = resString.split("\\t", -1);
                    if (resStrings.length == resSentence.inputLettersList.size() + 1) {
                        for (String topnRes : resStrings) {
                            List<ResWord> topnWordsList = splitTopnResPc(topnRes);
                            resSentence.resWordsList.add(topnWordsList);
                        }
                    } else {
                        System.out.println("input letters length != res strings length: " + lineString);
                        System.out.println(resSentence.inputLettersList + " " + resString);
                        System.out.println((resSentence.inputLettersList.size() + 1) + " " + resStrings.length);
                        continue;
                    }
                    if (resSentence.inputLettersList.size() + resSentence.inputContextsList.size() > 0) {
                        resSentencesList.add(resSentence);
                    }
                } else {
                    System.out.println("split line String error : " + lineString);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printResSentencesList() {
        for (ResSentence resSentence : resSentencesList) {
            System.out.println(resSentence.lineString);
            System.out.println(resSentence.inputContextsList + " |#| " + resSentence.inputLettersList + " |#| " + resSentence.outputWordsList + " |#| " + resSentence.resWordsList);
        }
        System.out.println("total " + resSentencesList.size() + " sentences");
    }
}
