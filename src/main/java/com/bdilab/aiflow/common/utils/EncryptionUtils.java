package com.bdilab.aiflow.common.utils;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

/**
 * @Decription TODO
 * @Author Humphrey
 * @Date 2019/9/12 15:02
 * @Version 1.0
 **/
public class EncryptionUtils {
    /**
     * RSA的加密长度只能加密117bytes的内容
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * 一次只能解密256bytes的内容
     */
    private static final int MAX_DECRYPT_BLOCK=256;


    /**
     * 私钥存放地址
     */

    /**
     * 使用私钥加密
     * @param data
     * @return
     * @throws Exception
     */

    public static String encryptLicense(String data,String keystorePath,String certificatePath) throws Exception{
        String encryptData ="";
        FileInputStream in = new FileInputStream(keystorePath);

        // JKS: Java KeyStoreJKS，可以有多种类型
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(in, "123456".toCharArray());
        in.close();

        // 记录的别名
        String alias = "mykey";
        // 记录的访问密码
        String pswd = "123456";

        //获取私钥
        PrivateKey privateKey = (PrivateKey) ks.getKey(alias, pswd.toCharArray());
        //私钥加密
        Cipher cipher = Cipher.getInstance("rsa");
        SecureRandom random = new SecureRandom();
        cipher.init(Cipher.ENCRYPT_MODE, privateKey, random);

        try {
            int length = data.getBytes().length;
            int offset = 0;
            byte[] cache;
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            int i = 0;
            while(length - offset > 0){
                if(length - offset > MAX_ENCRYPT_BLOCK){
                    cache = cipher.doFinal(data.getBytes(), offset, MAX_ENCRYPT_BLOCK);
                }else{
                    cache = cipher.doFinal(data.getBytes(), offset, length - offset);
                }
                outStream.write(cache, 0, cache.length);
                i++;
                offset = i * MAX_ENCRYPT_BLOCK;
            }
            return Base64.getEncoder().encodeToString(outStream.toByteArray());
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return encryptData;
    }

    /**
     * 使用公钥解密
     * @param data
     * @return
     * @throws Exception
     */
    public static String decryptLicense(String data,String certificatePath) throws Exception{
        //证书读取流
        FileInputStream is = new FileInputStream(certificatePath);
        //获取X.509的证书工厂
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        //获取证书实例
        X509Certificate certificate = (X509Certificate) cf.generateCertificate(is);

        // 获得公钥
        PublicKey publicKey = certificate.getPublicKey();

        Cipher cipher = Cipher.getInstance("rsa");
        SecureRandom random = new SecureRandom();

        byte[] bEncrypt = Base64.getDecoder().decode(data);
        //公钥解密
        cipher.init(Cipher.DECRYPT_MODE, publicKey, random);

        int inputLen = bEncrypt.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(bEncrypt, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(bEncrypt, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return  new String(decryptedData);
    }

    public static String getMd5(String str) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

