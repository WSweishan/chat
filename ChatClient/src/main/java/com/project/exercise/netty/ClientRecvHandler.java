package com.project.exercise.netty;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.project.exercise.constant.EnMsgType;
import com.project.exercise.service.FileRecv;
import com.project.exercise.util.JsonUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.concurrent.SynchronousQueue;

/**
 * 处理客户端从服务器接收数据的情况
 */
@ChannelHandler.Sharable
public class ClientRecvHandler extends SimpleChannelInboundHandler {
    //服务端返回数据在工作线程中，发送数据在主线程中，利用该队列在父子线程之间通信，
    public static volatile SynchronousQueue<Integer> queue = new SynchronousQueue ();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buff=(ByteBuf)msg;
        byte[] bytes = new byte[buff.readableBytes()];
        buff.readBytes(bytes);
        String s=new String(bytes);
        ObjectNode objectNode = JsonUtils.getObjectNode(s);
        String msgtype = objectNode.get("msgtype").asText();
        if (String.valueOf(EnMsgType.EN_MSG_ACK).equals(msgtype)) {
            //返回响应消息
            //获取响应消息的类型
            String srctype = objectNode.get("srctype").asText();
            if (String.valueOf(EnMsgType.EN_MSG_REGISTER).equals(srctype)) {
                //返回注册消息的响应消息
                int code = objectNode.get("code").asInt();
                System.out.println("注册消息的响应码："+code);
                //将返回状态码发给父线程处理
                queue.put(code);
            }
            if (String.valueOf(EnMsgType.EN_MSG_LOGIN).equals(srctype)) {
                //返回登录消息的响应消息
                int code = objectNode.get("code").asInt();
                System.out.println("登录消息的响应码："+code);
                //将返回状态码发给父线程处理
                queue.put(code);
            }
            if (String.valueOf(EnMsgType.EN_MSG_FORGET_PWD).equals(srctype)) {
                //返回找回密码消息的响应消息
                int code = objectNode.get("code").asInt();
                System.out.println("找回密码消息的响应码："+code);
                //将返回状态码发给父线程处理
                queue.put(code);
            }
            if (String.valueOf(EnMsgType.EN_MSG_MODIFY_PWD).equals(srctype)) {
                //返回修改密码消息的响应消息
                int code = objectNode.get("code").asInt();
                System.out.println("修改密码消息的响应码："+code);
                //将返回状态码发给父线程处理
                queue.put(code);
            }
            if (String.valueOf(EnMsgType.EN_MSG_MODIFY_EMAIL).equals(srctype)) {
                //返回修改邮箱消息的响应消息
                int code = objectNode.get("code").asInt();
                System.out.println("修改邮箱消息的响应码："+code);
                //将返回状态码发给父线程处理
                queue.put(code);
            }
            if (String.valueOf(EnMsgType.EN_MSG_DELETE).equals(srctype)) {
                //返回注销消息的响应消息
                int code = objectNode.get("code").asInt();
                System.out.println("注销消息的响应码："+code);
                //将返回状态码发给父线程处理
                queue.put(code);
            }
            if (String.valueOf(EnMsgType.EN_MSG_SHOW_MY_FRIENDS).equals(srctype)) {
                //返回查看好友列表消息的响应消息
                int code = objectNode.get("code").asInt();
                System.out.println("查看好友列表消息的响应码："+code);
                if(code==100){
                    System.out.println("==============================");
                    System.out.println("我的好友列表：");
                    int count=objectNode.get("count").asInt();
                    String laber;
                    for (int i = 1; i <= count; i++) {
                        laber="friend"+i;
                        System.out.println(objectNode.get(laber).asText());
                    }
                    System.out.println("共"+count+"位好友");
                    System.out.println("==============================");
                }
                //将返回状态码发给父线程处理
                queue.put(code);
            }
            if (String.valueOf(EnMsgType.EN_MSG_ADD_FRIEND).equals(srctype)) {
                //返回添加好友消息的响应消息
                int code = objectNode.get("code").asInt();
                System.out.println("添加好友消息的响应码："+code);
                //将返回状态码发给父线程处理
                queue.put(code);
            }
            if (String.valueOf(EnMsgType.EN_MSG_DELETE_FRIEND).equals(srctype)) {
                //返回删除好友消息的响应消息
                int code = objectNode.get("code").asInt();
                System.out.println("删除好友消息的响应码："+code);
                //将返回状态码发给父线程处理
                queue.put(code);
            }
            if (String.valueOf(EnMsgType.EN_MSG_SHOW_GROUPCHATS).equals(srctype)) {
                //返回查看群聊列表消息的响应消息
                int code = objectNode.get("code").asInt();
                System.out.println("查看群聊列表消息的响应码："+code);
                if(code==100){
                    System.out.println("==============================");
                    System.out.println("我的群聊列表：");
                    int count=objectNode.get("count").asInt();
                    String laber;
                    for (int i = 1; i <= count; i++) {
                        laber="groupChat"+i;
                        System.out.println(objectNode.get(laber).asText());
                    }
                    System.out.println("共"+count+"个群聊");
                    System.out.println("==============================");
                }
                //将返回状态码发给父线程处理
                queue.put(code);
            }
            if (String.valueOf(EnMsgType.EN_MSG_CREATE_GROUPCHAT).equals(srctype)) {
                //返回创建群聊消息的响应消息
                int code = objectNode.get("code").asInt();
                System.out.println("创建群聊消息的响应码："+code);
                //将返回状态码发给父线程处理
                queue.put(code);
            }
            if (String.valueOf(EnMsgType.EN_MSG_JOIN_GROUPCHAT).equals(srctype)) {
                //返回加入群聊消息的响应消息
                int code = objectNode.get("code").asInt();
                System.out.println("加入群聊消息的响应码："+code);
                //将返回状态码发给父线程处理
                queue.put(code);
            }
            if (String.valueOf(EnMsgType.EN_MSG_DELETE_GROUPCHAT_EXIST).equals(srctype)) {
                //返回删除群聊消息的响应消息
                int code = objectNode.get("code").asInt();
                System.out.println("删除群聊消息的响应码："+code);
                //将返回状态码发给父线程处理
                queue.put(code);
            }
            if (String.valueOf(EnMsgType.EN_MSG_DELETE_GROUPCHAT).equals(srctype)) {
                //返回退出群聊消息的响应消息
                int code = objectNode.get("code").asInt();
                System.out.println("退出群聊消息的响应码："+code);
                //将返回状态码发给父线程处理
                queue.put(code);
            }
            if (String.valueOf(EnMsgType.EN_MSG_CHAT).equals(srctype)){
                //返回一对一聊天建立消息的响应消息
                int code = objectNode.get("code").asInt();
                System.out.println("一对一聊天建立消息的响应码："+code);
                //将返回状态码发给父线程处理
                queue.put(code);
            }
            if (String.valueOf(EnMsgType.EN_MSG_CHAT_ALL).equals(srctype)){
                //返回群聊建立消息的响应消息
                int code = objectNode.get("code").asInt();
                System.out.println("群聊建立消息的响应码："+code);
                //将返回状态码发给父线程处理
                queue.put(code);
            }
            if (String.valueOf(EnMsgType.EN_MSG_CHAT_FILE).equals(srctype)){
                //返回单聊文件消息的响应消息
                int code = objectNode.get("code").asInt();
                System.out.println("单聊文件消息的响应码："+code);
                if(code==200){
                    //服务器返回端口号成功
                    int port=objectNode.get("port").asInt();
                    queue.put(port);
                }else{
                    queue.put(0);
                }
            }
            if (String.valueOf(EnMsgType.EN_MSG_CHAT_ALL_FILE).equals(srctype)){
                //返回群聊文件消息的响应消息
                int code = objectNode.get("code").asInt();
                System.out.println("群聊文件消息的响应码："+code);
                if(code==200){
                    //服务器返回端口号成功
                    int port=objectNode.get("port").asInt();
                    queue.put(port);
                }else{
                    queue.put(0);
                }
            }
        }
        if (String.valueOf(EnMsgType.EN_MSG_NOTIFY_OFFLINE).equals(msgtype)) {
            //返回用户下线消息
            String name = objectNode.get("name").asText();
            System.out.println("提醒：好友"+name+"下线了......");
        }
        if (String.valueOf(EnMsgType.EN_MSG_NOTIFY_ONLINE).equals(msgtype)) {
            //返回用户上线消息
            String name = objectNode.get("name").asText();
            System.out.println("提醒：好友"+name+"上线了......");
        }
        if (String.valueOf(EnMsgType.EN_MSG_CHAT_MSG).equals(msgtype)){
            //返回一对一聊天文本消息
            String name=objectNode.get("name").asText();
            String information=objectNode.get("msg").asText();
            System.out.println("好友"+name+"发送消息:"+information);
        }
        if (String.valueOf(EnMsgType.EN_MSG_CHAT_FILE).equals(msgtype)){
            //返回一对一聊天文件消息
            String type=objectNode.get("type").asText();
            if(type.equals("ok")){
                //创建子线程开始接收文件
                int port=objectNode.get("port").asInt();
                String fromName= String.valueOf(objectNode.get("fromName"));
                new FileRecv(port,fromName,null).start();
            }
        }
        if (String.valueOf(EnMsgType.EN_MSG_CHAT_ALL_MSG).equals(msgtype)){
            //返回群聊文本消息
            String name=objectNode.get("name").asText();
            String groupChat=objectNode.get("groupChat").asText();
            String information=objectNode.get("msg").asText();
            System.out.println(name+"在群聊"+groupChat+"中发送消息:"+information);
        }
        if (String.valueOf(EnMsgType.EN_MSG_CHAT_ALL_FILE).equals(msgtype)){
            //返回群聊文件消息
            String type=objectNode.get("type").asText();
            if(type.equals("ok")){
                //创建子线程开始接收文件
                int port=objectNode.get("port").asInt();
                String fromName= String.valueOf(objectNode.get("fromName"));
                String fromGroupChat= String.valueOf(objectNode.get("fromGroupChat"));
                new FileRecv(port,fromName,fromGroupChat).start();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("服务器"+ctx.channel().remoteAddress()+"异常关闭......");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务器"+ctx.channel().remoteAddress()+"已连接......");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务器"+ctx.channel().remoteAddress()+"已断开连接......");
    }
}