package com.example.msbackend.config;

import com.example.msbackend.interceptor.JwtInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类 - 配置JWT拦截器
 * 功能：注册JwtInterceptor并配置拦截规则，设置不需要拦截的路径
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  @Resource
  private JwtInterceptor jwtInterceptor;

  /**
   * 配置路径匹配
   * 注意：不再添加统一的API路径前缀，因为控制器已经在@RequestMapping中使用了/api前缀
   * 保留此方法是为了将来可能的路径匹配配置
   */
  @Override
  public void configurePathMatch(PathMatchConfigurer configurer) {
    // 不再添加统一的API前缀，控制器已在@RequestMapping中使用/api前缀
  }

  /**
   * 配置拦截器
   * 添加JWT拦截器并设置拦截规则
   */
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    // 注册JWT拦截器
    registry.addInterceptor(jwtInterceptor)
        // 拦截所有路径
        .addPathPatterns("/api/**")
        // 排除不需要拦截的路径
        .excludePathPatterns(
            // 1. 认证相关接口
            "/auth/login",          // 登录接口
            "/auth/logout",         // 登出接口
            "/auth/refresh-token",  // 刷新令牌接口
            "/api/user/register",       // 注册接口
            "/auth/captcha",        // 验证码接口
            
            // 2. 公开资源
            "/api/public/**",           // 公开API
            
            // 3. 静态资源
            "/swagger-ui/**",           // Swagger文档
            "/v3/api-docs/**",          // API文档
            "/webjars/**",              // Webjars资源
            "/favicon.ico",             // 网站图标
            "/error",                   // 错误页面
            
            // 4. 其他常见的不需要认证的接口
            "/api/common/**"             // 公共功能接口
        );
    
    /**
     * 如何添加新的不需要拦截的接口：
     * 1. 在excludePathPatterns方法的参数列表中添加新的路径
     * 2. 路径可以是具体的接口路径，也可以使用通配符
     * 3. 例如：要放行所有以/api/test开头的接口，添加"/api/test/**"
     * 
     * 注意事项：
     * - 路径模式使用Ant风格：
     *   - ? 匹配一个字符
     *   - * 匹配零个或多个字符
     *   - ** 匹配零个或多个目录
     * - 确保只放行必要的接口，以保证系统安全性
     * - 添加新接口后，建议重新启动应用或热加载配置
     */
  }

  /**
   * 配置跨域请求
   * 允许来自不同源的请求，支持前后端分离架构
   * 当前端和后端部署在不同docker容器或不同服务器时必须配置
   */
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        // 允许所有来源的请求（生产环境建议指定具体域名）
        .allowedOriginPatterns("*")
        // 允许的HTTP方法
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        // 允许的请求头
        .allowedHeaders("*")
        // 允许携带凭证（如Cookie）
        .allowCredentials(true)
        // 预检请求的缓存时间（秒）
        .maxAge(3600);
  }
}
