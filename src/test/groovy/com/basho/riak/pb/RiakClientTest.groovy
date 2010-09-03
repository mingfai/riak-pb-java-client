package com.basho.riak.pb

import org.junit.Test;
import static org.junit.Assert.*

import org.jboss.netty.logging.Log4JLoggerFactory
import org.jboss.netty.logging.InternalLoggerFactory
import org.apache.commons.logging.LogFactory
import org.apache.commons.logging.Log

class RiakClientTest{
  static Log log = LogFactory.getLog(RiakClientTest.class);
  static {
    InternalLoggerFactory.setDefaultFactory(new Log4JLoggerFactory());
  }

  @Test(timeout = 10000L) void testNettyClientPing(){
    assertTrue new RiakNettyClient().ping()
  }

  @Test(timeout = 10000L) void testNettyClientListBuckets(){
    Long begin = System.currentTimeMillis()
    def bucketList = new RiakNettyClient().listBuckets()
    if (log.isDebugEnabled()) log.debug("testNettyClientListBuckets() - elapsed: " + (System.currentTimeMillis() - begin));
    bucketList.each { println it}
  }

  @Test(timeout = 10000L) void testNettyClientGetBuckets(){
    def buckets = new RiakNettyClient().getBuckets()
    buckets.each { println it}
  }

  @Test(timeout = 10000L) void testNettyClientGetKeys(){
    assertNotNull new RiakNettyClient().listKeys("test").each {
      println it
    }
  }

  /*@Test(timeout = 10000L) void testMinaClientGetKeys(){
    assertNotNull new RiakMinaClient().listKeys("test").each {
      println it
    }
  }*/
}
