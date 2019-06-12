package com.project.exercise.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 基于netty的客户端，可以连接服务器并访问其提供的各种服务。
*/
public class Client {
    /**
     * 启动客户端
     */
    public static void start() {
        //通过读取配置文件获取服务器IP和端口信息
        InputStream resource = Client.class.getClassLoader().getResourceAsStream("Client.properties");
        Properties properties = new Properties();
        //事件循环组
        NioEventLoopGroup loopGroup=null;
        try {
            properties.load(resource);
            String host=properties.getProperty("host");
            Integer port= Integer.valueOf(properties.getProperty("port"));
            loopGroup=new NioEventLoopGroup();
            //启动辅助类
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(loopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            ChannelPipeline pipeline = nioSocketChannel.pipeline();
                            pipeline.addLast(new ClientRecvHandler());
                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(new StringEncoder());
                        }
                    });
            //和服务端同步建立连接
            Channel channel = bootstrap.connect(host, port).sync().channel();
            System.out.println("客户端连上服务器了......");
            //和服务器进行通讯
            ClientSendHandler clientSendHandler = new ClientSendHandler();
            clientSendHandler.dispose(channel);
            //关闭通道
            channel.closeFuture().sync();
            System.out.println("客户端与服务器断开连接......");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if(loopGroup!=null){
                loopGroup.shutdownGracefully();
            }
        }
    }
}