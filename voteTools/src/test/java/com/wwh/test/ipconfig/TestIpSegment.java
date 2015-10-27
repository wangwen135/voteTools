package com.wwh.test.ipconfig;

import static org.junit.Assert.*;
import org.junit.Test;

import com.wwh.ipconfig.IpSegment;

/**
 * <pre>
 * 
 * </pre>
 *
 * @author wwh
 * @date 2015年10月2日 下午10:49:59
 *
 */
public class TestIpSegment {

    @Test
    public void testGetTotals() {
        IpSegment segment = new IpSegment("127.0.0.1", "127.0.0.255");
        assertEquals("计算IP段总数错误",  254,segment.getTotals());
    }

    @Test
    public void testGetIpAddress() {
        IpSegment segment = new IpSegment("127.0.0.1", "127.0.0.255");
       String targetIp = segment.getIpAddress(3l);
       assertEquals("计算错误","127.0.0.4", targetIp);
    }

}
