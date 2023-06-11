# API-PLATFORM
开放接口平台升级版

项目介绍：基于Spring Boot+ Feign + Gateway +Redis+SpringSecurity+RabbitMQ的接口开放调用平台。管理员可以接入并发布接口，可视化各接
口调用情况,实现接口调用次数排行榜；用户可以开通接口调用权限、浏览接口及在线调试，并通过客户端 SDK 轻松调用接
口。
主要职责
· 基于 Spring Security、RBAC权限模型，实现访问控制、权限管理。
· 使用 Redis 实现分布式 Session，解决集群间登录态同步问题
· 为防止接口被恶意调用，设计 API 签名认证算法，分配ak/sk以鉴权，避免重放攻击，确保接口调用的安全性。
· 基于Spring Boot Starter 开发了客户端 SDK，一行代码 即可调用接口，方便调用接口。
· 使用Swagger+knife4j生成OpenAPI规范的文档，便于前后端协调。
· 选用 Spring Cloud Gateway 作为 API 网关，实现了路由转发、黑白名单访问控制、并集中处理签名校验、接口调用统计
等业务逻辑、便于系统开发维护。
