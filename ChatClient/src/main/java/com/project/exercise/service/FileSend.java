package com.project.exercise.service;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Properties;

public class FileSend extends Thread{
    private int port;
    private File file;
    public FileSend(int port,File file){
        this.port=port;
        this.file=file;
    }
    @Override
    public void run() {
        System.out.println("client------filesend.........................");
        InputStream resource = FileSend.class.getClassLoader().getResourceAsStream("Service.properties");
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
        System.out.println(Thread.currentThread().getName() + " 发送文件子线程连接服务器成功");
        try {
            //文件发送
            //读取磁盘文件
            FileInputStream fileInputStream = new FileInputStream(file);
            String name=file.getName();
            //发送流：socket
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF(name);
            int len=0;
            byte[] bytes = new byte[1024];
            while((len=fileInputStream.read(bytes))!=-1){
                dataOutputStream.write(bytes,0,len);
            }
            dataOutputStream.close();
            fileInputStream.close();
            System.out.println(Thread.currentThread().getName() + " 发送文件子线程发送成功");
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
}
