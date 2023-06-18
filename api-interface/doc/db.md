##PlatClient 客户端

工具：hutool 

controller：/name开头定义了三个方法。

利用hutool的HttpUtil工具类，一键调用如：
http://localhost:8090/api/name/user
，并在请求头携带签名，以及用户名。

SignUtils:原密码+"."+user



##平台接口开发

接口信息：主键、name、描述、请求方式、url、状态等等

功能（管理员）：1.发布接口、 2.下线接口

逻辑：1.判断请求是否存在、
 2.判断接口是否存在、是否可以调用
 3.修改接口的状态。
 
 





