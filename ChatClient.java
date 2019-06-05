/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author LaboratorioU005_11
 */
public class ChatClient {
    
    public static void main(String[] args){
        String buffer;

        BufferedReader reader =
                   new BufferedReader(new InputStreamReader(System.in));
        
        HiloMensajes hm = new HiloMensajes();
        hm.start();
        
        while(true){                               
            try {                
                buffer = reader.readLine();
                
                if (buffer!=null){
                    System.out.print("> ");
                    hm.enviar(buffer);
                    
                    if (buffer.equals("Salir")){
                        hm.cerrar();
                        break;
                    }                    
                }
                                
            } catch (IOException ex) {
                Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }                                              
            
    }    
}

class HiloMensajes extends Thread {
    Socket clientSocket;
    PrintWriter out;
    BufferedReader in;              
    String buffer;
    SimpleDateFormat formatterMDY;
    boolean salir = false;
    
    public void run(){        
        String fechaHora;
        formatterMDY = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            
        try {
            clientSocket = new Socket("172.16.33.54",3333);
            //192.168.43.204
            out =
                    new PrintWriter(clientSocket.getOutputStream(), true);
            
            in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));  
            
            while(!this.salir){                
                buffer = in.readLine();
                
                if (buffer!=null){
                    fechaHora = formatterMDY.format(Calendar.getInstance().getTime());
                    System.out.println(fechaHora+ " < "+buffer);
                    System.out.print("> ");
                }                
            }                        
        } catch (IOException ex) {
            Logger.getLogger(HiloMensajes.class.getName()).log(Level.SEVERE, null, ex);
        }                           
    }

    void enviar(String mensaje) {
        out.println(mensaje);
    }

    void cerrar() {
        try {
            clientSocket.close();
            in.close();
            out.close();
            this.salir = true;
        } catch (IOException ex) {
            Logger.getLogger(HiloMensajes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}