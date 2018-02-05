package org.lpro.control;
import org.mindrot.jbcrypt.BCrypt;

public class PasswordManagement {
    public static String digestPassword(String s){
        try{
            String h=BCrypt.hashpw(s,BCrypt.gensalt());
            return h;
        }catch(Exception e){
            throw new RuntimeException("Error in password hash");
        }
    }
}
