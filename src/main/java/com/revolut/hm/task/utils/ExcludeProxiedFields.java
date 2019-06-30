package com.revolut.hm.task.utils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

public class ExcludeProxiedFields implements ExclusionStrategy {

    @Override
    public boolean shouldSkipField(FieldAttributes fa) {
        return fa.getAnnotation(ManyToOne.class) != null ||
                fa.getAnnotation(OneToOne.class) != null ||
                fa.getAnnotation(ManyToMany.class) != null ||
                fa.getAnnotation(OneToMany.class) != null;
    }

    @Override
    public boolean shouldSkipClass(Class<?> type) {
        return false;
    }
}
