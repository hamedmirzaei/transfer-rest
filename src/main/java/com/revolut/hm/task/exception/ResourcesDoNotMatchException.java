package com.revolut.hm.task.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class ResourcesDoNotMatchException extends RuntimeException {
    private String resourceName1, resourceName2;
    private String fieldName1, fieldName2;
    private Object fieldValue1, fieldValue2;

    public ResourcesDoNotMatchException(String resourceName1, String fieldName1, Object fieldValue1,
                                        String resourceName2, String fieldName2, Object fieldValue2) {
        super(String.format("%s with %s : '%s' does not match to %s with %s : '%s'",
                resourceName1, fieldName1, fieldValue1, resourceName2, fieldName2, fieldValue2));
        this.resourceName1 = resourceName1;
        this.fieldName1 = fieldName1;
        this.fieldValue1 = fieldValue1;
        this.resourceName2 = resourceName2;
        this.fieldName2 = fieldName2;
        this.fieldValue2 = fieldValue2;
    }

    public String getResourceName1() {
        return resourceName1;
    }

    public String getFieldName1() {
        return fieldName1;
    }

    public Object getFieldValue1() {
        return fieldValue1;
    }

    public String getResourceName2() {
        return resourceName2;
    }

    public String getFieldName2() {
        return fieldName2;
    }

    public Object getFieldValue2() {
        return fieldValue2;
    }
}
