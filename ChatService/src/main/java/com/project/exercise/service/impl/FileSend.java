package com.project.exercise.service.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.project.exercise.bean.Users;
import com.project.exercise.constant.EnMsgType;
import com.project.exercise.controller.DispatcherController;
import com.project.exercise.util.JsonUtils;
import com.project.exercise.util.PortUtils;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FileSend extends Thread{
    private String toName;
    private int port;

    public FileSend(String toName, int port) {
        this.toName = toName;
        this.port = port;
    }

    private void sendPort(){
        String fromName=null,fromGroupChat=null;
        fromName=Users.getUserByName(toName).getFiles().peek().getFromName();
        fromGroupChat=Users.getUserByName(toName).getFiles().peek().getFromGroupChat();
        if(fromGroupChat==null){
            sendPort(fromName,toName,port);
        }else{
            sendPortGroup(fromName,toName,fromGroupChat,port);
        }
    }
    /**
     * 发送给接收方端口号，告知接收方服务器要给它发送消息
     */
    private void sendPort(String fromName, String toName,int port) {
        ObjectNode nodes=JsonUtils.getObjectNode();
        nodes.put("msgtype", String.valueOf(EnMsgType.EN_MSG_CHAT_FILE));
        nodes.put("type","ok");
        nodes.put("fromName",fromName);
        nodes.put("toName",toName);
        nodes.put("port",port);
        DispatcherController.getCTXByName(toName).channel().writeAndFlush(nodes.toString());
    }
    private void sendPortGroup(String fromName, String toName,String fromGroupChat,int port) {
        ObjectNode nodes=JsonUtils.getObjectNode();
        nodes.put("msgtype", String.valueOf(EnMsgType.EN_MSG_CHAT_ALL_FILE));
        nodes.put("type","ok");
        nodes.put("fromName",fromName);
        nodes.put("toName",toName);
        nodes.put("fromGroupChat",fromGroupChat);
        nodes.put("port",port);
        DispatcherController.getCTXByName(toName).channel().writeAndFlush(nodes.toString());
    }
    @Override
    public void run() {
        System.out.println("service------filesend.........................");
        ServerSocket serversocket=null;
        Socket socket=null;
        try {
            serversocket=new ServerSocket(port);
            sendPort();
            socket=serversocket.accept();
            if(Users.getUserByName(toName)==null&&Users.getUserByName(toName).getFiles().peek()==null){
                return;
            }
            File file = Users.getUserByName(toName).getFiles().poll().getFile();
            FileInputStream fileInputStream = new FileInputStream(file);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF(file.getName());
            int len=0;
            byte[] bytes = new byte[1024];
            while((len=fileInputStream.read(bytes))!=-1){
                dataOutputStream.write(bytes,0,len);
            }
            dataOutputStream.close();
            fileInputStream.close();
            System.out.println("文件"+file.getName()+"已发送给"+toName);
            Users.getUserByName(toName).getFiles().poll();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(serversocket!=null){
                try {
                    serversocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(socket!=null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            PortUtils.closePort(port);
        }
    }
}
