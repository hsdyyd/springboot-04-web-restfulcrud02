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

#10.错误处理机制

1）、SpringBoot默认的错误处理机制

    默认效果：
    ​	1）、浏览器，返回一个默认的错误页面 
        2）、如果是其他客户端，默认响应一个json数据
    原理：
    ​	可以参照ErrorMvcAutoConfiguration；错误处理的自动配置；
        给容器中添加了以下组件
  	
    ​	1、DefaultErrorAttributes：  
        帮我们在页面共享信息；
        @Override
        public Map<String, Object> getErrorAttributes(RequestAttributes requestAttributes,
                boolean includeStackTrace) {
            Map<String, Object> errorAttributes = new LinkedHashMap<String, Object>();
            errorAttributes.put("timestamp", new Date());
            addStatus(errorAttributes, requestAttributes);
            addErrorDetails(errorAttributes, requestAttributes, includeStackTrace);
            addPath(errorAttributes, requestAttributes);
            return errorAttributes;
        }         
    
        2、BasicErrorController：处理默认/error请求
        @Controller
        @RequestMapping("${server.error.path:${error.path:/error}}")
        public class BasicErrorController extends AbstractErrorController {
            
            @RequestMapping(produces = "text/html")//产生html类型的数据；浏览器发送的请求来到这个方法处理
        	public ModelAndView errorHtml(HttpServletRequest request,
        			HttpServletResponse response) {
        		HttpStatus status = getStatus(request);
        		Map<String, Object> model = Collections.unmodifiableMap(getErrorAttributes(
        				request, isIncludeStackTrace(request, MediaType.TEXT_HTML)));
        		response.setStatus(status.value());
                
                //去哪个页面作为错误页面；包含页面地址和页面内容
        		ModelAndView modelAndView = resolveErrorView(request, response, status, model);
        		return (modelAndView == null ? new ModelAndView("error", model) : modelAndView);
        	}
        
        	@RequestMapping
        	@ResponseBody    //产生json数据，其他客户端来到这个方法处理；
        	public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        		Map<String, Object> body = getErrorAttributes(request,
        				isIncludeStackTrace(request, MediaType.ALL));
        		HttpStatus status = getStatus(request);
        		return new ResponseEntity<Map<String, Object>>(body, status);
        	}
        	
        3、ErrorPageCustomizer：
        	@Value("${error.path:/error}")
        	private String path = "/error";  系统出现错误以后来到error请求进行处理；（web.xml注册的错误页面规则）
        	
        4、DefaultErrorViewResolver：
        @Override
        	public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status,
        			Map<String, Object> model) {
        		ModelAndView modelAndView = resolve(String.valueOf(status), model);
        		if (modelAndView == null && SERIES_VIEWS.containsKey(status.series())) {
        			modelAndView = resolve(SERIES_VIEWS.get(status.series()), model);
        		}
        		return modelAndView;
        	}
        
        	private ModelAndView resolve(String viewName, Map<String, Object> model) {
                //默认SpringBoot可以去找到一个页面？  error/404
        		String errorViewName = "error/" + viewName;
                
                //模板引擎可以解析这个页面地址就用模板引擎解析
        		TemplateAvailabilityProvider provider = this.templateAvailabilityProviders
        				.getProvider(errorViewName, this.applicationContext);
        		if (provider != null) {
                    //模板引擎可用的情况下返回到errorViewName指定的视图地址
        			return new ModelAndView(errorViewName, model);
        		}
                //模板引擎不可用，就在静态资源文件夹下找errorViewName对应的页面   error/404.html
        		return resolveResource(errorViewName, model);
        	}
        	
        步骤：
        
        ​	一但系统出现4xx或者5xx之类的错误；ErrorPageCustomizer就会生效（定制错误的响应规则）；就会来到/error请求；就会被**BasicErrorController**处理；
        ​	1）响应页面；去哪个页面是由**DefaultErrorViewResolver**解析得到的；
        
        protected ModelAndView resolveErrorView(HttpServletRequest request,
              HttpServletResponse response, HttpStatus status, Map<String, Object> model) {
            //所有的ErrorViewResolver得到ModelAndView
           for (ErrorViewResolver resolver : this.errorViewResolvers) {
              ModelAndView modelAndView = resolver.resolveErrorView(request, status, model);
              if (modelAndView != null) {
                 return modelAndView;
              }
           }
           return null;
        }
        
   2）、如果定制错误响应：
    1）、如何定制错误的页面；
    ​			1）、有模板引擎的情况下；error/状态码; 【将错误页面命名为  错误状态码.html 放在模板引擎文件夹里面的 error文件夹下】，发生此状态码的错误就会来到  对应的页面；
    ​			我们可以使用4xx和5xx作为错误页面的文件名来匹配这种类型的所有错误，精确优先（优先寻找精确的状态码.html）；		
    ​			页面能获取的信息；
    ​				timestamp：时间戳
    ​				status：状态码
    ​				error：错误提示
    ​				exception：异常对象
    ​				message：异常消息
    ​				errors：JSR303数据校验的错误都在这里
    
    ​			2）、没有模板引擎（模板引擎找不到这个错误页面），静态资源文件夹下找；
    ​			3）、以上都没有错误页面，就是默认来到SpringBoot默认的错误提示页面；
    
    2）、如何定制错误的json数据；
    ​		1）、自定义异常处理&返回定制json数据；    
            @ControllerAdvice
            public class MyExceptionHandler {
            
                @ResponseBody
                @ExceptionHandler(UserNotExistException.class)
                public Map<String,Object> handleException(Exception e){
                    Map<String,Object> map = new HashMap<>();
                    map.put("code","user.notexist");
                    map.put("message",e.getMessage());
                    return map;
                }
            }
            //没有自适应效果...
            
            2）、转发到/error进行自适应响应效果处理
             @ExceptionHandler(UserNotExistException.class)
                public String handleException(Exception e, HttpServletRequest request){
                    Map<String,Object> map = new HashMap<>();
                    //传入我们自己的错误状态码  4xx 5xx，否则就不会进入定制错误页面的解析流程
                    /**
                     * Integer statusCode = (Integer) request
                     .getAttribute("javax.servlet.error.status_code");
                     */
                    request.setAttribute("javax.servlet.error.status_code",500);
                    map.put("code","user.notexist");
                    map.put("message",e.getMessage());
                    //转发到/error
                    return "forward:/error";
                }
                
   3）、将我们的定制数据携带出去；
    
    出现错误以后，会来到/error请求，会被BasicErrorController处理，响应出去可以获取的数据是由getErrorAttributes得到的（是AbstractErrorController（ErrorController）规定的方法）；
    
    ​	1、完全来编写一个ErrorController的实现类【或者是编写AbstractErrorController的子类】，放在容器中；
    ​	2、页面上能用的数据，或者是json返回能用的数据都是通过errorAttributes.getErrorAttributes得到；
    ​			容器中DefaultErrorAttributes.getErrorAttributes()；默认进行数据处理的；
        
        自定义ErrorAttributes  
        //给容器中加入我们自己定义的ErrorAttributes
        @Component
        public class MyErrorAttributes extends DefaultErrorAttributes {
        
            @Override
            public Map<String, Object> getErrorAttributes(RequestAttributes requestAttributes, boolean includeStackTrace) {
                Map<String, Object> map = super.getErrorAttributes(requestAttributes, includeStackTrace);
                map.put("company","atguigu");
                return map;
            }
        }
                  