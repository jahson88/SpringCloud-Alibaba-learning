# SpringCloud-Alibaba-learning
springcloud-alibaba 学习demo,基于https://github.com/rstyro/SpringCloud-Alibaba-learning的例子升级依赖包spring cloud 3.0.1，并调试成功


http://localhost:8848/nacos/



http://localhost:8810/test/sayHi?authority=rstyro&name=test

http://localhost:8810/test/sayHi?authority=admin&name=test



http://localhost:8848/nacos/v1/ns/instance?serviceName=nacos-provider&ip=192.168.1.5&port=8810


http://localhost:8848/nacos/v1/ns/instance/list?serviceName=nacos-provider



http://localhost:8801/feign/hello?authority=user&name=test



"http://" + this.endpoint + "/nacos/serverlist";


Nacos自身提供了SDK和API来完成服务注册与发现等操作，SDK本质上就是针对HTTP请求的一层封装，因为服务端只提供了REST接口，那么我们带着规范，去了解一下nacos的核心接口：
1、注册实例
    1、作用：将服务地址信息注册到Nacos Server中
    2、API：/nacos/v1/ns/instance   (POST)
    3、SDK：
        void registerInstance(String serviceName , String ip , int port) throws NacosException;
 
        void registerInstance(String serviceName, String ip,int port,String clusterName) throws NacosException;
 
        void registerInstance(String serviceName,Instance instance) throws NacosException;

    4、讲解：
    serviceName：服务名称，相当于配置文件中的spring.application.name
    ip：服务实例ip
    port：服务实例port
    clusterName：集群名称，标识当前服务实例属于哪个集群
    instance：实例属性，其实就是把上面的参数封装成一个对象
    5、调用方式
        NamingService naming=NamingFactory.createNamingService(System.getProperty("serveAddr"));
        naming.registerInstance("nacos_name","192.168.80.1",8080,"DEFAULT");

2、获取全部实例

    1、作用：根据服务名称从Nacos注册中心上获取所有服务的实例集合
    2、API： /nacos/v1/ns/instance/list    (GET)
    3、SDK：
        List<Instance> getAllInstances(String serviceName) throws NacosException;
        List<Instance> getAllInstances(String serviceName,List<String> cluster) throws NacosException;
    4、讲解：
    serviceName：服务名称
    cluster：集群列表，入参是一个String集合
    5、调用方式：
        NamingService naming=NamingFactory.createNamingService(System.getProperty("serveAddr"));
        System.out.println(naming.getAllInstances("nacos_name),true);

3、服务监控

    1、作用：为了实时让客户端感知到服务提供者实例的变化，相当于Eureka中的心跳机制
    2、API：/nacos/v1/ns/instance/list   (GET) 
    3、SDK：
        void subscribe(String serviceName,EventListener listener) throws NacosException;
        void subscribe(String serviceName,List<String> clusters,EventListener listener) throw NacosException;

    4、讲解：
    EventListener：当提供者服务实例出现异常，比如上下限时，会调用一个事件回调
    监听有两种方式：第一种：客户端调用/nacos/v1/ns/instance/list进行定时轮询
                    第二种：基于DatagramSocket的UDP协议，实现服务端的主动推送


# application.yml配置文件

```code
spring:
  profiles:
    # 对应环境
    active: dev
  application:
    # 服务名
    name: pearl-test
  cloud:
    nacos:
      config:
        # 是否开启配置中心 默认true
        enabled: true
        # 配置中心地址
        server-addr: localhost:8848
        # 当要上阿里云时，阿里云上面的一个云账号名
        access-key: accessKey
        # 当要上阿里云时，阿里云上面的一个云账号密码
        secret-key: secretKey
        # Nacos Server 对外暴露的 context path
        context-path: nacos
        # 读取的配置内容对应的编码 默认UTF-8
        encode: ISO-8859-1
        # 配置文件后缀
        file-extension: yml
        # 配置对应的分组
        group: PEARL_GROUP
        # 命名空间 常用场景之一是不同环境的配置的区分隔离，例如开发测试环境和生产环境的资源（如配置、服务）隔离等
        namespace: 771d3d1a-374b-47fe-b88b-c53a0b271acf
        # 文件名前缀 默认为 ${spring.appliction.name}
        prefix: prefix
        # 客户端获取配置的超时时间(毫秒) 默认3000
        timeout: 5000
        # 配置成Nacos集群名称
        #cluster-name: clusterName
        # Nacos 认证用户
        username: nacos
        # Nacos 认证密码
        password: 123456
        # 长轮询的重试次数 默认3
        max-retry: 5
        # 长轮询任务重试时间，单位为毫秒
        config-retry-time: 1000
        # 长轮询的超时时间，单位为毫秒
        config-long-poll-timeout: 1000
        # 监听器首次添加时拉取远端配置 默认false
        enable-remote-sync-config: true
        # 地域的某个服务的入口域名，通过此域名可以动态地拿到服务端地址
        #endpoint: localhost
        # 是否开启监听和自动刷新
        refresh-enabled: true
        # 支持多个共享 Data Id 的配置，优先级小于extension-configs,自定义 Data Id 配置 属性是个集合，内部由 Config POJO 组成。Config 有 3 个属性，分别是 dataId, group 以及 refresh
        shared-configs[0]:
          data-id: pearl-common.yml
          group: DEV_GROUP   # 默认为DEFAULT_GROUP
          refresh: true   # 是否动态刷新，默认为false
        shared-configs[1]:
          data-id: pearl-test.yml
          group: DEV_GROUP
          refresh: true
        # 支持多个扩展 Data Id 的配置 ，优先级小于prefix+dev.yaml
        # extension-configs:
```


# NamingService  配置属性    与上面的路径不同，估计是版本问题。
{accessKey=${nacos.access-key:}, clusterName=${nacos.cluster-name:}, configLongPollTimeout=${nacos.configLongPollTimeout:}, configRetryTime=${nacos.configRetryTime:}, contextPath=${nacos.context-path:}, enableRemoteSyncConfig=${nacos.enableRemoteSyncConfig:}, encode=${nacos.encode:UTF-8}, endpoint=${nacos.endpoint:}, maxRetry=${nacos.maxRetry:}, namespace=${nacos.namespace:}, password=${nacos.password:}, secretKey=${nacos.secret-key:}, serverAddr=${nacos.server-addr:}, username=${nacos.username:}}






# feign config
```code
feign:
  client:
    config:
      defalut: # feign请求默认配置
        connectTimeout: 2000
        readTimeout: 3000
      fiegnName: # fiegnName服务请求的配置，优先defalut配置。
        connectTimeout: 5000 # 链接超时时间
        readTimeout: 5000  # 请求
        loggerLevel: full   # 日志级别
        errorDecoder: com.example.SimpleErrorDecoder #异常处理
        retryer: com.example.SimpleRetryer # 重试策略
        defaultQueryParameters: # 默认参数条件
          query: queryValue
        defaultRequestHeaders:  # 默认默认header
          header: headerValue
        requestInterceptors:    # 默认拦截器
          - com.example.FooRequestInterceptor
          - com.example.BarRequestInterceptor
        decode404: false   #404响应 true-直接返回，false-抛出异常       
        encoder: com.example.SimpleEncoder  #传输编码
        decoder: com.example.SimpleDecoder  #传输解码
        contract: com.example.SimpleContract #传输协议


spring.cloud.loadbalancer.nacos.enabled
```




