## 用途
	
	JDBC 写入关系型数据库。
	

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
