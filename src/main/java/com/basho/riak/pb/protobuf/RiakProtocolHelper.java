package com.basho.riak.pb.protobuf;

import com.basho.riak.pb.RiakProtocol;
import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;

public class RiakProtocolHelper{
    static Log log = LogFactory.getLog(RiakProtocolHelper.class);

    public static int readMessageCode(InputStream in) throws IOException{
        byte[] bytes = new byte[1];
        in.read(bytes);
        return (int) bytes[0];
    }


    public static MessageLite getPrototype(int msgCode){
        MessageLite result = null;
        switch (msgCode){
            case RiakMessageCode.LIST_BUCKETS_RESP:
                result = RiakProtocol.RpbListBucketsResp.getDefaultInstance();
                break;
            case RiakMessageCode.LIST_KEYS_RESP:
                result = RiakProtocol.RpbListKeysResp.getDefaultInstance();
                break;
            case RiakMessageCode.PING_RESP:
                result = new EmptyMessage(RiakMessageCode.PING_RESP);
                break;
            default:
                log.warn("getPrototype() - unknown msgCode - return null");
        }
        //if (log.isTraceEnabled()) log.trace("getPrototype() - messageCode: " + msgCode + ", result: " + result);
        return result;
    }

    public static int getMessageCode(MessageLite msg){
        int result = -1;
        if (msg instanceof RiakProtocol.RpbListKeysReq){
            result = RiakMessageCode.LIST_KEYS_REQ;
        } else if (msg instanceof EmptyMessage){
            result = ((EmptyMessage) msg).messageCode;
        } else{
            log.warn("getMessageCode() - unknown message - msg.class: " + msg.getClass() + ", msg: " + msg);
        }
        return result;
    }
}
