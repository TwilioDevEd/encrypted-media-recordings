import base64

from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives import hashes, serialization
from cryptography.hazmat.primitives.asymmetric import padding
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes


def decrypt_recording():
    print("Twilio Sample Code to Decrypt Twilio encrypted recordings")
    # This code sample assumes you have added cryptography.hazmat library to your project

    # Follow "Per Recording Decryption Steps"
    # https://www.twilio.com/docs/voice/tutorials/call-recording-encryption#per-recording-decryption-steps-customer

    # 1) Obtain encrypted_cek, iv parameters within EncryptionDetails via recordingStatusCallback or
    # by performing a GET on the recording resource

    encrypted_cek = "EbtJoGNWb8NiwTjDP3tb3mqtNtP5BBI3reukZqcXUyl7l9E/i9w7rBxIXXY7kMXIuhQ0omYkgSSXjmzhTMJDp7uZBZj9jqCrC4pz1Q3wVILMB6RYOQN2oXsLsp4dE1KpRb/P4E/EW1s9jACirUesvzzur1yVq3qE8ug58PYUAlppxbObT7H12P1+V0LfSZwlttaV+zksCLlKdhBw5zTu6Xt0OwZT5zyCVOk2BB0g+qiBdwqBecjkPhapdCvjven86JuLDtLqgdByM4VzQ7P3QizRgTwYz++S9aCAQXgjSzK8WL/YlAuQRV5gDaiHPOMe7dI3X/sS7uva43p7x5oqoQ=="
    iv = "qypnAE1OJcp80C4e"

    # 2) Retrieve customer private key corresponding to public_key_sid and use it to decrypt base 64 decoded
    # encrypted_cek via RSAES-OAEP-SHA256-MGF1

    private_key = open("/Users/pyoon/private_key.pem", mode="rb")
    key = serialization.load_pem_private_key(private_key.read(), password=None, backend=default_backend())
    private_key.close()

    encrypted_recording_file_path = "/Users/pyoon/Desktop/RE9e9d8986ad2e98e922bc4a7bc997d842.wav"
    decrypted_recording_file_path = "/Users/pyoon/Desktop/RE9e9d8986ad2e98e922bc4a7bc997d842-decrypted.wav"

    decrypted_cek = key.decrypt(
        base64.b64decode(encrypted_cek),
        padding.OAEP(
            mgf=padding.MGF1(algorithm=hashes.SHA256()),
            algorithm=hashes.SHA256(),
            label=None
        )
    )

    # 3) Initialize a AES256-GCM SecretKey object with decrypted CEK and base 64 decoded iv

    decryptor = Cipher(
        algorithms.AES(decrypted_cek),
        modes.GCM(base64.b64decode(iv)),
        backend=default_backend()
    ).decryptor()

    # 4) Decrypt encrypted recording using the SecretKey

    decrypted_recording_file = open(decrypted_recording_file_path, "wb")
    encrypted_recording_file = open(encrypted_recording_file_path, "rb")

    for chunk in iter(lambda: encrypted_recording_file.read(4 * 1024), b''):
        decrypted_chunk = decryptor.update(chunk)
        decrypted_recording_file.write(decrypted_chunk)

    decrypted_recording_file.close()
    encrypted_recording_file.close()
    print("Recordong decrypted Successfully. You can play the recording from " + decrypted_recording_file_path);


if __name__ == "__main__":
    decrypt_recording()
