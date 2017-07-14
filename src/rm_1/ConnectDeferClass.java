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
public class ConnectDeferClass implements Serializable {
    private String hostDefer;
    private int priorityDefer;
    
    
    public void setHostDefer(String host) {
        this.hostDefer = hostDefer;
    }

   
    public String getHostDefer() {
        return hostDefer;
    }
    
    public int getPriorityDefer() {
        return priorityDefer;
    }
    public void sePriorityDefer(int priorityDefer) {
        this.priorityDefer = priorityDefer;
    }

     
    public ConnectDeferClass(String hostDefer,int priorityDefer) {
        this.hostDefer = hostDefer;
    }

}
