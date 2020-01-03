
public class EfficiencyAnalyzerNewFormTest {

    public static void main(String[] args) {
        EfficiencyAnalyzerNewForm analyzer = new EfficiencyAnalyzerNewForm();
//        String resFile = "../../seq2word_split_model/test_result";
//        String rnnVocabFile = "/home/tangying/workspace/kikaIME-engine-model/seq2word_split_model/training_data_400w_fix_phrase/vocab_out";
//        String fullVocabFile = "../../data_producer/src/main/resources/words_dict/main_en_1_unigram_emoji";
//        String emojiFile = "../../data_producer/src/main/resources/words_dict/emojis";
//        String phraseFile = "/home/tangying/workspace/kikaIME-engine-model/seq2word_split_model/training_data_400w_fix_phrase/vocab_phrase";
        String resFile = args[0];
        String rnnVocabFile = args[1];
        String fullVocabFile = args[2];
        String emojiFile = args[3];
        String phraseFile = args[4];
        analyzer.analyzePc(resFile, rnnVocabFile, fullVocabFile, emojiFile, phraseFile);
    }

}