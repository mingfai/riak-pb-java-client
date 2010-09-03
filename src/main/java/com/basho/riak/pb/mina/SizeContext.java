package com.basho.riak.pb.mina;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import java.io.IOException;

public class SizeContext{

    /**
     * The key of attribute in {@link IoSession}.
     */
    static String KEY = String.format("%s#KEY", SizeContext.class.getCanonicalName());

    /**
     * Expected size of the data needed to decode message.
     */
    private final int size;

    /**
     * Retrieves instance of the context form given {@link IoSession}. If there is no
     * attribute holding the instance of context the new one is created. In that
     * case the size of the message is being read from {@link IoBuffer} provided.
     * The size is encoded as protobuf varint (int32).
     *
     * @param session the session that holds instance of context as attribute
     * @param in
     * @return
     * @throws IOException
     */
    static SizeContext get(IoSession session, IoBuffer in) throws IOException{
        SizeContext ctx = (SizeContext) session.getAttribute(KEY);
        if (ctx == null){
            int size = CodedInputStream.newInstance(in.array(), in.position(), 5).readRawVarint32();
            ctx = new SizeContext(SizeContext.computeTotal(size));
            session.setAttribute(KEY, ctx);
        }
        return ctx;
    }

    /**
     * Computes total size of the message that includes the size
     * itself encoded as protobuf varint (int32). In contrast to
     * the fixed int32 encoding varint may span from 1 up to 5 bytes.
     * <p/>
     * The method is being used by the {@link ProtobufEncoder} to
     * allocate {@link IoBuffer}.
     *
     * @param size Size of the message.
     * @return The total size (message size and size itself).
     */
    static int computeTotal(int size){
        return size + CodedOutputStream.computeRawVarint32Size(size);
    }

    /**
     * Creates instance of context.
     *
     * @param size The size of the message.
     */
    private SizeContext(int size){
        this.size = size;
    }

    /**
     * Determines weather the buffer contains enough data
     * to decode message. Uses method IoBuffer.remaining.
     *
     * @param in The buffer
     * @return <code>true</code> if buffer contains enough data,
     *         otherwise <code>false</code>.
     */
    boolean hasEnoughData(IoBuffer in){
        return in.remaining() >= size;
    }

    /**
     * Creates the protobuf {@link CodedInputStream} from {@link IoBuffer}.
     * To avoid consumption of the whole buffer uses internal array and size
     * to extract only the slice of the buffer as source for the stream.
     *
     * @param in The buffer
     * @return The protobuf stream used to read messages.
     */
    CodedInputStream getInputStream(IoBuffer in){
        return CodedInputStream.newInstance(
                in.array(),
                in.position(),
                size);
    }

    /**
     * Shifts position of the {@link IoBuffer} and removes
     * the context from {@link IoSession}. This method is
     * being called once the message has been successfully
     * decoded and the framework can proceed with the next
     * message in sequence.
     *
     * @param session The session
     * @param in      The buffer
     */
    void shiftPositionAndReset(IoSession session, IoBuffer in) {
		in.position(in.position() + size);
		session.removeAttribute(KEY);
	}

}
