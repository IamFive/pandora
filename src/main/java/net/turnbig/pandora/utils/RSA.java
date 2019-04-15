package net.turnbig.pandora.utils;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

/**
*
* @author QianBiao.NG
* @date   2018-11-10 22:39:53
*/
public class RSA {

	public static final String SIGN_ALGORITHMS = "RSA";


	public static String signWithPublicKey(String data, String publicKeyBase64String, String charset) {
		try {
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKeyBase64String));
			PublicKey key = KeyFactory.getInstance("RSA").generatePublic(keySpec);

			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] dataToEncrypt = data.getBytes("utf-8");
			byte[] encryptedData = cipher.doFinal(dataToEncrypt);
			String encryptString = Base64.encodeBase64String(encryptedData);
			return encryptString;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public static void main(String[] args) {
		String pk = "671f60185347ed7f42c289bd49daf1e577c317e2c4146bc251458920e936cb410929ad1ad617c79bc96316544e00eb5b1ed1d305f0cd8c8046ad257ec8d1c242ab461941bdb0e25b1c554e2bf866e1b55ea4dc840e456612b50796c2c931fa16d0c5846bf90df14ad4f1239fdcb5dbcc6836badf582b8ce592e3fe93b0710f62db7f02f663798782423a5e491f575aa0591cb7c836fbc8efd710d9f1ee1da3bac1e5bb4957ddb452d57507b1954844028987c4ec0c98fd32278667a53e05644d92e10cd0f054ae9d24a24d95042e289b8c17cbea7a95c1f3e0d136db994ca1d4003996944f773f0d9a2ddbab65b954665ca9aaa238f8fd38d98f73dffba66b99";
		
		String toSign = "123";
		System.out.println(signWithPublicKey(toSign, pk, "UTF-8"));
	}

}
