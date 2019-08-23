package com.mg.sn.utils.security;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.mg.sn.utils.common.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class SecurityUtils {

    private static final Logger log = LoggerFactory.getLogger(SecurityUtils.class);

    private static final String hexDigIts[] = {"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"};

    private static final String public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDXErdRy/ZamvJBMubIN5P5hTOi56pT6wVIdEHcAf7qbaz6l1TmZq/URjYhlDocbiIOO9FW/yQTf/iiBjwtLjoPgickRMHEcOKouiaVAaDwS4bDexbGRHZzTll8ByXsJ4+G6Fvq+PeoksJEqJNgtR4xIxAeeHXOvof/XUmCdvrFBwIDAQAB";

    /**
     * 32位大写MD5
     * @param origin  字符
     * @param charsetname  编码
     * @param secretType  秘钥种类
     * @return
     */
    public static String MD5Encode (String origin, String charsetname, String secretType) {

        if (StringUtils.isEmpty(origin)) {
            return null;
        }

        String resultString = "";
        try {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance(secretType);
            if (StringUtils.isEmpty(charsetname)) {
                resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
            }else {
                resultString = byteArrayToHexString(md.digest(resultString.getBytes(charsetname)));
            }
        } catch (Exception  e) {
            log.error(secretType + "加密失败," + e.getMessage());
            e.printStackTrace();
        }
        return resultString.toUpperCase();
    }

    public static String byteArrayToHexString(byte b[]){
        StringBuffer resultSb = new StringBuffer();
        for(int i = 0; i < b.length; i++){
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }

    public static String byteToHexString(byte b){
        int n = b;
        if(n < 0){
            n += 256;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigIts[d1] + hexDigIts[d2];
    }

    public static void main (String[] args) {
        long currentTime = TimeUtils.getCurrentTime();
        System.out.println("tt--" + System.currentTimeMillis() / 1000);

        String MD5Result = MD5Encode("10585" + "d26522e33d694aaddb693c9c99fbd964" + currentTime, "UTF-8", "MD5");
        System.out.println(MD5Result);

        try {
            RSAPublicKey rsaPublicKey = loadPublicKey(public_key);
            String encrypt = encrypt(rsaPublicKey, "1565857866STAR_NODE".getBytes());
            System.out.println("encrypt-------" + encrypt);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 根据公钥字符串加载公钥
     *
     * @param publicKeyStr 公钥字符串
     * @return
     * @throws Exception
     */
    public static RSAPublicKey loadPublicKey(String publicKeyStr) throws Exception {
        try {
            byte[] buffer = javax.xml.bind.DatatypeConverter.parseBase64Binary(publicKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法", e);
        } catch (InvalidKeySpecException e) {
            throw new Exception("公钥非法", e);
        } catch (NullPointerException e) {
            throw new Exception("公钥数据为空", e);
        }
    }

    /**
     * 公钥加密
     *
     * @param publicKey     公钥
     * @param plainTextData 明文数据
     * @return
     * @throws Exception 加密过程中的异常信息
     */
    public static String encrypt(RSAPublicKey publicKey, byte[] plainTextData) throws Exception {
        if (publicKey == null) {
            throw new Exception("加密公钥为空, 请设置");
        }
        Cipher cipher = null;
        try {
            // 使用默认RSA
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] output = cipher.doFinal(plainTextData);
            return base64ToStr(output);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此加密算法");
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e) {
            throw new Exception("加密公钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("明文长度非法");
        } catch (BadPaddingException e) {
            throw new Exception("明文数据已损坏");
        }
    }

    public static String base64ToStr(byte[] b) {
        return javax.xml.bind.DatatypeConverter.printBase64Binary(b);
    }

}
