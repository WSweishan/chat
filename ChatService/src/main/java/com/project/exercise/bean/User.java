package com.project.exercise.bean;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.HashMap;
import java.util.LinkedList;

public class User {
    /**
     * 用户名
     */
    private String name;
    /**
     * 用户是否在线
     */
    private boolean isOnline=false;
    /**
     * 用户好友列表
     */
    private HashMap<String,User> friends=new HashMap<>();
    /**
     * 用户群聊列表
     */
    private HashMap<String,HashMap<String,User>> groupChats=new HashMap <>();
    /**
     * 用户好友消息列表
     */
    private LinkedList<ObjectNode> chatMSG=new LinkedList<>();
    /**
     * 用户群聊消息列表
     */
    private LinkedList<ObjectNode> groupChatMSG=new LinkedList<>();
    /**
     * 用户文件消息列表
     */
    private LinkedList<FileMsg> files=new LinkedList <>();
    /**
     * 构造方法设置用户名
     */
    public User(String name){
        this.name=name;
    }
    /**
     * 获取用户名
     */
    public String getName() {
        return name;
    }

    /**
     * 获取用户在线状态
     */
    public boolean isOnline() {
        return isOnline;
    }

    /**
     * 设置用户在线状态
     */
    public void setOnline(boolean online) {
        isOnline = online;
    }

    /**
     * 获取好友列表
     */
    public HashMap <String, User> getFriends() {
        return friends;
    }

    /**
     * 添加好友
     */
    public void addFriend(String name){
        friends.put(name,Users.getUserByName(name));
    }

    /**
     * 删除好友
     */
    public void deleteFriend(String name){
        friends.remove(name);
    }

    /**
     * 获取用户群聊列表
     */
    public HashMap <String, HashMap <String, User>> getGroupChats() {
        return groupChats;
    }

    /**
     * 加入群聊
     */
    public void addGroupChat(String groupChatName){
        Users.getGroupChatByGroupChatName(groupChatName).put(name,this);
        groupChats.put(groupChatName,Users.getGroupChatByGroupChatName(groupChatName));
    }

    /**
     * 退出群聊
     */
    public void deleteGroupChat(String groupChatName){
        Users.getGroupChatByGroupChatName(groupChatName).remove(name);
        groupChats.remove(groupChatName);
    }
    /**
     * 获取好友消息列表
     */
    public LinkedList <ObjectNode> getChatMSG() {
        return chatMSG;
    }
    /**
     * 获取群聊消息列表
     */
    public LinkedList <ObjectNode> getGroupChatMSG() {
        return groupChatMSG;
    }
    /**
     * 获取文件消息
     */
    public LinkedList<FileMsg> getFiles(){
        return files;
    }
}
