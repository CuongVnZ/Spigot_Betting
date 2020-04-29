package net.cuongvnz.business1.utils.gson;

import com.google.gson.GsonBuilder;

@FunctionalInterface
public interface GsonBuilderModifiers {
    public GsonBuilder modify(GsonBuilder builder);
}