package com.project.exercise.service.impl;

import com.project.exercise.bean.Users;
import com.project.exercise.service.FileService;
import com.project.exercise.util.PortUtils;
import java.util.Objects;

public class FileServiceImpl implements FileService {
    @Override
    public void getFileFromSend(String name, String toName, int port) {
        //接收文件并将其保存在接收方文件列表中
        new FileRecv(name,toName,null,port).start();
    }

    @Override
    public void sendFileToRecv(String toName) {
        if(toName==null||Users.getUserByName(toName)==null){
            return;
        }
        if(Users.getUserByName(toName).getFiles().peek()==null){
            return;
        }
        String fromGroupChat=null;
        fromGroupChat=Objects.requireNonNull(Users.getUserByName(toName).getFiles().peek()).getFromGroupChat();
        int port;
        if(Users.getUserByName(toName)!=null&&Users.getUserByName(toName).getFiles().peek()!=null){
            port=PortUtils.getFreePort();
            if(fromGroupChat==null){
                //单聊
                new FileSend(toName,port).start();
            }else{
                //群聊
                new FileSend(toName,port).start();
            }
        }
    }

    @Override
    public void getFileFromSendGroup(String name, String groupChat, int port) {
        //接收文件并将其保存在接收方文件列表中
        new FileRecv(name,null,groupChat,port).start();
    }

}
