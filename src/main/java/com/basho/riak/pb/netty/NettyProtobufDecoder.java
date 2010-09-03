package com.basho.riak.pb.netty;

import com.basho.riak.pb.protobuf.EmptyMessage;
import com.basho.riak.pb.protobuf.RiakProtocolHelper;
import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

import java.io.InputStream;

/**
 * This decoded read the message code byte to determine what message type it is
 */
public class NettyProtobufDecoder extends OneToOneDecoder{
    static Log log = LogFactory.getLog(NettyProtobufDecoder.class);

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception{
        if (!(msg instanceof ChannelBuffer)) return msg;

        InputStream stream = new ChannelBufferInputStream((ChannelBuffer) msg);
        int messageCode = RiakProtocolHelper.readMessageCode(stream);
        MessageLite prototype = RiakProtocolHelper.getPrototype(messageCode);
        if (prototype instanceof EmptyMessage) return prototype;
        assert prototype != null;
        return prototype.newBuilderForType().mergeFrom(stream).build();
    }

}
