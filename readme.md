#1.资源导入
   静态资源放入static目录
   页面放入templates,需要thymeleaf支持,加入相关依赖
   同时加入java代码资源
   
#2.解决登录页面在templates中问题
    a.在controller中添加处理/或/index.html请求的方法　
    b.在config中添加处理/或index.html的资源映射
    
#3.静态资源的引用
    在页面中使用thymeleaf的＠语法引用资源位置
    a.在webjars中的资源:/webjars/资源的路径
    b.自己的资源:/静态资源的路径       