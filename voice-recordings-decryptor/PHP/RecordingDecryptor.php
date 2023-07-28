<?php
/**
 * Dependencies:
 *
 * https://pecl.php.net/package/libsodium (for your PHP version)
 * https://phpseclib.com/ version 3
 */

require './vendor/autoload.php';
use phpseclib3\Crypt\PublicKeyLoader;

// 1) Obtain encrypted_cek, iv parameters within EncryptionDetails via recordingStatusCallback or
// by performing a GET on the recording resource
$encryptedCek = "iq54n5fYF8l8zLCBqqDtCDKBb2QuCrgdrPynaWFJquwXfCdInB7sPGpgcJVcXdhGC8qPK/3qkuunVVwijpSCAeieecNCu1" .
    "/O96VmuIA0wloVtS2WVD7zJbdcyGnYc1OtwcPiMl/pr3mh7to9StBVcBzPTybFX9ij7kO+0PN6vmmAgP+Z6fBFSPzUn7itSBguSBqOZ93J" .
    "RfswpVeKT9T9Axu7cE7dNixS5hTdSprTuBpdbAw/SfkwcqiLOtTYQgDaLUiBVIyyHHGAYQT66QJk2dploVeEfsZg9ceosZ6H7KZ2+A+n1W" .
    "f0W2T56QQZloRqlArHSBaXFIueg1itbLVu8g==";
$iv = "A7Ye4IDXstSuLLon";
$encryptedRecordingPath = "./crypt.wav";
$decryptedRecordingPath = "./decrypt.wav";

decrypt(
    './private_key.pem',
    $encryptedCek,
    $iv,
    $encryptedRecordingPath,
    $decryptedRecordingPath
);

/**
 * @throws \SodiumException
 */
function decrypt($privateKeyPath, $base64EncryptedCEK, $base64Iv, $encryptedRecordingPath, $decryptedRecordingPath){
    // 2) load key
    $key = PublicKeyLoader::load(file_get_contents($privateKeyPath))->withHash('sha256');

    // 3) decrypt CEK and decode IV
    $decryptedCek = $key->decrypt(base64_decode($base64EncryptedCEK));
    $iv = base64_decode($base64Iv);

    // 4) Decrypt encrypted recording using the decrypted cek (key)
    $data = file_get_contents($encryptedRecordingPath);
    $result = sodium_crypto_aead_aes256gcm_decrypt($data, null, $iv, $decryptedCek);

    file_put_contents($decryptedRecordingPath, $result);
}