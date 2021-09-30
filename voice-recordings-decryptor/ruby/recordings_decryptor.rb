# frozen_string_literal: true

require 'base64'
require 'openssl'
require 'openssl/oaep'

encrypted_cek = 'P8nvwWXagtQoRJvV2pZaex1kgAs6iiNsjANuh0vv8KqDIWK4ytZyDIF4kZhqEUGsT1t7MDOZsxmLnjngfDeDmHvxVwXKZeYTVIxiK2tSM1tZUDM7Sl9f+EF7Ux7iCc09NSsQcbLPZb8fv86RQD0BU4fmZ8eoAKHRDe9kdu+TEJmFrDd8ZAegkfetfP9kUoxnqmsM9YgfrT749YB4gFE+nvfPV0H7QDInXAz2o0Diup5kat/9rg5oWfHFYCBV2n4wjkYTdDIvQxZyCqnq/N/lfk9WBZkKiJIzdRy9RvYBCkIZHHmMhV83TMNPNzi6v2pjpDXGsogwj4jxOb5tVjEeMg=='
iv = '59KNw+X/uclh59ad'

encrypted_recording_file_path = '/Users/jack/recordings/RE7f1de66bd51bcc91d4938b881c8ee15c.wav'
decrypted_recording_file_path = '/Users/jack/recordings/RE7f1de66bd51bcc91d4938b881c8ee15c.decrypted.wav'
private_key_path = '/Users/jack/recordings/private_key.pem'

rsa_key = OpenSSL::PKey::RSA.new(File.open(private_key_path, 'rb'))
decrypted_cek = rsa_key.private_decrypt_oaep(Base64.decode64(encrypted_cek), '', OpenSSL::Digest::SHA256)

cipher = OpenSSL::Cipher.new('aes-256-gcm')
cipher.iv = Base64.decode64(iv)
cipher.key = decrypted_cek

decrypted_recording_file = File.open(decrypted_recording_file_path, 'wb')
encrypted_recording_file = File.open(encrypted_recording_file_path, 'rb')

CHUNK_SIZE = 4 * 1024
until encrypted_recording_file.eof?
  chunk = encrypted_recording_file.read(CHUNK_SIZE)
  decrypted_chunk = cipher.update(chunk)
  decrypted_recording_file.write(decrypted_chunk)
end

decrypted_recording_file.close
encrypted_recording_file.close
