package site.higgs.limiterdemo;

import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 模拟发送http请求
 */
public class ApplicationTest {


    public static void main(String[] args) {

         testExchange();
        //testExchange1();
        //testExchange2();
    }

    /**
     * 模拟瞬间发送多个请求
     * 只会有一个请求成功
     */
    private static void testExchange() {

        for (int i = 0; i < 50; i++) {
            request("http://127.0.0.1:8080/exchange?redeemCode=abcd");
        }
    }

    /**
     * 模拟请求频率是20pps
     * 接口限制10pps
     */
    public static void testExchange1() {
        for (int i = 0; i < 100; i++) {
            request("http://127.0.0.1:8080/exchange1?redeemCode=abcd");
            try {
                Thread.sleep(120);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 限制并发数测试
     * 接口限制5并发
     * 只会有5个请求成功
     */
    public static void testExchange2() {
        for (int i = 0; i < 50; i++) {
            request("http://127.0.0.1:8080/exchange2?redeemCode=abcd");
        }
    }


    private static void request(String url) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        okHttpClient.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        System.out.println(response.body().string());
                    }
                });
    }
}
