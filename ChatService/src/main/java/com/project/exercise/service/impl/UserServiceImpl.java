package com.project.exercise.service.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.project.exercise.bean.User;
import com.project.exercise.bean.Users;
import com.project.exercise.constant.EnMsgType;
import com.project.exercise.controller.DispatcherController;
import com.project.exercise.dao.UserDao;
import com.project.exercise.dao.impl.UserDaoImpl;
import com.project.exercise.service.UserService;
import com.project.exercise.util.EmailUtils;
import com.project.exercise.util.JsonUtils;
import com.project.exercise.util.PortUtils;
import java.util.HashMap;
import java.util.Iterator;

public class UserServiceImpl implements UserService {
    private UserDao userDao = new UserDaoImpl();
    /**
     * 注册操作
     */
    @Override
    public int userRegister(String name, String pwd, String email) {
        int code=userDao.userRegister(name,pwd,email);
        if(code==100){
            Users.addUser(name);
        }
        return code;
    }
    /**
     * 登录操作
     */
    @Override
    public int userLogin(String name, String pwd) {
        int code=userDao.userLogin(name,pwd);
        if(code==300){
            //用户上线
            Users.getUserByName(name).setOnline(true);
            //将用户上线的消息发送给其所有在线好友
            login(name);
        }
        return code;
    }
    /**
     * 找回密码操作
     */
    @Override
    public int userSearchPWD(String name, String email) {
        int code=userDao.userSearchPWD(name,email);
        if(code==300){
            //发送邮件
            try {
                EmailUtils.sendEmail(userDao.getEmailByName(name),userDao.getPWDByName(name));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return code;
    }
    /**
     * 修改密码操作
     */
    @Override
    public int updatePWD(String name, String pwd) {
        return userDao.updatePWD(name,pwd);
    }
    /**
     * 修改邮箱操作
     */
    @Override
    public int updateEmail(String name, String email) {
        return userDao.updateEmail(name,email);
    }
    /**
     * 通过用户名删除用户(注销)
     */
    @Override
    public int delete(String name){
        int code=userDao.delete(name);
        if(code==100){
            Users.deleteUser(name);
        }
        return code;
    }
    /**
     * 用户查看好友列表
     */
    @Override
    public int showMyFriends(String name, ObjectNode objectNode) {
        int code=200;
        if(objectNode!=null){
            Users.traverse(Users.getUserByName(name).getFriends(),2,objectNode);
            code=100;
        }
        return code;
    }
    /**
     * 用户添加好友
     */
    @Override
    public int addFriend(String name, String friendName) {
        int code=200;
        if(Users.getUserByName(friendName)!=null){
            Users.getUserByName(name).addFriend(friendName);
            code=100;
        }else{
            code=101;
        }
        return code;
    }
    /**
     * 用户删除好友
     */
    @Override
    public int deleteFriend(String name, String friendName) {
        int code=200;
        if(Users.getUserByName(name).getFriends().get(friendName)!=null){
            Users.getUserByName(name).deleteFriend(friendName);
            code=100;
        }else{
            code=101;
        }
        return code;
    }
    /**
     * 用户查看群聊列表
     */
    @Override
    public int showMyGroupChats(String name,ObjectNode objectNode){
        int code=200;
        if(objectNode!=null){
            Users.traverse(Users.getUserByName(name).getGroupChats(),1,objectNode);
            code=100;
        }
        return code;
    }
    /**
     * 用户创建群聊
     */
    @Override
    public int createGroupChat(String name, String groupChatName) {
        int code=200;
        if(Users.getGroupChatByGroupChatName(groupChatName)==null){
            Users.createGroupChat(name,groupChatName);
            code=100;
        }else{
            code=101;
        }
        return code;
    }
    /**
     * 用户加入群聊
     */
    @Override
    public int joinGroupChat(String name, String groupChatName) {
        int code=200;
        if(Users.getGroupChatByGroupChatName(groupChatName)!=null){
            Users.addGroupChatExist(name,groupChatName);
            code=100;
        }else{
            code=101;
        }
        return code;
    }
    /**
     * 用户删除群聊
     */
    @Override
    public int deleteGroupChatExist(String name, String groupChatName) {
        int code=200;
        if(Users.getGroupChatByGroupChatName(groupChatName)!=null){
            if(Users.getGroupChatByGroupChatName(groupChatName).get(null).getName().equals(name)){
                Users.deleteGroupChat(groupChatName);
                code=100;
            }else{
                code=102;
            }
        }else{
            code=101;
        }
        return code;
    }
    /**
     * 用户退出群聊
     */
    @Override
    public int deleteGroupChat(String name, String groupChatName) {
        int code=200;
        if(Users.getGroupChatByGroupChatName(groupChatName)!=null){
            if(!Users.getGroupChatByGroupChatName(groupChatName).get(null).getName().equals(name)){
                Users.deleteGroupChatExist(name,groupChatName);
                code=100;
            }else{
                code=102;
            }
        }else{
            code=101;
        }
        return code;
    }
    /**
     * 给在线好友群发用户下线消息
     */
    @Override
    public void leave(String name) {
        Users.getUserByName(name).setOnline(false);
        //封装返回消息
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_NOTIFY_OFFLINE));
        objectNode.put("name",name);
        String resultMsg = objectNode.toString();
        //找到该用户的好友列表，为其在线好友发送消息
        HashMap<String,User> friends=getFriends(name);
        Iterator <String> iterator = friends.keySet().iterator();
        String friendName;
        while(iterator.hasNext()){
            friendName=iterator.next();
            if(isOnline(friendName)){
                DispatcherController.getCTXByName(friendName).channel().writeAndFlush(resultMsg);
            }
        }
    }
    /**
     * 给在线好友群发用户上线消息
     */
    private void login(String name) {
        //封装返回消息
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_NOTIFY_ONLINE));
        objectNode.put("name",name);
        String resultMsg = objectNode.toString();
        //找到该用户的好友列表，为其在线好友发送消息
        HashMap<String,User> friends=getFriends(name);
        Iterator <String> iterator = friends.keySet().iterator();
        String friendName;
        while(iterator.hasNext()){
            friendName=iterator.next();
            if(isOnline(friendName)){
                DispatcherController.getCTXByName(friendName).channel().writeAndFlush(resultMsg);
            }
        }
    }
    /**
     * 判断好友是否在线
     */
    private boolean isOnline(String name){
        return Users.getUserByName(name).isOnline();
    }
    /**
     * 获取好友列表
     */
    private HashMap<String,User> getFriends(String name){
        return Users.getUserByName(name).getFriends();
    }
    /**
     * 一对一聊天建立消息
     */
    @Override
    public int chatting(String name, String toName) {
        int code=-1;
        if(Users.getUserByName(toName)!=null){
            if(Users.getUserByName(name).getFriends().get(toName)!=null){
                code=100;
            }else{
                code=200;
            }
        }else{
            code=101;
        }
        return code;
    }
    /**
     * 一对一聊天文本消息
     */
    @Override
    public void chattingMSG(ObjectNode nodes) {
        //解析参数
        String toName=nodes.get("toName").asText();
        if(Users.getUserByName(toName).isOnline()){
            DispatcherController.getCTXByName(toName).channel().writeAndFlush(nodes.toString());
        }else{
            Users.getUserByName(toName).getChatMSG().add(nodes);
        }
        return;
    }
    /**
     * 一对一聊天文件消息
     */
    @Override
    public ObjectNode chattingFILE(ObjectNode nodes) {
        int code=300,port=-1;
        if(nodes!=null){
            port=PortUtils.getFreePort();
            code=200;
            nodes.put("code",code);
            nodes.put("port",port);
        }
        return nodes;
    }
    /**
     * 群聊建立消息
     */
    @Override
    public int groupChatting(String name, String groupName) {
        int code=-1;
        if(Users.getGroupChatByGroupChatName(groupName)!=null){
            if(Users.getUserByName(name).getGroupChats().get(groupName)!=null){
                code=100;
            }else{
                code=200;
            }
        }else{
            code=101;
        }
        return code;
    }
    /**
     * 群聊文本消息
     */
    @Override
    public void groupChattingMSG(ObjectNode nodes) {
        //解析参数
        String groupChat=nodes.get("groupChat").asText();
        HashMap <String, User> map = Users.getGroupChatByGroupChatName(groupChat);
        Iterator <String> iterator = map.keySet().iterator();
        String toName;
        while(iterator.hasNext()){
            toName=iterator.next();
            if(toName!=null){
                if(Users.getUserByName(toName).isOnline()){
                    DispatcherController.getCTXByName(toName).channel().writeAndFlush(nodes.toString());
                }else{
                    Users.getUserByName(toName).getGroupChatMSG().add(nodes);
                }
            }
        }
        return;
    }
    /**
     * 群聊文件消息
     */
    @Override
    public ObjectNode groupChattingFILE(ObjectNode nodes) {
        int code=300,port=-1;
        if(nodes!=null){
            port=PortUtils.getFreePort();
            code=200;
            nodes.put("code",code);
            nodes.put("port",port);
        }
        return nodes;
    }
    /**
     * 获取离线消息
     */
    @Override
    public void information(ObjectNode nodes) {
        FileServiceImpl fileService = new FileServiceImpl();
        String name=nodes.get("name").asText();
        if(name==null){
            return;
        }
        //查看用户是否有离线文本消息，若有，发送给用户
        while(true){
            if(Users.getUserByName(name).getChatMSG().size()==0){
                break;
            }
            DispatcherController.getCTXByName(name).channel().writeAndFlush(
                    Users.getUserByName(name).getChatMSG().remove().toString());
        }
        //查看用户是否有离线群聊消息，若有，发送给用户
        while(true){
            if(Users.getUserByName(name).getGroupChatMSG().size()==0){
                break;
            }
            DispatcherController.getCTXByName(name).channel().writeAndFlush(
                    Users.getUserByName(name).getGroupChatMSG().remove().toString());
        }
        //查看用户是否有离线文件，若有，发送给用户
        while(true){
            if(Users.getUserByName(name).getFiles().size()==0){
                break;
            }
            fileService.sendFileToRecv(name);
        }
        return;
    }
}
