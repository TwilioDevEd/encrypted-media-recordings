# Decrypting Twilio Encrypted Call Recordings

Twilio provides additional security on your call recordings by encrypting them with your public key. These code samples demonstrate how you can decrypt them once you have downloaded the encrypted recording and associated encryption metadata.

For more detailed information, please see our documentation on [Voice Recording Encryption](https://www.twilio.com/docs/voice/tutorials/voice-recording-encryption).


To use the sample code in this repository, you should replace the encryption parameters with your information (`encrypted_cek`, `iv`,private key file path and encrypted recording file path).

## Sample Code

* [Java](java/RecordingsDecryptor.java)
* [C#](c%23/RecordingsDecryptor.cs)
* [JavaScript](javascript/RecordingsDecryptor.html)
* [Python](python/RecordingsDecryptor.py)
