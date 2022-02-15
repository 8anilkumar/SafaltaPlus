package com.safaltaclass.plus.utility;

import android.content.Context;
import android.content.SharedPreferences;

import com.safaltaclass.plus.SafaltaPlusApplication;

import java.util.HashSet;
import java.util.Set;

public class SafaltaPlusPreferences {

    public static final String PREFS_NAME = "SAFALATAPLUS";
    public static final String KEY_USER = "USERKEY";
    public static final String KEY_USER_TYPE = "USERTYPE";
    public static final String KEY_DEVICE_ID = "ANDROIDID";
    public static final String KEY_SAVEDATA = "SAVEDATA";
    public static final String KEY_NAME = "KEYNAME";
    public static final String KEY_EMAIL = "KEYEMAIL";
    public static final String KEY_MOBILE = "KEYMOBILE";
    public static final String KEY_DEVICE_BRAND = "KEYDEVICEBRAND";
    public static final String KEY_DEVICE_MODEL = "KEYDEVICEMODEL";
    public static final String KEY_OSVERSION = "KEYOSVERSION";
    public static final String LATITUDE = "LATITUDE";
    public static final String LONGITUDE = "LONGITUDE";
    public static final String CITY = "CITY";
    public static final String STATE = "STATE";
    public static final String COUNTRY = "COUNTRY";
    public static final String COUNTRYCODE = "COUNTRYCODE";
    public static final String POSTALCODE = "POSTALCODE";
    public static final String SUBADMINAREA = "SUBADMINAREA";
    public static final String ISROOTED = "ISROOTED";
    public static final String VIDEOSAVED = "VIDEOSAVED";
    public static final String VIDEOPENDING = "VIDEOPENDING";
    public static final String VIDEOSTATUS = "VIDEOSTATUS";


    private static SafaltaPlusPreferences sInstance;
    private final SharedPreferences mPref;
    private SharedPreferences.Editor editor;

    private SafaltaPlusPreferences(Context context) {
        mPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SafaltaPlusPreferences getInstance() {
        if (sInstance == null) {
            sInstance = new SafaltaPlusPreferences(SafaltaPlusApplication.getAppContext());
        }
        return sInstance;
    }

    public boolean saveLoginData(String userKey, String userType, String name, String email, String mobile) {
        editor = mPref.edit();

        editor.putString(KEY_USER, userKey);
        editor.putString(KEY_USER_TYPE, userType);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_MOBILE, mobile);
        editor.putBoolean(KEY_SAVEDATA,true);
        if(editor != null && editor.commit())
            return true;
        else
            return false;
    }

    public boolean saveDeviceId(String deviceId,String brand,String model,String version,String isRooted){
        editor = mPref.edit();

        editor.putString(KEY_DEVICE_ID,deviceId );
        editor.putString(KEY_DEVICE_BRAND, brand);
        editor.putString(KEY_DEVICE_MODEL, model);
        editor.putString(KEY_OSVERSION, version);
        editor.putString(ISROOTED,isRooted);
        if(editor != null && editor.commit())
            return true;
        else
            return false;
    }

    public boolean saveLocation(String latitude,String longitude,String city,String state,String country,String countryCode,String postalCode,String subAdminArea){

        editor = mPref.edit();

        editor.putString(LATITUDE,latitude );
        editor.putString(LONGITUDE, longitude);
        editor.putString(CITY, city);
        editor.putString(STATE, state);
        editor.putString(COUNTRY, country);
        editor.putString(COUNTRYCODE, countryCode);
        editor.putString(POSTALCODE, postalCode);
        editor.putString(SUBADMINAREA, subAdminArea);

        if(editor.commit())
            return true;
        else
            return false;
    }

    public String getLatitude() {
        String latitude;
        latitude = mPref.getString(LATITUDE, "");
        return latitude;
    }

    public String getLongitude() {
        String longitude;
        longitude = mPref.getString(LONGITUDE, "");
        return longitude;
    }

    public String getCity() {
        String city;
        city = mPref.getString(CITY, "");
        return city;
    }

    public String getState() {
        String state;
        state = mPref.getString(STATE, "");
        return state;
    }

    public String getCountry() {
        String country;
        country = mPref.getString(COUNTRY, "");
        return country;
    }

    public String getCountrycode() {
        String countryCode;
        countryCode = mPref.getString(COUNTRYCODE, "");
        return countryCode;
    }

    public String getPostalcode() {
        String postalCode;
        postalCode = mPref.getString(POSTALCODE, "");
        return postalCode;
    }

    public String getSubAdminArea() {
        String subAdminArea;
        subAdminArea = mPref.getString(SUBADMINAREA, "");
        return subAdminArea;
    }

    public String getIsrooted() {
        String isRooted;
        isRooted = mPref.getString(ISROOTED, "");
        return isRooted;
    }

    public String getKeyUser() {
        String name;
        name = mPref.getString(KEY_USER, "");
        return name;
    }

    public String getName() {
        String name;
        name = mPref.getString(KEY_NAME, "");
        return name;
    }

    public String getEmail() {
        String email;
        email = mPref.getString(KEY_EMAIL, "");
        return email;
    }

    public String getMobile() {
        String mobile;
        mobile = mPref.getString(KEY_MOBILE, "");
        return mobile;
    }

    public String getKeyUserType() {
        String mobile;
        mobile = mPref.getString(KEY_USER_TYPE, null);
        return mobile;
    }

    public String getKeyDeviceId() {
        String deviceId;
        deviceId = mPref.getString(KEY_DEVICE_ID, "");
        return deviceId;
    }

    public String getDeviceBrand() {
        String brand;
        brand = mPref.getString(KEY_DEVICE_BRAND, "");
        return brand;
    }

    public String getDeviceModel() {
        String model;
        model = mPref.getString(KEY_DEVICE_MODEL, "");
        return model;
    }

    public String getOsVersion() {
        String version;
        version = mPref.getString(KEY_OSVERSION, "");
        return version;
    }

    public boolean isDataSave(Context context){
        boolean isData;
        isData = mPref.getBoolean(KEY_SAVEDATA,false);
        return isData;
    }

    public void clearSharedPreference(Context context) {
        editor = mPref.edit();
        editor.clear();
        editor.commit();
    }

    public void removeData() {
        editor = mPref.edit();
        editor.remove(KEY_USER);
        editor.remove(KEY_NAME);
        editor.remove(KEY_EMAIL);
        editor.remove(KEY_MOBILE);
        editor.remove(KEY_USER_TYPE);

        editor.commit();
    }

    public  void addDownloaded(String UniqueId) {
        editor = mPref.edit();
        editor.putString(VIDEOSAVED, UniqueId);
        editor.commit();
    }

    public void removeDownloaded() {
        editor = mPref.edit();
        editor.remove(VIDEOSAVED);
        editor.commit();
    }
    public  String isDownloaded() {
        String id;
        id = mPref.getString(VIDEOSAVED, "");
        return id;
    }

    public  void addDownloading(String UniqueId) {
        editor = mPref.edit();
        editor.putString(VIDEOPENDING, UniqueId);
        editor.commit();
    }

    public void removeDownloading() {
        editor = mPref.edit();
        editor.remove(VIDEOPENDING);
        editor.commit();
    }

    public  String isDownloading() {
        String id;
        id = mPref.getString(VIDEOPENDING, "");
        return id;
    }

    public  void addVideoDownloadStatus(String UniqueId) {
        editor = mPref.edit();
        editor.putString(VIDEOSTATUS, UniqueId);
        editor.commit();
    }

    public void removeVideoDownloadStaus() {
        editor = mPref.edit();
        editor.remove(VIDEOSTATUS);
        editor.commit();
    }

    public  String isStatus() {
        String id;
        id = mPref.getString(VIDEOSTATUS, "");
        return id;
    }


    public void removeAllPendingVideoStatus(){
        editor = mPref.edit();
        editor.remove(VIDEOSTATUS);
        editor.commit();
    }

    /*

    public boolean saveLoginData(Context context, String userKey, String userType, String name, String email, String mobile) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
        editor = settings.edit(); //2

        editor.putString(KEY_USER, userKey); //3
        editor.putString(KEY_USER_TYPE, userType);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_MOBILE, mobile);
        editor.putBoolean(KEY_SAVEDATA,true);
        if(editor.commit())
            return true;
        else
            return false;
    }

    public boolean saveDeviceId(Context context,String deviceId,String brand,String model,String version){
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
        editor = settings.edit();

        editor.putString(KEY_DEVICE_ID,deviceId );
        editor.putString(KEY_DEVICE_BRAND, brand);
        editor.putString(KEY_DEVICE_MODEL, model);
        editor.putString(KEY_OSVERSION, version);
        if(editor.commit())
            return true;
        else
            return false;
    }

    public boolean saveLocation(Context context,String latitude,String longitude){
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
        editor = settings.edit();

        editor.putString(LATITUDE,latitude );
        editor.putString(LONGITUDE, longitude);
        if(editor.commit())
            return true;
        else
            return false;
    }

    public static String getLatitude(Context context) {
        SharedPreferences settings;
        String latitude;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        latitude = settings.getString(LATITUDE, "");
        return latitude;
    }

    public static String getLongitude(Context context) {
        SharedPreferences settings;
        String longitude;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        longitude = settings.getString(LONGITUDE, "");
        return longitude;
    }

    public static String getKeyUser(Context context) {
        SharedPreferences settings;
        String name;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        name = settings.getString(KEY_USER, "");
        return name;
    }

    public static String getName(Context context) {
        SharedPreferences settings;
        String name;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        name = settings.getString(KEY_NAME, "");
        return name;
    }

    public static String getEmail(Context context) {
        SharedPreferences settings;
        String email;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        email = settings.getString(KEY_EMAIL, "");
        return email;
    }

    public static String getMobile(Context context) {
        SharedPreferences settings;
        String mobile;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        mobile = settings.getString(KEY_MOBILE, "");
        return mobile;
    }

    public String getKeyUserType(Context context) {
        SharedPreferences settings;
        String mobile;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        mobile = settings.getString(KEY_USER_TYPE, null);
        return mobile;
    }

    public static String getKeyDeviceId(Context context) {
        SharedPreferences settings;
        String deviceId;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        deviceId = settings.getString(KEY_DEVICE_ID, "");
        return deviceId;
    }

    public static String getDeviceBrand(Context context) {
        SharedPreferences settings;
        String brand;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        brand = settings.getString(KEY_DEVICE_BRAND, "");
        return brand;
    }

    public static String getDeviceModel(Context context) {
        SharedPreferences settings;
        String model;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        model = settings.getString(KEY_DEVICE_MODEL, "");
        return model;
    }

    public static String getOsVersion(Context context) {
        SharedPreferences settings;
        String version;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        version = settings.getString(KEY_OSVERSION, "");
        return version;
    }

    public boolean isDataSave(Context context){
        SharedPreferences settings;
        boolean isData;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        isData = settings.getBoolean(KEY_SAVEDATA,false);
        return isData;
    }

    public void clearSharedPreference(Context context) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.clear();
        editor.commit();
    }

    public void removeData(Context context) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.remove(KEY_USER);
        editor.remove(KEY_NAME);
        editor.remove(KEY_EMAIL);
        editor.remove(KEY_MOBILE);
        editor.remove(KEY_USER_TYPE);
        editor.remove(KEY_DEVICE_ID);
        editor.remove(KEY_OSVERSION);
        editor.remove(KEY_DEVICE_BRAND);
        editor.remove(KEY_DEVICE_MODEL);

        editor.commit();
    }*/
}
