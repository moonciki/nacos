package com.alibaba.nacos.config.server.exception;

import com.alibaba.nacos.common.model.RestResult;
import com.alibaba.nacos.config.server.result.code.ResultCodeEnum;

/**
 * nacos custom exception.
 */
public class NacosWebException extends RuntimeException {

    private Integer errorCode;

    private String errorMsg;

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    protected NacosWebException(String msg) {
        super(msg);
    }

    protected NacosWebException(Throwable te) {
        super(te);
    }

    protected NacosWebException(String message, Throwable cause) {
        super(message, cause);
    }

    protected void initException(WebCode error) {
        if (error == null) {
            error = ResultCodeEnum.ERROR.info();
        }

        this.errorCode = error.getCode();
        this.errorMsg = errorMsg;

    }

    /**
     * 创建捕获到的异常.
     * @param webCode webCode
     * @return NacosWebException
     */
    public static NacosWebException createException(WebCode webCode) {

        if (webCode == null) {
            webCode = ResultCodeEnum.ERROR.info();
        }

        String errorMsg = webCode.getMsg();

        NacosWebException ne = new NacosWebException(errorMsg);
        ne.initException(webCode);

        return ne;
    }

    /**
     * 创建捕获到的异常.
     * @return NacosWebException
     */
    public static NacosWebException createException() {
        NacosWebException oe = createException(ResultCodeEnum.ERROR.info());
        return oe;
    }

    /**
     * 创建捕获到的异常.
     * @param t t
     * @return NacosWebException
     */
    public static NacosWebException createException(Throwable t) {
        NacosWebException oe = null;

        if (t instanceof NacosWebException) {
            oe = (NacosWebException) t;
        } else {
            oe = new NacosWebException(t);
            oe.initException(null);
        }

        return oe;
    }

    /**
     * 返回失败 .
     * @param t t
     * @return RestResult
     */
    public static RestResult failResponse(Throwable t) {

        NacosWebException ne = NacosWebException.createException(t);

        Integer errorCode = ne.getErrorCode();
        String errorMsg = ne.getErrorMsg();

        RestResult resp = new RestResult(errorCode, errorMsg);

        return resp;
    }

}
