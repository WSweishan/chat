package com.project.exercise.service;

public interface FileService {
    /**
     * 从发送方接收文件，缓存到服务器上
     */
    void getFileFromSend(String name,String toName,int port);
    /**
     * 将服务器上缓存的文件发给接收方
     */
    void sendFileToRecv(String toName);
    /**
     * 从发送方接收文件，缓存到服务器上（群聊）
     */
    void getFileFromSendGroup(String name,String groupChat,int port);
}
