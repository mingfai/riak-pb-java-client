package com.basho.riak;

import java.util.Collection;
import java.util.List;

public interface RiakClient extends RiakCoreAPI{
    public List<Bucket> getBuckets();

    /**
     * rw - "default"
     */
    public boolean delete(String bucket, String key);
}
