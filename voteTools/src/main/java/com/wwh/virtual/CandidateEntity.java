package com.wwh.virtual;

public class CandidateEntity {
    private boolean selected;
    private String optionid;
    private String contentid;
    private String name;
    private String link;
    private String thumb;
    private String sort;
    private Integer votes;
    private String db_votes;
    private Double percent;

    @Override
    public String toString() {
        return "ResultJSONEntity [optionid=" + optionid + ", contentid=" + contentid + ", name=" + name + ", link=" + link + ", thumb=" + thumb + ", sort="
                + sort + ", votes=" + votes + ", db_votes=" + db_votes + ", percent=" + percent + "]";
    }

    /**
     * 获取 selected
     *
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * 设置 selected
     *
     * @param selected
     *            the selected to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getOptionid() {
        return optionid;
    }

    public void setOptionid(String optionid) {
        this.optionid = optionid;
    }

    public String getContentid() {
        return contentid;
    }

    public void setContentid(String contentid) {
        this.contentid = contentid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    /**
     * 获取 votes
     *
     * @return the votes
     */
    public Integer getVotes() {
        return votes;
    }

    /**
     * 设置 votes
     *
     * @param votes
     *            the votes to set
     */
    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    public String getDb_votes() {
        return db_votes;
    }

    public void setDb_votes(String db_votes) {
        this.db_votes = db_votes;
    }

    public Double getPercent() {
        return percent;
    }

    public void setPercent(Double percent) {
        this.percent = percent;
    }

}
