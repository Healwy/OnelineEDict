package com.example.onelineedict.utils;

import ohos.system.DeviceInfo;

public class DeviceUtils {
    private static final String TV = "tv";
    private static final String WEARABLE = "wearable";

    public static boolean isTv() {
        return DeviceInfo.getDeviceType().equals(TV);
    }

    public static boolean isWearable() {
        return DeviceInfo.getDeviceType().equals(WEARABLE);
    }
}
