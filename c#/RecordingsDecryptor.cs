using Org.BouncyCastle.Security;
using Org.BouncyCastle.Crypto;
using Org.BouncyCastle.Crypto.Parameters;
using Org.BouncyCastle.OpenSsl;
using System.IO;
using System;
using Org.BouncyCastle.Crypto.IO;

namespace RecordingsDecryptor
{
    class RecordingsDecryptor
    {
        static void Main(string[] args)
        {
            //This code sample assumes you have added BouncyCastle Nuget package into your project

            // Follow "Per Recording Decryption Steps"
            // https://www.twilio.com/docs/voice/tutorials/call-recording-encryption#per-recording-decryption-steps-customer

            Console.WriteLine("Twilio Sample Code - Public Key Recordings Encryption - C#");
            var privateKeyPath = "/Users/bkumar/Desktop/private_key.pem";

            // 1) Obtain encrypted_cek, iv parameters within EncryptionDetails via recordingStatusCallback or
            // by performing a GET on the recording resource
            var encryptedCek = "dYiRmp39kCJt3HOT2VmBGp7SHVEzcuku4o5eRftvNvdes1aPKvrHJbvzqN8Bhr3tmVtSQnmP0oannvo098oCTRJYsWIe0sWmnOpwWWC09YAOwowgRtYgY/gr+IR357exAJ/1L0cWVz5MuSHFBN2Vbd673WhK51QzOXi9H+wuXEhGJmiVKK38BmMVYg2EopFXKVlwA/kOQHVvRlyQ6RlNB1pvLFY/isnJ3FCldyyayf/LqvFXU2ZAVl446Z0ZLpQCJBR3wIIZcABaEGYtzAjBLWEHkc9FICfM5xMWGfHzgYzmgTfIn8ldMUXPYbU0kh67ErbEa6v0w5zDifOQxsX5Cg==";
            var iv = "uFM4mEKKptceQs/L";
            var encryptedRecordingPath = "/Users/bkumar/Desktop/RE41523cf58cc74597e38b957be84d6d13.wav";
            var decryptedRecordingPath = "/Users/bkumar/Desktop/RE41523cf58cc74597e38b957be84d6d13-decrypted.wav";
            decrypt(privateKeyPath, encryptedCek, iv, encryptedRecordingPath, decryptedRecordingPath);
            Console.WriteLine("Recording decrypted Successfully. You can play the recording from " + decryptedRecordingPath);
        }

        private static void decrypt(String privateKeyPath,
                                    String encryptedCEK,
                                    String iv,
                                    String encryptedRecordingPath,
                                    String decryptedRecordingPath)
        {
            // 2) Retrieve customer private key corresponding to public_key_sid and use it to decrypt base 64 decoded
            // encrypted_cek via RSAES-OAEP-SHA256-MGF1
            Object pemObject;
            using (var txtreader = File.OpenText(@privateKeyPath))
                pemObject = new PemReader(txtreader).ReadObject();
            
            var privateKey = (RsaPrivateCrtKeyParameters)((pemObject.GetType() == typeof(AsymmetricCipherKeyPair)) ? 
                 ((AsymmetricCipherKeyPair)pemObject).Private : pemObject);

            var rsaDecryptEngine = CipherUtilities.GetCipher("RSA/ECB/OAEPWITHSHA256ANDMGF1PADDING");

            rsaDecryptEngine.Init(false, privateKey);
            var encryptedCekArr = Convert.FromBase64String(encryptedCEK);
            var decryptedCekArr = rsaDecryptEngine.DoFinal(encryptedCekArr);

            // 3) Initialize a AES256-GCM SecretKey object with decrypted CEK and base 64 decoded iv
            var aesDecryptEngine = CipherUtilities.GetCipher("AES/GCM/NOPADDING");
            KeyParameter keyParameter = ParameterUtilities.CreateKeyParameter("AES", decryptedCekArr);
            ICipherParameters cipherParameters = new ParametersWithIV(keyParameter, Convert.FromBase64String(iv));
            aesDecryptEngine.Init(false, cipherParameters);

            // 4) Decrypt encrypted recording using the SecretKey
            var decryptedFile = File.Create(@decryptedRecordingPath);
            CipherStream cipherStream = new CipherStream(File.OpenRead(@encryptedRecordingPath), aesDecryptEngine, null);
            cipherStream.CopyTo(decryptedFile);
            decryptedFile.Close();
        }
    }
}