package com.bhkj.pdjhforotherapp.common.utils;

import android.annotation.SuppressLint;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.bhkj.pdjhforotherapp.app.App;
import com.blankj.utilcode.util.AppUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Tools {

    /**
     * 通过拼接serial和设备id构成设备唯一识别id
     */
    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getDeviceId() {
        int min = 10;
        int max = 99;
        Random random = new Random();
        return String.valueOf(random.nextInt(max)%(max-min+1)+min);
    }


    //TODO:android与服务器之间传输数据加解密 RSAUtils
    public final static class RSAUtils {
        private static final String ALGORITHM = "RSA";
        private static final String TRANSFORMATION = "RSA/None/PKCS1Padding";

        /**
         * 从文件中输入流中加载公钥
         *
         * @param in 公钥输入流
         * @throws Exception 加载公钥时产生的异常
         */
        public static RSAPublicKey loadPublicKey(InputStream in) throws Exception {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String readLine = null;
                StringBuilder sb = new StringBuilder();
                while ((readLine = br.readLine()) != null) {
                    if (readLine.charAt(0) == '-') {
                        continue;
                    } else {
                        sb.append(readLine);
                        sb.append('\r');
                    }
                }
                return loadPublicKey(sb.toString());
            } catch (IOException e) {
                throw new Exception("公钥数据流读取错误");
            } catch (NullPointerException e) {
                throw new Exception("公钥输入流为空");
            }
        }

        /**
         * 从字符串中加载公钥
         *
         * @param publicKeyStr 公钥数据字符串
         * @return
         * @throws Exception 加载公钥时产生的异常
         */
        public static RSAPublicKey loadPublicKey(String publicKeyStr)
                throws Exception {
            try {
                byte[] buffer = Base64.decode(publicKeyStr, Base64.DEFAULT);
                KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
                return (RSAPublicKey) keyFactory.generatePublic(keySpec);
            } catch (NoSuchAlgorithmException e) {
                throw new Exception("无此算法");
            } catch (InvalidKeySpecException e) {
                throw new Exception("公钥非法");
            } catch (NullPointerException e) {
                throw new Exception("公钥数据为空");
            }
        }

        /**
         * 从文件中加载私钥
         *
         * @param in 私钥输入流
         * @return
         * @throws Exception
         */
        public static RSAPrivateKey loadPrivateKey(InputStream in) throws Exception {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String readLine = null;
                StringBuilder sb = new StringBuilder();
                while ((readLine = br.readLine()) != null) {
                    if (readLine.charAt(0) == '-') {
                        continue;
                    } else {
                        sb.append(readLine);
                        sb.append('\r');
                    }
                }
                return loadPrivateKey(sb.toString());
            } catch (IOException e) {
                throw new Exception("私钥数据读取错误");
            } catch (NullPointerException e) {
                throw new Exception("私钥输入流为空");
            }
        }

        /**
         * 从字符串中加载私钥
         *
         * @param privateKeyStr 私钥字符串
         * @return
         * @throws Exception
         * @desc
         */
        public static RSAPrivateKey loadPrivateKey(String privateKeyStr)
                throws Exception {
            try {
                byte[] buffer = Base64.decode(privateKeyStr, Base64.DEFAULT);
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
                KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
                return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
            } catch (NoSuchAlgorithmException e) {
                throw new Exception("无此算法");
            } catch (InvalidKeySpecException e) {
                throw new Exception("私钥非法");
            } catch (NullPointerException e) {
                throw new Exception("私钥数据为空");
            }
        }

        /**
         * 公钥加密
         *
         * @param data
         * @param publicKey
         * @return
         * @throws Exception
         */
        public static String encryptByPublicKey(String data, RSAPublicKey publicKey)
                throws Exception {
            // 模长
            int key_len = publicKey.getModulus().bitLength() / 8;
            // 加密数据长度 <= 模长-11
            String[] datas = splitString(data, key_len - 11);
            String mi = "";
            // 如果明文长度大于模长-11则要分组加密
            for (String s : datas) {
                mi += bcd2Str(encryptByPublicKey(s.getBytes(), publicKey));
            }
            return mi;
        }

        /**
         * 公钥加密
         *
         * @param data
         * @param publicKey
         * @return
         * @throws Exception
         * @desc
         */
        public static byte[] encryptByPublicKey(byte[] data, RSAPublicKey publicKey)
                throws Exception {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(data);
        }

        /**
         * 私钥加密
         *
         * @param data
         * @param privateKey
         * @return
         * @throws Exception
         * @desc
         */
        public static byte[] encryptByPrivateKey(byte[] data,
                                                 RSAPrivateKey privateKey) throws Exception {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return cipher.doFinal(data);
        }


        /**
         * 私钥加密
         *
         * @param data
         * @param privateKey
         * @return
         * @throws Exception
         * @desc
         */
        public static String encryptByPrivateKey(String data,
                                                 RSAPrivateKey privateKey) throws Exception {
            // 模长
            int key_len = privateKey.getModulus().bitLength() / 8;
            // 加密数据长度 <= 模长-11
            String[] datas = splitString(data, key_len - 11);
            String mi = "";
            // 如果明文长度大于模长-11则要分组加密
            for (String s : datas) {
                mi += bcd2Str(encryptByPrivateKey(s.getBytes(), privateKey));
            }
            return mi;
        }

        /**
         * 私钥解密
         *
         * @param data
         * @param privateKey
         * @return
         * @throws Exception
         */
        public static String decryptByPrivateKey(String data,
                                                 RSAPrivateKey privateKey) throws Exception {
            // 模长
            int key_len = privateKey.getModulus().bitLength() / 8;
            byte[] bytes = data.getBytes();
            byte[] bcd = ASCII_To_BCD(bytes, bytes.length);
            // 如果密文长度大于模长则要分组解密
            String ming = "";
            byte[][] arrays = splitArray(bcd, key_len);
            for (byte[] arr : arrays) {
                ming += new String(decryptByPrivateKey(arr, privateKey));
            }
            return ming;
        }

        /**
         * 私钥解密
         *
         * @param data
         * @param privateKey
         * @return
         * @throws Exception
         * @desc
         */
        public static byte[] decryptByPrivateKey(byte[] data,
                                                 RSAPrivateKey privateKey) throws Exception {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(data);
        }

        /**
         * 公钥解密
         *
         * @param data
         * @param publicKey
         * @return
         * @throws Exception
         * @desc
         */
        public static String decryptByPublicKey(String data,
                                                RSAPublicKey publicKey) throws Exception {
            // 模长
            int key_len = publicKey.getModulus().bitLength() / 8;
            byte[] bytes = data.getBytes();
            byte[] bcd = ASCII_To_BCD(bytes, bytes.length);
            // 如果密文长度大于模长则要分组解密
            String ming = "";
            byte[][] arrays = splitArray(bcd, key_len);
            for (byte[] arr : arrays) {
                ming += new String(decryptByPublicKey(arr, publicKey));
            }
            return ming;
        }

        /**
         * 公钥解密
         *
         * @param data
         * @param publicKey
         * @return
         * @throws Exception
         * @desc
         */
        public static byte[] decryptByPublicKey(byte[] data,
                                                RSAPublicKey publicKey) throws Exception {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return cipher.doFinal(data);
        }

        /**
         * ASCII码转BCD码
         */
        private static byte[] ASCII_To_BCD(byte[] ascii, int asc_len) {
            byte[] bcd = new byte[asc_len / 2];
            int j = 0;
            for (int i = 0; i < (asc_len + 1) / 2; i++) {
                bcd[i] = asc_to_bcd(ascii[j++]);
                bcd[i] = (byte) (((j >= asc_len) ? 0x00 : asc_to_bcd(ascii[j++])) + (bcd[i] << 4));
            }
            return bcd;
        }

        private static byte asc_to_bcd(byte asc) {
            byte bcd;

            if ((asc >= '0') && (asc <= '9'))
                bcd = (byte) (asc - '0');
            else if ((asc >= 'A') && (asc <= 'F'))
                bcd = (byte) (asc - 'A' + 10);
            else if ((asc >= 'a') && (asc <= 'f'))
                bcd = (byte) (asc - 'a' + 10);
            else
                bcd = (byte) (asc - 48);
            return bcd;
        }

        /**
         * BCD转字符串
         */
        private static String bcd2Str(byte[] bytes) {
            char temp[] = new char[bytes.length * 2], val;

            for (int i = 0; i < bytes.length; i++) {
                val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);
                temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');

                val = (char) (bytes[i] & 0x0f);
                temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
            }
            return new String(temp);
        }

        /**
         * 拆分字符串
         */
        private static String[] splitString(String string, int len) {
            int x = string.length() / len;
            int y = string.length() % len;
            int z = 0;
            if (y != 0) {
                z = 1;
            }
            String[] strings = new String[x + z];
            String str = "";
            for (int i = 0; i < x + z; i++) {
                if (i == x + z - 1 && y != 0) {
                    str = string.substring(i * len, i * len + y);
                } else {
                    str = string.substring(i * len, i * len + len);
                }
                strings[i] = str;
            }
            return strings;
        }

        /**
         * 拆分数组
         */
        private static byte[][] splitArray(byte[] data, int len) {
            int x = data.length / len;
            int y = data.length % len;
            int z = 0;
            if (y != 0) {
                z = 1;
            }
            byte[][] arrays = new byte[x + z][];
            byte[] arr;
            for (int i = 0; i < x + z; i++) {
                arr = new byte[len];
                if (i == x + z - 1 && y != 0) {
                    System.arraycopy(data, i * len, arr, 0, y);
                } else {
                    System.arraycopy(data, i * len, arr, 0, len);
                }
                arrays[i] = arr;
            }
            return arrays;
        }
    }

    public final static class AESUtil {

        // /** 算法/模式/填充 **/
        private static final String CipherMode = "AES/ECB/PKCS5Padding";
        // private static final String CipherMode = "AES";

        /**
         * 生成一个AES密钥对象
         *
         * @return
         */
        public static SecretKeySpec generateKey() {
            try {
                KeyGenerator kgen = KeyGenerator.getInstance("AES");
                kgen.init(128, new SecureRandom());
                SecretKey secretKey = kgen.generateKey();
                byte[] enCodeFormat = secretKey.getEncoded();
                SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
                return key;
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 生成一个AES密钥字符串
         *
         * @return
         */
        public static String generateKeyString() {
            return byte2hex(generateKey().getEncoded());
        }

        /**
         * 加密字节数据
         *
         * @param content
         * @param key
         * @return
         */
        public static byte[] encrypt(byte[] content, byte[] key) {
            try {
                Cipher cipher = Cipher.getInstance(CipherMode);
                cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
                byte[] result = cipher.doFinal(content);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 通过byte[]类型的密钥加密String
         *
         * @param content
         * @param key
         * @return 16进制密文字符串
         */
        public static String encrypt(String content, byte[] key) {
            try {
                Cipher cipher = Cipher.getInstance(CipherMode);
                cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
                byte[] data = cipher.doFinal(content.getBytes("UTF-8"));
                String result = byte2hex(data);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 通过String类型的密钥加密String
         *
         * @param content
         * @param key
         * @return 16进制密文字符串
         */
        public static String encrypt(String content, String key) {
            byte[] data = null;
            try {
                data = content.getBytes("UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            data = encrypt(data, new SecretKeySpec(hex2byte(key), "AES").getEncoded());
            return byte2hex(data);
        }

        /**
         * 通过byte[]类型的密钥解密byte[]
         *
         * @param content
         * @param key
         * @return
         */
        public static byte[] decrypt(byte[] content, byte[] key) {
            try {
                Cipher cipher = Cipher.getInstance(CipherMode);
                cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"));
                byte[] result = cipher.doFinal(content);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 通过String类型的密钥 解密String类型的密文
         *
         * @param content
         * @param key
         * @return
         */
        public static String decrypt(String content, String key) {
            byte[] data = null;
            try {
                data = hex2byte(content);
            } catch (Exception e) {
                e.printStackTrace();
            }
            data = decrypt(data, hex2byte(key));
            if (data == null)
                return null;
            String result = null;
            try {
                result = new String(data, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return result;
        }

        /**
         * 通过byte[]类型的密钥 解密String类型的密文
         *
         * @param content
         * @param key
         * @return
         */
        public static String decrypt(String content, byte[] key) {
            try {
                Cipher cipher = Cipher.getInstance(CipherMode);
                cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"));
                byte[] data = cipher.doFinal(hex2byte(content));
                return new String(data, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 字节数组转成16进制字符串
         *
         * @param b
         * @return
         */
        public static String byte2hex(byte[] b) { // 一个字节的数，
            final StringBuffer sb = new StringBuffer(b.length * 2);
            String tmp = "";
            for (byte aB : b) {
                // 整数转成十六进制表示
                tmp = (Integer.toHexString(aB & 0XFF));
                if (tmp.length() == 1) {
                    sb.append("0");
                }
                sb.append(tmp);
            }
            return sb.toString().toUpperCase(); // 转成大写
        }

        /**
         * 将hex字符串转换成字节数组
         *
         * @param inputString
         * @return
         */
        private static byte[] hex2byte(String inputString) {
            if (inputString == null || inputString.length() < 2) {
                return new byte[0];
            }
            inputString = inputString.toLowerCase();
            int l = inputString.length() / 2;
            byte[] result = new byte[l];
            for (int i = 0; i < l; ++i) {
                String tmp = inputString.substring(2 * i, 2 * i + 2);
                result[i] = (byte) (Integer.parseInt(tmp, 16) & 0xFF);
            }
            return result;
        }
    }


    public static String encodeRsa(String msg) {
        RSAPublicKey publicKey = null;
        String rsaContent = "";
        try {
            publicKey = RSAUtils.loadPublicKey(App.getCtx().getAssets().open("rsa_public_key.pem"));
            rsaContent = RSAUtils.encryptByPublicKey(msg, publicKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rsaContent;
    }
}
