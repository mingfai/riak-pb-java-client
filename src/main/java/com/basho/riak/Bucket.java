package com.basho.riak;

import java.util.List;

public interface Bucket{

    public String getName();

    public List<String> getKeys();
}
