package com.jiz.jiz_commons.uid_gen;

public class JizIDGenReslut {
	private long code;
	private JizIDGenStatus status;
	
	public long getCode() {
		return code;
	}

	public void setCode(long code) {
		this.code = code;
	}

	public JizIDGenStatus getStatus() {
		return status;
	}

	public void setStatus(JizIDGenStatus status) {
		this.status = status;
	}

	public JizIDGenReslut(long code, JizIDGenStatus status) {
		super();
		this.code = code;
		this.status = status;
	}
}
