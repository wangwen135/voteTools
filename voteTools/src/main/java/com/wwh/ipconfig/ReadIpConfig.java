package com.wwh.ipconfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * <pre>
 * 
 * </pre>
 *
 * @author wwh
 * @date 2015年9月28日 下午6:33:28
 *
 */
public class ReadIpConfig {
    public static final String pathname = "ip.config";

    public static IpConfig readIpConfig() throws IOException {
        IpConfig ipconfig = new IpConfig();

        BufferedReader bufRead = null;
        IpSegment ipSegment;
        try {
            bufRead = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(pathname)));
            String line;
            while ((line = bufRead.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }
                if (line.length() < 16) {
                    continue;
                }

                String[] segment = line.split("\\s*-\\s*");
                if (segment.length != 2) {
                    continue;
                }
                ipSegment = new IpSegment(segment[0], segment[1]);

                ipconfig.addIpSegment(ipSegment);

            }

            return ipconfig;

        } finally {
            if (bufRead != null) {
                try {
                    bufRead.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
