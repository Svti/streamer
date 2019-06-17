## 用途
	
	JDBC维度表，可以用于范围内的去重，作为一个缓存空间的用途，也可以作为一个原料库的用途，判断某信息是否存在，如果不存在则动态添加的操作。每个任务，支持多个维度表，并作为方法在SQL中操作。
	

## 举例


```

CREATE SINK jdbcSink(
)WITH(
    type='jdbc',
    driver='com.mysql.jdbc.Driver',
    url='jdbc:mysql://host:3306/table?useSSL=false',
    user='user',
    password='password',
    sql='INSERT INTO tables (day,pv,uv) VALUES (?,?,?) ON DUPLICATE KEY UPDATE pv = pv + ? , uv = uv + ? '
);

-- 此处在SELECT 中，需要注意输出内容，按照 java.sql.Preparestatement 的规范，顺序映射，并不会根据名字匹配。

```
