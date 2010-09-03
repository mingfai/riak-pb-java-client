package com.basho.riak.impl;

import com.basho.riak.Bucket;

public abstract class AbstractBucket implements Bucket{
    protected String name;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }
}
