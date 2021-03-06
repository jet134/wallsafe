package fi.kennyhei.wallsafe.security;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

public class Security {

    private static final Logger LOG = Logger.getLogger(Security.class);

    // Ciphering (encryption and decryption) password/key.
    private static final char[] PASSWORD = "Unauthorized_Personel_Is_Unauthorized".toCharArray();

    // Cipher salt.
    private static final byte[] SALT = {
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,};

    private static String encrypt(byte[] property) throws GeneralSecurityException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");

        pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));

        // Encrypt and save to temporary storage.
        String encrypted = Base64.encodeBase64String(pbeCipher.doFinal(property));

        // Cleanup data-sources - Leave no traces behind.
        for (int i = 0; i < property.length; i++) {
            property[i] = 0;
        }

        property = null;
        System.gc();

        // Return encryption result.
        return encrypted;
    }

    private static String encrypt(char[] property) throws GeneralSecurityException {
        //Prepare and encrypt.
        byte[] bytes = new byte[property.length];

        for (int i = 0; i < property.length; i++) {
            bytes[i] = (byte) property[i];
        }

        String encrypted = encrypt(bytes);

        /*
         * Cleanup property here. (child data-source 'bytes' is cleaned inside 'encrypt(byte[])').
         * It's not being done because the sources are being used multiple times for the different layer samples.
         */
        for (int i = 0; i < property.length; i++) { //cleanup allocated data.
            property[i] = 0;
        }

        property = null; // de-allocate data (set for GC).
        System.gc(); // Attempt triggering garbage-collection.

        return encrypted;
    }

    private static String encrypt(String property) throws GeneralSecurityException {
        String encrypted = encrypt(property.getBytes());
        /*
         * Strings can't really have their allocated data cleaned before CG,
         * that's why secure data should be handled with char[] or byte[].
         * Still, don't forget to set for GC, even for data of sesser importancy;
         * You are making everything safer still, and freeing up memory as bonus.
         */
        property = null;
        return encrypted;
    }

    private static String decrypt(String property) throws GeneralSecurityException, IOException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));

        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));

        return new String(pbeCipher.doFinal(Base64.decodeBase64(property)));
    }

    public static String createEncryptedData(
                    Map<String, char[]> secureAttributes,
                    int layers)
                    throws GeneralSecurityException, FileNotFoundException, IOException {

        StringBuilder sb = new StringBuilder();

        // First encryption layer. Encrypts secure attribute values only.
        for (String key : secureAttributes.keySet()) {

            String encryptedValue;

            if (layers >= 1) {
                encryptedValue = encrypt(secureAttributes.get(key));
            } else {
                encryptedValue = new String(secureAttributes.get(key));
            }

            sb.append(key)
              .append(": ")
              .append(encryptedValue)
              .append(System.lineSeparator());
        }

        if (layers >= 2) {
            sb = new StringBuilder(encrypt(sb.toString().trim()));
        } else {
            sb = new StringBuilder(sb.toString().trim());
        }

        LOG.info("DATA AFTER ENCRYPTION: " + sb.toString());

        return sb.toString();
    }

    public static Map<String, char[]> decryptedData(String data) throws GeneralSecurityException, IOException {

        Map<String, char[]> credentials = new HashMap<>();

        // Decrypt first layer. (REMEMBER: Layers are in reverse order from writing).
        String decryptedContent = decrypt(data);
        LOG.info("\nREAD: [first layer decrypted]\n" + decryptedContent + "\n");

        // Decrypt second layer (secure data).
        for (String line : decryptedContent.split("\n")) {
            String[] pair = line.split(": ", 2);

            if (pair[0].equalsIgnoreCase("username")) {
                credentials.put("username", decrypt(pair[1]).toCharArray());
            }

            if (pair[0].equalsIgnoreCase("password")) {
                credentials.put("password", decrypt(pair[1]).toCharArray());
            }
        }

        return credentials;
    }
}
