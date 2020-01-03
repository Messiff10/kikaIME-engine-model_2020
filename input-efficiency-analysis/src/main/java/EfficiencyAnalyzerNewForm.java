import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EfficiencyAnalyzerNewForm {

    private String UNK_FLAG = "<unk>";

    private String UND_FLAG = "<und>";

    private ResultNewForm result = null;

    private Set<String> rnnVocabSet = null;

    private Set<String> fullVocabSet = null;

    private Set<String> emojiSet = null;

    private Set<String> phraseSet = null;

    private Set<String> loadVocab(String fileName, String splitFlag) {
        try {
            Set<String> vocabSet = new HashSet<>();
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
        result = new ResultNewForm();
        result.parseResultPcNewForm(resFileName);
        System.out.println("total " + result.resSentencesList.size() + " sentences");
    }

    private boolean endOfEmoji(List<String> inputContextsList) {
        if (inputContextsList.size() > 0) {
            String lastWord = inputContextsList.get(inputContextsList.size() - 1);
            if (emojiSet.contains(lastWord)) {
                return true;
            }
        }
        return false;
    }

    private String getWord(List<String> letterList, int letterNum) {
        StringBuilder outWord = new StringBuilder();
        for (int i = 0; i < letterNum; i++) {
            outWord.append(letterList.get(i));
        }
        return outWord.toString();
    }

    private boolean isEmojis(String wordsIn) {
        String[] wordsStrings = wordsIn.split("\\s");
        for (String word : wordsStrings) {
            if (!emojiSet.contains(word)) {
                return false;
            }
        }
        return true;
    }

    private boolean isEquals(List<String> outputWordsList, String wordsIn) {

        String[] wordsStrings = wordsIn.split("\\s");
        if (outputWordsList.size() < wordsStrings.length) {
            return false;
        }
        for (int i = 0; i < wordsStrings.length; i++) {
            String outWord = outputWordsList.get(i);
            if (!rnnVocabSet.contains(outWord) && rnnVocabSet.contains(outWord.toLowerCase())){
                outWord = outWord.toLowerCase();
            }
            if (!wordsStrings[i].equals(outWord)) {
                return false;
            }
        }
        return true;
    }

    private String isContains(Set<String> resSet, List<String> outputWordsList) {
        String matchString = null;
        for (String res : resSet) {
            if (isEquals(outputWordsList, res)) {
                if (matchString == null || res.length() > matchString.length()) {
                    matchString = res;
                }
            }
        }
        return matchString;
    }

    private boolean isResContainsPhrase(Set<String> resSet) {
        for (String res : resSet) {
            if (res.split("\\s").length > 1) {
                return true;
                }
        }
        return false;
    }

    private boolean isContainsPhrase(List<String> outputWordsList) {
        if (outputWordsList.size() < 2){
            return false;
        }
        String outputMatchedPhrase = "";

        if (outputWordsList.size() == 2){
            outputMatchedPhrase = outputWordsList.get(0) + " " + outputWordsList.get(1);
        }

        if (outputWordsList.size() >= 3){
            outputMatchedPhrase = outputWordsList.get(0) + " " + outputWordsList.get(1) + " " +  outputWordsList.get(2);
        }

        if (phraseSet.contains(outputMatchedPhrase)){
            return true;
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
        int sumInputNotSameNum = 0;
        int sumInputNotSameWrongNum = 0;

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

        int sumOutputPhrase = 0;
        int sumCorrectOutputPhrase = 0;

        int sumResPhrase = 0;
        int sumCorrectResPhrase = 0;

        for (ResultNewForm.ResSentence resSentence : result.resSentencesList) {
            List<List<ResultNewForm.ResWord>> resWordsList = resSentence.resWordsList;
            int inputLetterNum = -1;
            int matchWordFinalNum = 0;
            boolean isCorrect = false;
            boolean isEmoji = false;
            boolean isLmCorrect = false;

            String inputLetters = getWord(resSentence.inputLettersList, resSentence.inputLettersList.size());
            String outputWord = resSentence.outputWordsList.get(0);
            if (!rnnVocabSet.contains(outputWord) && rnnVocabSet.contains(outputWord.toLowerCase())){
                outputWord = outputWord.toLowerCase();
            }
            String matchWordFinal = "";
            for (int letterNum = 0; letterNum < resWordsList.size(); letterNum++) {
                List<ResultNewForm.ResWord> topnWordsList = resWordsList.get(letterNum);
                Set<String> resSet = new HashSet<>();

                for (int i = 0; i < Math.min(topn, topnWordsList.size()); i++) {
                    String word = topnWordsList.get(i).word;
                    String wordLetter = getWord(resSentence.inputLettersList, letterNum);
                    if (isEmojis(word)) {
                        isEmoji = true;
                    }
//                    if (word.equals(UNK_FLAG)) {
//                        word = wordLetter;
//                    }
//                    if (word.equals(UND_FLAG)) {
//                        if (fullVocabSet.contains(wordLetter)) {
//                            word = wordLetter;
//                        }
//                    }
                    if (word.equals(UNK_FLAG) || word.equals(UND_FLAG)) {
                        word = wordLetter;
                    }
                    resSet.add(word);
                }
                String matchWordLetter = isContains(resSet, resSentence.outputWordsList);
                String [] top1WordOrPhrase = topnWordsList.get(0).word.split("\\s");
                String top1Word = top1WordOrPhrase[0];

                if (matchWordLetter != null) {
                    if (inputLetterNum == -1) {
                        inputLetterNum = letterNum;
                        matchWordFinal = matchWordLetter;
                    }
                    if (letterNum == resWordsList.size() - 1) {
                        isCorrect = true;
                    }
                    if (letterNum == 0) {
                        isLmCorrect = true;
                        matchWordFinalNum = matchWordLetter.split("\\s").length;
                        if (top1Word.equals(outputWord)){
                            if (isContainsPhrase(resSentence.outputWordsList)) {
                                sumOutputPhrase++;
                                if (matchWordFinalNum > 1) {
                                    sumCorrectOutputPhrase++;
                                }
                            }
                            if (isResContainsPhrase(resSet)) {
                                sumResPhrase++;
                                if (matchWordFinalNum > 1) {
                                    sumCorrectResPhrase++;
                                }
                            }
                        }
                    }
                }
            }

            if (isEmoji) {
                sumEmoji++;
            }

//            if (!isCorrect && (topn == 1)) {
//                System.out.println(resSentence.lineString);
//            }

//            if (matchWordFinal.length() > 0 && !matchWordFinal.equals(outputWord)) {
//                System.out.println(resSentence.lineString);
//            }

            int effectiveLetterNum = matchWordFinal.length();
            if (inputLetterNum == -1) {
                inputLetterNum = resSentence.inputLettersList.size();
                effectiveLetterNum = 0;
            }

            sumInputLetterNum += inputLetterNum;
            sumEffectiveLetterNum += effectiveLetterNum;
            if (emojiSet.contains(outputWord)) {
                sumEmojiOutputNum++;
            } else {
                sumWordOutputNum++;
                sumWordInputLetterNum += inputLetterNum;
                sumWordEffectiveLetterNum += effectiveLetterNum;
            }
            if (inputLetters.equals(outputWord)) {
                sumInputSameNum++;
                if (!isCorrect) {
                    sumInputSameWrongNum++;
                }
            }
            if (!inputLetters.equals(outputWord) && (!emojiSet.contains(outputWord))){
                sumInputNotSameNum++;
                if (!isCorrect){
                    sumInputNotSameWrongNum++;
//                    if (topn == 1) {
//                        System.out.println(outputWord + " " + inputLetters);
//                        System.out.println(resSentence.resWordsList);
//                    }

                }
            }
            if (isCorrect) {
                sumCorrectNum++;
                if (emojiSet.contains(outputWord)) {
                    sumCorrectEmojiNum++;
                } else {
                    sumCorrectWordNum++;
                }
            }

            boolean isUnk = false;
            boolean isUnd = false;
            List<ResultNewForm.ResWord> topnWordsList = resWordsList.get(resWordsList.size() - 1);
            for (int i = 0; i < Math.min(topn, topnWordsList.size()); i++) {
                String word = topnWordsList.get(i).word;
                if (word.equals(UNK_FLAG)) {
                    isUnk = true;
                    sumResUnk++;
                }
                if (word.equals(UND_FLAG)) {
                    isUnd = true;
                    sumResUnd++;
                }
            }

            if (!fullVocabSet.contains(outputWord)) {
                sumOutputUnk++;
                if (isUnk) {
                    sumCorrectUnk++;
                }
            } else {
                if (!rnnVocabSet.contains(outputWord)) {
                    sumOutputUnd++;
                    if (isUnd) {
                        sumCorrectUnd++;
                    } else if (isUnk) {
                        sumOutputUndResUnk++;
                    }
                }
            }

            if (!endOfEmoji(resSentence.inputContextsList) && emojiSet.contains(outputWord)) {
                sumWordToEmoji++;
                if (isCorrect) {
                    sumCorrectWordToEmoji++;
                }
            }

            if (resSentence.inputContextsList.size() > 0 && !emojiSet.contains(outputWord)) {
                sumOutputWord++;
                if (isLmCorrect) {
                    sumCorrectLmWord++;
//                    sumCorrectLmWord = sumCorrectLmWord + matchWordFinalNum;
                }
                if (!isEmoji) {
                    sumResNotEmoji++;
                }
            }
        }
        System.out.println("top " + topn + " input efficiency = " + (double) sumEffectiveLetterNum / sumInputLetterNum +
                ", accuracy = " + (double) sumCorrectNum / result.resSentencesList.size());
        System.out.println("top " + topn + " word input efficiency = " + (double) sumWordEffectiveLetterNum / sumWordInputLetterNum +
                ", accuracy = " + (double) sumCorrectWordNum / sumWordOutputNum +
                ", same wrong rate = " + (double) sumInputSameWrongNum / sumInputSameNum +
                ", not same wrong rate = " + (double) sumInputNotSameWrongNum / sumInputNotSameNum);
        System.out.println("top " + topn + " emoji popup rate = " + (double) sumEmoji / result.resSentencesList.size() +
                ", recall = " + (double) sumCorrectEmojiNum / sumEmojiOutputNum +
                ", word to emoji recall = " + (double) sumCorrectWordToEmoji / sumWordToEmoji);
        System.out.println("top " + topn + " language model word recall = " + (double) sumCorrectLmWord / sumOutputWord +
                ", not emoji correct rate = " + (double) sumResNotEmoji / sumOutputWord);
        System.out.println("top " + topn + " unk accuracy = " + (double) sumCorrectUnk / sumResUnk + ", unk recall = " +
                (double) sumCorrectUnk / sumOutputUnk);
        System.out.println("top " + topn + " und accuracy = " + (double) sumCorrectUnd / sumResUnd + ", und recall = " +
                (double) sumCorrectUnd / sumOutputUnd + ", und unk recall = " + (double) sumOutputUndResUnk / sumOutputUnd);
        System.out.println("top " + topn + " phrase recall = " + (double) sumCorrectOutputPhrase / sumOutputPhrase);
        System.out.println("top " + topn + " phrase accuracy = " + (double) sumCorrectResPhrase / sumResPhrase);
    }

    public void analyzePc(String resFile, String rnnVocabFile, String fullVocabFile, String emojiFile, String phraseFile) {
        rnnVocabSet = loadVocab(rnnVocabFile, "##");
        fullVocabSet = loadVocab(fullVocabFile, "[\\t]+");
        emojiSet = loadVocab(emojiFile, "[\\t]+");
        phraseSet = loadVocab(phraseFile, "##");
        parseResultPc(resFile);
        System.out.println();
        analyze(1);
        System.out.println();
        analyze(3);
    }
}
