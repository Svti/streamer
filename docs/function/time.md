

`NOW_TIMESTAMP` 当前系统13位时间戳
NOW_TIMESTAMP() → long . 例如:`NOW_TIMESTAMP()` ，返回 1574661210869



`DAY_DIFF` 日期天数比较
DAY_DIFF(long , long) → int ，例如: `DAY_DIFF(0,NOW_TIMESTAMP())` ， 返回 18225



`HOUR_DIFF` 日期小时数据比较
HOUR_DIFF(long , long) → int ，例如: `DAY_DIFF(0,NOW_TIMESTAMP())` ， 返回 437406



`TIMESTAMP_STR` 时间戳转字符串
TIMESTAMP_STR(long , string) → string ，例如: `DAY_DIFF(0, 'yyyyMMdd')` ， 返回 19700101
TIMESTAMP_STR(long , string , string) → string ，例如: `DAY_DIFF(0, 'yyyyMMdd' , '+8')` ，可入参时区， 返回 19700101



`STR_TIMESTAMP` 字符串转时间戳
STR_TIMESTAMP(string , string) → long ，例如: `DAY_DIFF('19700101', 'yyyyMMdd')` ， 返回 0
STR_TIMESTAMP(string , string , string ) → long ，例如: `DAY_DIFF('19700101', 'yyyyMMdd' , '+8')` ，可入参时区， 返回 0


`TIMESTAMP_MOD_MIN` 以某个单位时间取模的时间格式化，例如 14:02 以5min为单位，取模后为 14:00 , 14:07，则为14:05。
STR_TIMESTAMP(int , long , string) → int ，例如: `STR_TIMESTAMP(5, 1574661731384 , 'yyyyMMddHHmm') `， 返回  201911251400
STR_TIMESTAMP(int , long , string , string ) → int ，例如: `STR_TIMESTAMP(5, 1574661731384 , 'yyyyMMddHHmm', '+8') `， 返回  201911251400 