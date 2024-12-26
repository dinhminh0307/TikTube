package com.example.tiktube.backend.utils;

import java.util.UUID;

public class UidGenerator {
    public UidGenerator() {}
    public static String generateUID() {
        return UUID.randomUUID().toString();
    }
}
