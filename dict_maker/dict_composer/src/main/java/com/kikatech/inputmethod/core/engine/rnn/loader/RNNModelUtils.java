package com.kikatech.inputmethod.core.engine.rnn.loader;

import java.util.logging.Logger;

import java.security.MessageDigest;

/**
 * Created by tianli on 17-9-7.
 */

public class RNNModelUtils {

    private static final String TAG = "RNNModelUtils";

    private static Logger log = Logger.getLogger("myLogger");

    public static byte[] getMD5ByteArray(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return new byte[0];
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            if(bytes.length <= 4096){
                return digest.digest(bytes);
            }else{
                // header
                byte[] part = new byte[4096];
//                Log.e("RNNModel", "bytes =  " + bytes.length);
                System.arraycopy(bytes, 0, part, 0, 1024);
                // middle
                int middle = (bytes.length - 4096)/2;
//                Log.e("RNNModel", "b =  " + bytes.length);
                System.arraycopy(bytes, 1024 + middle, part, 1024, 2048);
                // tail
                System.arraycopy(bytes, bytes.length - 1024, part, part.length - 1024, 1024);
                return digest.digest(part);
            }
        } catch (Exception e) {
            log.info(TAG + " getMD5ByteArray " + e);
        }
        return null;
    }

    public static boolean equals(byte[] lhs, byte[] rhs){
        if(lhs == null || lhs.length == 0){
            return rhs == null || rhs.length == 0;
        }
        if(rhs == null || rhs.length == 0){
            return false;
        }
        int min = Math.min(lhs.length, rhs.length);
        for(int i = 0; i < min; i++){
            if(lhs[i] != rhs[i]){
                return false;
            }
        }
        return true;
    }
}
