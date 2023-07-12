package src.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;

public class SecUtils {
  String encoded;
  public String GeneratePrivateKey(String text){
    try{
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
      encoded  = Base64.getEncoder().encodeToString(hash);
    } catch (NoSuchAlgorithmException e) {
      System.out.println("error :"+e);
    }
    return encoded;
  }

  public String GenerateHash(String key){
    String hash1 = GenerateHashH(key);
    String hash2 = GenerateHashH(hash1);
    return hash2;
  }

  private String GenerateHashH(String key){
    // key == address + created at + sender + receiver+ private key hash of the block
    try{
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(key.getBytes(StandardCharsets.UTF_8));
      encoded  = Base64.getEncoder().encodeToString(hash);
      encoded = encoded.replace("/","7");
      encoded = encoded.replace("\\\\","y");
    } catch (NoSuchAlgorithmException e) {
      System.out.println("error :"+e);
    }
    return encoded;
  }


  public String GenerateRandString(){
    String wfName = "";
    Random r = new Random();
    for (var i=0;i<32; i++ ){
      char randomChar = (char) (97+ r.nextInt(26));
      wfName = wfName+ randomChar;
    }
    System.out.println(wfName);
    return wfName;
  }
}