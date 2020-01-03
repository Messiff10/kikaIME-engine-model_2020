import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Result {

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
        public String inputLetters = "";
        public String inputContexts = "";
        public String outputWord = "";
        public List<List<ResWord>> resWordsList = new ArrayList<>();
    }

    public List<ResSentence> resSentencesList = new ArrayList<>();

    private List<ResWord> splitTopnResPc(String resString) {
        String[] resWordsStrings = resString.split(",\\s");
        List<ResWord> topnWordsList = new ArrayList<>();
        for (int j = 0; j < resWordsStrings.length; j++) {
            ResWord resWord = new ResWord();
            String word = resWordsStrings[j].trim();
            word = word.substring(1, word.length() - 1);
            int idx = word.lastIndexOf(":");
            if (idx > 0) {
                resWord.word = word.substring(0,idx);
                resWord.probability = Double.parseDouble(word.substring(idx + 1, word.length()));
            } else {
                resWord.word = word;
            }
            topnWordsList.add(resWord);
        }
        return topnWordsList;
    }

    public void parseResultPc(String fileName, boolean isLatinime) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
            String lineString = "";
            while ((lineString = reader.readLine()) != null) {
                ResSentence resSentence = new ResSentence();
                resSentence.lineString = lineString;

                String[] lineStrings = lineString.split("\\|");
                String inputString = lineStrings[0];
                String[] inputStrings = inputString.split("\\t+");
                if (inputStrings.length == 3) {
                    resSentence.inputContexts = inputStrings[0].trim();
                    String inputLetters = inputStrings[1].trim();
                    inputLetters = inputLetters.substring(3, inputLetters.length() - 4).replaceAll("\\s", "");
                    resSentence.inputLetters = inputLetters;
                    resSentence.outputWord = inputStrings[2].trim();
                } else {
                    System.out.println("split line String error : " + lineString);
                    continue;
                }

                String resString = lineStrings[1].trim();
                resString = resString.substring(2, resString.length() - 2);
                String[] resStrings = resString.split("\\], \\[");
                if (resStrings.length == resSentence.inputLetters.length() + 1 && !isLatinime ||
                        resStrings.length == resSentence.inputLetters.length() && isLatinime) {
                    if (isLatinime) {
                        List<ResWord> topnWordsList = new ArrayList<>();
                        resSentence.resWordsList.add(topnWordsList);
                    }
                    for (int i = 0; i < resStrings.length; i++) {
                        List<ResWord> topnWordsList = splitTopnResPc(resStrings[i]);
                        resSentence.resWordsList.add(topnWordsList);
                    }
                } else {
                    System.out.println("input letters length != res strings length");
                    continue;
                }

                if (resSentence.inputLetters.length() + resSentence.inputContexts.length() > 0) {
                    resSentencesList.add(resSentence);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parseResultAndroid(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
            String taskLineString = "";
            while ((taskLineString = reader.readLine()) != null) {
                if (taskLineString.startsWith("TASK")) {
                    ResSentence resSentence = new ResSentence();

                    Pattern pat = Pattern.compile("TASK: Prefix = '(.*)', input=(.*) desire=(.*)");
                    Matcher mat = pat.matcher(taskLineString);
                    if (mat.find()) {
                        resSentence.inputContexts = mat.group(1).trim();
                        resSentence.inputLetters = mat.group(2).trim();
                        resSentence.outputWord = mat.group(3).trim();
//                        System.out.println(resSentence.inputContexts + " | " + resSentence.inputLetters + " | " + resSentence.outputWord);
                    } else {
                        System.out.println("parse task line error : " + taskLineString);
                        continue;
                    }

                    String suggestionLineString = reader.readLine();
                    if (suggestionLineString != null && suggestionLineString.startsWith("SUGGESTION")) {
                        pat = Pattern.compile("SUGGESTION: \\(got=(true|false)\\) (.*)");
                        mat = pat.matcher(suggestionLineString);
                        if (mat.find()) {
                            String resWordsSring = mat.group(2).trim();
//                            System.out.println(resWordsSring);
                            String[] resWordsStrings = resWordsSring.split("\\s+");
                            if (resWordsStrings.length <= 1) {
                                System.out.println("parse suggestion line res strings error : " + suggestionLineString);
                                continue;
                            }
//                            System.out.println(resWordsSring + " " + resWordsStrings.length);
                            List<ResWord> topnWordsList = new ArrayList<>();
                            for (int i = 0; i < resWordsStrings.length; i++) {
                                ResWord resWord = new ResWord();
                                String word = resWordsStrings[i].trim();
                                word = word.substring(2, word.length());
                                resWord.word = word;
                                topnWordsList.add(resWord);
                            }
                            resSentence.resWordsList.add(topnWordsList);
                        } else {
                            System.out.println("parse suggestion line error : " + suggestionLineString);
                            continue;
                        }
                    } else {
                        System.out.println("suggestion line not found : " + suggestionLineString);
                        continue;
                    }

                    for (int i = 0; i < resSentence.inputLetters.length(); i ++) {
                        String candidateLineString = reader.readLine();
                        if (candidateLineString != null && candidateLineString.startsWith("CANDIDATE")) {
                            pat = Pattern.compile("CANDIDATE: \\(found=(true|false), default=(true|false)\\) (.*)");
                            mat = pat.matcher(candidateLineString);
                            if (mat.find()) {
                                String resWordsSring = mat.group(3).trim();
//                            System.out.println(resWordsSring);
                                String[] resWordsStrings = resWordsSring.split("\\s+");
                                if (resWordsStrings.length <= 1) {
                                    System.out.println("parse candidate line res strings error : " + suggestionLineString);
                                    continue;
                                }
                                List<ResWord> topnWordsList = new ArrayList<>();
                                System.out.println(resWordsSring);
                                for (int j = 0; j < resWordsStrings.length; j++) {
                                    ResWord resWord = new ResWord();
                                    String word = resWordsStrings[j].trim();
                                    word = word.substring(2, word.length());
                                    resWord.word = word;
                                    topnWordsList.add(resWord);
                                }
                                resSentence.resWordsList.add(topnWordsList);
                            } else {
                                System.out.println("parse candidate line error : " + candidateLineString);
                                continue;
                            }
                        } else {
                            System.out.println("candidate line not found : " + candidateLineString);
                            continue;
                        }
                    }

                } else {
                    System.out.println("not task line : " + taskLineString);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printResSentencesList() {
        for (int i = 0; i < resSentencesList.size(); i++) {
            ResSentence resSentence = resSentencesList.get(i);
            System.out.println(resSentence.lineString);
            System.out.print(resSentence.inputContexts + " | " + resSentence.inputLetters + " | " + resSentence.outputWord + " |#| ");
            List<List<ResWord>> resWordsList = resSentence.resWordsList;
            for (int j = 0; j < resWordsList.size(); j++) {
                List<ResWord> topnWordsList = resWordsList.get(j);
                if (j > 0) {
                    System.out.print(" | ");
                }
                for (int k = 0; k < topnWordsList.size(); k++) {
                    if (k > 0) {
                        System.out.print(", ");
                    }
                    System.out.print(topnWordsList.get(k).word + ":" + topnWordsList.get(k).probability);
                }
            }
            System.out.println();
        }
        System.out.println("total " + resSentencesList.size() + " sentences");
    }

}
