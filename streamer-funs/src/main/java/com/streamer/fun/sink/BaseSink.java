package com.streamer.fun.sink;

import java.util.List;

import com.streamer.core.message.Row;

public abstract class BaseSink {

    public abstract void checkConfig();

    public abstract boolean process(List<List<Row>> list) throws Exception;

    public abstract void destory();
}
