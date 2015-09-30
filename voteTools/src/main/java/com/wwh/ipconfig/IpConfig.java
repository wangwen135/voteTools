package com.wwh.ipconfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * <pre>
 * ip配置
 * </pre>
 *
 * @author wwh
 * @date 2015年9月28日 下午11:19:16
 *
 */
public class IpConfig {

    private long totals;

    private List<IpSegment> ipList;

    private List<Long> indexList;

    public IpConfig() {
        ipList = new ArrayList<IpSegment>();
        indexList = new ArrayList<Long>();
    }

    public void addIpSegment(IpSegment ipsg) {
        totals += ipsg.getTotals();
        ipList.add(ipsg);
        indexList.add(totals);
    }

    public int binarySearch(long indexVal) {
        int low = 0;
        int high = indexList.size() - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            long midVal = indexList.get(mid);

            if (midVal < indexVal) {
                low = mid + 1;
            } else if (midVal > indexVal) {
                if (mid == 0) {
                    return mid;
                } else {
                    long midval2 = indexList.get(mid - 1);
                    if (midval2 < indexVal) {
                        return mid;
                    } else {
                        high = mid - 1;
                    }
                }
            } else {
                return mid;
            }
        }
        // 没有找到
        return 0;
    }

    public String randomIP() {
        if (totals == 0)
            return null;
        Random r = new Random();

        long randIndex = (long) (r.nextDouble() * totals);

        // 二分法查找
        int i = binarySearch(randIndex);
        if (i == 0) {
            return ipList.get(i).getIpAddress(randIndex);
        } else {
            return ipList.get(i).getIpAddress(randIndex - indexList.get(i - 1));
        }

    }

    /**
     * 获取 totals
     *
     * @return the totals
     */
    public long getTotals() {
        return totals;
    }

    /**
     * 获取 ipList
     *
     * @return the ipList
     */
    public List<IpSegment> getIpList() {
        return ipList;
    }

    /**
     * 获取 indexList
     *
     * @return the indexList
     */
    public List<Long> getIndexList() {
        return indexList;
    }

}
