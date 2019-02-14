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
    
#4.国际化
    springmvc国际化的步骤
      1）、编写国际化配置文件；      
      2）、使用ResourceBundleMessageSource管理国际化资源文件      
      3）、在页面使用fmt:message取出国际化内容         
      
    springboot的国际化
      1）、编写国际化配置文件，抽取页面需要显示的国际化消息
      2）、SpringBoot自动配置好了管理国际化资源文件的组件；
        ```java
        @ConfigurationProperties(prefix = "spring.messages")
        public class MessageSourceAutoConfiguration {
            
            /**
        	 * Comma-separated list of basenames (essentially a fully-qualified classpath
        	 * location), each following the ResourceBundle convention with relaxed support for
        	 * slash based locations. If it doesn't contain a package qualifier (such as
        	 * "org.mypackage"), it will be resolved from the classpath root.
        	 */
        	private String basename = "messages";  
            //我们的配置文件可以直接放在类路径下叫messages.properties；
            
            @Bean
        	public MessageSource messageSource() {
        		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        		if (StringUtils.hasText(this.basename)) {
                    //设置国际化资源文件的基础名（去掉语言国家代码的）
        			messageSource.setBasenames(StringUtils.commaDelimitedListToStringArray(
        					StringUtils.trimAllWhitespace(this.basename)));
        		}
        		if (this.encoding != null) {
        			messageSource.setDefaultEncoding(this.encoding.name());
        		}
        		messageSource.setFallbackToSystemLocale(this.fallbackToSystemLocale);
        		messageSource.setCacheSeconds(this.cacheSeconds);
        		messageSource.setAlwaysUseMessageFormat(this.alwaysUseMessageFormat);
        		return messageSource;
        	}
      3) 、指定basename 
        在application.properties中使用spring.messages.basename来指定
      4）、去页面获取国际化的值； 	
      
      5).效果：根据浏览器语言设置的信息切换了国际化；
      原理：
      ​	国际化Locale（区域信息对象）；LocaleResolver（获取区域信息对象）；
     
#5.登陆
     
     开发期间模板引擎页面修改以后，要实时生效
     
     1）、禁用模板引擎的缓存
     # 禁用缓存
     spring.thymeleaf.cache=false 
     
     2）、页面修改完成以后ctrl+f9：重新编译；
     登陆错误消息的显示
     <p style="color: red" th:text="${msg}" th:if="${not #strings.isEmpty(msg)}"></p>
     
#6.拦截器进行登陆检查
    拦截器
    public class LoginHandlerInterceptor implements HandlerInterceptor {
        //目标方法执行之前
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            Object user = request.getSession().getAttribute("loginUser");
            if(user == null){
                //未登陆，返回登陆页面
                request.setAttribute("msg","没有权限请先登陆");
                request.getRequestDispatcher("/index.html").forward(request,response);
                return false;
            }else{
                //已登陆，放行请求
                return true;
            }
    
        }
    
        @Override
        public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    
        }
    
        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    
        }
    }
    
    注册拦截器
    @Bean
    public WebMvcConfigurer webMvcConfigurer(){
        WebMvcConfigurer webMvcConfigurer = new WebMvcConfigurer() {
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                registry.addViewController("/").setViewName("login");
                registry.addViewController("/index.html").setViewName("login");
                registry.addViewController("/main.html").setViewName("dashboard");
            }

            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new LoginHandlerInterceptor()).addPathPatterns("/**")
                        .excludePathPatterns("/","/index.html","/user/login");
            }
        };
        return webMvcConfigurer;
    }     
     
#7.CRUD-员工列表
    实验要求：
    1）、RestfulCRUD：CRUD满足Rest风格；
    URI：  /资源名称/资源标识       HTTP请求方式区分对资源CRUD操作
    实验要求：
    
    1）、RestfulCRUD：CRUD满足Rest风格；
    
    URI：  /资源名称/资源标识       HTTP请求方式区分对资源CRUD操作
    
    |      | 普通CRUD（uri来区分操作） | RestfulCRUD       |
    | ---- | ------------------------- | ----------------- |
    | 查询 | getEmp                    | emp---GET         |
    | 添加 | addEmp?xxx                | emp---POST        |
    | 修改 | updateEmp?id=xxx&xxx=xx   | emp/{id}---PUT    |
    | 删除 | deleteEmp?id=1            | emp/{id}---DELETE |
    
    2）、实验的请求架构;
    
    | 实验功能                             | 请求URI | 请求方式 |
    | ------------------------------------ | ------- | -------- |
    | 查询所有员工                         | emps    | GET      |
    | 查询某个员工(来到修改页面)           | emp/1   | GET      |
    | 来到添加页面                         | emp     | GET      |
    | 添加员工                             | emp     | POST     |
    | 来到修改页面（查出员工进行信息回显） | emp/1   | GET      |
    | 修改员工                             | emp     | PUT      |
    | 删除员工                             | emp/1   | DELETE   |
    
#8.员工列表
    thymeleaf公共页面元素抽取
    
    1、抽取公共片段
    <div th:fragment="copy">
    &copy; 2011 The Good Thymes Virtual Grocery
    </div>
    
    2、引入公共片段
    <div th:insert="~{footer :: copy}"></div>
    ~{templatename::selector}：模板名::选择器
    ~{templatename::fragmentname}:模板名::片段名
    
    3、默认效果：
    insert的公共片段在div标签中
    如果使用th:insert等属性进行引入，可以不用写~{}：
    行内写法可以加上：[[~{}]];[(~{})]；
    
    三种引入公共片段的th属性：
    th:insert：将公共片段整个插入到声明引入的元素中
    th:replace：将声明引入的元素替换为公共片段
    th:include：将被引入的片段的内容包含进这个标签中
    <footer th:fragment="copy">
    &copy; 2011 The Good Thymes Virtual Grocery
    </footer>
    
    引入方式
    <div th:insert="footer :: copy"></div>
    <div th:replace="footer :: copy"></div>
    <div th:include="footer :: copy"></div>
    
    效果
    <div>
        <footer>
        &copy; 2011 The Good Thymes Virtual Grocery
        </footer>
    </div>
    
    <footer>
    &copy; 2011 The Good Thymes Virtual Grocery
    </footer>
    
    <div>
    &copy; 2011 The Good Thymes Virtual Grocery
    </div>

#9.引入片段时传入参数:链接高亮列表完成
    <nav class="col-md-2 d-none d-md-block bg-light sidebar" id="sidebar">
        <div class="sidebar-sticky">
            <ul class="nav flex-column">
                <li class="nav-item">
                    <a class="nav-link active"
                       th:class="${activeUri=='main.html'?'nav-link active':'nav-link'}"
                       href="#" th:href="@{/main.html}">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-home">
                            <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
                            <polyline points="9 22 9 12 15 12 15 22"></polyline>
                        </svg>
                        Dashboard <span class="sr-only">(current)</span>
                    </a>
                </li>
    
    <!--引入侧边栏;传入参数-->
    <div th:replace="commons/bar::#sidebar(activeUri='emps')"></div>
    
    
    
