package com.ks0100.common.util.coder;

import java.math.BigInteger;

import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;


public class DHCoderTest {

  //  static Encoder encoder =Base64.getEncoder();
   // static Decoder decoder =Base64.getDecoder();
    static DHCoder dh = new DHCoder();
	public static void main(String[] args)throws Exception {

		String p1="111111";
		String account="chenhaifeng1@zdksii.com";
		byte[] p_byte=Coder.encryptHMAC(p1.getBytes(), Coder.encryptBASE64(account.getBytes()));
		
	//	String p2=new String(p_byte);
	//	System.out.println("p2:"+p2);
		System.out.println(new BigInteger(p_byte).toString(16));
		System.out.println(p_byte.toString());
	//	String aa="nJfRXGF0HdJWOa9v4dfnYw==";
		System.out.println(Coder.encryptBASE64(p_byte));
	  //  String a="gqym9u8QAvGgG7Wr9pYUyKeswAbIt4jGD/2wfOaJ/yqzhuTs3W2xr/28A4oFV961xtahKtWM8NJkA3pIXF4NFw==";
	   // System.out.println(Coder.decryptBASE64(a).);
	    String aa="adadad!";
	    System.out.println(aa.replaceAll("!", "\\"));
		try
        {    		    
         //   test();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

	}
	
	
	
	

	public static void test() throws Exception {
/*      //  byte[] a = decoder.decode("JHfn0XqtgWzgdpomyIwyfUw4+xrUCjG35dnYSvNdAplZ");
       // byte[] b = decoder.decode("JHfn0XqtgWzgdpomyIwyfUw4+xrUCjG35dnYSvNdApk=");
		byte[] a = Coder.decryptBASE64("JHfn0XqtgWzgdpomyIwyfUw4+xrUCjG35dnYSvNdAplZ");
		byte[] b = Coder.decryptBASE64("JHfn0XqtgWzgdpomyIwyfUw4+xrUCjG35dnYSvNdApk=");
        // 产生乙方密钥对儿
        dh.initWithPG();
        DHPublicKey pubkey = (DHPublicKey)dh.getKeyPair().getPublic();
        DHPrivateKey prikey = (DHPrivateKey)dh.getKeyPair().getPrivate();
        
        System.out.println("乙方公钥:\n" +pubkey.getY().toString(16));
        System.out.println("乙方私钥:\n" + prikey.getX().toString(16));
        
        //甲方公钥
        String publicKey="A9E05393276DEAEE1DEA241A0417AE2343BACAB5BE50FE5BE913E6E94B268C59807C8BCB9017581442A54DF6DFD6A631515957DEC5169FA906FE0A3999519217";
        String exkey = "jscMtav4AYdYDw7l9Ubonz38VdnsHg1lzGCFI/ga8la3j0ZgS12eDmiM8GomugVFdaE1lMiaxTCf47xN"
                    +"Tla5gTk/wpaSdrS0DdqHLahfN02oFQ7MN+NhSp4MrF4PEZZN4HjCjr2mxm6cHb+JcYgrZGNkX0nPPeeT"
                    +"k8d7DSDPYwc8kkTsqMX4PRaOj3H4V8MiLBEfEcaGShv7qllhvaqnhzt04JhhrxKfXUFCP/CAdgEkXIAi"
                    +"CIWuFrDLmt21kFDDh6izdrR3EgXm17+48TyrPUIxNAjogfN9U702XXcAU9TpbUta6iQmtB9vjSdR1iBY"
                    +"gae6Afa5typxLOaLm9Qlhg==";
       // String secKey = encoder.encodeToString(dh.genSecretKey(publicKey));
        String secKey = Coder.encryptBASE64(dh.genSecretKey(publicKey));
        System.err.println("共享秘钥:\r" + secKey);
        System.err.println(exkey.equals(secKey));*/
		
		DHCoder a=new DHCoder();
		a.initWithPG();
		DHCoder b=new DHCoder();
		b.initWithPG();
		DHPublicKey aPubkey = (DHPublicKey)a.getKeyPair().getPublic();
        DHPrivateKey aPrikey = (DHPrivateKey)a.getKeyPair().getPrivate();
        
        String aPubkeyStr=aPubkey.getY().toString(16);
        String aPrikeyStr=aPrikey.getX().toString(16);
        
         
        System.out.println("a方公钥:\n" +aPubkeyStr);
        System.out.println("a方私钥:\n" + aPrikeyStr);
        
        //由a生成b的密钥对儿
        byte[] bbyte=b.genSecretKey(aPubkey.getY().toString(16));
      
        
        DHPublicKey bPubkey = (DHPublicKey)b.getKeyPair().getPublic();
        DHPrivateKey bPrikey = (DHPrivateKey)b.getKeyPair().getPrivate();
        
        String bPubkeyStr=bPubkey.getY().toString(16);
        String bPrikeyStr=bPrikey.getX().toString(16);
        
        System.out.println("b方公钥:\n" +bPubkeyStr);
        System.out.println("b方私钥:\n" + bPrikeyStr);
        
        byte[] abyte=a.genSecretKey(bPubkey.getY().toString(16));
        System.out.println("bbyte:"+Coder.encryptBASE64(bbyte));
        System.out.println("abyte:"+Coder.encryptBASE64(abyte)); 
        byte[] iv  ="abcdefghijklmnop".getBytes();

        String data="12348684641a6fd5asdfmaosidfjasl;dfwea";
        
        //a方公钥加密
        byte[] out = dh.encrypt(data.getBytes(), abyte, iv);
        System.out.println("密文:\n"+Coder.encryptBASE64(out));
        
        byte[] in = dh.decrypt(out, bbyte, iv);
        System.out.println("明文:\n"+new String(in));
	}
	
	
//
//	@Test
	public static void test2() throws Exception {
	    byte[] key ="1234567890123456".getBytes();
	    byte[] iv  ="abcdefghijklmnop".getBytes();
	    byte[] data ="12348684641a6fd5asdfmaosidfjasl;dfwea".getBytes();
	    
	    byte[] out = dh.encrypt(data, key, iv);
	    byte[] in = dh.decrypt(out, key, iv);
       // System.err.println("密文:\n"+encoder.encodeToString(out));
	    System.err.println("密文:\n"+Coder.encryptBASE64(out));
        System.err.println("明文:\n"+new String(in));
        
	}

}

