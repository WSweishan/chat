package com.project.exercise.service.impl;

import com.project.exercise.bean.FileMsg;
import com.project.exercise.bean.Users;
import com.project.exercise.service.FileService;
import com.project.exercise.util.PortUtils;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;

/**
 * 接收文件并将其保存在接收方文件列表中
 */
public class FileRecv extends Thread{
    private String fromName;
    private String toName;
    private String fromGroupChat;
    private int port;
    private FileService fileService=new FileServiceImpl();

    public FileRecv(String fromName, String toName, String fromGroupChat, int port) {
        this.fromName = fromName;
        this.toName = toName;
        this.fromGroupChat = fromGroupChat;
        this.port = port;
    }

    @Override
    public void run() {
        System.out.println("service------filerecv.........................");
        ServerSocket serversocket=null;
        Socket socket=null;
        try {
            serversocket=new ServerSocket(port);
            socket=serversocket.accept();
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            String fileName=dataInputStream.readUTF();
            File file=new File(fileName);
            String savePath=file.getAbsolutePath();
            FileOutputStream fileOutputStream=new FileOutputStream(savePath);
            int len=0;
            byte[] bytes = new byte[1024];
            while((len=dataInputStream.read(bytes))!=-1){
                fileOutputStream.write(bytes,0,len);
            }
            fileOutputStream.close();
            dataInputStream.close();
            if(fromGroupChat==null){
                //单聊文件
                Users.getUserByName(toName).getFiles().add(new FileMsg(file,fromName,toName,fromGroupChat));
                System.out.println("文件"+fileName+"已保存在服务器缓存中......");
                //如果接收方在线，将文件发送给接收方...
                onlineSend(toName);
            }else{
                //群聊文件
                String group;
                Iterator iterator=Users.getGroupChatByGroupChatName(fromGroupChat).keySet().iterator();
                while(iterator.hasNext()) {
                    group = String.valueOf(iterator.next());
                    if(Users.getUserByName(group)!=null&&Users.getUserByName(group).getFiles()!=null){
                        Users.getUserByName(group).getFiles().add(new FileMsg(file, fromName, group, fromGroupChat));
                    }
                    //如果接收方在线，将文件发送给接收方...
                    onlineSend(group);
                }
                System.out.println("群聊文件"+fileName+"已保存在服务器缓存中......");
            }
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

    /**
     * 如果接收方在线，将文件发送给接收方...
     */
    private void onlineSend(String toName){
        if(toName==null){
            return;
        }
        if(Users.getUserByName(toName)!=null&&Users.getUserByName(toName).isOnline()){
            System.out.println("接收方在线，将文件发送给接收方...");
            fileService.sendFileToRecv(toName);
        }
    }
}
