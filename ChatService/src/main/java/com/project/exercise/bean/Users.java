package com.project.exercise.bean;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 保存所有用户的信息
 */
public class Users {
    /**
     * 计数
     */
    private static int count=0;
    /**
     * 所有用户及其相应的信息
     */
    private static HashMap<String, User> users=new HashMap <>();
    /**
     * 所有群聊及其相应信息（群聊名称，群聊成员）（key为空时，对应的用户为群主）
     */
    private static HashMap<String,HashMap<String, User>> groupChats=new HashMap <>();
    /**
     * 添加用户,在用户注册时
     */
    public static void addUser(String name){
        users.put(name,new User(name));
    }
    /**
     * 删除用户，在用户注销时
     */
    public static void deleteUser(String name){
        //注销用户退出群聊
        Iterator <Map.Entry <String, HashMap <String, User>>> iteratorGroup=groupChats.entrySet().iterator();
        Map.Entry <String, HashMap <String, User>> mapGroup;
        while(iteratorGroup.hasNext()){
            mapGroup=iteratorGroup.next();
            if(mapGroup.getValue().get(null).getName().equals(name)){
                //注销用户是群主，删除群聊
                deleteGroupChat(mapGroup.getKey());
            }else{
                //注销用户不是群主
                Users.getUserByName(name).deleteGroupChat(mapGroup.getKey());
            }
        }
        users.remove(name);
        //用户好友列表中删除注销用户
        Iterator<Map.Entry<String, User>> iterator=users.entrySet().iterator();
        Map.Entry<String,User> map;
        while (iterator.hasNext()){
            map=iterator.next();
            if(map.getValue().getFriends().get(name)!=null){
                map.getValue().deleteFriend(name);
            }
        }
    }
    /**
     * 根据用户名获取用户信息
     */
    public static User getUserByName(String name){
        return users.get(name);
    }
    /**
     * 根据群聊名称获取群聊
     */
    public static HashMap<String, User> getGroupChatByGroupChatName(String groupChatName){
        return groupChats.get(groupChatName);
    }
    /**
     * 创建新群聊
     */
    public static void createGroupChat(String hostName,String groupChatName){
        groupChats.put(groupChatName,new HashMap <String,User>());
        //设置群主
        groupChats.get(groupChatName).put(null,Users.getUserByName(hostName));
        //群主入群
        Users.getUserByName(hostName).addGroupChat(groupChatName);
    }
    /**
     * 加入已有群聊
     */
    public static void addGroupChatExist(String name,String groupChatName){
        Users.getUserByName(name).addGroupChat(groupChatName);
    }
    /**
     * 删除已有群聊(唯有群主可以删除群聊)
     */
    public static void deleteGroupChat(String groupChatName){
        Iterator<Map.Entry<String, User>> iterator=Users.getGroupChatByGroupChatName(groupChatName).entrySet().iterator();
        Map.Entry<String, User> map;
        while(iterator.hasNext()){
            map=iterator.next();
            if(map.getKey()!=null){
                //群成员，包括群主
                map.getValue().getGroupChats().remove(groupChatName);
            }
        }
        groupChats.remove(groupChatName);
    }
    /**
     * 退出已加入群聊
     */
    public static void deleteGroupChatExist(String name,String groupChatName){
        Users.getUserByName(name).deleteGroupChat(groupChatName);
    }
    /**
     * 遍历相应的hashmap集合，并根据相关选择条件进行处理。
     */
    public static void traverse(HashMap map,int select,ObjectNode objectNode){
        Iterator iterator=map.entrySet().iterator();
        Map.Entry userEntry=null;
        count=0;
        while(iterator.hasNext()){
            userEntry= (Map.Entry) iterator.next();
            //根据相关选择条件进行处理
            select(userEntry,select,objectNode);
        }
        userEntry=null;
        objectNode.put("count",count);
    }
    /**
     * 选择条件
     */
    public static void select(Map.Entry userEntry,int selcet,ObjectNode objectNode){
        switch(selcet){
            case 1://查看群聊列表(仅显示群聊名称)
                count++;
                String label1="groupChat"+count;
                String groupChatName= (String) userEntry.getKey();
                objectNode.put(label1,groupChatName);
                break;
            case 2://查看好友列表(显示好友姓名及在线状态)
                count++;
                String label="friend"+count;
                String state;
                if(((User)userEntry.getValue()).isOnline()){
                    state="在线";
                }else{
                    state="不在线";
                }
                String friend=userEntry.getKey()+":"+state;
                objectNode.put(label,friend);
                break;
            default:
                break;
        }
    }
}
