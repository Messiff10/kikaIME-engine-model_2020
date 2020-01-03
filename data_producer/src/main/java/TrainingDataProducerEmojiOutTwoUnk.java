import utilities.SumMap;

import java.io.*;
import java.util.*;

/**
 * Created by gaoxin on 17-7-10.
 * modified by lizhen on 18-1-10.
 */
public class TrainingDataProducerEmojiOutTwoUnk {
	
	private static final String WORD_REGEX = "[a-zA-Z']+";
	
	private static final String NUM_REGEX = "[+-]*[0-9]+.*[0-9]*";
	
	private static final String PUN_REREX = "[^a-zA-Z0-9']";
	
	private static final String SPLIT_FLAG = "##";
	
	private static final String PAD_FLAG = "_PAD";
	
	private static final String IN_EOS_FLAG = "<eos>";
	
	private static final String OUT_EOS_FLAG = "<eos>";
	
	private static final String NUM_FLAG = "<num>";
	
	private static final String PUNCTUATION_FLAG = "<pun>";
	
	private static final String EMOJI_FLAG = "<emoji>";
	
	private static final String UNKNOWN_FLAG = "<unk>";
	
	private static final String UNKNOWN_INDICT_FLAG = "<und>";
	
	private static final String KEY_START_FLAG = "<start>";
	
	private static final String NOT_PHRASE_FLAG = "<unp>";
	
	private static final int PAD_ID = 0;
	
	private static final int IN_EOS_ID = 1;
	
	private static final int UNKNOWN_WORD_ID = 2;
	
	private static final int NUM_ID = 3;
	
	private static final int PUNCTUATION_ID = 4;
	
	private static final int EMOJI_ID = 5;
	
	private static final int UNKNOWN_LETTER_ID = 0;
	
	private static final int KEY_START_ID = 1;
	
	private static final int OUT_EOS_ID = 0;
	
	private static final int UNKNOWN_OUT_ID = 1;
	
	private static final int UNKNOWN_OUT_INDICT_ID = 2;
	
	private static final int NOT_PHRASE_ID = 1;
	
	private static Map<String, Double> smallWordsDictMap = null;
	
	private static Map<String, Double> phraseDictMap = null;
	
	private static Set<String> emojisSet = null;
	
	private static Set<String> bigWordsSet = null;
	
	private static Set<String> dictLettersSet = null;
	
	private static Set<String> dataLettersSet = null;
	
	private static Set<String> dataWordsSet = null;
	
	private static Set<String> dataOutWordSet = null;
	
	private static Map<String, Integer> wordIdMap = null;
	
	private static Map<String, Integer> letterIdMap = null;
	
	private static Map<String, Integer> outWordIdMap = null;
	
	private static Map<String, Integer> phraseIdMap = null;
	
	private static SumMap<String> wordsCountMapFromData = null;
	
	private static SumMap<String> phraseCountMapFromData = null;
	
	private static int lineNum =0;
	
	private static void loadEmojisSet(String emojiFile) {
		try {
			emojisSet = new HashSet<>();
			String tempString;
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(emojiFile), "UTF-8"));
			while ((tempString = reader.readLine()) != null) {
				String[] tempStrings = tempString.split("\\s+");
				if (tempStrings.length == 2) {
					String emoji = tempStrings[0];
					emojisSet.add(emoji);
				} else {
					System.out.println("split emojis file error:" + tempString);
				}
			}
			reader.close();
			System.out.println("emojisSet size = " + emojisSet.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static boolean isWordOrEmoji(String word){
		if ((word.length() > 0 && word.matches(WORD_REGEX)) || emojisSet.contains(word)) {
			return true;
		}
		else{
			return false;
		}
	}
	
	private static boolean isWord(String word){
		if (word.length() > 0 && word.matches(WORD_REGEX)) {
			return true;
		}
		else{
			return false;
		}
	}
	
	private static void calculateWordsAndPhraseFrequencyOneLine(String lineString) {
		String[] lineStrings = lineString.split("\\t+");
		if (lineStrings.length == 3) {
			String inStringWords = lineStrings[0].trim();
			String outString = lineStrings[2].trim();
			String lineWords = inStringWords + " " + outString;
			String[] inStringWordsStrings = lineWords.split("\\s+");
			
			for (String word : inStringWordsStrings) {
				if (isWordOrEmoji(word)) {
					wordsCountMapFromData.add(word, 1.0);
				}
			}
			
			for (int i = 0; i < inStringWordsStrings.length; i ++){
				if (i + 1 < inStringWordsStrings.length){
					if (isWord(inStringWordsStrings[i]) && isWord(inStringWordsStrings[i+1])) {
						String phrase2 = inStringWordsStrings[i] + " " + inStringWordsStrings[i+1];
						phraseCountMapFromData.add(phrase2, 1.0);
					}
				}
				if (i + 2 < inStringWordsStrings.length){
					if (isWord(inStringWordsStrings[i]) && isWord(inStringWordsStrings[i+1]) && isWord(inStringWordsStrings[i+2])) {
						String phrase3 = inStringWordsStrings[i] + " " + inStringWordsStrings[i+1] + " " + inStringWordsStrings[i+2];
						phraseCountMapFromData.add(phrase3, 1.0);
					}
				}
			}
			
		} else {
			System.out.println("split error : " + lineString);
		}
	}
	
	private static void calculateWordsFrequencyAllData(String dataPathIn) {
		try {
			File rootPathFile = new File(dataPathIn);
			File[] dirsArray = rootPathFile.listFiles();
			wordsCountMapFromData = new SumMap<>();
			phraseCountMapFromData = new SumMap<>();
			for (File dir : dirsArray) {
				if (dir.getName().equals(".DS_Store")) {
					continue;
				}
				System.out.println(dir.getAbsolutePath());
				File subPathFile = new File(dir.getAbsolutePath());
				File[] filesArray = subPathFile.listFiles();
				String lastLine = null;
				String currentLine = null;
				for (File file : filesArray) {
					if (file.getName().matches("part.+")) {
						String filePath =file.getAbsoluteFile().toString();
						BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"));
						while ((currentLine = reader.readLine()) != null) {
							if (currentLine.startsWith("\t") && lastLine != null) {
								lineNum ++;
								calculateWordsAndPhraseFrequencyOneLine(lastLine);
							}
							lastLine = currentLine;
						}
						if (lastLine != null) {
							calculateWordsAndPhraseFrequencyOneLine(lastLine);
						}
						reader.close();
					}
				}
			}
			System.out.println("wordsCountMapFromData size = " + wordsCountMapFromData.size());
//			System.out.println("wordsCountMapFromData = " + wordsCountMapFromData.sort().toString());
			System.out.println("lineNum = " + lineNum);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void combineWordsDict(String wordsDictFile, int wordsNum, int phraseNum) {
		try {
			SumMap<String> wordsCountMapFromVocab = new SumMap<>();
			String tempString;
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(wordsDictFile), "UTF-8"));
			while ((tempString = reader.readLine()) != null) {
				String[] tempStrings = tempString.split("\\s+");
				if (tempStrings.length == 2) {
					String word = tempStrings[0];
					String countString = tempStrings[1];
					int count = Integer.parseInt(countString);
					wordsCountMapFromVocab.add(word, count);
				} else {
					System.out.println("split error:" + tempString);
				}
			}
			reader.close();
			System.out.println("wordsCountMapFromVocab size = " + wordsCountMapFromVocab.size());
			
			smallWordsDictMap = new LinkedHashMap<>();
			dictLettersSet = new HashSet<>();
			bigWordsSet = new HashSet<>();
			phraseDictMap = new LinkedHashMap<>();
			for (String word : wordsCountMapFromData.sort().keySet()) {
				if (wordsCountMapFromVocab.keySet().contains(word)) {
					if (wordsNum == 0 || smallWordsDictMap.size() < wordsNum) {
						smallWordsDictMap.put(word, wordsCountMapFromData.get(word));
						if (!emojisSet.contains(word)) {
							for (int i = 0; i < word.length(); i++) {
								dictLettersSet.add(String.valueOf(word.charAt(i)));
							}
						}
					}
				}
			}
			
			for (String phrase : phraseCountMapFromData.sort().keySet()){
				String[] word_from_phrase = phrase.split("\\s+");
				if (word_from_phrase.length == 3){
					String phrase2 = word_from_phrase[0] + " " + word_from_phrase[1];
					phraseCountMapFromData.add(phrase2, -phraseCountMapFromData.get(phrase));
				}
			}
			
			for (String phrase : phraseCountMapFromData.sort().keySet()){
				boolean phrase_to_save = true;
				String[] word_from_phrase = phrase.split("\\s+");
				for (String word : word_from_phrase){
					if (!smallWordsDictMap.containsKey(word)){
						phrase_to_save = false;
						break;
					}
				}
				if (phrase_to_save == true){
					if (phraseDictMap.size() < phraseNum){
						phraseDictMap.put(phrase, phraseCountMapFromData.get(phrase));
					}
				}
			}
			
			
			for (String word : wordsCountMapFromVocab.sort().keySet()) {
				if (!smallWordsDictMap.containsKey(word)) {
					bigWordsSet.add(word);
				}
			}
			
			System.out.println("smallWordsDictMap size = " + smallWordsDictMap.size());
//			System.out.println("smallWordsDictMap = " + smallWordsDictMap.toString());
			System.out.println("bigWordsSet size = " + bigWordsSet.size());
			System.out.println("dictLettersSet size = " + dictLettersSet.size());
//			System.out.println("dictLettersSet = " + dictLettersSet.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String convertInWords(String words, double wordRateThreshold) {
		
		String[] wordsStrings = words.split("\t");
		int wordNum = 0;
		int unknownWordNum = 0;
		String convertedWords = "";
		for (String word : wordsStrings) {
			String wordConverted = "";
			if (smallWordsDictMap.containsKey(word)) {
				wordConverted = word;
				wordNum ++;
			} else if (emojisSet.contains(word)) {
				wordConverted = EMOJI_FLAG;
				wordNum ++;
			} else if (word.matches(NUM_REGEX))  {
				wordConverted = NUM_FLAG;
				unknownWordNum ++;
			} else if (word.matches(PUN_REREX)) {
				wordConverted = PUNCTUATION_FLAG;
				unknownWordNum ++;
			} else {
				wordConverted = UNKNOWN_FLAG;
				unknownWordNum ++;
			}
			convertedWords = convertedWords + " " + wordConverted;
		}
		convertedWords = convertedWords.trim();
		float wordRate = (float)wordNum / (wordNum + unknownWordNum);
		if (wordRate >= wordRateThreshold) {
			return convertedWords;
		} else {
			return null;
		}
	}
	
	private static String convertInIds(String[] inWords, Map<String, Integer> inWordsMap) {
		ArrayList<String> inIdsArray = new ArrayList<String>();
		if (inWords.length >= 2){
			inIdsArray.add(inWordsMap.get(IN_EOS_FLAG).toString());
			for (int i = 0; i < inWords.length; i++) {
				String inWord = inWords[i];
				if (inWordsMap.containsKey(inWord)) {
					inIdsArray.add(inWordsMap.get(inWord).toString());
				} else if (emojisSet.contains(inWord)) {
					inIdsArray.add(inWordsMap.get(EMOJI_FLAG).toString());
				} else if (inWord.matches(NUM_REGEX))  {
					inIdsArray.add(inWordsMap.get(NUM_FLAG).toString());
				} else if (inWord.matches(PUN_REREX)) {
					inIdsArray.add(inWordsMap.get(PUNCTUATION_FLAG).toString());
				} else {
					inIdsArray.add(inWordsMap.get(UNKNOWN_FLAG).toString());
				}
			}
			return String.join(" ", inIdsArray);
		}
		else {
			return null;
		}
		
	}
	
	private static String convertOutWords(String words, double wordRateThreshold) {
		
		String[] wordsStrings = words.split("\t");
		int wordNum = 0;
		int unknownWordNum = 0;
		String convertedWords = "";
		
		for (String word : wordsStrings) {
			String wordConverted = "";
			if (smallWordsDictMap.containsKey(word)) {
				wordConverted = word;
				wordNum ++;
			} else if (bigWordsSet.contains(word) && !emojisSet.contains(word)) {
				wordConverted = UNKNOWN_INDICT_FLAG;
				wordNum ++;
			} else {
				wordConverted = UNKNOWN_FLAG;
				unknownWordNum ++;
			}
			convertedWords = convertedWords + " " + wordConverted;
		}
		convertedWords = convertedWords.trim();
		float wordRate = (float)wordNum / (wordNum + unknownWordNum);
		if (wordRate >= wordRateThreshold) {
			return convertedWords;
		} else {
			return null;
		}
	}
	
	private static String convertOutIds(String[] outWords, Map<String, Integer> outWordsMap) {
		
		ArrayList<String> outIdsArray = new ArrayList<String>();
		if (outWords.length >= 2){
			outIdsArray.add(outWordsMap.get(OUT_EOS_FLAG).toString());
			for (int i = 0; i < outWords.length; i++) {
				String outWord = outWords[i];
				if (outWordsMap.containsKey(outWord)) {
					outIdsArray.add(outWordsMap.get(outWord).toString());
				} else if (bigWordsSet.contains(outWord) && !emojisSet.contains(outWord))  {
					outIdsArray.add(outWordsMap.get(UNKNOWN_INDICT_FLAG).toString());
				} else {
					outIdsArray.add(outWordsMap.get(UNKNOWN_FLAG).toString());
				}
			}
			return String.join(" ", outIdsArray);
		}
		else {
			return null;
		}
	}
	
	private static String convertLetters(String inLetters) {
		
		String[] lettersStrings = inLetters.split("\t");
		ArrayList<String> lettersList = new ArrayList<String>();
		for (String lettersString : lettersStrings){
			if (lettersString.length() > 0) {
				String convertedLetters = "";
				String[] letters = lettersString.split("\\s+");
				for (String letter : letters) {
					if (dictLettersSet.contains(letter)) {
						convertedLetters = convertedLetters + " " + letter;
					} else {
						convertedLetters = convertedLetters + " " + UNKNOWN_FLAG;
					}
				}
				lettersList.add(convertedLetters.trim());
				
			} else {
				lettersList.add("");
			}
		}
		
		return String.join("\t", lettersList) ;
	}
	
	private static String convertLettersIds(String[] letters, Map<String, Integer> lettersMap) {
		
		ArrayList<String> lettersIdsArray = new ArrayList<String>();
		if (letters.length>=2){
			String eosStartId = lettersMap.get(KEY_START_FLAG).toString();
			lettersIdsArray.add(eosStartId.trim());
			for (String letterString : letters) {
				String letterId = lettersMap.get(KEY_START_FLAG).toString();
				if (letterString.trim().length() != 0){
					String[] letterList = letterString.split("\\s+");
					if (letterList.length > 0) {
						for (String letter : letterList) {
							
							if (lettersMap.containsKey(letter)) {
								letterId = letterId + " " + lettersMap.get(letter).toString();
							} else {
								letterId = letterId + " " + lettersMap.get(UNKNOWN_FLAG).toString();
							}
						}
					}
				}
				lettersIdsArray.add(letterId.trim());
			}
			return String.join("#",lettersIdsArray);
			
		}
		else{
			return null;
		}
		
	}
	
	private static String convertPhraseIds(String[] words, Map<String, Integer> phraseMap) {
		
		ArrayList<String> phraseList = new ArrayList<String>();
		if (words.length >= 2){
			phraseList.add(phraseMap.get(PAD_FLAG).toString());
			for (int i = 0; i < words.length; i ++){
				if (i + 1 < words.length){
					String phrase2 = words[i] + " " + words[i+1];
					if (phraseMap.containsKey(phrase2)){
						phraseList.add(phraseMap.get(phrase2).toString());
						continue;
					}
				}
				if (i + 2 < words.length){
					String phrase3 = words[i] + " " + words[i+1] + " " + words[i+2];
					if (phraseMap.containsKey(phrase3)){
						phraseList.add(phraseMap.get(phrase3).toString());
						continue;
					}
				}
				if (i + 1 < words.length){
					phraseList.add(phraseMap.get(NOT_PHRASE_FLAG).toString());
				}
				else{
					phraseList.add(phraseMap.get(PAD_FLAG).toString());
				}
			}
			return String.join(" ", phraseList);
		}
		else{
			return null;
		}
		
	}
	
	private static Map<String, String> convertLine(String lineString) {
		Map<String, String> map = new HashMap<String, String>();
		String[] lineSplit = lineString.split("\\t+");
		String word;
		String letter;
		if (lineSplit.length == 3) {
			String letterIn = lineSplit[1].trim();
			word = lineSplit[2].trim();
			int idxStart = letterIn.indexOf("<b>");
			int idxEnd = letterIn.lastIndexOf("</b>");
			if (idxStart >= 0 && idxEnd >= 0) {
				letter = letterIn.substring(idxStart + 3, idxEnd).trim();
			}
			else {
				System.out.println("letter split error: " + lineString);
				letter = null;
			}
		}
		else {
			System.out.println("line split error: " + lineString);
			word = null;
			letter = null;
		}
		map.put("word", word);
		map.put("letter", letter);
		return map;
	}
	
	private static void convertSentence(ArrayList<String> wordsArray, ArrayList<String> lettersArray, double rateThreshold,
										BufferedWriter writer, boolean isTrain) {
		try {
			if (isTrain){
				String words = String.join("\t", wordsArray);
				String letters = String.join("\t", lettersArray);
				
				String inWordsConverted = convertInWords(words, rateThreshold);
				String outWordsConverted = convertOutWords(words, rateThreshold);
				String lettersConverted = convertLetters(letters);
				
				if (inWordsConverted != null && outWordsConverted != null) {
					
					String[] inLetters = lettersConverted.split("\t");
					for (String inLetter : inLetters) {
						String[] letterString = inLetter.split("\\s+");
						for (String letter : letterString) {
							if (letter.length() > 0) {
								dataLettersSet.add(letter);
							}
						}
					}
					String[] inWords = inWordsConverted.split("\\s+");
					for (String inWord : inWords) {
						if (inWord.length() > 0) {
							dataWordsSet.add(inWord);
						}
					}
					String[] outWords = outWordsConverted.split("\\s+");
					for (String outWord : outWords) {
						if (outWord.length() > 0) {
							dataOutWordSet.add(outWord);
						}
					}
					writer.write(String.join("\t", lettersArray) + "|#|" + String.join("\t", wordsArray) + "\n");
				}
				
			}
			else {
				writer.write(String.join("\t", lettersArray) + "|#|" + String.join("\t", wordsArray) + "\n");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void convertData(String dataPathIn, double rateThreshold, int trainDataNum,
									int devDataNum, int testDataNum, String dataPathOut) {
		
		try {
			File rootPathFile = new File(dataPathIn);
			File[] dirsArray = rootPathFile.listFiles();
			double rateTrain = (double)trainDataNum / lineNum;
			double rateDev = (double)devDataNum / lineNum;
			double rateTest = (double)testDataNum / lineNum;
			
			File outPathFile = new File(dataPathOut);
			if (!outPathFile.exists()) {
				outPathFile.mkdirs();
			}
			
			
			BufferedWriter writerTrain = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPathOut
					+ "/train_data"), "UTF-8"));
			BufferedWriter writerDev = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPathOut
					+ "/dev_data"), "UTF-8"));
			BufferedWriter writerTest = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataPathOut
					+ "/test_data"), "UTF-8"));
			
			Random rand = new Random(0);
			String lineString;
			dataLettersSet = new HashSet<>();
			dataWordsSet = new HashSet<>();
			dataOutWordSet = new HashSet<>();
			
			for (File dir : dirsArray) {
				if (dir.getName().equals(".DS_Store")) {
					continue;
				}
				System.out.println(dir.getAbsolutePath());
				File subPathFile = new File(dir.getAbsolutePath());
				File[] filesArray = subPathFile.listFiles();
				for (File file : filesArray) {
					if (file.getName().matches("part.+")) {
						String filePath =file.getAbsoluteFile().toString();
						System.out.println(filePath);
						BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),
								"UTF-8"));
						lineString = reader.readLine();
						while (true) {
							if (lineString == null) {
								break;
							}
							if (lineString.startsWith("\t")) {
								ArrayList<String> letters=new ArrayList<String>();
								ArrayList<String> words=new ArrayList<String>();
								double randDouble = rand.nextDouble();
								if (randDouble < rateTrain) {
									while (true) {
										Map<String, String> wordLetterMap = convertLine(lineString);
										if (wordLetterMap.get("word") == null || wordLetterMap.get("letter") == null){
											break;
										}
										words.add(wordLetterMap.get("word"));
										letters.add(wordLetterMap.get("letter"));
										lineString = reader.readLine();
										if (lineString == null || lineString.startsWith("\t")) {
											convertSentence(words, letters, rateThreshold, writerTrain, true);
											break;
										}
									}
								} else if (randDouble < rateTrain + rateDev) {
									while (true) {
										Map<String, String> wordLetterMap = convertLine(lineString);
										if (wordLetterMap.get("word") == null || wordLetterMap.get("letter") == null){
											break;
										}
										words.add(wordLetterMap.get("word"));
										letters.add(wordLetterMap.get("letter"));
										lineString = reader.readLine();
										if (lineString == null || lineString.startsWith("\t")) {
											convertSentence(words, letters, rateThreshold, writerDev,false);
											break;
										}
									}
								} else if (randDouble < rateTrain + rateDev + rateTest) {
									while (true) {
										Map<String, String> wordLetterMap = convertLine(lineString);
										if (wordLetterMap.get("word") == null || wordLetterMap.get("letter") == null){
											break;
										}
										words.add(wordLetterMap.get("word"));
										letters.add(wordLetterMap.get("letter"));
										lineString = reader.readLine();
										if (lineString == null || lineString.startsWith("\t")) {
											convertSentence(words, letters, rateThreshold, writerTest,false);
											break;
										}
									}
								} else {
									while (true) {
										lineString = reader.readLine();
										if (lineString == null || lineString.startsWith("\t")) {
											break;
										}
									}
								}
								
							}
							else {
								lineString = reader.readLine();
							}
						}
						reader.close();
					}
				}
			}
			
			writerTest.close();
			writerTrain.close();
			writerDev.close();
			System.out.println("dataWordsSet size = " + dataWordsSet.size());
//			System.out.println("dataWordsSet = " + dataWordsSet.toString());
			System.out.println("dataLettersSet size = " + dataLettersSet.size());
//			System.out.println("dataLettersSet = " + dataLettersSet.toString());
			System.out.println("dataOutWordSet size = " + dataOutWordSet.size());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void makeVocabMap() {
		wordIdMap = new LinkedHashMap<>();
		letterIdMap = new LinkedHashMap<>();
		outWordIdMap = new LinkedHashMap<>();
		phraseIdMap = new LinkedHashMap<>();
		
		wordIdMap.put(PAD_FLAG, PAD_ID);
		wordIdMap.put(IN_EOS_FLAG, IN_EOS_ID);
		wordIdMap.put(UNKNOWN_FLAG, UNKNOWN_WORD_ID);
		wordIdMap.put(NUM_FLAG, NUM_ID);
		wordIdMap.put(PUNCTUATION_FLAG, PUNCTUATION_ID);
		wordIdMap.put(EMOJI_FLAG, EMOJI_ID);
		
		letterIdMap.put(UNKNOWN_FLAG, UNKNOWN_LETTER_ID);
		letterIdMap.put(KEY_START_FLAG, KEY_START_ID);
		
		outWordIdMap.put(OUT_EOS_FLAG, OUT_EOS_ID);
		outWordIdMap.put(UNKNOWN_FLAG, UNKNOWN_OUT_ID);
		outWordIdMap.put(UNKNOWN_INDICT_FLAG, UNKNOWN_OUT_INDICT_ID);
		
		phraseIdMap.put(PAD_FLAG, PAD_ID);
		phraseIdMap.put(NOT_PHRASE_FLAG, NOT_PHRASE_ID);
		
		int id = letterIdMap.size();
		List<String> lettersList = new ArrayList<>();
		for (String letter : dataLettersSet) {
			lettersList.add(letter);
		}
		Collections.sort(lettersList);
		for (String letter : lettersList) {
			if (!letterIdMap.containsKey(letter)) {
				letterIdMap.put(letter, id);
				id ++;
			}
		}
		System.out.println("letterIdMap size = " + letterIdMap.size());
//		System.out.println("letterIdMap = " + letterIdMap.toString());
		id = wordIdMap.size();
		for (String word : smallWordsDictMap.keySet()) {
			if (dataWordsSet.contains(word)) {
				if (!wordIdMap.containsKey(word)) {
					wordIdMap.put(word, id);
					id++;
				}
			}
		}
		System.out.println("wordIdMap size = " + wordIdMap.size());
//		System.out.println("wordIdMap = " + wordIdMap.toString());
		
		id = outWordIdMap.size();
		for (String word : smallWordsDictMap.keySet()) {
			if (dataOutWordSet.contains(word)) {
				outWordIdMap.put(word, id);
				id ++;
			}
		}
		
		id = phraseIdMap.size();
		for (String phrase : phraseDictMap.keySet()){
			boolean phrase_to_save = true;
			String[] word_from_phrase = phrase.split("\\s+");
			for (String word : word_from_phrase){
				if (!wordIdMap.containsKey(word) && !outWordIdMap.containsKey(word)){
					phrase_to_save = false;
					break;
				}
			}
			if (phrase_to_save == true){
				phraseIdMap.put(phrase, id);
				id ++;
			}
		}
		
		System.out.println("outWordIdMap size = " + outWordIdMap.size());
//		System.out.println("outWordIdMap = " + outWordIdMap.toString());
	}
	
	private static void saveMapToFile(Map<String, Integer> map, String filePath) {
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"));
			for (String key : map.keySet()) {
				writer.write(key + SPLIT_FLAG + map.get(key) + "\n");
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void saveVocabFiles(String dataPathOut) {
		makeVocabMap();
		saveMapToFile(wordIdMap, dataPathOut + "/vocab_in_words");
		saveMapToFile(letterIdMap, dataPathOut + "/vocab_in_letters");
		saveMapToFile(outWordIdMap, dataPathOut + "/vocab_out");
		saveMapToFile(phraseIdMap, dataPathOut + "/vocab_phrase");
	}
	
	private static void convertToIdsFile(Map<String, Integer> inWordsMap, Map<String, Integer> outWordsMap,
										 Map<String, Integer> lettersMap, Map<String, Integer> phraseMap,
										 String fileInPath, String fileOutPath, String fileOutPathLetters,
										 String fileOutPathPhrass) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileInPath), "UTF-8"));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOutPath), "UTF-8"));
			BufferedWriter writerLetters = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOutPathLetters), "UTF-8"));
			BufferedWriter writerPhrase = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOutPathPhrass), "UTF-8"));
			
			String lineString;
			while ((lineString = reader.readLine()) != null) {
				String[] line = lineString.split("\\|#\\|");
				String[] letterStrings = line[0].split("\t", -1);
				String[] wordStrings = line[1].split("\t");
				if (letterStrings.length != wordStrings.length){
					System.out.println("line error: " + lineString);
				}
				if (wordStrings.length >= 2) {
					String idsWordsIn = convertInIds(wordStrings, inWordsMap);
					String idsWordsOut = convertOutIds(wordStrings, outWordsMap);
					String idsLetters = convertLettersIds(letterStrings, lettersMap);
					String idsPhrase = convertPhraseIds(wordStrings, phraseMap);
					
					
					if (idsWordsIn != null && idsWordsOut != null && idsLetters != null && idsPhrase != null){
						writer.write(idsWordsIn.trim() + "#" + idsWordsOut.trim() + "\n");
						writerLetters.write(idsLetters.trim() + "\n");
						writerPhrase.write(idsPhrase.trim() + "\n");
					}
					
				}
			}
			reader.close();
			writer.close();
			writerLetters.close();
			writerPhrase.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void convertToIds(String dataPathIn, String dataPathOut) {
		convertToIdsFile(wordIdMap, outWordIdMap, letterIdMap, phraseIdMap, dataPathIn + "/train_data",
				dataPathOut + "/train_in_ids_lm", dataPathOut + "/train_in_ids_letters",
				dataPathOut + "/train_ids_phrase");
		convertToIdsFile(wordIdMap, outWordIdMap, letterIdMap, phraseIdMap, dataPathIn + "/dev_data",
				dataPathOut + "/dev_in_ids_lm", dataPathOut + "/dev_in_ids_letters",
				dataPathOut + "/dev_ids_phrase");
	}
	
	private static void saveWords(String wordsFile) {
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(wordsFile), "UTF-8"));
			for (String word : smallWordsDictMap.keySet()) {
				writer.write(word + "\n");
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] arg0) {

//        String wordsDictFile = "/home/pubsrv/data/lizhen/split_model/dl-tensorflow-dev/seq2word_word_letter_separated/data_producer/src/main/resources/words_dict/main_en_1_unigram_emoji";
//        String emojisFile = "/home/pubsrv/data/lizhen/split_model/dl-tensorflow-dev/seq2word_word_letter_separated/data_producer/src/main/resources/words_dict/emojis";
		
		String wordsDictFile = "/Users/lizhen/KikaCode/seq2word_word_letter_separated/dl-tensorflow-dev/seq2word_word_letter_separated_v2/data_producer/src/main/resources/words_dict/main_en_1_unigram_emoji";
		String emojisFile = "/Users/lizhen/KikaCode/seq2word_word_letter_separated/dl-tensorflow-dev/seq2word_word_letter_separated_v2/data_producer/src/main/resources/words_dict/emojis";
		
		int wordsNum = 20000;
		int phraseNum = 10000;
		String dataPathIn = "/Users/lizhen/KikaCode/seq2word_word_letter_separated/dl-tensorflow-dev/seq2word_word_letter_separated_v2/data_producer/src/main/resources/data_words_letters_dir_emoji/";
		
		//String dataPathIn = "/home/pubsrv/data/gaoxin/data/20161223_20170803_emojis_cleaned/";
		
		String dataPathOut = "training_data_sample_fixed_phrase_pad";
		double rateThreshold = 0.8;
		int trainDataNum = 5000;
		int devDataNum = 5000;
		int testDataNum = 5000;
		
		if (arg0.length == 10 && arg0[0].equals("convertDataWordsLetters")) {
			wordsDictFile = arg0[1];
			emojisFile = arg0[2];
			wordsNum = Integer.valueOf(arg0[3]);
			dataPathIn = arg0[4];
			dataPathOut = arg0[5];
			rateThreshold = Double.parseDouble(arg0[6]);
			trainDataNum = Integer.valueOf(arg0[7]);
			devDataNum = Integer.valueOf(arg0[8]);
			testDataNum = Integer.valueOf(arg0[9]);
			loadEmojisSet(emojisFile);
			calculateWordsFrequencyAllData(dataPathIn);
			combineWordsDict(wordsDictFile, wordsNum, phraseNum);
			convertData(dataPathIn, rateThreshold, trainDataNum, devDataNum, testDataNum, dataPathOut);
			saveVocabFiles(dataPathOut);
			convertToIds(dataPathOut, dataPathOut);
			System.out.println("input id size = " + (wordIdMap.size() + letterIdMap.size()));
			System.out.println("output id size = " + outWordIdMap.size());
		}
		
		if (arg0.length == 0) {
			loadEmojisSet(emojisFile);
			calculateWordsFrequencyAllData(dataPathIn);
			combineWordsDict(wordsDictFile, wordsNum, phraseNum);
			convertData(dataPathIn, rateThreshold, trainDataNum, devDataNum, testDataNum, dataPathOut);
			saveVocabFiles(dataPathOut);
			convertToIds(dataPathOut, dataPathOut);
			System.out.println("input id size = " + (wordIdMap.size() + letterIdMap.size()));
			System.out.println("output id size = " + outWordIdMap.size());
		}
	}
}
