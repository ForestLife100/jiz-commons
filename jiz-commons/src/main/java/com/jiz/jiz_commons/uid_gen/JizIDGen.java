package com.jiz.jiz_commons.uid_gen;

public interface JizIDGen {
	public JizIDGenReslut nextId(String key) throws Exception;

	public JizIDGenStatus initGenerator();
}
