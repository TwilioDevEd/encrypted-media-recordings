<?php

use phpseclib3\Crypt\PublicKeyLoader;

class RecordingDecryptor
{
    /**
     * RecordingDecryptor constructor.
     * @param string $privateKey path to the private key
     */
    public function __construct(
        private string $privateKey
    )
    {
    }

    /**
     * @param array $metadata the recording file metadata from twilio
     * @param string $encryptedData you can use file_get_contents on the encrypted file
     * @return string|false return the decrypted file contents. you can use the file_put_contents to write the result to an output file
     * @throws SodiumException
     */
    public function decrypt(array $metadata, string $encryptedData): string | false
    {
        $iv = $metadata['encryption_details']['iv'];
        $cek = $metadata['encryption_details']['encrypted_cek'];
        // 1) load key
        $key = PublicKeyLoader::load(file_get_contents($this->privateKey))->withHash('sha256');
        // 2) decrypt CEK and decode IV
        $decryptedCek = $key->decrypt(base64_decode($cek));
        $iv = base64_decode($iv);
        // 3) Decrypt encrypted recording using the decrypted cek (key)
        return sodium_crypto_aead_aes256gcm_decrypt($encryptedData, null, $iv, $decryptedCek);
    }
}