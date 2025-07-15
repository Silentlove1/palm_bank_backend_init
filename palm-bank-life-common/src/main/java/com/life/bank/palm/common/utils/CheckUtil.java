package com.life.bank.palm.common.utils;

import com.life.bank.palm.common.exception.BaseBizCodeEnum;
import com.life.bank.palm.common.exception.CommonBizException;
import com.life.bank.palm.common.exception.ErrorCodeEnum;
import org.apache.commons.lang3.StringUtils;
import java.util.Objects;

public class CheckUtil {

    public static class Biz {
        public static Biz INSTANCE = new Biz();

        private Biz() {}

        public Biz isTrue(boolean condition, String errMsg) {
            if (!condition) {
                throw new CommonBizException(BaseBizCodeEnum.COMMON_ERROR.getErrorCode(), errMsg);
            }
            return this;
        }

        public Biz noNull(Object obj, String errMsg) {
            if (Objects.isNull(obj)) {
                throw new CommonBizException(BaseBizCodeEnum.COMMON_ERROR.getErrorCode(), errMsg);
            }
            return this;
        }

        public Biz strNotBlank(String str, String errMsg) {
            if (StringUtils.isBlank(str)) {
                throw new CommonBizException(BaseBizCodeEnum.COMMON_ERROR.getErrorCode(), errMsg);
            }
            return this;
        }
    }
}