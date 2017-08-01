#!/usr/bin/perl -w
#@defgroup   ¼ÓÃÜ½âÃÜÄ£¿é
#@author     hamming
#@version    1.0
#@date       20141216


sub get3DESDecrypt {
	
	my($src,$spkey) = @_;

	use Inline Java => <<'END', AUTOSTUDY => 1 ;
	import java.io.IOException;
	import java.net.URLDecoder;
	import java.security.MessageDigest;
	
	import javax.crypto.Cipher;
	import javax.crypto.SecretKey;
	import javax.crypto.SecretKeyFactory;
	import javax.crypto.spec.DESedeKeySpec;
	
	import sun.misc.BASE64Decoder;
	
	
	public class Test {
				
		public String getPwd(String src,String spkey) throws IOException{
			
			String URLValue = getURLDecoderdecode(src);
			
			BASE64Decoder base64Decode = new BASE64Decoder();
			byte[] base64DValue = base64Decode.decodeBuffer(URLValue);
			
			
			String strDe = null;
			Cipher cipher = null;
			try {
				cipher = Cipher.getInstance("DESede");
				byte[] key = getEnKey(spkey);
				DESedeKeySpec dks = new DESedeKeySpec(key);
				SecretKeyFactory keyFactory = SecretKeyFactory
						.getInstance("DESede");
				SecretKey sKey = keyFactory.generateSecret(dks);
				cipher.init(Cipher.DECRYPT_MODE, sKey);
				byte ciphertext[] = cipher.doFinal(base64DValue);
				strDe = new String(ciphertext, "UTF-16LE");
			} catch (Exception ex) {
				strDe = "";
				ex.printStackTrace();
			}
			
			return strDe;
			
		}
	
		public String getURLDecoderdecode(String src) {
			String requestValue = "";
			try {
	
				requestValue = URLDecoder.decode(src);
			} catch (Exception e) {
				e.printStackTrace();
			}
	
			return requestValue;
		}
		
	
		private byte[] getEnKey(String spKey) {
			byte[] desKey = null;
			try {
				byte[] desKey1 = md5(spKey);
				desKey = new byte[24];
				int i = 0;
				while (i < desKey1.length && i < 24) {
					desKey[i] = desKey1[i];
					i++;
				}
				if (i < 24) {
					desKey[i] = 0;
					i++;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	
			return desKey;
		}
	
	
		private byte[] md5(String strSrc) {
			String encode = "gbk" ;
			
			byte[] returnByte = null;
			try {
				MessageDigest md5 = MessageDigest.getInstance("MD5");
				returnByte = md5.digest(strSrc.getBytes(encode));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return returnByte;
		}
	
	}
	
END
	
	my $obj = new Test() ;
	my $hm = $obj->getPwd($src,$spkey) ;
	return $hm;
}

1;