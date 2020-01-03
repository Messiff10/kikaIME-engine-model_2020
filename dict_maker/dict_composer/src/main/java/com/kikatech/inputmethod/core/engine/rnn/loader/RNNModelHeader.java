package com.kikatech.inputmethod.core.engine.rnn.loader;

import java.io.IOException;

/**
 * Created by msj on 2018/7/12.
 */
public class RNNModelHeader {

    private final static String TAG = "RNNModelHeader";

    private int version = 0;

    public final static int SECTION_VOCAB_IN_LETTERS = 1;
    public final static int SECTION_VOCAB_IN_WORDS = 2;
    public final static int SECTION_VOCAB_OUT_WORDS = 3;
    public final static int SECTION_MODEL_LM = 4;
    public final static int SECTION_MODEL_KC = 5;

    private int sectionCount = 0;

    private byte[] vocabInLettersMd5 = null;
    private byte[] vocabInWordsMd5 = null;
    private byte[] vocabOutWordsMd5 = null;
    private byte[] modelLMMd5 = null;
    private byte[] modelKCMd5 = null;

    private long vocabInLettersOffset = -1;
    private long vocabInLettersSize = 0;
    private long vocabInWordsOffset = -1;
    private long vocabInWordsSize = 0;
    private long vocabOutWordsOffset = -1;
    private long vocabOutWordsSize = 0;
    private long modelLMOffset = -1;
    private long modelLMSize = 0;
    private long modelKCOffset = -1;
    private long modelKCSize = 0;

    public void visit(RNNModelVisitor visitor) throws IOException {
        version = visitor.readInt();
        readSections(visitor);
    }

    private void readSections(RNNModelVisitor visitor) throws IOException {
        sectionCount = visitor.readInt();
        for(int i = 0; i < sectionCount; i++) {
            readSection(visitor);
        }
    }

    private void readSection(RNNModelVisitor visitor) throws IOException {
        int type = visitor.readInt();
        int size = visitor.readInt();
        switch (type) {
            case SECTION_VOCAB_IN_LETTERS:
                vocabInLettersOffset = visitor.readLong();
                vocabInLettersSize = visitor.readLong();
                vocabInLettersMd5 = visitor.read(16);
                break;
            case SECTION_VOCAB_IN_WORDS:
                vocabInWordsOffset = visitor.readLong();
                vocabInWordsSize = visitor.readLong();
                vocabInWordsMd5 = visitor.read(16);
                break;
            case SECTION_VOCAB_OUT_WORDS:
                vocabOutWordsOffset = visitor.readLong();
                vocabOutWordsSize = visitor.readLong();
                vocabOutWordsMd5 = visitor.read(16);
                break;
            case SECTION_MODEL_LM:
                modelLMOffset = visitor.readLong();
                modelLMSize = visitor.readLong();
                modelLMMd5 = visitor.read(16);
                break;
            case SECTION_MODEL_KC:
                modelKCOffset = visitor.readLong();
                modelKCSize = visitor.readLong();
                modelKCMd5 = visitor.read(16);
                break;
        }
        size -= 32;
        if (size > 0) {
            visitor.skip(size);
        }
    }

    public boolean isValid() {
        return vocabInLettersOffset >= 0 && vocabInLettersSize > 0 &&
                vocabInWordsOffset >= 0 && vocabInWordsSize > 0 &&
                vocabOutWordsOffset >= 0 && vocabOutWordsSize > 0 &&
                modelLMOffset >= 0 && modelLMSize > 0 &&
                modelKCOffset >= 0 && modelKCSize > 0;
    }

    public int getVersion() {
        return version;
    }

    public byte[] getVocabInLettersMd5() {
        return vocabInLettersMd5;
    }

    public byte[] getVocabInWordsMd5() {
        return vocabInWordsMd5;
    }

    public byte[] getVocabOutWordsMd5() {
        return vocabOutWordsMd5;
    }

    public byte[] getModelLMMd5() {
        return modelLMMd5;
    }

    public byte[] getModelKCMd5() {
        return modelKCMd5;
    }

    public long getVocabInLettersOffset() {
        return vocabInLettersOffset;
    }

    public long getVocabInLettersSize() {
        return vocabInLettersSize;
    }

    public long getVocabInWordsOffset() {
        return vocabInWordsOffset;
    }

    public long getVocabInWordsSize() {
        return vocabInWordsSize;
    }

    public long getVocabOutWordsOffset() {
        return vocabOutWordsOffset;
    }

    public long getVocabOutWordsSize() {
        return vocabOutWordsSize;
    }

    public long getModelLMOffset() {
        return modelLMOffset;
    }

    public long getModelLMSize() {
        return modelLMSize;
    }

    public long getModelKCOffset() {
        return modelKCOffset;
    }

    public long getModelKCSize() {
        return modelKCSize;
    }
}
