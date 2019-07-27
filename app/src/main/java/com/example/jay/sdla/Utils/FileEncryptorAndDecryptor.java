package com.example.jay.sdla.Utils;

import android.util.Log;

import com.example.jay.sdla.Dialogs.ExceptionDialog;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileEncryptorAndDecryptor {

    private File destinationFile;
    //private double accumulator = 0;

    private String securityKey;

    public String getSecurityKey() {
        return securityKey;
    }

    public void setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
    }

    boolean areHashesEqual(File file, String keyHash) throws FileNotFoundException, IOException
    {
        BufferedInputStream fileReader = new BufferedInputStream(new FileInputStream(file.getAbsolutePath()));
        // reading key hash from file
        StringBuffer keyHashFromfile = new StringBuffer(128);
        for(int i=0; i<128; i++){
            keyHashFromfile.append((char)fileReader.read());
        }

        // verifying both hashes
        Log.i("TAG", "keyHashFromFile = " +keyHashFromfile);
        Log.i("TAG", "keyHash = " + keyHash);
        fileReader.close();
        if(keyHashFromfile.toString().equals(keyHash)){
            return true;
        }

        return false;
    }

    private byte[] getHashInBytes(String key) throws NoSuchAlgorithmException {

        byte[] keyHash;
        final MessageDigest md = MessageDigest.getInstance("SHA-512");
        keyHash = md.digest(key.getBytes());
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<keyHash.length; i++){

            sb.append(Integer.toString((keyHash[i] & 0xff) + 0x100, 16).substring(1));
        }
        String hashOfPassword = sb.toString();
        Log.i("TAG", "hashOfPassword length = " + hashOfPassword.length());
        Log.i("TAG", "hashOfPassword = " + hashOfPassword);

        return hashOfPassword.getBytes();
    }

    private String getHashInString(String key) throws NoSuchAlgorithmException
    {
        //setSecurityKey(key);

        byte[] keyHash;
        final MessageDigest md = MessageDigest.getInstance("SHA-512");
        keyHash = md.digest(key.getBytes());
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< keyHash.length ;i++)
        {
            sb.append(Integer.toString((keyHash[i] & 0xff) + 0x100, 16).substring(1));
        }
        String hashOfPassword = sb.toString();
        Log.i("TAG", "hashOfPassword length = " + hashOfPassword.length());
        Log.i("TAG", "hashOfPassword = " + hashOfPassword);
        return hashOfPassword;

    }

    public void encrypt(File file, String key){

        byte[] keyHash;
        // double percentageOfFileCopied = 0;
        if(!file.isDirectory()){


            try {
                keyHash = getHashInBytes(key);

                destinationFile = new File(file.getAbsolutePath().concat(".enc"));
                if(destinationFile.exists()){
                    destinationFile.delete();
                    destinationFile = new File(file.getAbsolutePath().concat(".enc"));
                }


                BufferedInputStream fileReader = new BufferedInputStream(new FileInputStream(file.getAbsolutePath()));
                FileOutputStream fileWriter = new FileOutputStream(destinationFile, true);

                // writing key hash to file
                fileWriter.write(keyHash, 0, 128);

                // encrypting content and writing
                byte[] buffer = new byte[262144];
                int bufferSize = buffer.length;
                int keySize = key.length();

                while(fileReader.available() > 0){

                    int bytesCopied = fileReader.read(buffer);
                    for(int i=0, keyCounter = 0; i<bufferSize; i++, keyCounter%=keySize){
                        buffer[i] += key.toCharArray()[keyCounter];
                    }

                    fileWriter.write(buffer, 0, bytesCopied);

                    Log.i("TAG", "des file length = " + destinationFile.length());

                    fileReader.close();
                    fileWriter.close();

                    if(!file.delete()){

                        encrypt(file, key);
                    }
                }


            } catch (NoSuchAlgorithmException e) {
                new ExceptionDialog("NoSuchAlgorithmException!", "Something hugely badly unexpectadly went awfully wrong");
                //e.printStackTrace();
            }catch (SecurityException e) {
                new ExceptionDialog("File Security Error!!!", file+" doesn't allow you to do that!");
            } catch (FileNotFoundException e) {
                new ExceptionDialog("File Not Found!!!", file+" not found!");
                // e.printStackTrace();
            } catch (IOException e) {
                new ExceptionDialog("Can Not Read or Write file!!!", file+" can not be read or written!");
                //e.printStackTrace();
            } catch(Exception e){
                new ExceptionDialog("Unexpected System Error!", "Something hugely badly unexpectadly went awfully wrong");
            }

        }
    }

    public void decrypt(File file, String key) throws Exception {

        String keyHash;
        //double percentageOfFileCopied = 0;
        if (!file.isDirectory()) {

            try {
                keyHash = getHashInString(key);

                if (areHashesEqual(file, keyHash)) {
                    destinationFile = new File(file.getAbsolutePath().toString().substring(0, file.getAbsolutePath().toString().length() - 4));

                    BufferedInputStream fileReader = new BufferedInputStream(new FileInputStream(file.getAbsolutePath()));
                    FileOutputStream fileWriter = new FileOutputStream(destinationFile);

                    //decrypting content & writing
                    byte[] buffer = new byte[262144];
                    int bufferSize = buffer.length;
                    int keySize = key.length();

                    for (int i = 0; i < 128; i++) {
                        if (fileReader.available() > 0) {
                            fileReader.read();
                        }
                    }


                    while (fileReader.available() > 0) {
                        int bytesCopied = fileReader.read(buffer);
                        for (int i = 0, keyCounter = 0; i < bufferSize; i++, keyCounter %= keySize) {

                            buffer[i] -= key.toCharArray()[keyCounter];
                        }

                        fileWriter.write(buffer, 0, bytesCopied);

                        Log.i("TAG", "des file length = " + destinationFile.length());
                    }

                    fileReader.close();
                    fileWriter.close();
                    if (!file.delete()) {
                        decrypt(file, key);
                    }

                } else if (!areHashesEqual(file, keyHash)) {
                    //progressOfFilesTextField.append("\nPassword is verified using SHA-512 (128 bit) hash.\nLooks like the Input Password and the File Password differ!!\nEven if you bypass the hash (somehow) you won't be able to read the file because the file is encrypted at byte level.\nWithout the actual password you have no chance.\nYour Bad Luck Ã¢ËœÂº\n\n");

                    new ExceptionDialog("Password is verified using SHA-512 (128 bit) hash."
                            , "\nLooks like the Input Password and the File Password differ!!" +
                            "\nEven if you bypass the hash (somehow) you won't be able to read the file because the file is " +
                            "encrypted at byte level.\nWithout the actual password you have no chance.\nYour Bad Luck Ã¢ËœÂº\n\n");
                }

            } catch (NoSuchAlgorithmException e) {
                new ExceptionDialog("NoSuchAlgorithmException!", "Something hugely badly unexpectadly went awfully wrong");
                // e.printStackTrace();
            } catch (SecurityException e) {
                new ExceptionDialog("File Security Error!!!", file + " doesn't allow you to do that!");
            } catch (FileNotFoundException e) {
                new ExceptionDialog("File Not Found!!!", file + " not found!");
                //e.printStackTrace();
            } catch (IOException e) {
                new ExceptionDialog("Can Not Read or Write file!!!", file + " can not be read or written!");
                //e.printStackTrace();
            } catch (Exception e) {
                new ExceptionDialog("Unexpected System Error!", "Something hugely badly unexpectadly went awfully wrong");
            }
        }
    }

}