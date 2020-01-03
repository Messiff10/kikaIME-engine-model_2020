import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EfficiencyAnalyzer {

    private String UNK_FLAG = "<unk>";

    private String UND_FLAG = "<und>";

    private Result result = null;

    private Set<String> rnnVocabSet = null;

    private Set<String> fullVocabSet = null;

    private Set<String> emojiSet = null;

    private Set<String> loadVocab(String fileName, String splitFlag) {
        try {
            Set<String> vocabSet = new HashSet<String>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
            String lineString;
            while ((lineString = reader.readLine()) != null) {
                String[] lineStrings = lineString.split(splitFlag);
                if (lineStrings.length == 2) {
                    String word = lineStrings[0].trim();
                    vocabSet.add(word);
                } else {
                    System.out.println("split vocab file error : " + lineString);
                }
            }
            System.out.println("vocab num = " + vocabSet.size());
            return vocabSet;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void parseResultPc(String resFileName) {
        result = new Result();
        result.parseResultPc(resFileName, false);
        System.out.println("total " + result.resSentencesList.size() + " sentences");
    }

    private void parseResultAndroid(String resFileName) {
        result = new Result();
        result.parseResultAndroid(resFileName);
        System.out.println("total " + result.resSentencesList.size() + " sentences");
    }

    private boolean endOfEmoji(String context) {
        String[] words = context.split("\\s+");
        if (words.length > 0) {
            String lastWord = words[words.length - 1];
            if (emojiSet.contains(lastWord)) {
                return true;
            }
        }
        return false;
    }

    private void analyze(int topn) {
        int sumInputLetterNum = 0;
        int sumEffectiveLetterNum = 0;

        int sumWordInputLetterNum = 0;
        int sumWordEffectiveLetterNum = 0;

        int sumCorrectNum = 0;
        int sumWordOutputNum = 0;
        int sumEmojiOutputNum = 0;
        int sumCorrectWordNum = 0;
        int sumCorrectEmojiNum = 0;

        int sumInputSameNum = 0;
        int sumInputSameWrongNum = 0;

        int sumOutputUnk = 0;
        int sumOutputUnd = 0;
        int sumResUnk = 0;
        int sumResUnd = 0;
        int sumCorrectUnk = 0;
        int sumCorrectUnd = 0;
        int sumOutputUndResUnk = 0;

        int sumEmoji = 0;
        int sumWordToEmoji = 0;
        int sumCorrectWordToEmoji = 0;

        int sumOutputWord = 0;
        int sumCorrectLmWord = 0;
        int sumResNotEmoji = 0;

        for (Result.ResSentence resSentence : result.resSentencesList) {
            List<List<Result.ResWord>> resWordsList = resSentence.resWordsList;
            int inputLetterNum = -1;
            boolean isCorrect = false;
            boolean isEmoji = false;
            boolean isLmCorrect = false;
            for (int letterNum = 0; letterNum < resWordsList.size(); letterNum ++) {
                List<Result.ResWord> topnWordsList = resWordsList.get(letterNum);
                Set<String> resSet = new HashSet<String>();
                for (int i = 0; i < Math.min(topn, topnWordsList.size()); i++) {
                    String word = topnWordsList.get(i).word;
                    String wordLetter = resSentence.inputLetters.substring(0, letterNum);
                    if (emojiSet.contains(word) && letterNum == 0) {
                        isEmoji = true;
                    }
                    if (word.equals(UNK_FLAG)) {
                        word = wordLetter;
                    }
                    if (word.equals(UND_FLAG)) {
                        if (fullVocabSet.contains(wordLetter)) {
                            word = wordLetter;
                        }
                    }
                    resSet.add(word);
                }
                if (resSet.contains(resSentence.outputWord)) {
                    if (inputLetterNum == -1) {
                        inputLetterNum = letterNum;
                    }
                    if (letterNum == resWordsList.size() - 1) {
                        isCorrect = true;
                    }
                    if (letterNum == 0) {
                        isLmCorrect = true;
                    }
                }
            }

            if (isEmoji) {
                sumEmoji ++;
            }

            int effectiveLetterNum = resSentence.outputWord.length();
            if (inputLetterNum == -1) {
                inputLetterNum = resSentence.inputLetters.length();
                effectiveLetterNum = 0;
            }

            sumInputLetterNum += inputLetterNum;
            sumEffectiveLetterNum += effectiveLetterNum;
            if (emojiSet.contains(resSentence.outputWord)) {
                sumEmojiOutputNum ++;
            } else {
                sumWordOutputNum ++;
                sumWordInputLetterNum += inputLetterNum;
                sumWordEffectiveLetterNum += effectiveLetterNum;
            }
            if (resSentence.inputLetters.equals(resSentence.outputWord)) {
                sumInputSameNum ++;
                if (!isCorrect) {
                    sumInputSameWrongNum ++;
                }
            }
            if (isCorrect) {
                sumCorrectNum ++;
                if (emojiSet.contains(resSentence.outputWord)) {
                    sumCorrectEmojiNum ++;
                } else {
                    sumCorrectWordNum ++;
                }
            }

            boolean isUnk = false;
            boolean isUnd = false;
            List<Result.ResWord> topnWordsList = resWordsList.get(resWordsList.size() - 1);
            for (int i = 0; i < Math.min(topn, topnWordsList.size()); i++) {
                String word = topnWordsList.get(i).word;
                if (word.equals(UNK_FLAG)) {
                    isUnk = true;
                    sumResUnk ++;
                }
                if (word.equals(UND_FLAG)) {
                    isUnd = true;
                    sumResUnd ++;
                }
            }

            if (!fullVocabSet.contains(resSentence.outputWord)) {
                sumOutputUnk ++;
                if (isUnk) {
                    sumCorrectUnk ++;
                }
            } else {
                if (!rnnVocabSet.contains(resSentence.outputWord)) {
                    sumOutputUnd ++;
                    if (isUnd) {
                        sumCorrectUnd ++;
                    } else if (isUnk) {
                        sumOutputUndResUnk ++;
                    }
                }
            }

            if (!endOfEmoji(resSentence.inputContexts) && emojiSet.contains(resSentence.outputWord)) {
                sumWordToEmoji ++;
//                System.out.println(resSentence.inputContexts + " | " + resSentence.outputWord + " | " + resSentence.resWordsList.toString());
                if (isCorrect) {
//                    System.out.println(resSentence.inputContexts + " | " + resSentence.outputWord + " | " + resSentence.resWordsList.toString());
                    sumCorrectWordToEmoji ++;
                }
            }

            if (resSentence.inputContexts.length() > 0 && !emojiSet.contains(resSentence.outputWord)) {
                sumOutputWord ++;
                if (isLmCorrect) {
                    sumCorrectLmWord ++;
                }
                if (!isEmoji) {
                    sumResNotEmoji ++;
                }
            }
        }
        System.out.println("top " + topn + " input efficiency = " + (double)sumEffectiveLetterNum / sumInputLetterNum +
                ", accuracy = " + (double)sumCorrectNum / result.resSentencesList.size());
        System.out.println("top " + topn + " word input efficiency = " + (double)sumWordEffectiveLetterNum / sumWordInputLetterNum +
                ", accuracy = " + (double)sumCorrectWordNum / sumWordOutputNum +
                ", same wrong rate = " + (double)sumInputSameWrongNum / sumInputSameNum);
        System.out.println("top " + topn + " emoji popup rate = " + (double)sumEmoji / result.resSentencesList.size() +
                ", recall = " + (double)sumCorrectEmojiNum / sumEmojiOutputNum +
                ", word to emoji recall = " + (double)sumCorrectWordToEmoji / sumWordToEmoji);
        System.out.println("top " + topn + " language model word recall = " + (double)sumCorrectLmWord / sumOutputWord +
                ", not emoji correct rate = " + (double)sumResNotEmoji / sumOutputWord);
        System.out.println("top " + topn + " unk accuracy = " + (double)sumCorrectUnk / sumResUnk + ", unk recall = " +
                (double)sumCorrectUnk / sumOutputUnk);
        System.out.println("top " + topn + " und accuracy = " + (double)sumCorrectUnd / sumResUnd + ", und recall = " +
                (double)sumCorrectUnd / sumOutputUnd + ", und unk recall = " + (double)sumOutputUndResUnk / sumOutputUnd);
    }

    public void analyzePc(String resFile, String rnnVocabFile, String fullVocabFile, String emojiFile) {
        rnnVocabSet = loadVocab(rnnVocabFile, "##");
        fullVocabSet = loadVocab(fullVocabFile, "[\\t]+");
        emojiSet = loadVocab(emojiFile, "[\\t]+");
        parseResultPc(resFile);
        System.out.println();
        analyze(1);
        System.out.println();
        analyze(3);
    }

}
