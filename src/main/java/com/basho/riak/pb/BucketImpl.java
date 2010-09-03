package com.basho.riak.pb;

import com.basho.riak.RiakClient;
import com.basho.riak.impl.AbstractBucket;

import java.util.List;

public class BucketImpl extends AbstractBucket{
    protected RiakClient client;

    public BucketImpl(){
    }

    public BucketImpl(String name){ this.name = name;}

    public BucketImpl(String name, RiakClient client){
        this.name = name;
        this.client = client;
    }


    public String toString(){
        return "Bucket{ name: " + name + " }";
    }

    @Override public List<String> getKeys(){
        assert name != null;
        if (client == null) client = new RiakNettyClient();
        return client.listKeys(name);
    }

    public RiakClient getClient(){
        return client;
    }

    public void setClient(RiakClient client){
        this.client = client;
    }
}
