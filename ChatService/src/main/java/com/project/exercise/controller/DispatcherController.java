package com.project.exercise.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.project.exercise.constant.EnMsgType;
import com.project.exercise.service.FileService;
import com.project.exercise.service.UserService;
import com.project.exercise.service.impl.FileServiceImpl;
import com.project.exercise.service.impl.UserServiceImpl;
import com.project.exercise.util.JsonUtils;
import io.netty.channel.ChannelHandlerContext;
import java.net.SocketAddress;
import java.util.HashMap;

/**
 * 具体请求分发类
 */
public class DispatcherController {
    //通过单例模式实例化对象
    private static DispatcherController controller = new DispatcherController();
    //保存所有用户通信的channelHandler实例，方便群发消息
    private static HashMap<String,ChannelHandlerContext> ctxAll=new HashMap <>();
    //保存所有客户端及其当前用户，方便异常处理
    private static HashMap<SocketAddress,String> ctxNow=new HashMap <>();
    //对外提供的实例化方法
    public static DispatcherController getInstance() {
        return controller;
    }
    //对外提供通过用户名获取通道的方法
    public static ChannelHandlerContext getCTXByName(String name){
        return ctxAll.get(name);
    }
    //对外提供通过客户端地址获取客户端当前用户的方法
    public static String getNameNowBySocketAddress(SocketAddress socketAddress){
        return ctxNow.get(socketAddress);
    }
    //用户相关的service
    private UserService userService = new UserServiceImpl();
    private FileService fileService = new FileServiceImpl();
    /**
     * 分类的核心方法
     */
    public String process(ChannelHandlerContext ctx, String msg) {
        //解析消息
        ObjectNode jsonNodes = JsonUtils.getObjectNode(msg);
        if(jsonNodes.get("name")!=null){
            //获取用户名，和通道一起保存下来
            String name = jsonNodes.get("name").asText();
            DispatcherController.ctxAll.put(name,ctx);
            //获取客户端地址，和用户名一起保存下来
            SocketAddress client=ctx.channel().remoteAddress();
            DispatcherController.ctxNow.put(client,name);
        }
        //获取用户发送给服务器的消息类型
        String msgtype = jsonNodes.get("msgtype").asText();
        //服务器根据用户的消息类型选择相应的服务
        if (String.valueOf(EnMsgType.EN_MSG_REGISTER).equals(msgtype)) {
            //用户注册
            return userRegister(jsonNodes);
        }
        if (String.valueOf(EnMsgType.EN_MSG_LOGIN).equals(msgtype)) {
            //用户登录
            return userLogin(jsonNodes);
        }
        if (String.valueOf(EnMsgType.EN_MSG_FORGET_PWD).equals(msgtype)) {
            //用户找回密码
            return userSearchPWD(jsonNodes);
        }
        if (String.valueOf(EnMsgType.EN_MSG_MODIFY_PWD).equals(msgtype)) {
            //用户修改密码
            return updatePWD(jsonNodes);
        }
        if (String.valueOf(EnMsgType.EN_MSG_MODIFY_EMAIL).equals(msgtype)) {
            //用户修改邮箱
            return updateEmail(jsonNodes);
        }
        if (String.valueOf(EnMsgType.EN_MSG_DELETE).equals(msgtype)) {
            //用户注销
            return delete(jsonNodes);
        }
        if (String.valueOf(EnMsgType.EN_MSG_SHOW_MY_FRIENDS).equals(msgtype)) {
            //用户查看好友列表
            return showMyFriends(jsonNodes);
        }
        if (String.valueOf(EnMsgType.EN_MSG_ADD_FRIEND).equals(msgtype)) {
            //用户添加好友
            return addFriend(jsonNodes);
        }
        if (String.valueOf(EnMsgType.EN_MSG_DELETE_FRIEND).equals(msgtype)) {
            //用户删除好友
            return deleteFriend(jsonNodes);
        }
        if (String.valueOf(EnMsgType.EN_MSG_SHOW_GROUPCHATS).equals(msgtype)) {
            //用户查看群聊列表
            return showMyGroupChats(jsonNodes);
        }
        if (String.valueOf(EnMsgType.EN_MSG_CREATE_GROUPCHAT).equals(msgtype)) {
            //用户创建群聊
            return createGroupChat(jsonNodes);
        }
        if (String.valueOf(EnMsgType.EN_MSG_JOIN_GROUPCHAT).equals(msgtype)) {
            //用户加入群聊
            return joinGroupChat(jsonNodes);
        }
        if (String.valueOf(EnMsgType.EN_MSG_DELETE_GROUPCHAT_EXIST).equals(msgtype)) {
            //用户删除群聊
            return deleteGroupChatExist(jsonNodes);
        }
        if (String.valueOf(EnMsgType.EN_MSG_DELETE_GROUPCHAT).equals(msgtype)) {
            //用户退出群聊
            return deleteGroupChat(jsonNodes);
        }
        if (String.valueOf(EnMsgType.EN_MSG_NOTIFY_OFFLINE).equals(msgtype)) {
            //用户下线
            return leave(jsonNodes);
        }
        if (String.valueOf(EnMsgType.EN_MSG_CHAT).equals(msgtype)) {
            //一对一聊天建立消息
            return chatting(jsonNodes);
        }
        if (String.valueOf(EnMsgType.EN_MSG_CHAT_MSG).equals(msgtype)) {
            //一对一聊天文本消息
            return chattingMSG(jsonNodes);
        }
        if (String.valueOf(EnMsgType.EN_MSG_CHAT_FILE).equals(msgtype)) {
            //一对一聊天文件消息
            return chattingFILE(jsonNodes);
        }
        if (String.valueOf(EnMsgType.EN_MSG_CHAT_ALL).equals(msgtype)) {
            //群聊建立消息
            return groupChatting(jsonNodes);
        }
        if (String.valueOf(EnMsgType.EN_MSG_CHAT_ALL_MSG).equals(msgtype)) {
            //群聊文本消息
            return groupChattingMSG(jsonNodes);
        }
        if (String.valueOf(EnMsgType.EN_MSG_CHAT_ALL_FILE).equals(msgtype)) {
            //群聊文件消息
            return groupChattingFILE(jsonNodes);
        }
        if (String.valueOf(EnMsgType.EN_MSG_OFFLINE).equals(msgtype)) {
            //获取离线消息
            return information(jsonNodes);
        }
        return "";
    }
    /**
     * 用户注册
     */
    private String userRegister(ObjectNode nodes) {
        //解析参数
        String name=nodes.get("name").asText();
        String pwd=nodes.get("pwd").asText();
        String email=nodes.get("email").asText();
        //将用户的注册信息添加到数据库用户信息表中
        int userRegister = userService.userRegister(name, pwd, email);
        //封装返回消息
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_ACK));
        objectNode.put("srctype", String.valueOf(EnMsgType.EN_MSG_REGISTER));
        objectNode.put("code",userRegister);
        return objectNode.toString();
    }
    /**
     * 用户登录
     */
    private String userLogin(ObjectNode nodes) {
        //解析参数
        String name=nodes.get("name").asText();
        String pwd=nodes.get("pwd").asText();
        //检查用户的登录信息
        int userLogin = userService.userLogin(name, pwd);
        //封装返回消息
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_ACK));
        objectNode.put("srctype", String.valueOf(EnMsgType.EN_MSG_LOGIN));
        objectNode.put("code",userLogin);
        return objectNode.toString();
    }
    /**
     * 用户找回密码
     */
    private String userSearchPWD(ObjectNode nodes) {
        //解析参数
        String name=nodes.get("name").asText();
        String email=nodes.get("email").asText();
        //检查用户的信息
        int userSearchPWD = userService.userSearchPWD(name, email);
        //封装返回消息
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_ACK));
        objectNode.put("srctype", String.valueOf(EnMsgType.EN_MSG_FORGET_PWD));
        objectNode.put("code",userSearchPWD);
        return objectNode.toString();
    }
    /**
     * 修改密码
     */
    private String updatePWD(ObjectNode nodes){
        //解析参数
        String name=nodes.get("name").asText();
        String pwd=nodes.get("pwd").asText();
        //修改密码
        int updatePWD = userService.updatePWD(name, pwd);
        //封装返回消息
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_ACK));
        objectNode.put("srctype", String.valueOf(EnMsgType.EN_MSG_MODIFY_PWD));
        objectNode.put("code",updatePWD);
        return objectNode.toString();
    }
    /**
     * 修改邮箱
     */
    private String updateEmail(ObjectNode nodes){
        //解析参数
        String name=nodes.get("name").asText();
        String email=nodes.get("email").asText();
        //修改密码
        int updateEmail = userService.updateEmail(name,email);
        //封装返回消息
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_ACK));
        objectNode.put("srctype", String.valueOf(EnMsgType.EN_MSG_MODIFY_EMAIL));
        objectNode.put("code",updateEmail);
        return objectNode.toString();
    }
    /**
     * 用户注销
     */
    private String delete(ObjectNode nodes) {
        //解析参数
        String name=nodes.get("name").asText();
        //注销，将用户信息从数据库中删除
        int delete = userService.delete(name);
        //封装返回消息
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_ACK));
        objectNode.put("srctype", String.valueOf(EnMsgType.EN_MSG_DELETE));
        objectNode.put("code",delete);
        return objectNode.toString();
    }
    /**
     * 用户查看好友列表
     */
    private String showMyFriends(ObjectNode nodes) {
        //解析参数
        String name=nodes.get("name").asText();
        //封装返回消息
        ObjectNode objectNode = JsonUtils.getObjectNode();
        int showMyFriends = userService.showMyFriends(name,objectNode);
        objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_ACK));
        objectNode.put("srctype", String.valueOf(EnMsgType.EN_MSG_SHOW_MY_FRIENDS));
        objectNode.put("code",showMyFriends);
        return objectNode.toString();
    }
    /**
     * 用户添加好友
     */
    private String addFriend(ObjectNode nodes) {
        //解析参数
        String name=nodes.get("name").asText();
        String friendName=nodes.get("friendName").asText();
        //添加好友
        int addFriend = userService.addFriend(name,friendName);
        //封装返回消息
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_ACK));
        objectNode.put("srctype", String.valueOf(EnMsgType.EN_MSG_ADD_FRIEND));
        objectNode.put("code",addFriend);
        return objectNode.toString();
    }
    /**
     * 用户删除好友
     */
    private String deleteFriend(ObjectNode nodes) {
        //解析参数
        String name=nodes.get("name").asText();
        String friendName=nodes.get("friendName").asText();
        //删除好友
        int deleteFriend = userService.deleteFriend(name,friendName);
        //封装返回消息
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_ACK));
        objectNode.put("srctype", String.valueOf(EnMsgType.EN_MSG_DELETE_FRIEND));
        objectNode.put("code",deleteFriend);
        return objectNode.toString();
    }
    /**
     * 查看群聊列表
     */
    private String showMyGroupChats(ObjectNode nodes){
        //解析参数
        String name=nodes.get("name").asText();
        //封装返回消息
        ObjectNode objectNode = JsonUtils.getObjectNode();
        int showMyGroupChats = userService.showMyGroupChats(name,objectNode);
        objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_ACK));
        objectNode.put("srctype", String.valueOf(EnMsgType.EN_MSG_SHOW_GROUPCHATS));
        objectNode.put("code",showMyGroupChats);
        return objectNode.toString();
    }
    /**
     * 用户创建群聊
     */
    private String createGroupChat(ObjectNode nodes){
        //解析参数
        String name=nodes.get("name").asText();
        String groupChatName=nodes.get("groupChatName").asText();
        //添加好友
        int create = userService.createGroupChat(name,groupChatName);
        //封装返回消息
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_ACK));
        objectNode.put("srctype", String.valueOf(EnMsgType.EN_MSG_CREATE_GROUPCHAT));
        objectNode.put("code",create);
        return objectNode.toString();
    }
    /**
     * 用户加入群聊
     */
    private String joinGroupChat(ObjectNode nodes){
        //解析参数
        String name=nodes.get("name").asText();
        String groupChatName=nodes.get("groupChatNameJoin").asText();
        //添加好友
        int join = userService.joinGroupChat(name,groupChatName);
        //封装返回消息
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_ACK));
        objectNode.put("srctype", String.valueOf(EnMsgType.EN_MSG_JOIN_GROUPCHAT));
        objectNode.put("code",join);
        return objectNode.toString();
    }
    /**
     * 用户删除群聊
     */
    private String deleteGroupChatExist(ObjectNode nodes){
        //解析参数
        String name=nodes.get("name").asText();
        String groupChatName=nodes.get("groupChatName").asText();
        //添加好友
        int delete = userService.deleteGroupChatExist(name,groupChatName);
        //封装返回消息
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_ACK));
        objectNode.put("srctype", String.valueOf(EnMsgType.EN_MSG_DELETE_GROUPCHAT_EXIST));
        objectNode.put("code",delete);
        return objectNode.toString();
    }
    /**
     * 用户退出群聊
     */
    private String deleteGroupChat(ObjectNode nodes){
        //解析参数
        String name=nodes.get("name").asText();
        String groupChatName=nodes.get("groupChatNameJoin").asText();
        //添加好友
        int delete = userService.deleteGroupChat(name,groupChatName);
        //封装返回消息
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_ACK));
        objectNode.put("srctype", String.valueOf(EnMsgType.EN_MSG_DELETE_GROUPCHAT));
        objectNode.put("code",delete);
        return objectNode.toString();
    }
    /**
     * 用户下线
     */
    private String leave(ObjectNode nodes){
        //解析参数
        String name=nodes.get("name").asText();
        userService.leave(name);
        return "";
    }
    /**
     * 一对一聊天建立消息
     */
    private String chatting(ObjectNode nodes){
        //解析参数
        String name=nodes.get("name").asText();
        String toName=nodes.get("toName").asText();
        //添加好友
        int code = userService.chatting(name,toName);
        //封装返回消息
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_ACK));
        objectNode.put("srctype", String.valueOf(EnMsgType.EN_MSG_CHAT));
        objectNode.put("code",code);
        return objectNode.toString();
    }
    /**
     * 一对一聊天文本消息
     */
    private String chattingMSG(ObjectNode nodes){
        //处理
        userService.chattingMSG(nodes);
        return "";
    }
    /**
     * 一对一聊天文件消息
     */
    private String chattingFILE(ObjectNode nodes){
        //解析参数
        String type=nodes.get("type").asText();
        if(type.equals("syn")){
            //接收方请求向服务器发送文件
            ObjectNode objectNode=userService.chattingFILE(nodes);
            objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_ACK));
            objectNode.put("srctype",String.valueOf(EnMsgType.EN_MSG_CHAT_FILE));
            return objectNode.toString();
        }
        if(type.equals("start")){
            //接收方开始向服务器发送文件
            String name=nodes.get("name").asText();
            String toName=nodes.get("toName").asText();
            Integer port=nodes.get("port").asInt();
            if(name != null && toName != null){
                fileService.getFileFromSend(name,toName,port);
            }
        }
        return "";
    }
    /**
     * 群聊建立消息
     */
    private String groupChatting(ObjectNode nodes){
        //解析参数
        String name=nodes.get("name").asText();
        String groupChat=nodes.get("groupChat").asText();
        //添加好友
        int code = userService.groupChatting(name,groupChat);
        //封装返回消息
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_ACK));
        objectNode.put("srctype", String.valueOf(EnMsgType.EN_MSG_CHAT_ALL));
        objectNode.put("code",code);
        return objectNode.toString();
    }
    /**
     * 群聊文本消息
     */
    private String groupChattingMSG(ObjectNode nodes){
        //处理
        userService.groupChattingMSG(nodes);
        return "";
    }
    /**
     * 群聊文件消息
     */
    private String groupChattingFILE(ObjectNode nodes){
        //解析参数
        String type=nodes.get("type").asText();
        if(type.equals("syn")){
            //接收方请求向服务器发送文件
            ObjectNode objectNode=userService.groupChattingFILE(nodes);
            objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_ACK));
            objectNode.put("srctype",String.valueOf(EnMsgType.EN_MSG_CHAT_ALL_FILE));
            return objectNode.toString();
        }
        if(type.equals("start")){
            //接收方开始向服务器发送文件
            String name=nodes.get("name").asText();
            String groupChat=nodes.get("groupChat").asText();
            Integer port=nodes.get("port").asInt();
            if(name != null && groupChat != null){
                fileService.getFileFromSendGroup(name,groupChat,port);
            }
        }
        return "";
    }
    /**
     * 获取离线消息
     */
    private String information(ObjectNode nodes){
        userService.information(nodes);
        return "";
    }
}