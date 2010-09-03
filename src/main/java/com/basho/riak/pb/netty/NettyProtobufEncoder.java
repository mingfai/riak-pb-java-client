package com.basho.riak.pb.netty;

import com.basho.riak.pb.protobuf.RiakProtocolHelper;
import com.google.protobuf.MessageLite;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import static org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer;

public class NettyProtobufEncoder extends OneToOneEncoder{
    static Log log = LogFactory.getLog(NettyProtobufEncoder.class);

    @Override
    protected Object encode(ChannelHandlerContext channelHandlerContext, Channel channel, Object msg) throws Exception{
        if (!(msg instanceof MessageLite)){
            return msg;
        }
        byte[] msgCode = new byte[]{new Integer(RiakProtocolHelper.getMessageCode((MessageLite) msg)).byteValue()};
        byte[] body = ((MessageLite) msg).toByteArray();
        byte[] bytes = ArrayUtils.addAll(msgCode, body);
        if (log.isTraceEnabled()) log.trace("encode() - req - bytes.length (excl. length header): " + bytes.length + ", bytes: " + new String(Hex.encodeHex(bytes)));
        return wrappedBuffer(bytes);
    }
}
