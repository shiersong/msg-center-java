package org.msg.msgcenter.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.Iterator;

public class BindingResultUtil {

    public static String toString(BindingResult bindingResult, String separator) {
        StringBuffer result = new StringBuffer();
        if (bindingResult != null && ! CollectionUtils.sizeIsEmpty(bindingResult.getAllErrors())) {
            Iterator<ObjectError> iterator = bindingResult.getAllErrors().iterator();
            while (iterator.hasNext()) {
                ObjectError objectError = iterator.next();
                result.append(objectError.getDefaultMessage()).append(separator);
            }
        }

        if (result.length() > 0) {
            return result.substring(0, result.length() - 1);
        }
        else {
            return result.toString();
        }
    }
}
