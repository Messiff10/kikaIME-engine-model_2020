import utilities.SumMap;

import java.io.*;
import java.util.*;

/**
 * Created by gaoxin on 17-7-10.
 * modified by lizhen on 18-1-10.
 * modified by zzf on 19-9-10.
 * 已经生成vocab之后重新生成 开发集
 */
public class TrainingDataProducerEmojiOutTwoUnk_single_develop {
//
	//en
//	private static final String WORD_REGEX = "[a-zA-Z']+";
//
//	private static final String NUM_REGEX = "[+-]*[0-9]+.*[0-9]*";
//
//	private static final String PUN_REREX = "[^a-zA-Z0-9']";

//	//es_US
//	private static final String WORD_REGEX = "[qwertyuiopasdfghjklñzxcvbnmQWERTYUIOPASDFGHJKLÑZXCVBNM']+";
//	private static final String NUM_REGEX = "[+-]*[0-9]+.*[0-9]*";
//	private static final String PUN_REREX = "[^qwertyuiopasdfghjklñzxcvbnmQWERTYUIOPASDFGHJKLÑZXCVBNM0-9']";
	
	//sv
//	private static final String WORD_REGEX = "[abcdefghijklmnopqrstuvwxyzåäöABCDEFGHIJKLMNOPQRSTUVWXYZAÅÄOÖ']+";
//	private static final String NUM_REGEX = "[+-]*[0-9]+.*[0-9]*";
//	private static final String PUN_REREX = "[^abcdefghijklmnopqrstuvwxyzåäöABCDEFGHIJKLMNOPQRSTUVWXYZAÅÄOÖ0-9']";
	//de
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
	//ur
//	private static final String WORD_REGEX = "[ےیءھہونملگکقفغعظطضصشسژڑرذڈدخحچجثٹتپباآ']+";
//	private static final String NUM_REGEX = "[+-]*[0-9]+.*[0-9]*";
//	private static final String PUN_REREX = "[^ےیءھہونملگکقفغعظطضصشسژڑرذڈدخحچجثٹتپباآ0-9']";
	//cs
//	private static final String WORD_REGEX = "[aábcčdďeéěfghchiíjklmnňoópqrřsštťuúůvwxyýzžAÁBCČDĎEÉĚFGHChIÍJKLMNŇOÓPQRŘSŠTŤUÚŮVWXYÝZŽ']+";
//	private static final String NUM_REGEX = "[+-]*[0-9]+.*[0-9]*";
//	private static final String PUN_REREX = "[^aábcčdďeéěfghchiíjklmnňoópqrřsštťuúůvwxyýzžAÁBCČDĎEÉĚFGHChIÍJKLMNŇOÓPQRŘSŠTŤUÚŮVWXYÝZŽ0-9']";
	//fr
	private static final String WORD_REGEX = "[éèêëcçàâæazertyÿuiîïoôœpqsdfghjklmùûüwxcvbnAÀÆZEÉÈÊËCÇRTYŸUÛÜIÎÏOÔŒPQSDFGHJKLMWXCVBN']+";
	private static final String NUM_REGEX = "[+-]*[0-9]+.*[0-9]*";
	private static final String PUN_REREX = "[^éèêëcçàâæazertyÿuiîïoôœpqsdfghjklmùûüwxcvbnAÀÆZEÉÈÊËCÇRTYŸUÛÜIÎÏOÔŒPQSDFGHJKLMWXCVBN0-9']";
	
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
	
	private static Set<String> emojisSet = null;
	
	private static Set<String> bigWordsSet = null;
	
	private static Map<String,Integer> smallWordsDictMap = null;
	
	private static Set<String> dictLettersSet = null;
	
	private static Map<String, Integer> wordIdMap = null;
	
	private static Map<String, Integer> letterIdMap = null;
	
	private static Map<String, Integer> outWordIdMap = null;
//	private  static  ;
	
	public static void main(String[] arg0) {

//        String wordsDictFile = "/home/pubsrv/data/lizhen/split_model/dl-tensorflow-dev/seq2word_word_letter_separated/data_producer/src/main/resources/words_dict/main_en_1_unigram_emoji";
//        String emojisFile = "/home/pubsrv/data/lizhen/split_model/dl-tensorflow-dev/seq2word_word_letter_separated/data_producer/src/main/resources/words_dict/emojis";
		String s=arg0[0];
		int a=arg0[];
		String wordsDictFile = arg0[1];
		String emojisFile = arg0[2];
//		String dataPathIn_original = "/Users/ff/Desktop/train_data/ar/ar_im_fa_web.txt";
//		int wordsNum = 20000;
		int phraseNum =20000;
		String dataPathIn = arg0[3];
		String wordvocabfile = arg0[4];
		
		String lettervocabfile = arg0[5];
		
		//String dataPathIn = "/home/pubsrv/data/gaoxin/data/20161223_20170803_emojis_cleaned/";
		
		String dataPathOut = arg0[6];
		String vocabletters =arg0[7];
		String vocabwords =arg0[8];
		String vocabout =arg0[9];
		
		loadEmojisSet(emojisFile);
		//		calculateWordsFrequencyAllData(dataPathIn_original);  //
		combineWordsDict(wordsDictFile,wordvocabfile,lettervocabfile);

//		convertData(dataPathIn);
		makeVocabMap(vocabletters,vocabwords,vocabout);
		convertToIds(dataPathIn, dataPathOut,a);
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
	
	
	private static void combineWordsDict(String wordsDictFile,String wordvocabfile,String lettervocabfile) {
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
			
			while ((tempString = reader_vocab_word.readLine()) != null) {
//				System.out.println("temp"+tempString);
				smallWordsDictMap.put(tempString,0);
			}
			reader_vocab_word.close();
			BufferedReader reader_vocab_letter = new BufferedReader(new InputStreamReader(new FileInputStream(lettervocabfile), "UTF-8"));
			while ((tempString = reader_vocab_letter.readLine()) != null) {
				dictLettersSet.add(tempString);
			}
//
			for (String word : wordsCountMapFromVocab.sort().keySet()) {
				if (!smallWordsDictMap.keySet().contains(word)) {
					bigWordsSet.add(word); //在一元词表而不在2w词中的
				}
			}
			reader_vocab_letter.close();
			System.out.println("smallWordsDicSet size = " + smallWordsDictMap.size());
			System.out.println("smallWordsDictSet size:"+smallWordsDictMap.toString());
//            System.out.println("smallWordsDictMap = " + smallWordsDictMap.toString());
			System.out.println("bigWordsSet size = " + bigWordsSet.size());
			System.out.println("bigWordsSet:" + bigWordsSet.toString());
			System.out.println("dictLettersSet size = " + dictLettersSet.size());
			System.out.println("dictLettersSet: " + dictLettersSet.toString());
//            System.out.println("dictLettersSet = " + dictLettersSet.toString());
		} catch (Exception e) {
			e.printStackTrace();
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
	
	
	private static void makeVocabMap(String vocabletters,String vocabwords,String vocabout) {
		try {
			wordIdMap = new LinkedHashMap<>();
			letterIdMap = new LinkedHashMap<>();
			outWordIdMap = new LinkedHashMap<>();
			
			BufferedReader reader_letter = new BufferedReader(new InputStreamReader(new FileInputStream(vocabletters), "UTF-8"));
			BufferedReader reader_vocab_words = new BufferedReader(new InputStreamReader(new FileInputStream(vocabwords), "UTF-8"));
			BufferedReader reader_vocab_out = new BufferedReader(new InputStreamReader(new FileInputStream(vocabout), "UTF-8"));
			String temp_letter;
			while ((temp_letter=reader_letter.readLine())!=null){
				String[] letters;
				letters=temp_letter.trim().split("##");
				letterIdMap.put(letters[0], Integer.parseInt(letters[1]));
			}
			System.out.println("letterIdMap size = " + letterIdMap.size());
			String temp_words;
			while ((temp_words=reader_vocab_words.readLine())!=null){
				String[] words;
				words=temp_words.trim().split("##");
				wordIdMap.put(words[0], Integer.parseInt(words[1]));
			}
			String temp_out;
			while ((temp_out=reader_vocab_out.readLine())!=null){
				String[] out;
				out=temp_out.trim().split("##");
				outWordIdMap.put(out[0], Integer.parseInt(out[1]));
			}
			reader_vocab_out.close();
			reader_vocab_words.close();
			reader_letter.close();
			System.out.println("wordIdMap size = " + wordIdMap.size());
			System.out.println("wordIdMap:" + wordIdMap.toString());
			
			System.out.println("outWordIdMap size = " + outWordIdMap.size());
			System.out.println("outWordIdMap:" + outWordIdMap.toString());
		}catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
	private static void convertToIdsFile(Map<String, Integer> inWordsMap, Map<String, Integer> outWordsMap,
										 Map<String, Integer> lettersMap,
										 String fileInPath, String fileOutPath, String fileOutPathLetters
	) {//String fileOutPathPhrass
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileInPath), "UTF-8"));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOutPath), "UTF-8"));
			BufferedWriter writerLetters = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOutPathLetters), "UTF-8"));
//            BufferedWriter writerPhrase = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileOutPathPhrass), "UTF-8"));
			
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
//						String idsPhrase = convertPhraseIds(wordStrings, phraseMap);
						
						// && idsPhrase != null)
						if (idsWordsIn != null && idsWordsOut != null && idsLetters != null){
							
							writer.write(idsWordsIn.trim() + "#" + idsWordsOut.trim() + "\n");
							writerLetters.write(idsLetters.trim() + "\n");
//							writerPhrase.write(idsPhrase.trim() + "\n");
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
	
	private static void convertToIds(String dataPathIn, String dataPathOut,int a) {
		File outPathFile = new File(dataPathOut);
		if (!outPathFile.exists()) {
			outPathFile.mkdirs();
		}
		convertToIdsFile(wordIdMap, outWordIdMap, letterIdMap, dataPathIn,
				dataPathOut + "/test_data_dev_lm_"+a, dataPathOut + "/test_data_dev_letters_"+a);
	}
	
	
}
