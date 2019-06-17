##用途
	
	读取kafka数据源的内容，放在内存中，当作一张表来使用。提供系统自带几个属性可以供使用。
	
	自带属性:	
	_topic:		kafka消息中的 topic
	_key:			kafka消息中的 key
	_value:		kafka消息中的消息内容
	_offset:		kafka消息中的 offset
	_timestamp:	kafka消息中的 timestamp
	_partition:	kafka消息中的 partition
	
	只支持一个kafka的输入源，但是支持多topic。
	
	
##举例

```
CREATE TABLE kafkaTable(
	 _topic:topic string,
    context.data.state:state bigint,
    context.data.time:time bigint,
    context.data.money:money bigint
)WITH(
    type='kafka08',
    kafka.zkurl='zk:2181/kafka',
    kafka.topic='topic1|topic2',
    kafka.group='group',
    kafka.reset='smallest',
    kafka.batch='100'
    
    --支持kv切分， key｜value ，例如 " `a=100`b=msg "。 如果不设置则为JSON
    split='`|='
);
	
`context.data.state:state bigint` 采用JsonPath的API读取json内容，支持多级扩展，其中 冒号:是表中别名，bigint 是表示字段类型。 

```


