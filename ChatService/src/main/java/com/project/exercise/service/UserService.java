package com.project.exercise.service;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface UserService {
    /**
     * 注册操作
     */
    int userRegister(String name,String pwd,String email);
    /**
     * 登录操作
     */
    int userLogin(String name,String pwd);
    /**
     * 找回密码操作
     */
    int userSearchPWD(String name,String email);
    /**
     * 修改密码操作
     */
    int updatePWD(String name,String pwd);
    /**
     * 修改邮箱操作
     */
    int updateEmail(String name,String email);
    /**
     * 通过用户名删除用户
     */
    int delete(String name);
    /**
     * 用户查看好友列表
     */
    int showMyFriends(String name,ObjectNode objectNode);
    /**
     * 用户添加好友
     */
    int addFriend(String name,String friendName);
    /**
     * 用户删除好友
     */
    int deleteFriend(String name,String friendName);
    /**
     * 用户查看群聊列表
     */
    int showMyGroupChats(String name,ObjectNode objectNode);
    /**
     * 用户创建群聊
     */
    int createGroupChat(String name,String groupChatName);
    /**
     * 用户加入群聊
     */
    int joinGroupChat(String name,String groupChatName);
    /**
     * 用户删除群聊
     */
    int deleteGroupChatExist(String name,String groupChatName);
    /**
     * 用户退出群聊
     */
    int deleteGroupChat(String name,String groupChatName);
    /**
     * 用户下线
     */
    void leave(String name);
    /**
     * 一对一聊天建立消息
     */
    int chatting(String name,String toName);
    /**
     * 一对一聊天文本消息
     */
    void chattingMSG(ObjectNode nodes);
    /**
     * 一对一聊天文件消息
     */
    ObjectNode chattingFILE(ObjectNode nodes);
    /**
     * 群聊建立消息
     */
    int groupChatting(String name,String groupName);
    /**
     * 群聊文本消息
     */
    void groupChattingMSG(ObjectNode nodes);
    /**
     * 群聊文件消息
     */
    ObjectNode groupChattingFILE(ObjectNode nodes);
    /**
     * 获取离线消息
     */
    void information(ObjectNode nodes);
}
