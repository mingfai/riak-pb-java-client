package com.basho.riak.pb.netty;

import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.channel.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AbstractNettyHandler extends SimpleChannelUpstreamHandler{
    static Log log = LogFactory.getLog(AbstractNettyHandler.class);
    protected final BlockingQueue<MessageLite> answer = new LinkedBlockingQueue<MessageLite>();

    /*@Override public void handleUpstream(
            ChannelHandlerContext ctx, ChannelEvent e) throws Exception{
        if (e instanceof ChannelStateEvent && log.isTraceEnabled()) log.trace("handleUpstream() - " + e.toString());
        super.handleUpstream(ctx, e);
    }

    @Override public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception{
        if (log.isTraceEnabled()) log.trace("channelOpen() - channel: " + e.getChannel());
        super.channelOpen(ctx, e);
    }


    @Override public void writeComplete(ChannelHandlerContext ctx, WriteCompletionEvent e) throws Exception{
        if (log.isTraceEnabled()) log.trace("writeComplete() - writtenAmount: " + e.getWrittenAmount());
        super.writeComplete(ctx, e);
    }*/

    @Override public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception{
        //if (log.isTraceEnabled()) log.trace("messageReceived() - e.message: " + e.getMessage());
        boolean offered = answer.offer((MessageLite) e.getMessage());
        assert offered;
    }

    @Override public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception{
        log.error(e.getCause().getMessage(), e.getCause());
        e.getChannel().close();
    }
}
