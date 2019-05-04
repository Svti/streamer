# Streamer流计算引擎

## 介绍

## 特性

## UDF

## 举例

```
CREATE TABLE kafkaTable(
   context.device.did:did string,
   context.device.factory:factory string,
   context.device.model:model string,
   context.data.time:time timestamp
)WITH(
    type='kafka08',
    kafka.zkurl='kafka:2181/kafka',
    kafka.topic='topic',
    kafka.group='test',
    kafka.reset='smallest',
    kafka.batch='10'
);

CREATE SINK jdbcSink(
)WITH(
    type='jdbc',
    driver='com.mysql.jdbc.Driver',
    url='jdbc:mysql://host/tmp?useSSL=false',
    user='tmp',
    password='password',
    sql='INSERT INTO tmp (did,factory,model,createAt,updateAt) VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE factory =? , model =? , updateAt =? '
);

insert into jdbcSink select did,factory,model, MIN(CAST(`time` as timestamp)) as createAt , MAX(CAST(`time` as timestamp)) as updateAt , factory , model , MAX(CAST(`time` as timestamp)) as updateAt FROM kafkaTable GROUP BY did,factory,model;
```