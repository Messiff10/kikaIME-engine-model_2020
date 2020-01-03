package com.kikatech.dicttools;

import com.kikatech.inputmethod.core.engine.rnn.loader.RNNModelHeader;
import com.kikatech.inputmethod.core.engine.rnn.loader.RNNModelUtils;

import java.io.*;
import java.util.logging.Logger;

/**
 * Created by msj on 2018/7/12.
 */
public class DictComposer {

    private static Logger log = Logger.getLogger("myLogger");
    private static final String TAG = "DictComposer";

    private static final int VERSION = 3;
    private static final int SECTION_COUNT = 5;
    private static final int SECTION_SIZE = 32;

    private class SectionInfo {
        int id;
        byte[] buf;
        long offsetP;
    }

    private DictComposer() {}

    private void write(String vocabInLettersPath, String vocabInWordsPath, String vocabOutWordsPath, String modelLMPath, String modelKCPath, String dstDcitPath) {
        try {
            RandomAccessFile raf = new RandomAccessFile(dstDcitPath, "rw");

            // write header
            writeInt(raf, VERSION);
            log.info(TAG + ": write version");
            writeInt(raf, SECTION_COUNT);
            log.info(TAG + ": write section");
            SectionInfo vocabInLetters = writeSection(raf, RNNModelHeader.SECTION_VOCAB_IN_LETTERS, vocabInLettersPath, false);
            SectionInfo vocabInWords = writeSection(raf, RNNModelHeader.SECTION_VOCAB_IN_WORDS, vocabInWordsPath, false);
            SectionInfo vocabOutWords = writeSection(raf, RNNModelHeader.SECTION_VOCAB_OUT_WORDS, vocabOutWordsPath, false);
            SectionInfo modelLM = writeSection(raf, RNNModelHeader.SECTION_MODEL_LM, modelLMPath, true);
            SectionInfo modelKC = writeSection(raf, RNNModelHeader.SECTION_MODEL_KC, modelKCPath, true);

            // write body
            writeBody(raf, vocabInLetters);
            writeBody(raf, vocabInWords);
            writeBody(raf, vocabOutWords);
            writeBody(raf, modelLM);
            writeBody(raf, modelKC);

            log.info(TAG + ": Finish.");
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeInt(RandomAccessFile raf, int n) throws IOException {
        raf.write((n >>> 24) & 0xFF);
        raf.write((n >>> 16) & 0xFF);
        raf.write((n >>> 8) & 0xFF);
        raf.write((n >>> 0) & 0xFF);
    }

    private void writeLong(RandomAccessFile raf, long v) throws IOException {
        raf.write((int) (v >>> 56) & 0xFF);
        raf.write((int) (v >>> 48) & 0xFF);
        raf.write((int) (v >>> 40) & 0xFF);
        raf.write((int) (v >>> 32) & 0xFF);
        raf.write((int) (v >>> 24) & 0xFF);
        raf.write((int) (v >>> 16) & 0xFF);
        raf.write((int) (v >>> 8) & 0xFF);
        raf.write((int) (v >>> 0) & 0xFF);
    }

    private SectionInfo writeSection(RandomAccessFile raf, int section_id, String fileName, boolean zip) throws IOException {
        writeInt(raf, section_id);
        writeInt(raf, SECTION_SIZE);
        long offsetP = raf.getFilePointer();
        writeLong(raf, 0);
        byte[] buf = readAssetFile(fileName, zip);
        writeLong(raf, buf.length);
        raf.write(RNNModelUtils.getMD5ByteArray(buf));
        log.info(TAG + ": write header " + section_id);

        SectionInfo info = new SectionInfo();
        info.id = section_id;
        info.buf = buf;
        info.offsetP = offsetP;
        return info;
    }

    private void writeBody(RandomAccessFile raf, SectionInfo info) throws IOException {
        long startOffset = raf.getFilePointer();
        raf.seek(info.offsetP);
        raf.writeLong(startOffset);
        raf.seek(startOffset);
        raf.write(info.buf);

        log.info(TAG + ": write body " + info.id + ", offset " + startOffset + ", size " + info.buf.length);
    }

    private static byte[] readAssetFile(String path, boolean zip) throws IOException {
        File f = new File(path);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        InputStream is = null;
        try {
            is = new FileInputStream(f);
            byte[] buf = new byte[4096];
            int read;
            while ((read = is.read(buf)) >= 0) {
                os.write(buf, 0, read);
            }
//          Log.d(TAG, "compressed model read finished");
        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            try {
                is.close();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return os.toByteArray();
    }

    public static void main(String[] args) {
        DictComposer dict_composer = new DictComposer();
        dict_composer.write(args[0], args[1], args[2], args[3], args[4], args[5]);
    }
}
