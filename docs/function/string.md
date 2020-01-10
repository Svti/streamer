`UUID` UUID生成
UUID() → string . 例如：`UUID()` 返回 d8bc16ed-1c47-4e25-b2a9-fd86eebdda33


`IFNULL` 判断空则处理
IFNULL(string , long) → long . 例如：`IFNULL(a,0)`，返回 0
IFNULL(string , string) → string . 例如：`IFNULL(a,"-")`，返回 -


`TO_BIGINT` 处理强制转数字
TO_BIGINT(string) → long . 例如：`TO_BIGINT('a1') `，返回 0, `TO_BIGINT('12')` 返回 12


`LENGTH` 判断字符串长度
LENGTH(string) → long . 例如：`LENGTH('a1') `，返回 2


`SPLIT_LENGTH` 以某规则切割字符串后的长度
SPLIT_LENGTH(string) → long . 例如：`SPLIT_LENGTH('a:1:2',':') `，返回 3


`CONCAT` 字符串拼接,上限20个
CONCAT(string,string,string...) → string . 例如：`CONCAT('a','b','c') `，返回 'abc'


`STR_TO_MAP_GET` 字符串切分成map，然后获取里面某个字段, 参数：(input, 第一次切分k ， 第二次切分 v ，获取 x 的值)，获取不到返回 NULL
STR_TO_MAP_GET(string,string,string,string) -> string .  例如：`STR_TO_MAP_GET('a:b,c:1,d:dd',',',':','c')` , 返回 1
