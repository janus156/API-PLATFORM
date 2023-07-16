# API-PLATFORM
开放接口平台升级版

项目介绍：基于React+Spring Boot+ Dubbo + Gateway +Redis+SpringSecurity+RabbitMQ的接口开放调用平台。管理员可以接入并发布接口，可视化各接
口调用情况,实现接口调用次数排行榜；用户可以调用天气信息、IP定位、生成心灵鸡汤赞美、快速发短信等功能。

主要职责

· 基于 Spring Security、RBAC权限模型，实现访问控制、权限管理。

· 使用 Redis 实现分布式 Session，解决集群间登录态同步问题

· 为防止接口被恶意调用，设计 API 签名认证算法，分配ak/sk以鉴权。

· 使用Redis生成nonce、timestamp避免重放攻击，确保接口调用的安全性。

· 使用用户id、接口id、接口参数作为唯一值配合Redis+AOP+注解防止短时间内重复调用接口。

· 基于Spring Boot Starter 开发了客户端 SDK，一行代码 即可调用接口，方便调用接口。

· 选用 Spring Cloud Gateway 作为 API 网关，实现了路由转发、黑白名单访问控制、并集中处理签名校验、接口调用统计
等业务逻辑、便于系统开发维护。

· 使用死信队列+TTL实现订单30分钟未支付，取消订单的功能。
