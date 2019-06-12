package com.project.exercise.service;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Properties;

public class FileRecv extends Thread{
    private int port;
    String fromName;
    String fromGroupChat;

    public FileRecv(int port, String fromName, String fromGroupChat) {
        this.port = port;
        this.fromName = fromName;
        this.fromGroupChat = fromGroupChat;
    }

    @Override
    public void run() {
        System.out.println("client------filerecv.........................");
        InputStream resource = FileRecv.class.getClassLoader().getResourceAsStream("Service.properties");
        Properties properties = new Properties();
        //创建socket实例
        Socket socket = new Socket();
        try {
            properties.load(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String host=properties.getProperty("serviceIP");
        //连接服务器
        boolean isConnect=true;
        while(isConnect){
            try {
                socket.connect(new InetSocketAddress(host,port));
                break;
            } catch (IOException e) {
            }
        }
        System.out.println(Thread.currentThread().getName() + " 接收文件子线程连接服务器成功");
        try {
            //接收服务器发送的数据
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            //获取文件名
            String fileName=dataInputStream.readUTF();
            //获取文件路径
            String savePath=getDefaultPath()+File.separator+fileName;
            System.out.println("文件存储路径："+savePath);
            FileOutputStream fileOutputStream = new FileOutputStream(savePath);
            int len=0;
            byte[] bytes = new byte[1024];
            while((len=dataInputStream.read(bytes))!=-1){
                fileOutputStream.write(bytes,0,len);
            }
            fileOutputStream.close();
            dataInputStream.close();
            if(fromGroupChat!=null){
                System.out.println(fromName+"在群聊"+fromGroupChat+"中发送的文件接收成功!");
            }else{
                System.out.println(fromName+"发送的文件接收成功!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(socket!=null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static String getDefaultPath(){
        return new File("").getAbsolutePath();
    }
}