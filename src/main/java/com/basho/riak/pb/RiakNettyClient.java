package com.basho.riak.pb;

import com.basho.riak.Bucket;
import com.basho.riak.pb.netty.AbstractNettyHandler;
import com.basho.riak.pb.netty.NettyProtobufDecoder;
import com.basho.riak.pb.netty.NettyProtobufEncoder;
import com.basho.riak.pb.protobuf.EmptyMessage;
import com.basho.riak.pb.protobuf.RiakMessageCode;
import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.handler.logging.LoggingHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.jboss.netty.channel.Channels.pipeline;

/**
 * Read operations are synchronous;
 */
public class RiakNettyClient extends AbstractNettyHandler implements com.basho.riak.RiakClient{
    static Log log = LogFactory.getLog(RiakNettyClient.class);
    protected static Properties properties = new Properties(){{
        try{ this.load(RiakNettyClient.class.getResourceAsStream("/riak.properties")); } catch (IOException e){}
    }};
    protected ChannelFactory factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
    protected String host;
    protected int port;

    public RiakNettyClient(){
        this(properties.getProperty("riak.server.host"), Integer.parseInt(properties.getProperty("riak.server.port")));
    }

    public RiakNettyClient(String host, int port){
        this.host = host;
        this.port = port;
        if (log.isDebugEnabled()) log.debug("RiakClient() - host: " + host + ", port: " + port);
    }

    private static final int MAX_FRAME_BYTES_LENGTH = 1048576 * 50;

    private ClientBootstrap createBootstrap(){
        ClientBootstrap bootstrap = new ClientBootstrap(factory);
        bootstrap.setOption("remoteAddress", new InetSocketAddress(host, port));
        ChannelPipeline p = pipeline();
        p.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(MAX_FRAME_BYTES_LENGTH, 0, 4, 0, 4));
        p.addLast("protobufDecoder", new NettyProtobufDecoder());
        p.addLast("frameEncoder", new LengthFieldPrepender(4));
        p.addLast("protobufEncoder", new NettyProtobufEncoder());
        p.addLast("handler", this);
        //p.addLast("logging", new LoggingHandler());
        bootstrap.setPipeline(p);
        return bootstrap;
    }

    private MessageLite pollMessageFromQueue(){
        try{
            return answer.poll(1, TimeUnit.SECONDS);
        } catch (InterruptedException e){
            return null;
        }
    }

    private MessageLite takeMessage(){
        //if (log.isTraceEnabled()) log.trace("takeMessage()");
        while (true){
            try{
                MessageLite msg = pollMessageFromQueue();
                if (msg != null){
                    //if (log.isTraceEnabled()) log.trace("takeMessage() - return - msg: " + msg);
                    return msg;
                } else{
                    if (log.isTraceEnabled()) log.trace("takeMessage() - to sleep for 0.1s");
                    Thread.sleep(100);
                }
            } catch (InterruptedException e){}
        }
    }

    @Override public List<String> listKeys(String bucket){
        if (log.isTraceEnabled()) log.trace("listKeys() - bucket: " + bucket);
        Channel channel = createBootstrap().connect().awaitUninterruptibly().getChannel();
        ByteString content = ByteString.copyFromUtf8(bucket);
        RiakProtocol.RpbListKeysReq req = RiakProtocol.RpbListKeysReq.newBuilder().setBucket(content).build();

        List<ByteString> keysList = new ArrayList<ByteString>();

        RiakProtocol.RpbListKeysResp resp = RiakProtocol.RpbListKeysResp.getDefaultInstance();
        while (!resp.hasDone()){
            channel.write(req);
            resp = (RiakProtocol.RpbListKeysResp) pollMessageFromQueue();
            if (!resp.hasDone()) keysList.addAll(resp.getKeysList());
        }

        List<String> results = convert(keysList);
        channel.close();
        if (log.isTraceEnabled())
            log.trace("listKeys() - results.size(): " + results.size() + ", results: " + results);
        return results;
    }

    @Override public List<String> listBuckets(){
        ChannelFuture connectFuture = createBootstrap().connect();
        Channel channel = connectFuture.awaitUninterruptibly().getChannel();
        channel.write(new EmptyMessage(RiakMessageCode.LIST_BUCKETS_REQ));
        RiakProtocol.RpbListBucketsResp resp = (RiakProtocol.RpbListBucketsResp) takeMessage();
        List<String> results = convert(resp.getBucketsList());
        if (log.isTraceEnabled()) log.trace("listBuckets() - results.size(): " + results + ", results: " + results);
        return results;
    }

    private List<String> convert(Collection<ByteString> byteStrings){
        List<String> results = new ArrayList<String>();
        for (ByteString key : byteStrings) results.add(key.toStringUtf8());
        return results;
    }

    @Override public boolean ping(){
        Channel channel = createBootstrap().connect().awaitUninterruptibly().getChannel();
        channel.write(new EmptyMessage(RiakMessageCode.PING_REQ));
        MessageLite resp = takeMessage();
        boolean result = resp instanceof EmptyMessage;
        if (log.isTraceEnabled()) log.trace("ping() - " + result + ", resp: " + resp);
        return result;
    }

    @Override public boolean delete(String bucket, String key, int rw){
        Channel channel = createBootstrap().connect().awaitUninterruptibly().getChannel();
        channel.write(RiakProtocol.RpbDelReq.newBuilder().setBucket(ByteString.copyFromUtf8(bucket)).setKey(ByteString.copyFromUtf8(key)).setRw(rw));


        return false;
    }

    @Override public boolean delete(String bucket, String key){
        return delete(bucket, key, 0);
    }

    @Override public List<Bucket> getBuckets(){
        List<Bucket> results = new ArrayList<Bucket>();
        for (String name : listBuckets()) results.add(new BucketImpl(name, this));
        return results;
    }

    public static Properties getProperties(){
        return properties;
    }

    public static void setProperties(Properties properties){
        RiakNettyClient.properties = properties;
    }

    public ChannelFactory getFactory(){
        return factory;
    }

    public void setFactory(ChannelFactory factory){
        this.factory = factory;
    }

    public String getHost(){
        return host;
    }

    public void setHost(String host){
        this.host = host;
    }

    public int getPort(){
        return port;
    }

    public void setPort(int port){
        this.port = port;
    }
}
