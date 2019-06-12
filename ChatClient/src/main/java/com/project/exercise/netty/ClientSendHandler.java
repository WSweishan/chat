package com.project.exercise.netty;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.project.exercise.constant.EnMsgType;
import com.project.exercise.service.FileSend;
import com.project.exercise.util.JsonUtils;
import io.netty.channel.Channel;
import java.io.File;
import java.util.Scanner;

/**
 * 处理客户端向服务器发送数据的情况
 */
public class ClientSendHandler {
    /**
     * 处理客户端业务
     */
    public static void dispose(Channel channel){
        Scanner in=new Scanner(System.in);
        clientMenu();
        int select=in.nextInt();
        while(select!=4){
            switch (select){
                case 1://用户注册
                    register(channel);
                    break;
                case 2://用户登录
                    login(channel,null,null);
                    break;
                case 3://找回密码
                    searchPWD(channel);
                    break;
                default://输入错误
                    System.out.println("您的输入有误，请重新输入您的选择！");
                    break;
            }
            clientMenu();
            select=in.nextInt();
        }
        System.exit(0);
    }
    /**
     * 用户注册
     */
    private static void register(Channel channel) {
        Scanner in=new Scanner(System.in);
        System.out.println("==============================");
        System.out.println("欢迎注册聊天系统");
        System.out.println("==============================");
        System.out.println("请输入您的注册信息：");
        System.out.print("用户名：");
        String name=in.next();
        System.out.print("密码：");
        String pwd=in.next();
        System.out.print("邮箱：");
        String email=in.next();
        //封装给服务端发送的数据
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_REGISTER));
        objectNode.put("name",name);
        objectNode.put("pwd",pwd);
        objectNode.put("email",email);
        String msg=objectNode.toString();
        //给服务端发送消息
        channel.writeAndFlush(msg);
        //等服务端的返回消息
        int resultCode=-1;
        try {
            resultCode = (int) ClientRecvHandler.queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(resultCode==100){
            System.out.println("用户注册成功\n即将登录聊天系统......");
            //登录聊天系统
            login(channel,name,pwd);
        } else if(resultCode==101){
            System.out.println("用户名已存在，无法注册\n即将返回聊天系统登录界面......");
        } else if(resultCode==200){
            System.out.println("用户注册失败\n即将返回聊天系统登录界面......");
        } else{
            System.out.println("用户注册出现未知错误\n即将返回聊天系统登录界面......");
        }
    }
    /**
     * 用户登录
     */
    private static void login(Channel channel,String name,String pwd) {
        Scanner in=new Scanner(System.in);
        System.out.println("==============================");
        System.out.println("欢迎登录聊天系统");
        System.out.println("==============================");
        if(name==null&&pwd==null){
            System.out.print("请输入您的用户名：");
            name=in.next();
            System.out.print("请输入您的密码：");
            pwd=in.next();
        }
        //封装给服务端发送的数据
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_LOGIN));
        objectNode.put("name",name);
        objectNode.put("pwd",pwd);
        String msg=objectNode.toString();
        //给服务端发送消息
        channel.writeAndFlush(msg);
        //等服务端的返回消息
        int resultCode=-1;
        try {
            resultCode = (int) ClientRecvHandler.queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        switch (resultCode){
            case 300://用户登录成功
                System.out.println("用户登录成功\n即将进入聊天系统......");
                //进入聊天系统
                chat(channel,name);
                break;
            case 301://用户不存在
                System.out.println("用户不存在，登录失败\n即将返回聊天系统登录界面......");
                break;
            case 302://密码错误
                System.out.println("用户密码错误，登录失败\n即将返回聊天系统登录界面......");
                break;
            case 400://用户登录失败
                System.out.println("用户登录失败\n即将返回聊天系统登录界面......");
                break;
            default://用户登录出现未知错误
                System.out.println("用户登录出现未知错误\n即将返回聊天系统登录界面......");
                break;
        }
    }
    /**
     * 找回密码
     */
    private static void searchPWD(Channel channel) {
        Scanner in=new Scanner(System.in);
        System.out.println("==============================");
        System.out.println("请输入您的用户名和邮箱：");
        System.out.print("用户名：");
        String name=in.next();
        System.out.print("邮箱：");
        String email=in.next();
        //封装给服务端发送的数据
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_FORGET_PWD));
        objectNode.put("name",name);
        objectNode.put("email",email);
        String msg=objectNode.toString();
        //给服务端发送消息
        channel.writeAndFlush(msg);
        //等服务端的返回消息
        int resultCode=-1;
        try {
            resultCode = (int) ClientRecvHandler.queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        switch (resultCode){
            case 300://用户信息核对成功
                System.out.println("用户信息核对成功\n您的密码已发送到您注册的邮箱里......");
                break;
            case 301://用户不存在
                System.out.println("用户不存在，用户信息核对失败\n即将返回聊天系统登录界面......");
                break;
            case 303://邮箱错误
                System.out.println("用户邮箱错误，用户信息核对失败\n即将返回聊天系统登录界面......");
                break;
            case 400://用户找回密码失败
                System.out.println("用户找回密码失败\n即将返回聊天系统登录界面......");
                break;
            default://用户登录出现未知错误
                System.out.println("用户找回密码出现未知错误\n即将返回聊天系统登录界面......");
                break;
        }
    }
    /**
     * 聊天系统
     */
    public static void chat(Channel channel,String name) {
        Scanner in=new Scanner(System.in);
        boolean isDelete=false;
        chatMenu();
        //接收离线消息
        information(channel,name);
        int select=in.nextInt();
        while(select!=12){
            switch (select){
                case 1://修改密码
                    updatePWD(channel,name);
                    break;
                case 2://修改邮箱
                    updateEmail(channel,name);
                    break;
                case 3://添加好友
                    addFriend(channel,name);
                    break;
                case 4://删除好友
                    deleteFriend(channel,name);
                    break;
                case 5://查看好友列表
                    showMyFriends(channel,name);
                    break;
                case 6://选择好友开始聊天或发送文件
                    chatOne(channel,name);
                    break;
                case 7://添加群聊
                    addGroupChat(channel,name);
                    break;
                case 8://删除群聊
                    deleteGroupChat(channel,name);
                    break;
                case 9://查看群聊列表
                    showMyGroupChats(channel,name);
                    break;
                case 10://选择群聊开始聊天或发送文件
                    chatAll(channel,name);
                    break;
                case 11://注销
                    isDelete=delete(channel,name);
                    break;
                default://输入错误
                    System.out.println("您的输入有误，请重新输入您的选择！");
                    break;
            }
            if(select==11&&isDelete){
                return;
            }
            chatMenu();
            select=in.nextInt();
        }
        leave(channel,name);
    }
    /**
     * 客户端主界面
     */
    private static void clientMenu() {
        System.out.println("==============================");
        System.out.println("    欢迎进入聊天系统登录界面     ");
        System.out.println("------------------------------");
        System.out.println("    请输入您的选择：            ");
        System.out.println("    1.用户注册                 ");
        System.out.println("    2.用户登录                 ");
        System.out.println("    3.找回密码                 ");
        System.out.println("    4.退出系统                 ");
        System.out.println("==============================");
    }
    /**
     * 聊天系统主界面
     */
    private static void chatMenu() {
        System.out.println("==============================");
        System.out.println("    欢迎进入聊天系统            ");
        System.out.println("------------------------------");
        System.out.println("    请输入您的选择：            ");
        System.out.println("    1.修改密码                 ");
        System.out.println("    2.修改邮箱                 ");
        System.out.println("    3.添加好友                 ");
        System.out.println("    4.删除好友                 ");
        System.out.println("    5.查看好友列表              ");
        System.out.println("    6.选择好友开始聊天或发送文件 ");
        System.out.println("    7.添加群聊                 ");
        System.out.println("    8.删除群聊                 ");
        System.out.println("    9.查看群聊列表              ");
        System.out.println("    10.选择群聊开始聊天或发送文件 ");
        System.out.println("    11.注销                     ");
        System.out.println("    12.退出系统                 ");
        System.out.println("==============================");
    }
    /**
     * 修改密码
     */
    private static void updatePWD(Channel channel,String name) {
        Scanner in=new Scanner(System.in);
        System.out.println("==============================");
        System.out.print("请输入新密码：");
        String pwd=in.next();
        //封装给服务端发送的数据
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_MODIFY_PWD));
        objectNode.put("name",name);
        objectNode.put("pwd",pwd);
        String msg=objectNode.toString();
        //给服务端发送消息
        channel.writeAndFlush(msg);
        //等服务端的返回消息
        int resultCode=-1;
        try {
            resultCode = (int) ClientRecvHandler.queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(resultCode==100){
            System.out.println("用户修改密码成功\n即将返回聊天系统......");
        } else if(resultCode==200){
            System.out.println("用户修改密码失败\n即将返回聊天系统......");
        } else{
            System.out.println("用户修改密码出现未知错误\n即将返回聊天系统......");
        }
    }
    /**
     * 修改邮箱
     */
    private static void updateEmail(Channel channel,String name) {
        Scanner in=new Scanner(System.in);
        System.out.println("==============================");
        System.out.print("请输入新邮箱：");
        String email=in.next();
        //封装给服务端发送的数据
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_MODIFY_EMAIL));
        objectNode.put("name",name);
        objectNode.put("email",email);
        String msg=objectNode.toString();
        //给服务端发送消息
        channel.writeAndFlush(msg);
        //等服务端的返回消息
        int resultCode=-1;
        try {
            resultCode = (int) ClientRecvHandler.queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(resultCode==100){
            System.out.println("用户修改邮箱成功\n即将返回聊天系统......");
        } else if(resultCode==200){
            System.out.println("用户修改邮箱失败\n即将返回聊天系统......");
        } else{
            System.out.println("用户修改邮箱出现未知错误\n即将返回聊天系统......");
        }

    }
    /**
     * 注销
     */
    private static boolean delete(Channel channel,String name) {
        System.out.println("==============================");
        System.out.println("正在注销，请稍等......");
        //封装给服务端发送的数据
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_DELETE));
        objectNode.put("name",name);
        String msg=objectNode.toString();
        //给服务端发送消息
        channel.writeAndFlush(msg);
        //等服务端的返回消息
        int resultCode=-1;
        try {
            resultCode = (int) ClientRecvHandler.queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(resultCode==100){
            System.out.println("用户注销成功......");
            //进入聊天系统登录界面
            System.out.println("==============================");
            return true;
        } else if(resultCode==200){
            System.out.println("用户注销失败\n即将返回聊天系统......");
        } else{
            System.out.println("用户注销出现未知错误\n即将返回聊天系统......");
        }
        return false;
    }
    /**
     * 添加好友
     */
    private static void addFriend(Channel channel,String name){
        Scanner in=new Scanner(System.in);
        System.out.println("==============================");
        System.out.println("请输入您要添加的好友的用户名：");
        String friendName=in.next();
        if(friendName.equals(name)){
            System.out.println("您不能添加自己为好友！");
            return;
        }
        //封装给服务端发送的数据
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_ADD_FRIEND));
        objectNode.put("name",name);
        objectNode.put("friendName",friendName);
        String msg=objectNode.toString();
        //给服务端发送消息
        channel.writeAndFlush(msg);
        //等服务端的返回消息
        int resultCode=-1;
        try {
            resultCode = (int) ClientRecvHandler.queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(resultCode==100){
            System.out.println("添加好友成功\n即将返回聊天系统......");
        } else if(resultCode==101){
            System.out.println("好友不存在，无法添加\n即将返回聊天系统......");
        } else if(resultCode==200){
            System.out.println("添加好友失败\n即将返回聊天系统......");
        } else{
            System.out.println("添加好友出现未知错误\n即将返回聊天系统......");
        }
    }
    /**
     * 删除好友
     */
    private static void deleteFriend(Channel channel,String name){
        Scanner in=new Scanner(System.in);
        System.out.println("==============================");
        System.out.println("请输入您要删除的好友的用户名：");
        String friendName=in.next();
        //封装给服务端发送的数据
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_DELETE_FRIEND));
        objectNode.put("name",name);
        objectNode.put("friendName",friendName);
        String msg=objectNode.toString();
        //给服务端发送消息
        channel.writeAndFlush(msg);
        //等服务端的返回消息
        int resultCode=-1;
        try {
            resultCode = (int) ClientRecvHandler.queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(resultCode==100){
            System.out.println("删除好友成功\n即将返回聊天系统......");
        } else if(resultCode==101){
            System.out.println("好友不存在，无法删除\n即将返回聊天系统......");
        } else if(resultCode==200){
            System.out.println("删除好友失败\n即将返回聊天系统......");
        } else{
            System.out.println("删除好友出现未知错误\n即将返回聊天系统......");
        }
    }
    /**
     * 查看好友列表
     */
    private static void showMyFriends(Channel channel,String name) {
        System.out.println("==============================");
        System.out.println("正在获取好友列表......");
        //封装给服务端发送的数据
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_SHOW_MY_FRIENDS));
        objectNode.put("name",name);
        String msg=objectNode.toString();
        //给服务端发送消息
        channel.writeAndFlush(msg);
        //等服务端的返回消息
        int resultCode=-1;
        try {
            resultCode = (int) ClientRecvHandler.queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(resultCode==100){
            System.out.println("用户好友列表获取成功......");
        } else if(resultCode==200){
            System.out.println("用户好友列表获取失败\n即将返回聊天系统......");
        } else{
            System.out.println("用户好友列表获取出现未知错误\n即将返回聊天系统......");
        }
    }
    /**
     * 选择好友开始聊天或发送文件
     */
    private static void chatOne(Channel channel,String name){
        Scanner in=new Scanner(System.in);
        System.out.println("==============================");
        System.out.print("请输入好友的用户名：");
        String toName=in.next();
        //封装给服务端发送的数据
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_CHAT));
        objectNode.put("name",name);
        objectNode.put("toName",toName);
        String msg=objectNode.toString();
        //给服务端发送消息
        channel.writeAndFlush(msg);
        //等服务端的返回消息
        int resultCode=-1;
        try {
            resultCode = (int) ClientRecvHandler.queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(resultCode==100){//对方是该用户的好友，聊天建立
            //开始聊天
            chatting(channel,name,toName);
        } else if(resultCode==101){
            System.out.println("对方不存在，无法发送消息\n即将返回聊天系统......");
        } else if(resultCode==200){
            System.out.println("对方还不是您的好友，无法发送消息\n即将返回聊天系统......");
        } else{
            System.out.println("出现错误\n即将返回聊天系统......");
        }
    }
    /**
     * 添加群聊
     */
    private static void addGroupChat(Channel channel,String name){
        Scanner in=new Scanner(System.in);
        addGroupChatMenu();
        int select=in.nextInt();
        ObjectNode objectNode= JsonUtils.getObjectNode();
        int resultCode=-1;
        switch(select){
            case 1://创建群聊
                System.out.println("------------------------------");
                System.out.println("请输入您要创建的群聊名称：");
                String groupChatName=in.next();
                System.out.println("------------------------------");
                //封装给服务端发送的数据
                objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_CREATE_GROUPCHAT));
                objectNode.put("name",name);
                objectNode.put("groupChatName",groupChatName);
                //给服务端发送消息
                channel.writeAndFlush(objectNode.toString());
                //等服务端的返回消息
                try {
                    resultCode = (int) ClientRecvHandler.queue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(resultCode==100){
                    System.out.println("创建群聊成功\n即将返回聊天系统......");
                } else if(resultCode==101){
                    System.out.println("群聊已存在，无法创建\n即将返回聊天系统......");
                } else if(resultCode==200){
                    System.out.println("创建群聊失败\n即将返回聊天系统......");
                } else{
                    System.out.println("创建群聊出现未知错误\n即将返回聊天系统......");
                }
                break;
            case 2://加入群聊
                System.out.println("------------------------------");
                System.out.println("请输入您要加入的群聊名称：");
                String groupChatNameJoin=in.next();
                System.out.println("------------------------------");
                //封装给服务端发送的数据
                objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_JOIN_GROUPCHAT));
                objectNode.put("name",name);
                objectNode.put("groupChatNameJoin",groupChatNameJoin);
                //给服务端发送消息
                channel.writeAndFlush(objectNode.toString());
                //等服务端的返回消息
                try {
                    resultCode = (int) ClientRecvHandler.queue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(resultCode==100){
                    System.out.println("加入群聊成功\n即将返回聊天系统......");
                } else if(resultCode==101){
                    System.out.println("群聊不存在，无法加入\n即将返回聊天系统......");
                } else if(resultCode==200){
                    System.out.println("加入群聊失败\n即将返回聊天系统......");
                } else{
                    System.out.println("加入群聊出现未知错误\n即将返回聊天系统......");
                }
                break;
            default:
                break;
        }
    }
    /**
     * 删除群聊
     */
    private static void deleteGroupChat(Channel channel,String name){
        Scanner in=new Scanner(System.in);
        deleteGroupChatMenu();
        int select=in.nextInt();
        ObjectNode objectNode= JsonUtils.getObjectNode();
        int resultCode=-1;
        switch(select){
            case 1://删除群聊
                System.out.println("------------------------------");
                System.out.println("请输入您要删除的群聊名称：");
                String groupChatName=in.next();
                System.out.println("------------------------------");
                //封装给服务端发送的数据
                objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_DELETE_GROUPCHAT_EXIST));
                objectNode.put("name",name);
                objectNode.put("groupChatName",groupChatName);
                //给服务端发送消息
                channel.writeAndFlush(objectNode.toString());
                //等服务端的返回消息
                try {
                    resultCode = (int) ClientRecvHandler.queue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(resultCode==100){
                    System.out.println("删除群聊成功\n即将返回聊天系统......");
                } else if(resultCode==101){
                    System.out.println("群聊不存在，无法删除该群聊\n即将返回聊天系统......");
                } else if(resultCode==102){
                    System.out.println("您不是群主，无权删除该群聊\n即将返回聊天系统......");
                } else if(resultCode==200){
                    System.out.println("删除群聊失败\n即将返回聊天系统......");
                } else{
                    System.out.println("删除群聊出现未知错误\n即将返回聊天系统......");
                }
                break;
            case 2://退出群聊
                System.out.println("------------------------------");
                System.out.println("请输入您要退出的群聊名称：");
                String groupChatNameJoin=in.next();
                System.out.println("------------------------------");
                //封装给服务端发送的数据
                objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_DELETE_GROUPCHAT));
                objectNode.put("name",name);
                objectNode.put("groupChatNameJoin",groupChatNameJoin);
                //给服务端发送消息
                channel.writeAndFlush(objectNode.toString());
                //等服务端的返回消息
                try {
                    resultCode = (int) ClientRecvHandler.queue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(resultCode==100){
                    System.out.println("退出群聊成功\n即将返回聊天系统......");
                } else if(resultCode==101){
                    System.out.println("群聊不存在，无法退出群聊\n即将返回聊天系统......");
                } else if(resultCode==102){
                    System.out.println("您是群主，不能退出群聊\n如果您需要退出群聊，请执行删除群聊操作\n即将返回聊天系统......");
                } else if(resultCode==200){
                    System.out.println("退出群聊失败\n即将返回聊天系统......");
                } else{
                    System.out.println("退出群聊出现未知错误\n即将返回聊天系统......");
                }
                break;
            default:
                break;
        }
    }
    /**
     * 查看群聊列表
     */
    private static void showMyGroupChats(Channel channel,String name){
        System.out.println("==============================");
        System.out.println("正在获取群聊列表......");
        //封装给服务端发送的数据
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_SHOW_GROUPCHATS));
        objectNode.put("name",name);
        String msg=objectNode.toString();
        //给服务端发送消息
        channel.writeAndFlush(msg);
        //等服务端的返回消息
        int resultCode=-1;
        try {
            resultCode = (int) ClientRecvHandler.queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(resultCode==100){
            System.out.println("用户群聊列表获取成功......");
        } else if(resultCode==200){
            System.out.println("用户群聊列表获取失败\n即将返回聊天系统......");
        } else{
            System.out.println("用户群聊列表获取出现未知错误\n即将返回聊天系统......");
        }
    }
    /**
     * 选择群聊开始聊天或发送文件
     */
    private static void chatAll(Channel channel,String name){
        Scanner in=new Scanner(System.in);
        System.out.println("==============================");
        System.out.print("请输入群聊名称：");
        String groupChat=in.next();
        //封装给服务端发送的数据
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_CHAT_ALL));
        objectNode.put("name",name);
        objectNode.put("groupChat",groupChat);
        String msg=objectNode.toString();
        //给服务端发送消息
        channel.writeAndFlush(msg);
        //等服务端的返回消息
        int resultCode=-1;
        try {
            resultCode = (int) ClientRecvHandler.queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(resultCode==100){//该用户已加入该群聊，聊天建立
            //开始聊天
            groupChatting(channel,name,groupChat);
        } else if(resultCode==101){
            System.out.println("群聊不存在，无法发送消息\n即将返回聊天系统......");
        } else if(resultCode==200){
            System.out.println("您未加入该群聊，无法发送消息\n即将返回聊天系统......");
        } else{
            System.out.println("出现错误\n即将返回聊天系统......");
        }
    }
    /**
     * 添加群聊选项菜单
     */
    private static void addGroupChatMenu(){
        System.out.println("------------------------------");
        System.out.println("    请输入您的选择：            ");
        System.out.println("    1.创建群聊                 ");
        System.out.println("    2.加入群聊                 ");
        System.out.println("    输入其它数字退出            ");
        System.out.println("------------------------------");
    }
    /**
     * 删除群聊选项菜单
     */
    private static void deleteGroupChatMenu(){
        System.out.println("------------------------------");
        System.out.println("    请输入您的选择：            ");
        System.out.println("    1.删除群聊                 ");
        System.out.println("    2.退出群聊                 ");
        System.out.println("    输入其它数字退出            ");
        System.out.println("------------------------------");
    }
    /**
     * 用户下线
     */
    private static void leave(Channel channel,String name){
        //封装给服务端发送的数据
        ObjectNode objectNode = JsonUtils.getObjectNode();
        objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_NOTIFY_OFFLINE));
        objectNode.put("name",name);
        String msg=objectNode.toString();
        //给服务端发送消息
        channel.writeAndFlush(msg);
    }

    //文本消息格式->m:XXX
    //文件消息格式->f:XXX

    /**
     * 单聊
     */
    private static void chatting(Channel channel,String name,String toName){
        Scanner in=new Scanner(System.in);
        System.out.println("===============友情提示===============");
        System.out.println("请使用标准消息格式发送文本消息或文件消息");
        System.out.println("文本消息格式->m:XXX");
        System.out.println("文件消息格式->f:XXX");
        System.out.println("如果您要退出聊天，请直接输入:EOF");
        System.out.println("===============聊天开始===============");
        String msg;
        char[] send;
        char first;
        while(true){
            msg=in.next();
            if(msg.equals("EOF")){
                break;
            }
            first=msg.charAt(0);
            switch(first){
                case 'm'://发送文本消息
                    send=new char[msg.length()-2];
                    msg.getChars(2,msg.length(),send,0);
                    msg=new String(send);//实际发送的消息
                    //封装给服务端发送的数据
                    ObjectNode objectNode = JsonUtils.getObjectNode();
                    objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_CHAT_MSG));
                    objectNode.put("name",name);
                    objectNode.put("toName",toName);
                    objectNode.put("msg",msg);
                    channel.writeAndFlush(objectNode.toString());
                    break;
                case 'f'://发送文件
                    send=new char[msg.length()-2];
                    msg.getChars(2,msg.length(),send,0);
                    msg=new String(send);//实际发送的文件路径及文件名
                    //参数校验
                    File file=new File(msg);
                    if(!(file.isFile()&&file.exists())){
                        System.out.println("文件名不合法!");
                        break;
                    }
                    //封装给服务端发送的数据
                    ObjectNode nodes=JsonUtils.getObjectNode();
                    nodes.put("msgtype", String.valueOf(EnMsgType.EN_MSG_CHAT_FILE));
                    nodes.put("type","syn");
                    //给服务器发送消息
                    channel.writeAndFlush(nodes.toString());
                    //等待服务器返回端口号
                    int port=-1;
                    try {
                        port=ClientRecvHandler.queue.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(port>0){
                        //服务器返回端口号成功，客户端创建子线程连接服务器并发送文件
                        //封装给服务端发送的数据
                        nodes.put("msgtype", String.valueOf(EnMsgType.EN_MSG_CHAT_FILE));
                        nodes.put("name",name);
                        nodes.put("toName",toName);
                        nodes.put("port",port);
                        nodes.put("type","start");
                        channel.writeAndFlush(nodes.toString());
                        new FileSend(port,file).start();
                    }else{
                        //服务器返回端口号失败，请稍后重试
                        System.out.println("服务器返回端口号失败，请稍后重试");
                    }
                    break;
                default:
                    System.out.println("您输入的格式有误，请重新输入！");
                    break;
            }
        }
        System.out.println("===============聊天结束===============");
    }

    /**
     * 群聊
     */
    private static void groupChatting(Channel channel,String name,String groupChat){
        Scanner in=new Scanner(System.in);
        System.out.println("===============友情提示===============");
        System.out.println("请使用标准消息格式发送文本消息或文件消息");
        System.out.println("文本消息格式->m:XXX");
        System.out.println("文件消息格式->f:XXX");
        System.out.println("如果您要退出群聊，请直接输入:EOF");
        System.out.println("===============群聊开始===============");
        String msg;
        char[] send;
        char first;
        while(true){
            msg=in.next();
            if(msg.equals("EOF")){
                break;
            }
            first=msg.charAt(0);
            switch(first){
                case 'm'://发送文本消息
                    send=new char[msg.length()-2];
                    msg.getChars(2,msg.length(),send,0);
                    msg=new String(send);//实际发送的消息
                    //封装给服务端发送的数据
                    ObjectNode objectNode = JsonUtils.getObjectNode();
                    objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_CHAT_ALL_MSG));
                    objectNode.put("name",name);
                    objectNode.put("groupChat",groupChat);
                    objectNode.put("msg",msg);
                    channel.writeAndFlush(objectNode.toString());
                    break;
                case 'f'://发送文件
                    send=new char[msg.length()-2];
                    msg.getChars(2,msg.length(),send,0);
                    msg=new String(send);//实际发送的文件路径及文件名
                    //参数校验
                    File file=new File(msg);
                    if(!(file.isFile()&&file.exists())){
                        System.out.println("文件名不合法!");
                        break;
                    }
                    //封装给服务端发送的数据
                    ObjectNode nodes=JsonUtils.getObjectNode();
                    nodes.put("msgtype", String.valueOf(EnMsgType.EN_MSG_CHAT_ALL_FILE));
                    nodes.put("type","syn");
                    //给服务器发送消息
                    channel.writeAndFlush(nodes.toString());
                    //等待服务器返回端口号
                    int port=-1;
                    try {
                        port=ClientRecvHandler.queue.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(port>0){
                        //服务器返回端口号成功，客户端创建子线程连接服务器并发送文件
                        //封装给服务端发送的数据
                        nodes.put("msgtype", String.valueOf(EnMsgType.EN_MSG_CHAT_ALL_FILE));
                        nodes.put("name",name);
                        nodes.put("groupChat",groupChat);
                        nodes.put("port",port);
                        nodes.put("type","start");
                        channel.writeAndFlush(nodes.toString());
                        new FileSend(port,file).start();
                    }else{
                        //服务器返回端口号失败，请稍后重试
                        System.out.println("服务器返回端口号失败，请稍后重试");
                    }
                    break;
                default:
                    System.out.println("您输入的格式有误，请重新输入！");
                    break;
            }
        }
        System.out.println("===============群聊结束===============");
    }
    /**
     * 获取离线消息
     */
    private static void information(Channel channel,String name){
        ObjectNode objectNode=JsonUtils.getObjectNode();
        objectNode.put("name",name);
        objectNode.put("msgtype", String.valueOf(EnMsgType.EN_MSG_OFFLINE));
        channel.writeAndFlush(objectNode.toString());
    }
}
