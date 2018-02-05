package org.lpro.control;
import java.security.SecureRandom;

public class RandomToken {
    static final String CHARS="0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom random=new SecureRandom();

    public String randomString(int l){
        StringBuilder sb=new StringBuilder(l);
        for(int i=0;i<l; i++){
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}
