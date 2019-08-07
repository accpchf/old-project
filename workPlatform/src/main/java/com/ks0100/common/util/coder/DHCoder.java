package com.ks0100.common.util.coder;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPublicKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * DH安全编码组件
 * 
 * @author chenhaifeng
 * @version 1.0
 * @since 1.0
 */
public class DHCoder
{
    private static final String EXCHANGE_ALGORITHM = "DH";
	private static final String PUBLIC_KEY = "DHPublicKey";
	private static final String PRIVATE_KEY = "DHPrivateKey";
	
    /**
     * 默认密钥字节数
     * 
     * <pre>
     * DH
     * Default Keysize 1024  
     * Keysize must be a multiple of 64, ranging from 512 to 1024 (inclusive).
     * </pre>
     */
    private final int KEY_SIZE = 512;

    /**
     * DH加密下需要一种对称加密算法对数据加密，这里我们使用DES，也可以使用其他对称加密算法。
     */
    private static final String SECRET_ALGORITHM = "AES";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    private final BigInteger P = new BigInteger(
            "F1FE5FC1044041C561AC5754D0A3BEB86469A338732AB68F93BFA22CDE4BB289CC1116731108BC31"
            +"F15F9AEAE4F73A052952C94A38B091CFC1CA58A799657F7B", 16);
    private final BigInteger G = new BigInteger("05",16);

    private KeyPair keyPair;

    
    /**
     * 初始化
     * 
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchAlgorithmException
     */
    public synchronized  void initWithPG() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException
    {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(EXCHANGE_ALGORITHM);
        keyPairGenerator.initialize(new DHParameterSpec(P, G, KEY_SIZE));
        keyPair = keyPairGenerator.generateKeyPair();
        
    }
    
    /**
     * 初始化
     * 
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchAlgorithmException
     */
    public synchronized  Map<String, Object>  initWithPG2() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException
    {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(EXCHANGE_ALGORITHM);
        keyPairGenerator.initialize(new DHParameterSpec(P, G, KEY_SIZE));
        keyPair = keyPairGenerator.generateKeyPair();
        
        Map<String, Object> keyMap = new HashMap<String, Object>(2);

		// 甲方公钥
		DHPublicKey publicKey = (DHPublicKey) keyPair.getPublic();

		// 甲方私钥
		DHPrivateKey privateKey = (DHPrivateKey) keyPair.getPrivate();
		
		keyMap.put(PUBLIC_KEY, publicKey);
		keyMap.put(PRIVATE_KEY, privateKey);
		return keyMap;
    }

/*    public synchronized void init() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException
    {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(EXCHANGE_ALGORITHM);
        keyPairGenerator.initialize(KEY_SIZE);
        keyPair = keyPairGenerator.generateKeyPair();
    }*/

    /**
     * 生成协商密钥
     * 
     * @param publicKey
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     */
    public byte[] genSecretKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException,
            InvalidKeyException
    {

        KeyFactory keyFactory = KeyFactory.getInstance(EXCHANGE_ALGORITHM);

        BigInteger dhpubkey = new BigInteger(publicKey,16);
        DHPublicKeySpec dhKeySpec = new DHPublicKeySpec(dhpubkey, P, G);
        PublicKey pubKey = keyFactory.generatePublic(dhKeySpec);

        KeyAgreement keyAgree = KeyAgreement.getInstance(EXCHANGE_ALGORITHM);
        keyAgree.init(keyPair.getPrivate());
        keyAgree.doPhase(pubKey, true);

        // 生成本地密钥
        SecretKey secretKey = keyAgree.generateSecret(SECRET_ALGORITHM);

        return secretKey.getEncoded();
    }
/**
 * 加密
 * @param data 明文
 * @param key  密钥
 * @param iv   向量
 * @return 密文
 * @throws NoSuchAlgorithmException
 * @throws NoSuchPaddingException
 * @throws InvalidKeyException
 * @throws IllegalBlockSizeException
 * @throws BadPaddingException
 * @throws InvalidAlgorithmParameterException
 */
    public static byte[] encrypt(byte[] data, byte[] key, byte[] iv) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException,
            InvalidAlgorithmParameterException
    {
        Cipher aes = Cipher.getInstance(CIPHER_ALGORITHM);
        SecretKey secKey = new SecretKeySpec(key, 0, 16, SECRET_ALGORITHM);
        AlgorithmParameterSpec parameterSpec = new IvParameterSpec(iv, 0, 16);
        aes.init(Cipher.ENCRYPT_MODE, secKey, parameterSpec);
        return  aes.doFinal(data);
    }
    
/**
 * 
 * @param data 密文
 * @param key  密钥
 * @param iv   向量
 * @return
 * @throws NoSuchAlgorithmException
 * @throws NoSuchPaddingException
 * @throws InvalidKeyException
 * @throws IllegalBlockSizeException
 * @throws BadPaddingException
 * @throws InvalidAlgorithmParameterException
 */
    public static byte[] decrypt(byte[] data, byte[] key, byte[] iv) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException,
            InvalidAlgorithmParameterException
    {
        Cipher aes = Cipher.getInstance(CIPHER_ALGORITHM);
        SecretKey secKey = new SecretKeySpec(key, 0, 16, SECRET_ALGORITHM);
        AlgorithmParameterSpec parameterSpec = new IvParameterSpec(iv, 0, 16);
        aes.init(Cipher.DECRYPT_MODE, secKey, parameterSpec);
        return  aes.doFinal(data);
    }

    /**
     * 获取keyPair
     *@return the keyPair
     */
    public KeyPair getKeyPair()
    {
        return keyPair;
    }
        
}
