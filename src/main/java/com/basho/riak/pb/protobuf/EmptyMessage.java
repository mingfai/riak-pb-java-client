package com.basho.riak.pb.protobuf;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.MessageLite;

import java.io.IOException;
import java.io.OutputStream;

public class EmptyMessage implements MessageLite{
    protected Integer messageCode;

    public EmptyMessage(){ }

    public EmptyMessage(Integer messageCode){this.messageCode = messageCode;}

    public String toString(){
        return "EmptyMessage{ messageCode: " + messageCode + " }";
    }

    @Override public MessageLite getDefaultInstanceForType(){
        return new EmptyMessage();
    }

    @Override public boolean isInitialized(){
        return true;
    }

    @Override public void writeTo(CodedOutputStream codedOutputStream) throws IOException{
    }

    @Override public int getSerializedSize(){
        return 0;
    }

    @Override public ByteString toByteString(){
        return ByteString.copyFromUtf8("");
    }

    @Override public byte[] toByteArray(){
        return new byte[]{};
    }

    @Override public void writeTo(OutputStream outputStream) throws IOException{
    }

    @Override public void writeDelimitedTo(OutputStream outputStream) throws IOException{
    }

    @Override public Builder newBuilderForType(){
        throw new UnsupportedOperationException("unimplemented");
    }

    @Override public Builder toBuilder(){
        throw new UnsupportedOperationException("unimplemented");
    }
}
