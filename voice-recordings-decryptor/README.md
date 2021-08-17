# Decrypting Twilio Encrypted Voice Recordings

Twilio provides additional security on your voice recordings by encrypting them with your public key. The code samples demonstrate how you can decrypt your voice recordings once you have downloaded the encrypted version and associated encryption metadata.

For more detailed information, please see our documentation on [Voice Recording Encryption](https://www.twilio.com/docs/voice/tutorials/voice-recording-encryption).

*Note* These examples are for illustrating the key steps in recording decryption, rather than providing fully-built, production-grade solutions. It will be up to your engineering team to decide on preferred decryption libraries and specific implementation. 

## Sample Code

To use the sample code provided in this repository, you should replace the encryption parameters with your information (`encrypted_cek`, `iv`, the private key file path and encrypted recording file path).


* [Java](java/RecordingsDecryptor.java)
* [C#](c%23/RecordingsDecryptor.cs)
* [JavaScript](javascript/RecordingsDecryptor.html)
* [Python](python/RecordingsDecryptor.py)
