package com.project.exercise.netty;

import com.project.exercise.controller.DispatcherController;
import com.project.exercise.service.UserService;
import com.project.exercise.service.impl.UserServiceImpl;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 服务器与客户端的交互处理
 */
@ChannelHandler.Sharable
public class ServiceHandler extends SimpleChannelInboundHandler {
    //创建分发实例
    private DispatcherController controller = DispatcherController.getInstance();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {

    }
    /**
     * 用户端发送的消息接收方法
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buff=(ByteBuf)msg;
        byte[] bytes = new byte[buff.readableBytes()];
        buff.readBytes(bytes);
        String s=new String(bytes);
        //显示用户发来的信息（json格式）
        System.out.println(ctx.channel().remoteAddress()+":"+s);
        //不做具体业务逻辑处理，主要交给分发实例处理
        String resultMsg = controller.process(ctx, s);
        if (!"".equals(resultMsg)) {
            ctx.channel().writeAndFlush(resultMsg);
        }
    }
    /**
     * 有客户端连接时触发的方法
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端"+ctx.channel().remoteAddress()+"连上服务器了......");
    }
    /**
     * 客户端断开连接时触发的方法
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端"+ctx.channel().remoteAddress()+"与服务器断开连接......");
    }

    /**
     * 抓住异常，当发生异常的时候，做相应的处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("捕获异常！！！！！！！！！！！");
        String name=DispatcherController.getNameNowBySocketAddress(ctx.channel().remoteAddress());
        UserService userService = new UserServiceImpl();
        userService.leave(name);
    }
}

