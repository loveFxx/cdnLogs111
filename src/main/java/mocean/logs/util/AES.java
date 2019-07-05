package mocean.logs.util;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 使用3DES加密与解密,可对byte[],String类型进行加密与解密 密文可使用String,byte[]存储.
 *
 */

public class AES {
    private static final String Algorithm = "AES"; // 定义 加密算法,可用

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 获得秘密密钥
     *
     * @param secretKey
     * @return
     * @throws NoSuchAlgorithmException
     */
    private SecretKey generateKey(String secretKey) throws NoSuchAlgorithmException{

        //防止linux下 随机生成key
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(secretKey.getBytes());
        SecretKey returnSecretKey = null;
        try {
            // 为我们选择的DES算法生成一个KeyGenerator对象
            KeyGenerator kg = KeyGenerator.getInstance(Algorithm);
            kg.init(secureRandom);
            //kg.init(56, secureRandom);
            logger.info("key===" + kg.generateKey().hashCode());
            returnSecretKey = kg.generateKey();
        } catch (NoSuchAlgorithmException e) {
            logger.info(e.getMessage());
        }
        // 生成密钥
        return returnSecretKey;
    }


    /**
     * 加密String明文输入,String密文输出
     *
     * @param strMing
     *            String明文
     * @return String密文 16进制格式
     */
    public String encryptToHexString(String strMing, String password) {
        byte[] byteMi = null;
        byte[] byteMing = null;
        String strMi = "";
        try {
            byteMing = strMing.getBytes();
            byteMi = this.getEncCode(byteMing, password);
            strMi = AES.byteArr2HexStr(byteMi);
        } catch (Exception e) {
            logger.info(e.getMessage());
        } finally {
            byteMing = null;
            byteMi = null;
        }
        return strMi;
    }

    /**
     * 加密String明文输入,String密文输出
     *
     * @param strMing
     *            String明文
     * @return String密文 Base64编码
     */
    public String encryptToBase64String(String strMing, String password) {
        byte[] byteMi = null;
        byte[] byteMing = null;
        String strMi = "";
        try {
            byteMing = strMing.getBytes();
            byteMi = this.getEncCode(byteMing, password);
            strMi = Base64.encodeBase64String(byteMi);
        } catch (Exception e) {
            logger.info(e.getMessage());
        } finally {
            byteMing = null;
            byteMi = null;
        }
        return strMi;
    }

    /**
     * 解密 以String密文输入,String明文输出
     *
     * @param strMi
     *            String密文
     * @return String明文
     */
    public String decryptFromHexString(String strMi, String password) {
        byte[] byteMing = null;
        byte[] byteMi = null;
        String strMing = "";
        try {
            byteMi = AES.hexStr2ByteArr(strMi);
            byteMing = this.getDesCode(byteMi, password);
            strMing = new String(byteMing);
        } catch (Exception e) {
            logger.info(e.getMessage());
        } finally {
            byteMing = null;
            byteMi = null;
        }
        return strMing;
    }

    /**
     * 解密 以String密文输入,String明文输出
     *
     * @param strMi
     *            String密文 Base64格式
     * @return String明文
     */
    public String decryptFromBase64String(String strMi, String password) {
        byte[] byteMing = null;
        byte[] byteMi = null;
        String strMing = "";
        try {
            byteMi = Base64.decodeBase64(strMi);
            byteMing = this.getDesCode(byteMi, password);
            logger.info("byteMing===" + byteMing);
            strMing = new String(byteMing);
        } catch (Exception e) {
            logger.info(e.getMessage());
        } finally {
            byteMing = null;
            byteMi = null;
        }
        return strMing;
    }

    /**
     * 加密以byte[]明文输入,byte[]密文输出
     *
     * @param byteS
     *            byte[]明文
     * @return byte[]密文
     */
    private byte[] getEncCode(byte[] byteS, String password) {
        byte[] byteFina = null;
        Cipher cipher;
        try {
            Key key = generateKey(password);
            cipher = Cipher.getInstance(Algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byteFina = cipher.doFinal(byteS);
        } catch (Exception e) {
            logger.info(e.getMessage());
        } finally {
            cipher = null;
        }
        return byteFina;
    }

    /**
     * 解密以byte[]密文输入,以byte[]明文输出
     *
     * @param byteD
     *            byte[]密文
     * @return byte[]明文
     */
    private byte[] getDesCode(byte[] byteD, String password) {
        Cipher cipher;
        byte[] byteFina = null;
        try {
            Key key = generateKey(password);
            cipher = Cipher.getInstance(Algorithm);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byteFina = cipher.doFinal(byteD);
        } catch (Exception e) {
            logger.info(e.getMessage());
        } finally {
            cipher = null;
        }
        return byteFina;
    }

    // 16进制字符串转数组
    private static byte[] hexStr2ByteArr(String strIn) throws Exception {
        byte[] arrB = strIn.getBytes();
        int iLen = arrB.length;

        // 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
        byte[] arrOut = new byte[iLen / 2];
        for (int i = 0; i < iLen; i = i + 2) {
            String strTmp = new String(arrB, i, 2);
            arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
        }
        return arrOut;
    }

    // 数组转16进制字符串
    public static String byteArr2HexStr(byte[] arrB) throws Exception {
        int iLen = arrB.length;
        // 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
        StringBuffer sb = new StringBuffer(iLen * 2);
        for (int i = 0; i < iLen; i++) {
            int intTmp = arrB[i];
            // 把负数转换为正数
            while (intTmp < 0) {
                intTmp = intTmp + 256;
            }
            // 小于0F的数需要在前面补0
            if (intTmp < 16) {
                sb.append("0");
            }
            sb.append(Integer.toString(intTmp, 16));
        }
        // 最大128位
        String result = sb.toString();
        // if(result.length()>128){
        // result = result.substring(0,result.length()-1);
        // }
        return result;
    }

}