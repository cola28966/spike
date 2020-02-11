package com.spike.redis;

public interface KeyPrefix {
    int expireSeconds();
    String getPrefix();
}
