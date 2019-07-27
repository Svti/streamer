# 编译

    git clone https://github.com/Svti/streamer.git
    
    cd ./streamer
    
    mvn clean package
    
    streamer-web 是网页控制台
    
    streamer-worker 是执行节点

# 部署

### 控制节点
	数据库文件 docs/table.sql

	cd ./streamer-web/target
	
	streamer-web-1.0.1 的文件夹就所有需要的文件了, sh bin/startup.sh 就可以启动程序
	
	默认的用户名和密码
	application.admin.username=admin
	application.admin.password=admin
	
### 执行器节点	
	cd ./streamer-worker/target
	
	streamer-woker-1.0.1 的文件夹就所有需要的文件了, sh bin/startup.sh 就可以启动程序。
	
	执行器节点被访问端口，查看日志需要
	http-server.http.port=19991
	
	执行器节点绑定的IP，不填写则会自动查找，查看日志需要
	http-server.http.host=192.168.0.100
	
	
# 启动

	访问 streamer-web 所在的端口，就可以看到登录界面了， 默认在 28090。
	
	

