package com.streamer.web.rpc;

import java.io.IOException;

public interface RpcService {

    public boolean run(String node, String name) throws IOException;

    public boolean stop(String node, String name) throws IOException;

    public String log(String node, String name, long n) throws IOException;
}
