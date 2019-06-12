package com.project.exercise.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 基于netty的服务器，在客户端连接后，可为每个连接的客户提供服务器的各种服务。
 */
public class Service {
    /**
     * 启动服务器
     */
    public static void start() {
        //通过读取配置文件获取端口信息
        InputStream resource = Service.class.getClassLoader().getResourceAsStream("Service.properties");
        Properties properties = new Properties();
        NioEventLoopGroup boss=null;
        NioEventLoopGroup worker=null;
        try {
            properties.load(resource);
            Integer port= Integer.valueOf(properties.getProperty("port"));
            //实例化主事件循环组，负责接收客户端连接
            boss=new NioEventLoopGroup();
            //实例化工作线程组，负责处理具体业务逻辑
            worker=new NioEventLoopGroup();
            //服务器启动辅助类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap
                    .group(boss,worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            ChannelPipeline pipeline = nioSocketChannel.pipeline();
                            pipeline.addLast(new ServiceHandler());
                            pipeline.addLast(new StringDecoder());//解码
                            pipeline.addLast(new StringEncoder());//编码
                        }
                    });
            //同步启动服务端
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            System.out.println("服务器:"+port+"启动了");
            //关闭服务端
            channelFuture.channel().closeFuture().sync();
            System.out.println("服务器:"+port+"关闭");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //关闭事件循环组
            if(boss!=null){
                boss.shutdownGracefully();
            }
            if(worker!=null){
                worker.shutdownGracefully();
            }
        }
    }
}