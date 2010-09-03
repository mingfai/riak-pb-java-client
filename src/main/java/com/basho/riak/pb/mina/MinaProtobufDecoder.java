package com.basho.riak.pb.mina;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Message;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.jboss.netty.handler.codec.protobuf.ProtobufDecoder;

public class MinaProtobufDecoder extends CumulativeProtocolDecoder{
    private final Message prototype;
    private final ExtensionRegistry extentions;

    public MinaProtobufDecoder(Message prototype, ExtensionRegistry extentions){
        this.prototype = prototype;
        this.extentions = extentions;
    }

    @Override
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception{
        SizeContext ctx = SizeContext.get(session, in);
        if (ctx.hasEnoughData(in)){
            try{
                Message.Builder builder = prototype.newBuilderForType();
                ctx.getInputStream(in).readMessage(builder, extentions);
                out.write(builder.build());
                return true;
            } finally{
                ctx.shiftPositionAndReset(session, in);
            }
        }
        return false;

    }
}
