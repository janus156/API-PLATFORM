package cn.api.model.response;


import cn.api.model.enums.ErrorCodeEnum;

public class ResultUtils {

    /**
     * 成功
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * 失败
     *
     * @param ErrorCodeEnum
     * @return
     */
    public static BaseResponse error(ErrorCodeEnum ErrorCodeEnum) {
        return new BaseResponse<>(ErrorCodeEnum);
    }

    /**
     * 失败
     *
     * @param code
     * @param message
     * @return
     */
    public static BaseResponse error(int code, String message) {
        return new BaseResponse(code, null, message);
    }

    /**
     * 失败
     *
     * @param ErrorCodeEnum
     * @return
     */
    public static BaseResponse error(ErrorCodeEnum ErrorCodeEnum, String message) {
        return new BaseResponse(ErrorCodeEnum.getCode(), null, message);
    }
}
