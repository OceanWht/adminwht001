package com.wht.admin.demo.config;

import com.wht.admin.demo.session.OnlineSessionDAO;
import com.wht.admin.demo.session.OnlineSessionFactory;
import com.wht.admin.demo.session.OnlineWebSessionManager;
import com.wht.admin.demo.session.SpringSessionValidationScheduler;
import com.wht.admin.demo.utils.StringUtils;
import net.sf.ehcache.CacheManager;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.config.ConfigurationException;
import org.apache.shiro.io.ResourceUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionValidationScheduler;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;

/**
 * 首先要配置的是ShiroConfig类，Apache Shiro 核心通过 Filter 来实现，
 * 就好像SpringMvc 通过DispachServlet 来主控制一样。 既然是使用 Filter 一般也就能猜到，
 * 是通过URL规则来进行过滤和权限校验，所以我们需要定义一系列关于URL的规则和访问权限
 */
@Configuration
public class MyShiroConfig {

    public static final Log log = LogFactory.getLog(MyShiroConfig.class);

    // Session超时时间，单位为毫秒（默认30分钟）
    @Value("${shiro.session.expireTime}")
    private int expireTime;

    // 相隔多久检查一次session的有效性，单位毫秒，默认就是10分钟
    @Value("${shiro.session.validationInterval}")
    private int validationInterval;

    // 验证码开关
    @Value("${shiro.user.captchaEnabled}")
    private boolean captchaEnabled;

    // 验证码类型
    @Value("${shiro.user.captchaType}")
    private String captchaType;

    // 设置Cookie的域名
    @Value("${shiro.cookie.domain}")
    private String domain;

    // 设置cookie的有效访问路径
    @Value("${shiro.cookie.path}")
    private String path;

    // 设置HttpOnly属性
    @Value("${shiro.cookie.httpOnly}")
    private boolean httpOnly;

    // 设置Cookie的过期时间，秒为单位
    @Value("${shiro.cookie.maxAge}")
    private int maxAge;

    // 登录地址
    @Value("${shiro.user.loginUrl}")
    private String loginUrl;

    // 权限认证失败地址
    @Value("${shiro.user.unauthorizedUrl}")
    private String unauthorizedUrl;

    /**
     * Shiro过滤器配置
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        log.info("MyShiroConfig shiroFilter begin");
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // Shiro的核心安全接口,这个属性是必须的
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // 身份认证失败，则跳转到登录页面的配置
        shiroFilterFactoryBean.setLoginUrl(loginUrl);
        // 权限认证失败，则跳转到指定页面
        shiroFilterFactoryBean.setUnauthorizedUrl(unauthorizedUrl);
        // Shiro连接约束配置，即过滤链的定义
        LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        // 对静态资源设置匿名访问
        filterChainDefinitionMap.put("/favicon.ico**", "anon");
        filterChainDefinitionMap.put("/ruoyi.png**", "anon");
        filterChainDefinitionMap.put("/css/**", "anon");
        filterChainDefinitionMap.put("/docs/**", "anon");
        filterChainDefinitionMap.put("/fonts/**", "anon");
        filterChainDefinitionMap.put("/img/**", "anon");
        filterChainDefinitionMap.put("/ajax/**", "anon");
        filterChainDefinitionMap.put("/js/**", "anon");
        filterChainDefinitionMap.put("/ruoyi/**", "anon");
        filterChainDefinitionMap.put("/druid/**", "anon");
        filterChainDefinitionMap.put("/captcha/captchaImage**", "anon");
        // 退出 logout地址，shiro去清除session
        filterChainDefinitionMap.put("/logout", "logout");
        // 不需要拦截的访问
     //   filterChainDefinitionMap.put("/login", "anon,captchaValidate");
        // 系统权限列表
        // filterChainDefinitionMap.putAll(SpringUtils.getBean(IMenuService.class).selectPermsAll());

       /* Map<String, Filter> filters = new LinkedHashMap<>();
        filters.put("onlineSession", onlineSessionFilter());
        filters.put("syncOnlineSession", syncOnlineSessionFilter());
        filters.put("captchaValidate", captchaValidateFilter());
        // 注销成功，则跳转到指定页面
        filters.put("logout", logoutFilter());
        shiroFilterFactoryBean.setFilters(filters);*/

        // 所有请求需要认证
    //    filterChainDefinitionMap.put("/**", "user,onlineSession,syncOnlineSession");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        return shiroFilterFactoryBean;
    }


    /**
     * 缓存管理器 使用Ehcache实现
     * 查询的操作最频繁，，某个线程很多的查询都是重复的，或者有些数据跨线程查询也是重复。
     * 问题：造成数据库压力变大。传统的数据库查询效率就不高（网络、sql语句复杂），导致查询体验不好。
     * 解决：使用缓存机制
     */
    @Bean
    public EhCacheManager ehCacheManager() {
        CacheManager cacheManager = CacheManager.getCacheManager("wht");
        EhCacheManager ehCacheManager = new EhCacheManager();
        if (StringUtils.isNull(cacheManager)) {
            ehCacheManager.setCacheManager(new CacheManager(getConfigFileFromSystem()));
        } else {
            ehCacheManager.setCacheManager(cacheManager);
        }
        return ehCacheManager;
    }

    /**
     * 返回配置文件流 避免ehcache配置文件一直被占用，无法完全销毁项目重新部署
     */
    private InputStream getConfigFileFromSystem() {
        String configFile = "classpath:echace/ehcache-shiro.xml";
        //  String configFile = "echace/ehcache-shiro.xml";
        InputStream inputStream = null;
        try {
            inputStream = ResourceUtils.getInputStreamForPath(configFile);
            byte[] buffer = IOUtils.toByteArray(inputStream);
            InputStream in = new ByteArrayInputStream(buffer);
            return in;
        } catch (IOException e) {
            throw new ConfigurationException(
                    "Unable to obtain input stream for cacheManagerConfigFile [" + configFile + "]", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }


    }

    /**
     * 自定义Realm
     */
    @Bean
    public MyShiroRealm myShiroRealm(EhCacheManager cacheManager) {
        MyShiroRealm myShiroRealm = new MyShiroRealm();
        myShiroRealm.setCacheManager(cacheManager);
        return myShiroRealm;
    }

    /**
     * 自定义sessionDAO会话
     */
    @Bean
    public OnlineSessionDAO sessionDAO() {
        OnlineSessionDAO sessionDAO = new OnlineSessionDAO();
        return sessionDAO;
    }

    @Bean
    public OnlineSessionFactory sessionFactory() {
        OnlineSessionFactory sessionFactory = new OnlineSessionFactory();
        return sessionFactory;
    }

    /**
     * 会话管理器
     */
    @Bean
    public OnlineWebSessionManager sessionManager() {
        OnlineWebSessionManager manager = new OnlineWebSessionManager();
        //加入缓存管理器
        manager.setCacheManager(ehCacheManager());
        //删除过期的session
        manager.setDeleteInvalidSessions(true);
        //设置全局session超时时间 30分钟
        manager.setGlobalSessionTimeout(expireTime * 60 * 1000);
        // 去掉 JSESSIONID,不暴露url
        manager.setSessionIdUrlRewritingEnabled(false);
        //定义使用无效的Session定时调度器
      //  manager.setSessionValidationScheduler(sessionValidationScheduler());
        // 是否定时检查session
     //   manager.setSessionValidationSchedulerEnabled(true);
        //自定义sessionDAO
        manager.setSessionDAO(sessionDAO());
        //自定义sessionFactory
        manager.setSessionFactory(sessionFactory());
        return manager;
    }

    /**
     * 自定义sessionFactory调度器
     */
    private SessionValidationScheduler sessionValidationScheduler() {
        SpringSessionValidationScheduler springSessionValidationScheduler = new SpringSessionValidationScheduler();
        // 相隔多久检查一次session的有效性，单位毫秒，默认就是10分钟
        springSessionValidationScheduler.setSessionValidationInterval(validationInterval * 60 * 1000);
        // 设置会话验证调度器进行会话验证时的会话管理器
        springSessionValidationScheduler.setSessionManager(sessionManager());

        return springSessionValidationScheduler;
    }

    /**
     * 安全管理器  整个shiro的核心
     */
    @Bean
    public SecurityManager securityManager(MyShiroRealm myShiroRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //设置Reaml
        securityManager.setRealm(myShiroRealm);
        //注入缓存管理器
        securityManager.setCacheManager(ehCacheManager());
        // session管理器
        securityManager.setSessionManager(sessionManager());

        return securityManager;
    }
}
