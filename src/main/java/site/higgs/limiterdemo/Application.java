package site.higgs.limiterdemo;

import org.redisson.config.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import site.higgs.limiter.annotation.EnableLimiter;
import site.higgs.limiter.config.GlobalConfig;
import site.higgs.limiter.config.LimiterGlobalConfig;
import site.higgs.limiter.interceptor.ArgumentInjecter;
import site.higgs.limiter.interceptor.ErrorHandler;
import site.higgs.limiter.interceptor.LimiterFallbackResolver;
import site.higgs.limiter.lock.LockManager;
import site.higgs.limiter.lock.support.jdk.JdkLockManager;
import site.higgs.limiter.lock.support.redis.RedisLockManager;
import site.higgs.limiter.ratelimiter.RateLimiterManager;
import site.higgs.limiter.ratelimiter.support.guava.GuavaRateLimiterManager;
import site.higgs.limiter.ratelimiter.support.redis.RedisRateLimiterManager;
import site.higgs.limiter.semaphore.SemaphoreManager;
import site.higgs.limiter.semaphore.support.jdk.JdkSemaphoreManager;
import site.higgs.limiter.semaphore.support.redis.RedisSemaphoreManager;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableLimiter
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    /**
     * 定义一个降级接口，被拦截降级的请求将会返回 服务繁忙
     * 可以直接设置busyFallback 使用该组件
     *
     * @return
     */
    @Bean
    LimiterFallbackResolver<ResponseMessage> busyFallback() {
        return new BusyFallbackResolver();
    }

    /**
     * 定义一个参数注入器
     *
     * @return
     */
    @Bean
    public ArgumentInjecter injectUser() {
        return new UserInfoInjecter();
    }


    /**
     * 定义一个全局生效的配置
     *
     * @return
     */
    @Bean
    GlobalConfig globalConfig() {
        LimiterGlobalConfig limiterGlobalConfig = new LimiterGlobalConfig();
        // 当组件内遇到异常时是否进行降级，比如使用分布式锁时，
        // redis 宕机后的降级策略，返回true未不降级，false为降级
        limiterGlobalConfig.setErrorHandler(new ErrorHandler() {
            @Override
            public boolean handleError(RuntimeException runtimeException) {
                throw runtimeException;
            }
        });
        // 当没有配置降级接口时使用全局配置
        limiterGlobalConfig.setLimiterFallbackResolver(new LimiterFallbackResolver() {
            @Override
            public Object resolve(Method method, Class clazz, Object[] args, String key) {
                throw new RuntimeException("");
            }
        });
        return limiterGlobalConfig;
    }

    // 配置一个LockManager, 可以设置lockManager = "redisLockManager" 使用该LockManager
    @Bean
    public LockManager redisLockManager() {

        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379")
                .setDatabase(1);
        config.setLockWatchdogTimeout(1000 * 60 * 30);
        RedisLockManager redisLockManager = new RedisLockManager(config);
        return redisLockManager;
    }

    @Bean
    public LockManager jdkLockManager() {
        site.higgs.limiter.lock.support.jdk.Config config = new site.higgs.limiter.lock.support.jdk.Config();
        config.setSize(2 << 10);// //缓存锁的容量，当内存中存在的锁实例超过该阈值时将会根据LUR清除最近最少用到的锁实例
        config.setDuration(30);   //在多久没获取该锁时自动解锁
        config.setTimeUnit(TimeUnit.SECONDS);
        config.setTimerduration(86400000);// //看门狗 多久进行一次大扫除  单位毫秒 主要用来清除最近未使用到的锁 减少内存消耗
        return new JdkLockManager();
    }

    @Bean
    public RateLimiterManager redisRateLimiterManager() {
        Config config = new Config();
        // 不要和 lock 使用一个db 会有冲突 ，这里选择db2
        config.useSingleServer().setAddress("redis://127.0.0.1:6379").setDatabase(2);
        config.setLockWatchdogTimeout(1000 * 60 * 30);
        RedisRateLimiterManager redisRateLimiterManager = new RedisRateLimiterManager(config);
        return redisRateLimiterManager;
    }

    @Bean
    public RateLimiterManager guavaRateLimiterManager() {
        return new GuavaRateLimiterManager();
    }

    @Bean
    public SemaphoreManager redisSemaphoreManager() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379").setDatabase(3);
        config.setLockWatchdogTimeout(1000 * 60 * 30);
        RedisSemaphoreManager semaphoreManager = new RedisSemaphoreManager(config);
        return semaphoreManager;
    }


    @Bean
    public SemaphoreManager jdkSemaphoreManager() {
        return new JdkSemaphoreManager();
    }


}
