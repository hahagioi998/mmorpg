package org.forfun.mmorpg.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.forfun.mmorpg.rpc.codec.MessageDecoder;
import org.forfun.mmorpg.rpc.codec.MessageEncoder;

import java.net.InetSocketAddress;

public class RpcClient {

    private EventLoopGroup group = new NioEventLoopGroup(4);

    private RpcClientOptions clientOptions;

    public RpcClient(RpcClientOptions clientOptions) {
        this.clientOptions = clientOptions;
    }

    public Channel createSession() throws Exception {
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel arg0) throws Exception {
                    ChannelPipeline pipeline = arg0.pipeline();
                    pipeline.addLast(new MessageEncoder());
                    pipeline.addLast(new MessageDecoder());
                    pipeline.addLast(new RpcClientIoHandler());
                }
            });

            ChannelFuture f = b.connect(new InetSocketAddress(clientOptions.getIpAddr(), clientOptions.getPort())).sync();
            return f.channel();
        } catch (Exception e) {
            e.printStackTrace();
            group.shutdownGracefully();
            throw e;
        }
    }

    public RpcClientOptions getClientOptions() {
        return clientOptions;
    }

    public static void main(String[] args) throws Exception {


    }
}
