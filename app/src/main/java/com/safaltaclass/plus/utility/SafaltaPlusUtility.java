package com.safaltaclass.plus.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.core.util.Pair;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class SafaltaPlusUtility {

    public static final String AES_ALGORITHM = "AES";
    public static final String AES_TRANSFORMATION = "AES/CTR/NoPadding";
    private static final int AES_BLOCK_SIZE = 16;

    private static SafaltaPlusUtility instance;

    private SafaltaPlusUtility(){ }

    public static synchronized SafaltaPlusUtility getInstance(){
        if(instance == null){
            instance = new SafaltaPlusUtility();
        }
        return instance;
    }

    public boolean isConnected(Context context) {
        boolean result = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
                if (nc != null && nc.getLinkDownstreamBandwidthKbps() > 50) {
                    result = true;
                } else {
                    result = false;
                }
            } else {
                NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
                return activeNetworkInfo != null && (activeNetworkInfo.isConnected() || activeNetworkInfo.isConnectedOrConnecting());
            }
        }
        return result;
    }

    private boolean isConnectionFast(int type, int subType) {
        if (type == ConnectivityManager.TYPE_WIFI) {
            return true;

        } else if (type == ConnectivityManager.TYPE_MOBILE) {

            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return false; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true; // ~ 400-7000 kbps
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    public static Cipher encrypt(String key, String salt,long bytesoffset)
    {
        try
        {
            byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            SecretKeyFactory factory;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            } else{
                factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            }
            KeySpec spec = new PBEKeySpec(key.toCharArray(), salt.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), AES_ALGORITHM);

            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);

            if(bytesoffset > 0L) {
                return jumpToOffset(cipher, secretKey, ivspec, bytesoffset);
            }else {
                return cipher;
            }
        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }

        return null;
    }

    public static Pair<Cipher, Pair<SecretKeySpec,IvParameterSpec>> decrypt(String key, String salt) {
        try
        {
            byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            SecretKeyFactory factory;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                 factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            } else{
                factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            }
            KeySpec spec = new PBEKeySpec(key.toCharArray(), salt.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec mSecretKeySpec = new SecretKeySpec(tmp.getEncoded(), "AES");

            // Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, mSecretKeySpec, ivspec);
            return new Pair<>(cipher,new Pair<>(mSecretKeySpec,ivspec));
        }
        catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }

    public static final Cipher jumpToOffset(final Cipher c,
                                          final SecretKey aesKey, final IvParameterSpec iv, final long offset) {
        if (!c.getAlgorithm().toUpperCase().startsWith("AES/CTR")) {
            throw new IllegalArgumentException(
                    "Invalid algorithm, only AES/CTR mode supported");
        }

        if (offset < 0) {
            throw new IllegalArgumentException("Invalid offset");
        }

        final int skip = (int) (offset % AES_BLOCK_SIZE);
        final IvParameterSpec calculatedIVForOffset = calculateIVForOffset(iv,
                offset - skip);
        try {
            c.init(Cipher.ENCRYPT_MODE, aesKey, calculatedIVForOffset);
            final byte[] skipBuffer = new byte[skip];
            c.update(skipBuffer, 0, skip, skipBuffer);
            Arrays.fill(skipBuffer, (byte) 0);
            return c;
        } catch (ShortBufferException | InvalidKeyException
                | InvalidAlgorithmParameterException e) {
            throw new IllegalStateException(e);
        }
    }

    private static IvParameterSpec calculateIVForOffset(final IvParameterSpec iv,
                                                        final long blockOffset) {
        final BigInteger ivBI = new BigInteger(1, iv.getIV());
        final BigInteger ivForOffsetBI = ivBI.add(BigInteger.valueOf(blockOffset
                / AES_BLOCK_SIZE));

        final byte[] ivForOffsetBA = ivForOffsetBI.toByteArray();
        final IvParameterSpec ivForOffset;
        if (ivForOffsetBA.length >= AES_BLOCK_SIZE) {
            ivForOffset = new IvParameterSpec(ivForOffsetBA, ivForOffsetBA.length - AES_BLOCK_SIZE,
                    AES_BLOCK_SIZE);
        } else {
            final byte[] ivForOffsetBASized = new byte[AES_BLOCK_SIZE];
            System.arraycopy(ivForOffsetBA, 0, ivForOffsetBASized, AES_BLOCK_SIZE
                    - ivForOffsetBA.length, ivForOffsetBA.length);
            ivForOffset = new IvParameterSpec(ivForOffsetBASized);
        }

        return ivForOffset;
    }

    public void copyByte(InputStream is, OutputStream os) throws IOException
    {
        byte[] buf = new byte[8192];
        int numbytes;
        while((numbytes = is.read(buf)) != -1)
        {
            os.write(buf, 0, numbytes);
            os.flush();
        }
        os.close();
        is.close();
    }


    public static boolean hasFile(File mEncryptedFile) {
        return mEncryptedFile != null
                && mEncryptedFile.exists()
                && mEncryptedFile.length() > 0;

    }
}
