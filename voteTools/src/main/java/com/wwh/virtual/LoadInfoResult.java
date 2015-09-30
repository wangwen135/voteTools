package com.wwh.virtual;

import java.util.Map;

/**
 * <pre>
 * 
 * </pre>
 *
 * @author wwh
 * @date 2015年9月28日 上午11:44:12
 *
 */
public class LoadInfoResult {
    private String contentid;
    private String title;
    private String url;

    private Map<String, CandidateEntity> option;

    @Override
    public String toString() {
        return "LoadInfoResult [contentid=" + contentid + ", title=" + title + ", url=" + url + ", option=" + option + "]";
    }

    /**
     * 获取 contentid
     *
     * @return the contentid
     */
    public String getContentid() {
        return contentid;
    }

    /**
     * 设置 contentid
     *
     * @param contentid
     *            the contentid to set
     */
    public void setContentid(String contentid) {
        this.contentid = contentid;
    }

    /**
     * 获取 title
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置 title
     *
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取 url
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置 url
     *
     * @param url
     *            the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取 option
     *
     * @return the option
     */
    public Map<String, CandidateEntity> getOption() {
        return option;
    }

    /**
     * 设置 option
     *
     * @param option
     *            the option to set
     */
    public void setOption(Map<String, CandidateEntity> option) {
        this.option = option;
    }

}
