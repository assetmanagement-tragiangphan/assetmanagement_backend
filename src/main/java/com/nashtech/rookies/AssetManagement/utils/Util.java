package com.nashtech.rookies.AssetManagement.utils;

import com.nashtech.rookies.AssetManagement.config.Config;

public class Util {
    public static String getZeroPrefixId(Long id)
    {
        int length = Config.LENGTH_STAFF_ID-2;
        return String.format("%0"+length+"d", id);
    }

    public static void main(String[] args) {
        System.out.println(Util.getZeroPrefixId(12L));
    }
}
