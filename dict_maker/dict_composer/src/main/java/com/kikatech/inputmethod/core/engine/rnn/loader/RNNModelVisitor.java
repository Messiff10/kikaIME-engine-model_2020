package com.kikatech.inputmethod.core.engine.rnn.loader;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by tianli on 17-9-4.
 */

public class RNNModelVisitor {
    private RandomAccessFile mModel;

    public RNNModelVisitor(RandomAccessFile file){
        mModel = file;
    }

    public int readInt() throws IOException{
        int ch1 = mModel.read();
        int ch2 = mModel.read();
        int ch3 = mModel.read();
        int ch4 = mModel.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new EOFException();
        }
        // Big Endian
        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
    }

    public long readLong() throws IOException{
        // Big Endian
        return ((long)(readInt()) << 32) + (readInt() & 0xFFFFFFFFL);
    }

    public byte[] read(long length) throws IOException {
        byte[] result = new byte[(int)length];

        int got = 0;
        int left = (int)length;
        while (got < length) {
            int n = mModel.read(result, got, left);
            if (n < 0) {
                // EOF, should we raise exception?
                break;
            }
            else {
                got += n;
                left -= n;
            }
        }

        return result;
    }

    public void seek(long offset) throws IOException{
        mModel.seek(offset);
    }

    public void skip(int skip) throws IOException {
        mModel.skipBytes(skip);
    }

    public String readLine() throws IOException {
        return mModel.readLine();
    }

    public long getOffset() throws IOException{
        return mModel.getFilePointer();
    }
}
