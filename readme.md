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
     
   	 