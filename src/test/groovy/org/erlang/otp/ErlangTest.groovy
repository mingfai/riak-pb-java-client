package org.erlang.otp

import org.junit.Test;
import static org.junit.Assert.*
import com.ericsson.otp.erlang.OtpNode
import com.ericsson.otp.erlang.OtpMbox
import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpConnection
import com.ericsson.otp.erlang.OtpPeer
import com.ericsson.otp.erlang.OtpSelf
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangString;

class ErlangTest{

  @Test void testConnection(){
    OtpNode node = new OtpNode("java", "riak");
    assertNotNull node
    OtpMbox mbox = node.createMbox();

    assertTrue node.ping("riak@hk1", 1000)

  }

  @Test void testErlangList(){
    def list = new OtpErlangList(new OtpErlangString("riak@hk1"))
    println list
  }

  @Test void testRPC(){
    OtpSelf self = new OtpSelf("java", "riak");
    OtpPeer other = new OtpPeer("riak@hk1");
    OtpConnection connection = self.connect(other);
    connection.sendRPC("riak", "client_connect", new OtpErlangList(new OtpErlangString("riak@hk1")));
    OtpErlangObject received = connection.receiveRPC();
             println received
  }
}
