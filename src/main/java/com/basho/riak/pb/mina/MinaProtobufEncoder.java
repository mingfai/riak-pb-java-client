package com.basho.riak.pb.mina;

import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Message;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class MinaProtobufEncoder extends ProtocolEncoderAdapter{

    /**
     * Encodes the protobuf {@link Message} provided into the wire format.
     *
     * @param session The session (not used).
     * @param message The protobuf {@link Message}.
     * @param out     The encoder output used to write buffer into.
     */
    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception{
        Message msg = (Message) message;
        int size = msg.getSerializedSize();
        IoBuffer buffer = IoBuffer.allocate(SizeContext.computeTotal(size));
        CodedOutputStream cos = CodedOutputStream.newInstance(buffer.asOutputStream());
        cos.writeRawVarint32(size);
        msg.writeTo(cos);
        cos.flush();
        buffer.flip();
        out.write(buffer);
	}
    
}
