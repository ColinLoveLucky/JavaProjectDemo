/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.quark.cobra.Utils;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

/**
 * RSA非对称算法实现
 * @author xiaokai.sxk
 * @version $Id: AlgRSA.java, v 0.1 2016-08-02 9:46 xiaokai.sxk Exp $$
 */
public class RSAUtil {

    /** RSA 算法名字 */
    private static String KEY_ALGO_NAME = "RSA";

    /** 公钥名 */
    private static String PUBLICK_KEY   = "PUBLIC_KEY";

    /** 私钥名 */
    private static String PRIVATE_KEY   = "PRIVATE_KEY";

    public static final String ALGO_NAME = "RSA/ECB/PKCS1Padding";


    /**
     * 公共加密接口
     * @param data  待加密数据
     * @param key   秘钥
     * @param isPrivateKey  秘钥是否是私钥
     * @return  加密结果
     */
    public static byte[] encrypt(byte[] data, byte[] key, String algorithm, boolean isPrivateKey)
                                                                                                 throws Exception {

        Key secretKey = isPrivateKey ? recoverPrivateKey(key) : recoverPublicKey(key);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    /**
     * 公共解密接口
     * @param data 待解密数据
     * @param algorithm 算法
     * @param key   秘钥
     * @param isPrivateKey  秘钥是否为私钥
     * @return
     */
    public static byte[] decrypt(byte[] data, byte[] key, String algorithm, boolean isPrivateKey)
                                                                                                 throws Exception {
        Key secretKey = isPrivateKey ? recoverPrivateKey(key) : recoverPublicKey(key);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(data);

    }

    /**
     * 生成公私钥对
     * @param keySize
     * @return
     */
    public static Map<String, String> generateKey(int keySize) throws Exception {

        Map<String, String> rtn = new HashMap<String, String>();

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGO_NAME);
        keyPairGenerator.initialize(keySize);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        byte[] publicKey = keyPair.getPublic().getEncoded();
        byte[] privateKey = keyPair.getPrivate().getEncoded();
        rtn.put(PUBLICK_KEY, Base64Util.encode2Str(publicKey));
        rtn.put(PRIVATE_KEY, Base64Util.encode2Str(privateKey));

        return rtn;
    }

    /**
     * 恢复私钥
     * @param data
     * @return
     */
    public static PrivateKey recoverPrivateKey(byte[] data) throws Exception {

        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(data);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGO_NAME);
        return keyFactory.generatePrivate(pkcs8EncodedKeySpec);

    }

    /**
     * 恢复公钥
     * @param data
     * @return
     */
    public static PublicKey recoverPublicKey(byte[] data) throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(data);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGO_NAME);
        return keyFactory.generatePublic(keySpec);
    }


    /**
     * 解密接口
     * @param data
     * @param key
     * @param isPrivateKey
     * @return
     */
    public static String decrypt(String data, String key, String algoName, boolean isPrivateKey)throws Exception{

        byte[] dataByte = Base64Util.decode(data.getBytes());

        byte[] privateByte = Base64Util.decode(key.getBytes());

        byte[] rtnByte = decrypt(dataByte, privateByte, algoName, isPrivateKey);

        return new String(rtnByte);
    }

    public static void main(String[] args) throws  Exception{
        String original = "13265986584||316494646546486498||01||public";

        String algoName = "RSA/ECB/PKCS1Padding";

        String dataStr = "OHG447kqMDXuyHjxuJ2MiUbcGlrrkupTTZG23Cn1d9RxiwCjDHKzld3N5bn9wkIrnNTSPucJrcUSEPrOLQxUCG0t6ijqD0+SzwKooSEwLdpvoljzD+Wc6U6htDJ1+/S9PZpvlZ5BQGXt4tpTwMxBIFeTzF/ftcd/oA/Jdmb8Wgw=";

        String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAIjQTqoe9uNtNfBaHDYjqDz9StEb+13lKwOg6r0cr0mRsvYA0SXv0BV9Ha2v7EU4ghI0/ZnpcGR8tzyvOVV0v/iir6/poFfuC8zsW3sO4aRJaL7Zpc74+yP/HBvK6DJHUZzwXEOk7UKiG6W3l5Mc1jIXUOetrqXHefsRGyhJ9LERAgMBAAECgYBem+5KJm+EhhkuxKGNrsrAokgCot8I51j/gV/StutgbKjdBWOcYGH8+jBz05wHojQQcySMMqnOU5BDHsj/F2VIf+3TwS/uGuDwHmZgGUTqiFaKHSR7bEUqhdoUSuI/n1/XywNXulWrj7ZJdQPhy19PBQFdVNVtMPdQmmsffHETgQJBAMK5tcVcWRiz6yLnTXsD7BelGa4Z7bU/lh02Qj6BPWpay1KufXscx6vSdeHJrRzBoECpIxW3gLvJrX5gJw7CyMUCQQCz3XalIEEcYxJ1xVjqpbU6NWtOcNpvyhEkQW/1BlOd7WyONMkdIXjl6rA+7kLTBRns+hcqXVo+OwbXZdtGN9PdAkEAsfuIt3InRbr9yxNb8HqvSxVvGYE9kpMiJGU5u2PpvNJsUZCHxQWTQ+vEL7Jk9onMbg2qsejeU8aNO5urV4SWeQJBAJRJfnqOx/9uUpbT+AWkTnBKAEjwU7acYluAs5vP9noad0gajsA8i18KiPmpF2DAMOqmGRktU6xAn9hPumL+veUCQCtYnIdMh1OHx2hs3oWzWGm7OgtZtDEkNZD79aYU24FVIR0oQocLGdsbrmocLpljfpiiccneLu8b7jysyk5LXc4=";


        try {
            System.out.println(RSAUtil.decrypt(dataStr, privateKey, algoName, true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
