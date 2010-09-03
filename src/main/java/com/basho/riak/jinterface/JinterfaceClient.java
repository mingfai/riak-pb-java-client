package com.basho.riak.jinterface;

import com.basho.riak.Bucket;

import java.util.List;

public class JinterfaceClient implements com.basho.riak.RiakClient{


    

    @Override public List<Bucket> getBuckets(){

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public boolean delete(String bucket, String key){
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public boolean ping(){
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public List<String> listBuckets(){
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public List<String> listKeys(String bucket){
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public boolean delete(String bucket, String key, int rw){
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
