package com.gp.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.lionsoul.ip2region.xdb.Searcher;

import java.io.IOException;
import java.io.InputStream;
 
@Slf4j
public class IpAddressUtils {
    private static final byte[] dbPathBytes;
 
    static {
        try (InputStream is = IpAddressUtils.class.getResourceAsStream("/ip/ip2region.xdb")) {
            dbPathBytes = IOUtils.readFully(is, is.available());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
 
    public static String getRegion(String ip) {
        Searcher searcher = null;
        try {
            searcher = Searcher.newWithBuffer(dbPathBytes);
            String region = searcher.search(ip);
            return getCityInfo(region);
        } catch (Exception e) {
            log.error("failed to search({}): {}\n", ip, e.getMessage(), e);
        } finally {
            try {
                searcher.close();
            } catch (IOException e) {
            }
        }
        return null;
    }
 
    /**
     * 解析城市信息，国内显示城市名，国外显示国家名
     */
    private static String getCityInfo(String regionInfo) {
        if (StringUtils.isNotBlank(regionInfo)) {
            String[] cityArr = regionInfo.replace("|0", "").replace("0|", "").split("\\|");
            if (cityArr.length > 0) {
                if ("内网ip".equalsIgnoreCase(cityArr[0])) {
                    return "内网IP";
                }
                if ("中国".equals(cityArr[0])) {
                    return cityArr[1];
                }
                return cityArr[0];
            }
        }
        return "未知IP";
    }
}