## 用途

	用于输出给企业微信的通知，目前只支持MARKDOWN格式的消息。该 SINK 会根据输出的 结果字段数量，自动添加换行。
	
## 举例

```
CREATE SINK qywxSink(
)WITH(
    type='workwechat',
    corpid='...',
    agentid='...',
    secret='...',
    touser='...'
);

-- 此处会根据字段数量，自动添加换行，避免企业微信换行符转义
insert into qywxSink SELECT '### 我是标题' as b ,
CONCAT('时间:',times) c FROM kafkaTable;
```