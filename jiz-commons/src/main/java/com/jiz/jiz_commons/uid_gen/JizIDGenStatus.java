package com.jiz.jiz_commons.uid_gen;

import org.apache.commons.lang3.ObjectUtils;

public enum JizIDGenStatus {
    success(0),
    err_init(1),
    err_gen(2),
    err_excep(3);
    
	private int id;

	public int getId() {
		return id;
	}

	private JizIDGenStatus(int id) {
        this.id = id;
    }
	
	public static JizIDGenStatus idOf(Integer value) {
		if(ObjectUtils.isEmpty(value)) {
			return err_gen;
		}
		
		for(JizIDGenStatus item : JizIDGenStatus.values()){
			if(item.getId() == value){
				return item;
			}
		}
		
		return err_excep;
    }
}
