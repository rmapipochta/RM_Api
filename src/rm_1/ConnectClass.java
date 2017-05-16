/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rm_1;

import java.io.Serializable;

/**
 *
 * @author Саня
 */
public class ConnectClass implements Serializable {
    private String host;
    private String key;
    
    public void setHost(String host) {
        this.host = host;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getHost() {
        return host;
    }

    public String getKey() {
        return key;
    }

    public ConnectClass(String host, String key) {
        this.host = host;
        this.key = key;
    }

}
