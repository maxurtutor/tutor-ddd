package org.maxur.ddd.admin.domain;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class Password {

    private final String encryptedPassword;

    private Password(String password) {
        this.encryptedPassword = password;
    }

    public static Password restore(String password) {
        return new Password(password);
    }

    public static Password encrypt(String password) {
        return new Password(encryptPassword(password));
    }

    public static Password empty() {
        return new Password(null);
    }

    public String getPassword() {
        return encryptedPassword;
    }

    private static String encryptPassword(String password) {
        if (password == null) {
            return null;
        }
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
        messageDigest.reset();
        messageDigest.update(password.getBytes());
        byte[] digest = messageDigest.digest();
        BigInteger bigInt = new BigInteger(1, digest);
        String hashtext = bigInt.toString(16);
        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }
        return hashtext;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Password)) return false;
        Password password = (Password) o;
        return Objects.equals(encryptedPassword, password.encryptedPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(encryptedPassword);
    }
}