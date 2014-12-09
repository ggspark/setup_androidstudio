package com.example.androidstuido_app_example.utils;

import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * @author Gaurav Gupta <gaurav@thegauravgupta.com>
 * @since 09/Dec/2014
 */

public class Utility {

    /**
     * Just pass in the InputStream and it will read the whole file and return it as a string
     * <p/>Eg: Utility.loadStringFromInputStream(getResources().openRawResource(R.raw.churches));
     *
     * @param is an InputStream which can be from a file or raw resource or asset
     *
     * @return String
     */
    public static String loadStringFromInputStream(InputStream is) {
        String st;
        try {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            st = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return st;

    }

    /**
     * Just pass in the String and it will return you the hexadecimal representation of md5 digest
     * <p/>Eg: Utility.md5("Hello");
     * <p/>Dependency: 'commons-codec:commons-codec:1.9'
     *
     * @param s The String to be digested
     *
     * @return md5 digest as hexadecimal String
     */
    public static String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest(); //Gives us the MD5 digest of password
            //Hex.encodeHex() changes the byte array to its hexadecimal char array representation
            return new String(Hex.encodeHex(messageDigest));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Just pass in the String and it will return you the hexadecimal representation of sha1 digest
     * <p/>Eg: Utility.sha1("Hello");
     * <p/>Dependency: 'commons-codec:commons-codec:1.9'
     *
     * @param s The String to be digested
     *
     * @return sha1 digest as hexadecimal String
     */
    public static String sha1(final String s) {
        try {
            // Create SHA-1 Hash
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest(); //Gives us the SHA1 digest of password
            //Hex.encodeHex() changes the byte array to its hexadecimal char array representation
            return new String(Hex.encodeHex(messageDigest));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * TODO - Make a notification function
     */


    /**
     * TODO - Take functions from Ordering app
     */
}
