package com.project.exercise.constant;

public enum EnMsgType {
    /**
     * 用户登录消息
     */
    EN_MSG_LOGIN,
    /**
     * 用户注册消息
     */
    EN_MSG_REGISTER,
    /**
     * 用户忘记密码消息
     */
    EN_MSG_FORGET_PWD,
    /**
     * 修改密码消息
     */
    EN_MSG_MODIFY_PWD,
    /**
     * 修改邮箱消息
     */
    EN_MSG_MODIFY_EMAIL,
    /**
     * 用户注销消息
     */
    EN_MSG_DELETE,
    /**
     * 查看好友列表消息
     */
    EN_MSG_SHOW_MY_FRIENDS,
    /**
     * 添加好友的消息
     */
    EN_MSG_ADD_FRIEND,
    /**
     * 删除好友的消息
     */
    EN_MSG_DELETE_FRIEND,
    /**
     * 创建新群聊消息
     */
    EN_MSG_CREATE_GROUPCHAT,
    /**
     * 加入已有群聊消息
     */
    EN_MSG_JOIN_GROUPCHAT,
    /**
     * 删除已有群聊消息
     */
    EN_MSG_DELETE_GROUPCHAT_EXIST,
    /**
     * 退出已加入群聊消息
     */
    EN_MSG_DELETE_GROUPCHAT,
    /**
     * 查看群聊列表消息
     */
    EN_MSG_SHOW_GROUPCHATS,
    /**
     * 群发用户上线消息
     */
    EN_MSG_NOTIFY_ONLINE,
    /**
     * 群发用户下线消息
     */
    EN_MSG_NOTIFY_OFFLINE,
    /**
     * 一对一聊天建立消息
     */
    EN_MSG_CHAT,
    /**
     * 一对一聊天文本消息
     */
    EN_MSG_CHAT_MSG,
    /**
     * 一对一聊天文件消息
     */
    EN_MSG_CHAT_FILE,
    /**
     * 群聊建立消息
     */
    EN_MSG_CHAT_ALL,
    /**
     * 群聊文本消息
     */
    EN_MSG_CHAT_ALL_MSG,
    /**
     * 群聊文件消息
     */
    EN_MSG_CHAT_ALL_FILE,
    /**
     * 响应消息
     */
    EN_MSG_ACK,
    /**
     * 请求离线消息
     */
    EN_MSG_OFFLINE
}
