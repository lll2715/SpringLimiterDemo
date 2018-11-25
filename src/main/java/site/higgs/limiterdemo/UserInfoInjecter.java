package site.higgs.limiterdemo;

import site.higgs.limiter.interceptor.ArgumentInjecter;

import java.util.HashMap;
import java.util.Map;

public class UserInfoInjecter implements ArgumentInjecter {
    @Override
    public Map<String, Object> inject(Object... objects) {
        /**
         * 大多数项目中 当前登录用户都是存放在线程级变量中
         */
        User user = new User();
        user.setUserId("123");
        user.setUserName("higgs");
        Map<String, Object> retVal = new HashMap<>();
        retVal.put("user", user);
        return retVal;
    }
}
