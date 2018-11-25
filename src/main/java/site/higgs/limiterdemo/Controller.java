package site.higgs.limiterdemo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.higgs.limiter.lock.HLock;
import site.higgs.limiter.ratelimiter.HRateLimiter;
import site.higgs.limiter.semaphore.HSemaphore;

@RestController
public class Controller {





    /**
     * 限制键为 #redeemCode+#user.userId
     * 当多个请求同时到达时，只有一个会被正常处理，其他请求会被降级
     * 当正常的请求被处理完毕，锁会释放
     * 值得注意得是keys 本身不会包含方法名，最好前面加前缀同其他接口分开
     * @param redeemCode
     * @return
     */
    @RequestMapping(value = "/exchange", method = RequestMethod.GET)
    @HLock(keys = "#redeemCode+#user.userId", fallbackResolver = "busyFallback", lockManager = "redisLockManager", argInjecters = "injectUser")
    public ResponseMessage exchange(@RequestParam("redeemCode") String redeemCode) {
        try {
            // do something
            Thread.sleep(2000);
        } catch (InterruptedException e) {

        }
        return ResponseMessage.ok(null);
    }

    /**
     * 限制该接口的访问频率为 10次/秒
     * redis实现的限流器精度和网络环境和机器配置有关，自行测试效果
     * @param redeemCode
     * @return
     */
    @RequestMapping(value = "/exchange1", method = RequestMethod.GET)
    @HRateLimiter(keys = "'exchange1'+#redeemCode", fallbackResolver = "busyFallback", rateLimiterManager = "redisRateLimiterManager",pps = 10, argInjecters = "injectUser")
    public ResponseMessage exchange1(@RequestParam("redeemCode") String redeemCode) {
        try {
            // do something
            Thread.sleep(5000);
        } catch (InterruptedException e) {

        }
        return ResponseMessage.ok(null);
    }


    /**
     * 限制该接口并发数为10
     * redis实现的限流器精度和网络环境和机器配置有关，自行测试效果
     * @param redeemCode
     * @return
     */
    @RequestMapping(value = "/exchange2", method = RequestMethod.GET)
    @HSemaphore(keys = "'exchange2'+#redeemCode", fallbackResolver = "busyFallback", semaphoreManager = "redisSemaphoreManager",permits = 5, argInjecters = "injectUser")
    public ResponseMessage exchange2(@RequestParam("redeemCode") String redeemCode) {
        try {
            // do something
            Thread.sleep(2000);
        } catch (InterruptedException e) {

        }
        return ResponseMessage.ok(null);
    }




}
