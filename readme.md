## 架构说明
* 基础库统一通过Library对外提供服务
* 基础业务（non-ui）利用ServerManager方式对外提供服务，如CloudFile
* 基础业务（ui）利用MVVM架构，调用基础业务（non-ui）ServerManager服务进行ui展示和交互，如App下的CloudFile包，如果内容比较多可能会防止单独module上去
* 主业务利用MVVM架构，通过路由对基础业务（ui）进行调用，主要实现跳转逻辑

