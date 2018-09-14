# Decrypting Twilio Encrypted Call Recordings

Twilio provides additional security on your call recordings by encrypting them with your public key. These code samples demonstrate how you can decrypt them once you have downloaded the encrypted recording and associated encryption metadata.


### Generate RSA key pair using OpenSSL

 - Generate private key

```sh
openssl genrsa -out private_key.pem 2048
```
 - Generate public key for the private key (PKCS1) generated above

```sh
openssl rsa -pubout -in private_key.pem -out public_key.pem
```
 - Convert the above private key to PKCS8 format. 
	
```sh
openssl pkcs8 -topk8 -inform PEM -outform PEM -in private_key.pem -out private_key_pkcs8.pem -nocrypt
```
*Note: Webcrypto API used in Javascript code sample supports PKCS8 format. PKCS1 private key cannot be used here*

###Upload and Choose public key to use for encryption

- Create new credential, select type as Public Key, Copy the public key from the file and paste it in the Public Key Place holder

[![Twilio Public Key Upload](https://s3.amazonaws.com/com.twilio.prod.twilio-docs/original_images/create_credential.gif)](https://www.twilio.com/console/runtime/credentials/public-keys)

- Enable Recording Encryption and choose the public key you want to use for encrypting all your recordings.

[![Twilio Choose Public Key](https://lh3.googleusercontent.com/0MFgqDWLKW8e_iB88x8augChc6J4bIdntyJGxtMFZ0xWi4f_YqIB9lAcfgCkWgt9GhFKbrR7lR1IC6uye-1lPTBzOKLjdI5Bhq9OFL4iAOvzRPzSrzjovFCbzR7Y6J6NXp3lZaHnZZjh6hIWhNcqhundeVVT3Aub25aPKZAjubDfdYbAScHvxbIhKjJ8ZAKV71V0TDFU0Xx82vQJ--1qaYapZKZzT7Zr-PQXn6n971h_4Gv9gFO5lkpyPu1g8UaqbZ9z9Ch96nq01mGkpzMW5k_9NFJFhj6t8uUOurZN5urlinQAoTmZIPgAlkq1WtFu_hz8y-WEyWUwJr7cWZd10A6jfd7zMGOxUNKXf-4o37VdQCTqw3eAeYdQWwb2OFqlrDeAPbqgDWvM93Zwce5gcaWOcGxedHaqqzXOZIPD72ISk5j6SuagzifHobY9KtAdJ_HZ7u4_SiLa0KW1JQ0VHkImh2NF_chOTx2hr79X4wzD4lzatij8vYf5v45oCn1O2ghEgvE8yDhn8RMDY9eX1e_GA14sJQnTiZOXPWosfYxQLLLdVYcWBCkEyGNU3VqVrXNE7UKB1X49PUPvLRA5xCqaqUDXzDKO7QgfnQX1jz2ElpHcuL-oYYLBBCY6Z6A=w1200-h650-no)](https://www.twilio.com/console/voice/settings)

*Click on the images above to directly go to the Console locations*

###Per Recording Decryption
 - Download your recording file from Twilio Recording Url
```https://api.twilio.com/2010-04-01/Accounts/{AccountSid}/Recordings/{RecordingSid}.wav```

- Extract **encrypted_cek** and **iv** from **encryption_details** section of Recording Status Callback or Recording Resource.
 
*Sample Recording Status Callback Response* 

 ```json
{
  "account_sid": "AC29731d0cc4f3283bfd8230ee5eb50d5c",
  "api_version": "2010-04-01",
  "call_sid": "CA88c99f0db82b2f4db48d999ee6dc6e9f",
  "conference_sid": null,
  "date_created": "Thu, 30 Aug 2018 20:46:22 +0000",
  "date_updated": "Thu, 30 Aug 2018 20:46:48 +0000",
  "start_time": "Thu, 30 Aug 2018 20:46:22 +0000",
  "duration": "23",
  "sid": "RE9038847184f2f0adf0ad0bb578a2c972",
  "price": "-0.00250",
  "price_unit": "USD",
  "status": "completed",
  "channels": 1,
  "source": "StartCallRecordingAPI",
  "error_code": null,
  "uri": "/2010-04-01/Accounts/AC29731d0cc4f3283bfd8230ee5eb50d5c/Recordings/RE9038847184f2f0adf0ad0bb578a2c972.json",
  "encryption_details": {
    "public_key_sid": "CRcb85845768df25c20f7c15a15e1a84f0",
    "encrypted_cek": "L0PwoKYxXvwWoVAVf7l46/du40u8IYRm9b9uhIsNUJXURzSLBEWNxHfhEWZVeuxIR2WPcJEy05Y4bXxkVWcXeoFoi024BxsfHRF6mS8T7fX5Eft7FYm65New5lukvpuIjVHC6fWzdTPodVI4eu6LU9q3o7rjD14y1OmpcVk7Qhq89T+LkXfvBKWzN+eN8mnGAauox4sxVEOjVlwktL9AnbVNLXf5YtgrY9m9HqESu1vaJzybX+zdl5ux4xyD4/yOg+bYFoGS5SFQXassxp1iASenoxYcUlxQzqGIduOn2/wmdcVTM4oCQw9//m11zTS6B1JEON/HqSsXrH0L/4CvgQ==",
    "type": "rsa-aes",
    "iv": "WLY3ND199gE4kR2q"
  },
  "subresource_uris": {
    "add_on_results": "/2010-04-01/Accounts/AC29731d0cc4f3283bfd8230ee5eb50d5c/Recordings/RE9038847184f2f0adf0ad0bb578a2c972/AddOnResults.json",
    "transcriptions": "/2010-04-01/Accounts/AC29731d0cc4f3283bfd8230ee5eb50d5c/Recordings/RE9038847184f2f0adf0ad0bb578a2c972/Transcriptions.json"
  }
}
 ```
 
 - Replace the sample code encryption parameters (encrypted_cek, iv, private key file path and encrypted file path) and decrypt your recordings
