/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

/**
 *
 * @author LaboratorioU005_11
 */
public class ChatServer {
    ServerSocket serverSocket;
    ArrayList<ConexionHilo> conexiones;
    String buffer;    
    int    numConexion = 1;
    SimpleDateFormat formatterMDY;

    public ChatServer(){
        formatterMDY = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        conexiones = new ArrayList<ConexionHilo>();        
        buffer = "";
    }
        
    public static void main(String[] args){                
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ChatServer().start();
                System.out.println("Servidor finalizado.");
            }
        });                               
    }
    
    void start(){       
        System.out.println("ChatServer iniciado!");
        System.out.println(formatterMDY.format(Calendar.getInstance().getTime()));
        try {                           
            serverSocket = new ServerSocket(3333);
            System.out.println("Esperando conexiones ...");
            while(true){
                ConexionHilo hilo = new ConexionHilo(this, numConexion++, serverSocket.accept());                
                conexiones.add(hilo);                        
                hilo.start();
            }
        } catch (Exception e){
              System.out.println("Error en conexion: "+e.getMessage());
        }                
    }           
    
    void difundir(int origen, String nombre, String mensaje){
         
         for(ConexionHilo conexion: conexiones) {
             if (conexion.id != origen){
                 conexion.enviar(nombre,mensaje);
             }                          
         }         
    }    
}

class ConexionHilo extends Thread {
    ChatServer     chatServer;
    Socket         clientSocket;    
    int            id;
    PrintWriter    out;
    BufferedReader in;

    String         nombre;
    
    SimpleDateFormat formatterMDY;
    
    public ConexionHilo(ChatServer _chs, int _id, Socket _s){
        chatServer   = _chs;
        clientSocket = _s;     
        id           = _id; 
        
        formatterMDY = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    }
    
    void enviar(String sender, String mensaje){
        out.println(sender+" : "+mensaje);        
    }
    
    @Override
    public void run() {
        String buffer;
        
        try {
            in  = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));        
            out = new PrintWriter(this.clientSocket.getOutputStream(), true);
            
            System.out.println("Se recibe conexion desde la direccion: "+clientSocket.getInetAddress());
            out.println("Conectado a ChatServer, escriba su nombre de identificacion");
                                    
            buffer = in.readLine();
            nombre = buffer;
            
            System.out.println(formatterMDY.format(Calendar.getInstance().getTime())+" - Entrando: "+nombre);
            out.println("Bienvenido "+nombre);
            
            chatServer.difundir(id,nombre," se ha unido a la conversacion.");
            
            while(true){
                buffer = in.readLine();
                
                if (buffer!=null){
                    
                    if (buffer.equals("Salir")){
                        in.close();
                        out.close();
                        clientSocket.close();
                        break;
                    }
                                        
                    System.out.println(formatterMDY.format(Calendar.getInstance().getTime())+" - "+nombre+" : "+buffer);                               
                    chatServer.difundir(id,nombre,buffer);                                   
                }
            }                 
        } catch (Exception e){
            System.out.println(e);
        }
    }    
}
