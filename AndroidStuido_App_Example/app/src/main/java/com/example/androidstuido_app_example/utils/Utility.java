package com.example.androidstuido_app_example.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;

import com.google.gson.Gson;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
     * @param context
     *
     * @return IMEI for GSM and MEID for CDMA
     */
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }


    /**
     *
     * @param context
     * @return
     */
    public static String getPhoneNumber(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getLine1Number();
    }

    /**
     *
     * @param context
     * @return
     */
    public static String getPhoneOperator(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getNetworkOperatorName();
    }


    /**
     *
     * @param context
     * @return
     */
    public static List<String> getImagePaths(Context context) {
        ContentResolver cr = context.getContentResolver();
        Cursor cursor;
        LinkedList<String> paths = new LinkedList<String>();
        String[] projection = {MediaStore.Images.Media.DATA};
        cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,projection, null, null, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                paths.add(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));
            }
        }
        cursor.close();
        return paths;
    }

    public static Bitmap getImageThumbnails(Context context, String path){
        Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(path),90, 90);
        return bitmap;
    }

    public static Bitmap getImageFull(Context context, String path){
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        return bitmap;
    }



    /**
     * Get the last known location from all providers return best reading that is as accurate as minAccuracy meters and was taken no longer than minAge milliseconds ago, if none, return null.
     * If any of the parameters is less than or equal to zero, return best possible reading
     *
     * @param context
     * @param minAccuracy in meters
     * @param maxAge in milliseconds
     *
     * @return Location
     */
    public static Location getLastKnownLocation(Context context, float minAccuracy, long maxAge) {

        Location bestResult = null;
        float bestAccuracy = Float.MAX_VALUE;
        long bestAge = Long.MIN_VALUE;
        LocationManager locationManager;

        List<String> matchingProviders =(locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE)).getAllProviders();

        for (String provider : matchingProviders) {

            Location location = locationManager.getLastKnownLocation(provider);

            if (location != null) {

                float accuracy = location.getAccuracy();
                long time = location.getTime();

                if (accuracy < bestAccuracy) {

                    bestResult = location;
                    bestAccuracy = accuracy;
                    bestAge = time;

                }
            }
        }

        if (minAccuracy > 0 && maxAge > 0) {
            // Return best reading or null
            if (bestAccuracy > minAccuracy || (System.currentTimeMillis() - bestAge) > maxAge) {
                return null;
            }
            else {
                return bestResult;
            }
        }else
        {
            return bestResult;
        }
    }

    /**
     *
     * @param context
     * @return
     */
    public static List<Contact> getContacts(Context context){
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        LinkedList<Contact> contacts = new LinkedList<Contact>();
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                Contact contact = new Contact();
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                contact.name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    //Get phone numbers
                    Cursor phoneCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", new String[]{id}, null);
                    LinkedList<String> phoneNumber = new LinkedList<String>();
                    while (phoneCur.moveToNext()) {
                        String phoneNo = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneNumber.add(phoneNo);
                    }
                    contact.phoneNumbers= phoneNumber.toArray(new String[phoneNumber.size()]);
                    phoneCur.close();
                    //Get email addresses
                    Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",new String[]{id}, null);
                    LinkedList<String> emailAddress = new LinkedList<String>();
                    while (emailCur.moveToNext()) {
                        String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        emailAddress.add(email);
                    }
                    contact.emailAddresses= emailAddress.toArray(new String[emailAddress.size()]);
                    emailCur.close();
                    contacts.add(contact);
                }
            }
        }
        return contacts;
    }
    public static class Contact {
        public Contact(){}
        public String name;
        public String[] phoneNumbers;
        public String[] emailAddresses;
        public String toJson(){
            return (new Gson()).toJson(this);
        }
        public static Contact fromJson(String json){
            return (new Gson()).fromJson(json,Contact.class);
        }
        public static List<JSONObject> toJsonObjectList(List<Contact> contacts){
            List<JSONObject> jContacts = new ArrayList<JSONObject>(contacts.size());
            for(Contact contact: contacts)
                try {
                    jContacts.add(new JSONObject(contact.toJson()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            return jContacts;
        }
        public static List<Contact> fromJsonObjectList(List<JSONObject> jContacts){
            List<Contact> contacts = new ArrayList<Contact>(jContacts.size());
            for(JSONObject jContact: jContacts)
                contacts.add( Contact.fromJson(jContact.toString()));
            return contacts;
        }

    }

    public static class Image{
        public Image(){}
        public String devicePath;
        public String url;
        public String type;
        public String toJson(){
            return (new Gson()).toJson(this);
        }
        public static Image fromJson(String json){
            return (new Gson()).fromJson(json, Image.class);
        }
        public static List<JSONObject> toJsonObjectList(List<Image> images){
            List<JSONObject> jImages = new ArrayList<JSONObject>(images.size());
            for(Image image: images)
                try {
                    jImages.add(new JSONObject(image.toJson()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            return jImages;
        }
        public static List<Image> fromJsonObjectList(List<JSONObject> jImages){
            List<Image> images = new ArrayList<Image>(jImages.size());
            for(JSONObject jImage: jImages)
                images.add(Image.fromJson(jImage.toString()));
            return images;
        }
    }




}

