package com.wwh.virtual;

public class ResultJSONEntity {
	private String optionid;
	private String contentid;
	private String name;
	private String link;
	private String thumb;
	private String sort;
	private String votes;
	private String db_votes;
	private Double percent;

	@Override
	public String toString() {
		return "ResultJSONEntity [optionid=" + optionid + ", contentid="
				+ contentid + ", name=" + name + ", link=" + link + ", thumb="
				+ thumb + ", sort=" + sort + ", votes=" + votes + ", db_votes="
				+ db_votes + ", percent=" + percent + "]";
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

	public String getVotes() {
		return votes;
	}

	public void setVotes(String votes) {
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
