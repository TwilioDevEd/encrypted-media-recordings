
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemReader;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class RecordingsDecryptor {

    public static void main(final String[] args) {
//        This code sample assumes you have added BouncyCastle Nuget package into your project

        System.out.println("Twilio Sample Code to Decrypt Twilio encrypted recordings");

//        1) Obtain public_key_sid, encrypted_cek, iv parameters within EncryptionDetails via recordingStatusCallback or
//        by performing a GET on the recording resource

        final String encryptedCek = "dYiRmp39kCJt3HOT2VmBGp7SHVEzcuku4o5eRftvNvdes1aPKvrHJbvzqN8Bhr3tmVtSQnmP0oannvo098" +
                "oCTRJYsWIe0sWmnOpwWWC09YAOwowgRtYgY/gr+IR357exAJ/1L0cWVz5MuSHFBN2Vbd673WhK51QzOXi9H+wuXEhGJmiVKK38BmMVY" +
                "g2EopFXKVlwA/kOQHVvRlyQ6RlNB1pvLFY/isnJ3FCldyyayf/LqvFXU2ZAVl446Z0ZLpQCJBR3wIIZcABaEGYtzAjBLWEHkc9FICf" +
                "M5xMWGfHzgYzmgTfIn8ldMUXPYbU0kh67ErbEa6v0w5zDifOQxsX5Cg==";

        final String iv = "uFM4mEKKptceQs/L";

        final String privateKeyPath = "/Users/bkumar/Desktop/private_key.pem";

        final String encryptedRecordingPath = "/Users/bkumar/Desktop/RE41523cf58cc74597e38b957be84d6d13.wav";
        final String decryptedRecordingPath = "/Users/bkumar/Desktop/RE41523cf58cc74597e38b957be84d6d13-decrypted.wav";

        decrypt(privateKeyPath, encryptedCek, iv,encryptedRecordingPath, decryptedRecordingPath);

        System.out.println("Recording decrypted Successfully. You can play the recording from " + decryptedRecordingPath);
    }

    private static void decrypt(final String privateKeyPath,
                                final String base64EncryptedCEK,
                                final String base64Iv,
                                final String encryptedRecordingPath,
                                final String decryptedRecordingPath) {
        final BouncyCastleProvider bc = new BouncyCastleProvider();
        Security.addProvider(bc);

        PrivateKey privateKey;
        try {
//          2) Retrieve customer private key corresponding to public_key_sid and use it to decrypt base 64 decoded
//          encrypted_cek via RSAES-OAEP-SHA256-MGF1
            final byte[] pemContent = new PemReader(new FileReader(privateKeyPath)).readPemObject().getContent();
            final PKCS8EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(pemContent);
            final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            privateKey = keyFactory.generatePrivate(encodedKeySpec);

            final Cipher rsaCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA256AndMGF1Padding", bc);
            rsaCipher.init(2, privateKey);

            final byte[] encryptedCek = Base64.getDecoder().decode(base64EncryptedCEK);
            final byte[] decryptedCek = rsaCipher.doFinal(encryptedCek);

//          3) Initialize a AES256-GCM SecretKey object with decrypted CEK and base 64 decoded iv
            final byte[] iv = Base64.getDecoder().decode(base64Iv);
            final Key aesKey = new SecretKeySpec(decryptedCek, "AES");

//          4) Decrypt encrypted recording using the SecretKey
            final AlgorithmParameterSpec algorithmParameterSpec = new GCMParameterSpec(16 * 8, iv);
            final Cipher aesCipher = Cipher.getInstance("AES/GCM/NoPadding", bc);
            aesCipher.init(2, aesKey, algorithmParameterSpec);

            final FileOutputStream outputStream = new FileOutputStream(decryptedRecordingPath);
            final CipherOutputStream cos = new CipherOutputStream(outputStream, aesCipher);
            Files.copy(Paths.get(encryptedRecordingPath), cos);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
