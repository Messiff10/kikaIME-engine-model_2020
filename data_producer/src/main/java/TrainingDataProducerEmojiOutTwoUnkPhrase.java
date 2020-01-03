

import utilities.SumMap;

import java.io.*;
import java.util.*;

/**
 * Created by gaoxin on 17-7-10.
 * modified by lizhen on 18-1-10.
 * modified by zzf on 19-9-10.
 * 解决短语问题
 */
public class TrainingDataProducerEmojiOutTwoUnkPhrase {
//
	//en
	private static final String WORD_REGEX = "[a-zA-Z']+";

	private static final String NUM_REGEX = "[+-]*[0-9]+.*[0-9]*";

	private static final String PUN_REREX = "[^a-zA-Z0-9']";
	
	//es_US
//	private static final String WORD_REGEX = "[qwertyuiopasdfghjklñzxcvbnmQWERTYUIOPASDFGHJKLÑZXCVBNM']+";
//	private static final String NUM_REGEX = "[+-]*[0-9]+.*[0-9]*";
//	private static final String PUN_REREX = "[^qwertyuiopasdfghjklñzxcvbnmQWERTYUIOPASDFGHJKLÑZXCVBNM0-9']";
	
	//sv
//	private static final String WORD_REGEX = "[abcdefghijklmnopqrstuvwxyzåäöABCDEFGHIJKLMNOPQRSTUVWXYZAÅÄOÖ']+";
//	private static final String NUM_REGEX = "[+-]*[0-9]+.*[0-9]*";
//	private static final String PUN_REREX = "[^abcdefghijklmnopqrstuvwxyzåäöABCDEFGHIJKLMNOPQRSTUVWXYZAÅÄOÖ0-9']";
//	//de
//	private static final String WORD_REGEX = "[qwertzuiopüasdfghjklöäyxcvbnmßQWERTZUIOPÜASDFGHJKLÖÄYXCVBNMẞ']+";
//	private static final String NUM_REGEX = "[+-]*[0-9]+.*[0-9]*";
//	private static final String PUN_REREX = "[^qwertzuiopüasdfghjklöäyxcvbnmßQWERTZUIOPÜASDFGHJKLÖÄYXCVBNMẞ0-9']";
//	//ms_MY
//	private static final String WORD_REGEX = "[qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM']+";
//	private static final String NUM_REGEX = "[+-]*[0-9]+.*[0-9]*";
//	private static final String PUN_REREX = "[^qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM0-9']";
	//nb/da
//	private static final String WORD_REGEX = "[qwertyuiopåasdfghjkløæzxcvbnmQWERTYUIOPÅASDFGHJKLØÆZXCVBNM']+";
//	private static final String NUM_REGEX = "[+-]*[0-9]+.*[0-9]*";
//	private static final String PUN_REREX = "[^qwertyuiopåasdfghjkløæzxcvbnmQWERTYUIOPÅASDFGHJKLØÆZXCVBNM0-9']";
	//th
//	private static final String WORD_REGEX = "[\\u0E01\\u0E02\\u0E03\\u0E04\\u0E05\\u0E06\\u0E07\\u0E08\\u0E09" +
//			"\\u0E0A\\u0E0B\\u0E0C\\u0E0D\\u0E0E\\u0E0F\\u0E10\\u0E11\\u0E12\\u0E13\\u0E14\\u0E15\\u0E16\\u0E17" +
//			"\\u0E18\\u0E19\\u0E1A\\u0E1B\\u0E1C\\u0E1D\\u0E1E\\u0E1F\\u0E20\\u0E21\\u0E22\\u0E23\\u0E24\\u0E25" +
//			"\\u0E26\\u0E27\\u0E28\\u0E29\\u0E2A\\u0E2B\\u0E2C\\u0E2D\\u0E2E\\u0E2F\\u0E30\\u0E31\\u0E32\\u0E33" +
//			"\\u0E34\\u0E35\\u0E36\\u0E37\\u0E38\\u0E39\\u0E3A\\u0E3F\\u0E40\\u0E41\\u0E42\\u0E43\\u0E44\\u0E45" +
//			"\\u0E46\\u0E47\\u0E48\\u0E49\\u0E4A\\u0E4B\\u0E4C\\u0E4D\\u0E4E\\u0E4F\\u0E50\\u0E51\\u0E52\\u0E53" +
//			"\\u0E54\\u0E55\\u0E56\\u0E57\\u0E58\\u0E59\\u0E5A\\u0E5B']+";
//	private static final String NUM_REGEX = "[+-]*[0-9]+.*[0-9]*";
//	private static final String PUN_REREX = "[^\\u0E01\\u0E02\\u0E03\\u0E04\\u0E05\\u0E06\\u0E07\\u0E08\\u0E09" +
//			"\\u0E0A\\u0E0B\\u0E0C\\u0E0D\\u0E0E\\u0E0F\\u0E10\\u0E11\\u0E12\\u0E13\\u0E14\\u0E15\\u0E16\\u0E17" +
//			"\\u0E18\\u0E19\\u0E1A\\u0E1B\\u0E1C\\u0E1D\\u0E1E\\u0E1F\\u0E20\\u0E21\\u0E22\\u0E23\\u0E24\\u0E25" +
//			"\\u0E26\\u0E27\\u0E28\\u0E29\\u0E2A\\u0E2B\\u0E2C\\u0E2D\\u0E2E\\u0E2F\\u0E30\\u0E31\\u0E32\\u0E33" +
//			"\\u0E34\\u0E35\\u0E36\\u0E37\\u0E38\\u0E39\\u0E3A\\u0E3F\\u0E40\\u0E41\\u0E42\\u0E43\\u0E44\\u0E45" +
//			"\\u0E46\\u0E47\\u0E48\\u0E49\\u0E4A\\u0E4B\\u0E4C\\u0E4D\\u0E4E\\u0E4F\\u0E50\\u0E51\\u0E52\\u0E53" +
//			"\\u0E54\\u0E55\\u0E56\\u0E57\\u0E58\\u0E59\\u0E5A\\u0E5B0-9']";
	//ar
//	private static final String WORD_REGEX = "['ضصثقفغعهخحجشسيبلاتنمكطذءؤرىةوزظدئإأآڨڭپڢڤچ]+";
//	private static final String NUM_REGEX = "[٠0١1٢2٣3٤4٥5٦6٧7٨8٩9]+";
//	private static final String PUN_REREX = "[^'ضصثقفغعهخحجشسيبلاتنمكطذءؤرىةوزظدئإأآڨڭپڢڤچ٠0١1٢2٣3٤4٥5٦6٧7٨8٩9]";
	
	//fi
//	private static final String WORD_REGEX = "[abcdefghijklmnopqrstuvwxyzåäöABCDEFGHIJKLMNOPQRSTUVWXYZAÅÄOÖ']+";
//	private static final String NUM_REGEX = "[+-]*[0-9]+.*[0-9]*";
//	private static final String PUN_REREX = "[^abcdefghijklmnopqrstuvwxyzåäöABCDEFGHIJKLMNOPQRSTUVWXYZAÅÄOÖ0-9']";
	
	//it
//	private static final String WORD_REGEX = "[qwertyuiìíopèéùúasdfghjklòóàzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM']+";
//	private static final String NUM_REGEX = "[+-]*[0-9]+.*[0-9]*";
//	private static final String PUN_REREX = "[^qwertyuiìíopèéùúasdfghjklòóàzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM0-9']";
	//ru
//	private static final String WORD_REGEX = "[йцукенгшщзхфывапролджэячсмитьбюЙЦУКЕНГШЩЗХФЫВАПРОЛДЖЭЯЧСМИТЬБЮ']+";
//	private static final String NUM_REGEX = "[+-]*[0-9]+.*[0-9]*";
//	private static final String PUN_REREX = "[^йцукенгшщзхфывапролджэячсмитьбюЙЦУКЕНГШЩЗХФЫВАПРОЛДЖЭЯЧСМИТЬБЮ0-9']";
	
//	tr
//	private static final String WORD_REGEX = "[ertyuıopğüasdfghjklşizcvbnmöçERTYUIOPĞÜASDFGHJKLŞİZCVBNMÖÇ']+";
//	private static final String NUM_REGEX = "[+-]*[0-9]+.*[0-9]*";
//	private static final String PUN_REREX = "[^ertyuıopğüasdfghjklşizcvbnmöçERTYUIOPĞÜASDFGHJKLŞİZCVBNMÖÇ0-9']";
	//pl
//	private static final String WORD_REGEX = "[aąbcćdeęfghijklłmnńoóprsśtuwyzźżAĄBCĆDEĘFGHIJKLŁMNŃOÓPRSŚTUWYZŹŻ']+";
//	private static final String NUM_REGEX = "[+-]*[0-9]+.*[0-9]*";
//	private static final String PUN_REREX = "[^aąbcćdeęfghijklłmnńoóprsśtuwyzźżAĄBCĆDEĘFGHIJKLŁMNŃOÓPRSŚTUWYZŹŻ0-9']";
	//tr
//	private static final String WORD_REGEX = "[ertyuıopğüasdfghjklşizcvbnmöçERTYUIOPĞÜASDFGHJKLŞİZCVBNMÖÇ']+";
//	private static final String NUM_REGEX = "[+-]*[0-9]+.*[0-9]*";
//	private static final String PUN_REREX = "[^ertyuıopğüasdfghjklşizcvbnmöçERTYUIOPĞÜASDFGHJKLŞİZCVBNMÖÇ0-9']";
	
	//ru
//	private static final String WORD_REGEX = "[йцукенгшщзхфывапролджэячсмитьбюЙЦУКЕНГШЩЗХФЫВАПРОЛДЖЭЯЧСМИТЬБЮ']+";
//	private static final String NUM_REGEX = "[+-]*[0-9]+.*[0-9]*";
//	private static final String PUN_REREX = "[^йцукенгшщзхфывапролджэячсмитьбюЙЦУКЕНГШЩЗХФЫВАПРОЛДЖЭЯЧСМИТЬБЮ0-9']";
	//
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

//    private static Map<String, Double> smallWordsDictMap = null;
	
	private static Map<String,Integer> phraseDictMap = null;
	
	private static Set<String> emojisSet = null;
	
	private static Set<String> bigWordsSet = null;
	
	private static Map<String,Integer> smallWordsDictMap = null;
	
	private static Set<String> dictLettersSet = null;
	
	private static Set<String> dataLettersSet = null;
	
	private static Set<String> dataWordsSet = null;
	
	private static Set<String> dataOutWordSet = null;
	
	private static Map<String, Integer> wordIdMap = null;
	
	private static Map<String, Integer> letterIdMap = null;
	
	private static Map<String, Integer> outWordIdMap = null;
	
	private static Map<String, Integer> phraseIdMap = null;
	
	private static SumMap<String> wordsCountMapFromData = null;
	
	private static Map<String,Integer> phraseCountMapFromData = null;
	
	private static int lineNum =11125124; //
	private static int wordsNum = 20000;
	private static int phraseNum =20000;
	public static void main(String[] arg0) {
		String s="en_US";
		String wordsDictFile = "/Users/ff/Desktop/train_data/"+s+"/"+s+"_unigram_null";
		String emojisFile = "/Users/ff/Desktop/train_data/"+s+"/emojis_null";
//		String dataPathIn_original = "/Users/ff/Desktop/train_data/ar/ar_im_fa_web.txt";
//		int wordsNum = 20000;
		int phraseNum =20000;
		String dataPathIn = "/Users/ff/Desktop/train_data/"+s+"/"+s+"_user_web_pro";
		String wordvocabfile = "/Users/ff/Desktop/train_data/"+s+"/"+s+"_user_web_train/vocab_words_true";
		
		String lettervocabfile = "/Users/ff/Desktop/train_data/"+s+"/"+s+"_user_web_train/vocab_letters";
		String phrasevocabfile = "/Users/ff/Desktop/train_data/"+s+"/"+s+"_user_web_train/vocab_phrase";
		//String dataPathIn = "/home/pubsrv/data/gaoxin/data/20161223_20170803_emojis_cleaned/";
		
		String dataPathOut = "/Users/ff/Desktop/train_data/"+s+"/"+s+"_user_web_train/train_data_"+s+"_user_web_no_emoji_no_map_phrase";
		double rateThreshold = 0.8;
		int trainDataNum = 11025124;
		int devDataNum = 10000;
		int testDataNum = 10000;
		
		loadEmojisSet(emojisFile);
		//		calculateWordsFrequencyAllData(dataPathIn_original);  //
		combineWordsDict(wordsDictFile,wordvocabfile,lettervocabfile,phrasevocabfile);
		convertData(dataPathIn, rateThreshold, trainDataNum, devDataNum, testDataNum, dataPathOut);
		saveVocabFiles(dataPathOut);
		convertToIds(dataPathOut, dataPathOut);
	}
	
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

	
	private static void combineWordsDict(String wordsDictFile,String wordvocabfile,String lettervocabfile,String phrasefile) {
		try {
			SumMap<String> wordsCountMapFromVocab = new SumMap<>();
			String tempString;
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(wordsDictFile), "UTF-8"));
			while ((tempString = reader.readLine()) != null) {
				String[] tempStrings = tempString.split("\\s+");
//				if (tempStrings.length == 2) {
					String word = tempStrings[0];
//					String countString = tempStrings[1];
//					int count = Integer.parseInt(countString);
					wordsCountMapFromVocab.add(word, 1);
//				} else {
//					System.out.println("split error:" + tempString);
//				}
			}
			reader.close();
			System.out.println("wordsCountMapFromVocab size = " + wordsCountMapFromVocab.size());
			
			BufferedReader reader_vocab_word = new BufferedReader(new InputStreamReader(new FileInputStream(wordvocabfile), "UTF-8"));
//
			smallWordsDictMap = new LinkedHashMap<>();
			dictLettersSet = new HashSet<>();
			bigWordsSet = new HashSet<>();
			phraseDictMap = new LinkedHashMap<>();
			
			while ((tempString = reader_vocab_word.readLine()) != null) {
//				System.out.println("temp"+tempString);
				smallWordsDictMap.put(tempString,0);
			}
			BufferedReader reader_vocab_letter = new BufferedReader(new InputStreamReader(new FileInputStream(lettervocabfile), "UTF-8"));
			while ((tempString = reader_vocab_letter.readLine()) != null) {
				dictLettersSet.add(tempString);
			}
			
			BufferedReader reader_vocab_phrase = new BufferedReader(new InputStreamReader(new FileInputStream(phrasefile), "UTF-8"));
			while ((tempString = reader_vocab_phrase.readLine()) != null) {
				phraseDictMap.put(tempString.trim(), 1);
			}
//
			for (String word : wordsCountMapFromVocab.sort().keySet()) {
				if (!smallWordsDictMap.keySet().contains(word)) {
					bigWordsSet.add(word); //在一元词表而不在2w词中的
				}
			}
			
			System.out.println("smallWordsDicSet size = " + smallWordsDictMap.size());
//            System.out.println("smallWordsDictMap = " + smallWordsDictMap.toString());
			System.out.println("bigWordsSet size = " + bigWordsSet.size());
			System.out.println("dictLettersSet size = " + dictLettersSet.size());
			System.out.println("phraseDictMap size = " + phraseDictMap.size());
//            System.out.println("dictLettersSet = " + dictLettersSet.toString());
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
			if (smallWordsDictMap.keySet().contains(word)) {
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
			if (smallWordsDictMap.keySet().contains(word)) {
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
		String[] lineSplit = lineString.split("\\|#\\|");
//        System.out.println("line split length:"+lineSplit.length);
		String word;
		String letter;
		if (lineSplit.length == 2) {
			String[] letterIn = lineSplit[0].trim().split("\\t+");
			String[] words = lineSplit[1].trim().split("\\t+");
			for(int i=0;i<letterIn.length;i++){
				int idxStart = letterIn[i].indexOf("<b>");
				int idxEnd = letterIn[i].lastIndexOf("</b>");
				if (idxStart >= 0 && idxEnd >= 0) {
					letter = letterIn[i].substring(idxStart + 3, idxEnd).trim();
				}
				else {
					System.out.println("letter split error: " + lineString);
					letter = null;
				}
				map.put("letter", letter);
			}
			for(int j=0;j<words.length;j++){
				map.put("word", words[j]);
			}
			
		}
		else {
			System.out.println("line split error: " + lineString);
			word = null;
			letter = null;
			map.put("word", word);
			map.put("letter", letter);
		}
//        map.put("word", word);
//        map.put("letter", letter);
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
					if (file.getName().matches("part.+")&&!file.getName().endsWith(".crc")) {
						String filePath =file.getAbsoluteFile().toString();
						System.out.println(filePath);
						BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),
								"UTF-8"));
						lineString = reader.readLine();
						while (true) {
							if (lineString == null) {
								break;
							}
//                            if (lineString.startsWith("\t")) {
							ArrayList<String> letters=new ArrayList<String>();
							ArrayList<String> words=new ArrayList<String>();
							double randDouble = rand.nextDouble();
//							System.out.println("randDouble->"+randDouble);
							if (randDouble < rateTrain) {
//                                    while (true) {
//                                        Map<String, String> wordLetterMap = convertLine(lineString);
//                                        if (wordLetterMap.get("word") == null || wordLetterMap.get("letter") == null){
//                                            break;
//                                        }
								String[] lineSplit = lineString.split("\\|#\\|");
								//        System.out.println("line split length:"+lineSplit.length);
								String word;
								String letter;
								if (lineSplit.length == 2) {
									String[] letterIn = lineSplit[0].split("\\t+");
									String[] words1 = lineSplit[1].split("\\t+");
									if(letterIn.length>2&&words1.length>2&&letterIn.length==words1.length){
										for (int i = 0; i < letterIn.length; i++) {
											if(letterIn[i].length()>0){
//                                            |झ ग ड ा|	|क र ण े|	|क ो|#kika#झगडा	करणे	को
												letter = letterIn[i];
												letters.add(letter);
											}
											else break;
//                                        letters.add(letter);
										}
										for (int j = 0; j < words1.length; j++) {
											if(words1[j].length()>0){
												word = words1[j];
												words.add(word);
											}
											else {
												break;
											}
										}
									}
								}
								else {
									System.out.println("line split error: " + lineString);
									break;
								}
								
								if(letters.size()>0&&words.size()>0){
									convertSentence(words, letters, rateThreshold, writerTrain, true);
								}
								lineString = reader.readLine();
//                                if (lineString == null ) {
////                                            convertSentence(words, letters, rateThreshold, writerTrain, true);
//                                    break;
//                                }
//                                    }
							} else if (randDouble < rateTrain + rateDev) {
//                                    while (true) {
								String[] lineSplit = lineString.split("\\|#\\|");
								//        System.out.println("line split length:"+lineSplit.length);
								String word;
								String letter;
								if (lineSplit.length == 2) {
									String[] letterIn = lineSplit[0].split("\\t+");
									String[] words1 = lineSplit[1].split("\\t+");
									if(letterIn.length>2&&words1.length>2&&letterIn.length==words1.length){
										for (int i = 0; i < letterIn.length; i++) {
											if(letterIn[i].length()>0){
//                                            |झ ग ड ा|	|क र ण े|	|क ो|#kika#झगडा	करणे	को
												letter = letterIn[i];
												letters.add(letter);
											}
											else break;
										}
										for (int j = 0; j < words1.length; j++) {
											if(words1[j].length()>0){
												word = words1[j];
												words.add(word);
											}
											else {
												break;
											}
										}
									}
								}
								else {
									System.out.println("line split error: " + lineString);
									break;
								}
								if(letters.size()>0&&words.size()>0) {
									convertSentence(words, letters, rateThreshold, writerDev, false);
								}
								lineString = reader.readLine();
//                                if (lineString == null ) {
//                                    break;
//                                }
//                                    }
							} else if (randDouble < rateTrain + rateDev + rateTest) {
//                                    while (true) {
								String[] lineSplit = lineString.split("\\|#\\|");
								//        System.out.println("line split length:"+lineSplit.length);
								String word;
								String letter;
								if (lineSplit.length == 2) {
									String[] letterIn = lineSplit[0].split("\\t+");
									String[] words1 = lineSplit[1].split("\\t+");
									if(letterIn.length>2&&words1.length>2&&letterIn.length==words1.length){
										for (int i = 0; i < letterIn.length; i++) {
											if(letterIn[i].length()>0){
//                                            |झ ग ड ा|	|क र ण े|	|क ो|#kika#झगडा	करणे	को
												letter = letterIn[i];
												letters.add(letter);
											}
											else break;
										}
										for (int j = 0; j < words1.length; j++) {
											if(words1[j].length()>0){
												word = words1[j];
												words.add(word);
											}
											else {
												break;
											}
										}
									}
								}
								else {
									System.out.println("line split error: " + lineString);
									break;
								}
								if(letters.size()>0&&words.size()>0) {
									convertSentence(words, letters, rateThreshold, writerTest, false);
								}
								lineString = reader.readLine();
//                                if (lineString == null ) {
//                                    break;
//                                }
//                                    }
							} else {
//                                while (true) {
								lineString = reader.readLine();
//                                    if (lineString == null ) {
//                                        break;
//                                    }
//                                }
							}

//                            }
//                            else {
//                                lineString = reader.readLine();
//                            }
						}
						reader.close();
					}
				}
			}
			writerTest.close();
			writerTrain.close();
			writerDev.close();
			System.out.println("dataWordsSet size = " + dataWordsSet.size());
//            System.out.println("dataWordsSet = " + dataWordsSet.toString());
			System.out.println("dataLettersSet size = " + dataLettersSet.size());
//            System.out.println("dataLettersSet = " + dataLettersSet.toString());
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
//        System.out.println("letterIdMap = " + letterIdMap.toString());
		int idin= wordIdMap.size();
		int idout = outWordIdMap.size();
		for (String word : smallWordsDictMap.keySet()) {
//			System.out.println("small:"+word);
			if (dataWordsSet.contains(word)) {
				if (!wordIdMap.containsKey(word) && wordIdMap.size()<wordsNum+6) {
//					System.out.println("aa"+word);
					wordIdMap.put(word, idin);
					idin++;
				}
			}
			if (dataOutWordSet.contains(word) && outWordIdMap.size()<wordsNum+3) {
				outWordIdMap.put(word, idout);
				idout ++;
			}
		}
		System.out.println("wordIdMap size = " + wordIdMap.size());
		
		System.out.println("outWordIdMap size = " + outWordIdMap.size());
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
			if (phrase_to_save == true && phraseIdMap.size()<phraseNum+2 ){
				phraseIdMap.put(phrase, id);
				id ++;
			}
		}
	}
	
	private static void saveMapToFile(Map<String, Integer> map, String filePath) {
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"));
			for (String key : map.keySet()) {
//                System.out.println("key:"+key);
				writer.write(key + SPLIT_FLAG + map.get(key) + "\n");
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void saveVocabFiles(String dataPathOut) {
		makeVocabMap();
		System.out.println("wordidmap size:"+wordIdMap.size());
		System.out.println("letterIdMap size:"+letterIdMap.size());
		System.out.println("outWordIdMap size:"+outWordIdMap.size());
        System.out.println("phraseIdMap size:"+phraseIdMap.size());
		saveMapToFile(wordIdMap, dataPathOut + "/vocab_in_words");
		saveMapToFile(letterIdMap, dataPathOut + "/vocab_in_letters");
		saveMapToFile(outWordIdMap, dataPathOut + "/vocab_out");
        saveMapToFile(phraseIdMap, dataPathOut + "/vocab_phrase");
	}
	
	private static void convertToIdsFile(Map<String, Integer> inWordsMap, Map<String, Integer> outWordsMap,
										 Map<String, Integer> lettersMap,Map<String, Integer> phraseMap,
										 String fileInPath, String fileOutPath, String fileOutPathLetters,String fileOutPathPhrass
	) {//
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileInPath), "UTF-8"));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOutPath), "UTF-8"));
			BufferedWriter writerLetters = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOutPathLetters), "UTF-8"));
            BufferedWriter writerPhrase = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOutPathPhrass), "UTF-8"));
			
			String lineString;
			while ((lineString = reader.readLine()) != null) {
				String[] line = lineString.split("\\|#\\|");
				if(line.length==2){
//					System.out.println("line size"+line.length);
					String[] letterStrings = line[0].split("\\t+");
					String[] wordStrings = line[1].split("\\t+");
					if (letterStrings.length != wordStrings.length){
						System.out.println("line error: " + lineString);
					}
					if (wordStrings.length >= 2) {
						String idsWordsIn = convertInIds(wordStrings, inWordsMap);
						String idsWordsOut = convertOutIds(wordStrings, outWordsMap);
						String idsLetters = convertLettersIds(letterStrings, lettersMap);
						String idsPhrase = convertPhraseIds(wordStrings, phraseMap);
						
						// && idsPhrase != null)
						if (idsWordsIn != null && idsWordsOut != null && idsLetters != null){
							writer.write(idsWordsIn.trim() + "#" + idsWordsOut.trim() + "\n");
							writerLetters.write(idsLetters.trim() + "\n");
							writerPhrase.write(idsPhrase.trim() + "\n");
						}
						
					}
				}
			}
			reader.close();
			writer.close();
			writerLetters.close();
//            writerPhrase.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void convertToIds(String dataPathIn, String dataPathOut) {
		convertToIdsFile(wordIdMap, outWordIdMap, letterIdMap, phraseIdMap,dataPathIn + "/train_data",
				dataPathOut + "/train_in_ids_lm", dataPathOut + "/train_in_ids_letters",
                dataPathOut + "/train_ids_phrase");
		convertToIdsFile(wordIdMap, outWordIdMap, letterIdMap, phraseIdMap,dataPathIn + "/dev_data",
				dataPathOut + "/dev_in_ids_lm", dataPathOut + "/dev_in_ids_letters",
                dataPathOut + "/dev_ids_phrase");
	}

//    private static void saveWords(String wordsFile) {
//        try {
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(wordsFile), "UTF-8"));
//            for (String word : smallWordsDicSet.keySet()) {
//                writer.write(word + "\n");
//            }
//            writer.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
	

}
