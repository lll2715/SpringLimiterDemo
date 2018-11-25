package site.higgs.limiterdemo;

public class ResponseMessage {

    private Object data;

    private String message;

    private boolean success;

    private long timestamp;

    public ResponseMessage() {
        timestamp = System.currentTimeMillis();
    }

    public static ResponseMessage ok(Object data) {
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.data = data;
        responseMessage.message = "调用成功";
        responseMessage.success = true;
        return responseMessage;
    }

    public static ResponseMessage error(String messsage) {
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.message = messsage;
        responseMessage.success = false;
        return responseMessage;
    }

    public Object getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
