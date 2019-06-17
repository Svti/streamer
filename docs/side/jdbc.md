## 用途

    系统支持有多个维度表，JDBC维度表，可以用于范围内的去重，作为一个缓存空间的用途，也可以作为一个原料库的用途，判断某信息是否存在，如果不存在则动态添加的操作。每个任务，支持多个维度表，并作为方法在SQL中操作。
	

## 举例

```
CREATE SIDE side_ip(
)WITH(
    type='jdbc',
    driver='com.mysql.jdbc.Driver',
    url='jdbc:mysql://mysql-host:3306/table?useSSL=false',
    user='user',
    password='password',
    read.sql='SELECT 1 FROM cvt_ips WHERE day = ? AND ip = ?',
    write.sql='INSERT INTO cvt_ips (day,ip) VALUES (?,?)'
);

CREATE SIDE side_did(
)WITH(
    type='jdbc',
    ...
);

INSERT INTO consolesink SELECT `day`, pv, uv, pv , uv FROM (
    SELECT TIMESTAMP_STR(`time`,'yyyyMMdd') as `day`, 1 as pv , 
    
    --  如果存在则为0，不存在则为1
    CASE WHEN side_ip(TIMESTAMP_STR(`time`,'yyyyMMdd'),ip) NULL THEN 1
    
    ELSE 0 END uv
 	
 	FROM kafkaTable);

-- 此处在需要注意输出内容，按照 java.sql.Preparestatement 的规范，顺序映射，并不会根据名字匹配。

```
