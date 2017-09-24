/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatmulticast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

/**
 *
 * @author elivelton
 */
public class ListenerThread extends Thread{
    private final Chat chatGui;
    private MulticastSocket mcSocket;
    private InetAddress group;

    public ListenerThread(Chat chatGui, MulticastSocket mcSocket) {
        this.chatGui = chatGui;
        this.mcSocket = mcSocket;
    }
    
    public void entrarGrupo(String ip, Integer porta){
        try {   
            System.out.println("ip:"+ip+", porta: "+porta);
            // TODO add your handling code here:
            group = InetAddress.getByName(ip);
            mcSocket = new MulticastSocket(porta);
            mcSocket.joinGroup(group);
        } catch (SocketException ex) {
            System.out.println("Socket: " + ex.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        }
    }
    
    public void sairGrupo(){
        try {
            mcSocket.leaveGroup(group);
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        }
    }
    
    public boolean enviarMsg(String apelido, String mensagem, Integer porta){
        boolean msgOk = false;
        if(apelido.length() != 0 || mensagem.length() != 0){
            msgOk = true;
            /* cria um datagrama com a msg */
            byte[] m = mensagem.getBytes();
            DatagramPacket messageOut = new DatagramPacket(m, m.length, group, porta);
            try {
                /* envia o datagrama como multicast */
                mcSocket.send(messageOut);
            } catch (IOException e) {
                System.out.println("IO: " + e.getMessage());
            }
        }
        return msgOk;
    }

    @Override
    public void run() {
        while (true) {            
            /* aguarda o recebimento de msgs de outros peers */
            byte[] buffer = new byte[1024];
            for (int i = 0; i < 3; i++) {
                DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
                try {
                    mcSocket.receive(messageIn);
                } catch (IOException e) {
                    System.out.println("IO: " + e.getMessage());
                }
                //System.out.println("Recebido:" + new String(messageIn.getData()));
            }
        }
    }
}
