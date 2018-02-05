package org.lpro.control;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
public class KeyManagement {
    public static Key generateKey(){
        String key="gIpE";
        Key k= new SecretKeySpec(key.getBytes(),0,key.getBytes().length,"DES");
        return k;
    }
}
