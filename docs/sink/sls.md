## 用途

	用于写入阿里云的日志服务 Simple Log Service (SLS)。
	
## 举例

```
CREATE SINK slsSink(
)WITH(
    type='sls',
    project='...',
    logStore='...',
    endpoint='cn-hangzhou-intranet.log.aliyuncs.com',
    accessKeyId='...',
    accessKeySecret='...'
);

insert into slsSink SELECT a, b, c FROM kafkaTable;
```