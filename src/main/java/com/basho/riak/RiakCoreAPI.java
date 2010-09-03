package com.basho.riak;

import java.util.Collection;
import java.util.List;

/**
 * This interface shall cover all API provided by PBC API
 */
public interface RiakCoreAPI{

    public boolean ping();

    public List<String> listBuckets();

    public List<String> listKeys(String bucket);

    public boolean delete(String bucket, String key, int rw);
}
