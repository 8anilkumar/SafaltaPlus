package com.safaltaclass.plus.utility;

import com.google.android.exoplayer2.upstream.DataSource;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptedFileDataSourceFactory implements DataSource.Factory {

    private Cipher mCipher;
    private SecretKeySpec mSecretKeySpec;
    private IvParameterSpec mIvParameterSpec;


    public EncryptedFileDataSourceFactory(Cipher cipher, SecretKeySpec secretKeySpec, IvParameterSpec ivParameterSpec) {
        mCipher = cipher;
        mSecretKeySpec = secretKeySpec;
        mIvParameterSpec = ivParameterSpec;
    }

    @Override
    public EncryptedFileDataSource createDataSource() {
        return new EncryptedFileDataSource(mCipher, mSecretKeySpec, mIvParameterSpec);
    }

}
