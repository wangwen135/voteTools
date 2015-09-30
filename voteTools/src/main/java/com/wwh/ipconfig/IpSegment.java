package com.wwh.ipconfig;

/**
 * <pre>
 * IP段
 * </pre>
 *
 * @author wwh
 * @date 2015年9月28日 下午6:31:58
 *
 */
public class IpSegment {
    private String start;
    private String end;

    private int[] _start = new int[4];
    private int[] _end = new int[4];

    /**
     * 这个段中总共有多少IP
     */
    private long totals;

    public String getIpAddress(long index) {
        int[] ip = getIpByIndex(index);
        if (ip == null) {
            return null;
        }
        StringBuffer sbuf = new StringBuffer();
        sbuf.append(ip[0]);
        sbuf.append(".");
        sbuf.append(ip[1]);
        sbuf.append(".");
        sbuf.append(ip[2]);
        sbuf.append(".");
        sbuf.append(ip[3]);

        return sbuf.toString();

    }

    public int[] getIpByIndex(long index) {
        int[] _ip = new int[4];
        if (index < 0 || index > totals) {
            return null;
        }

        if (index == 0)
            return _start;
        if (index == totals)
            return _end;

        for (int i = 3; i >= 0; i--) {
            _ip[i] = (int) (index % 256);
            index /= 256;
            if (index <= 0) {
                break;
            }
        }

        // 加上开始段
        for (int i = 0; i < 4; i++) {
            _ip[i] += _start[i];
        }

        return _ip;
    }

    /**
     * <pre>
     * 构造方法
     * </pre>
     *
     * @param start
     * @param end
     */
    public IpSegment(String startIp, String endIp) {
        this.start = startIp.trim();
        this.end = endIp.trim();

        String[] s1 = start.split("[.]");

        if (s1.length != 4) {
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < s1.length; i++) {
            _start[i] = Integer.parseInt(s1[i]);
        }

        String[] s2 = end.split("[.]");

        if (s2.length != 4) {
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < s2.length; i++) {
            _end[i] = Integer.parseInt(s2[i]);
        }

        // 计算总共有多少
        for (int i = 0; i < 4; i++) {
            int a1 = _start[i];
            int b1 = _end[i];
            int diff = b1 - a1;
            if (diff == 0)
                continue;

            int x = 3 - i;
            totals += diff * Math.pow(256, x);

        }

    }

    /**
     * 获取 start
     *
     * @return the start
     */
    public String getStart() {
        return start;
    }

    /**
     * 获取 end
     *
     * @return the end
     */
    public String getEnd() {
        return end;
    }

    /**
     * 获取 totale
     *
     * @return the totale
     */
    public long getTotals() {
        return totals;
    }

}
