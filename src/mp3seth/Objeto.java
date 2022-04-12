/**
 * @(#)Object.java
 *
 *
 * @author 
 * @version 1.00 2014/6/19
 */
   
package mp3seth;

import java.util.*;
import java.net.*;
import java.io.*;
import java.awt.*;
//https://www.youtube.com/user/Renan6x3
public class Objeto {
        
    /**
     * Creates a new instance of <code>Object</code>.
     */
     Socket socketCliente;
   //  String user;
     String ip;
    // String avatar;
    public Objeto(Socket socket,String i)
    {
    		System.out.println ("i:"+i);
    ///	String tmp[]=i.split(Constantes.SEPARA_DATOS);
    	
    	socketCliente=socket;
   		ip=i;
    //	user=tmp[1];  	
    //	this.avatar=tmp[2];
    
    }
    
    
    
 
}
