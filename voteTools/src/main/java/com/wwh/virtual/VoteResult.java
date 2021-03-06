package com.wwh.virtual;

import java.util.Map;

public class VoteResult {
	private boolean state;
	private String error;

	private Map<String, CandidateEntity> data;

	@Override
	public String toString() {
		return "ResultJSON [state=" + state + ", error=" + error + ", data="
				+ data + "]";
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Map<String, CandidateEntity> getData() {
		return data;
	}

	public void setData(Map<String, CandidateEntity> data) {
		this.data = data;
	}

}
