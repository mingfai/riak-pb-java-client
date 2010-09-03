package com.basho.riak.pb.mina;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class AbstractMinaHandler extends IoHandlerAdapter{
    static Log log = LogFactory.getLog(AbstractMinaHandler.class);

    @Override public void sessionCreated(IoSession session) throws Exception{
        if (log.isTraceEnabled()) log.trace("sessionCreated()");
        super.sessionCreated(session);
    }

    @Override public void sessionOpened(IoSession session) throws Exception{
        if (log.isTraceEnabled()) log.trace("sessionOpened()");
        super.sessionOpened(session);

    }

    @Override public void messageSent(IoSession session, Object message) throws Exception{
        if (log.isTraceEnabled()) log.trace("messageSent()");
        super.messageSent(session, message);
    }

    @Override public void messageReceived(IoSession session, Object message) throws Exception{
        if (log.isTraceEnabled()) log.trace("messageReceived()");
        super.messageReceived(session, message);

    }

    @Override public void exceptionCaught(IoSession session, Throwable cause) throws Exception{
        log.error("exceptionCaught()", cause);
        super.exceptionCaught(session, cause);
    }

    @Override public void sessionIdle(IoSession session, IdleStatus status) throws Exception{
        if (log.isTraceEnabled()) log.trace("sessionIdle()");
        super.sessionIdle(session, status);
    }

    @Override public void sessionClosed(IoSession session) throws Exception{
        super.sessionClosed(session);
        if (log.isTraceEnabled()) log.trace("sessionClosed()");
    }
}
