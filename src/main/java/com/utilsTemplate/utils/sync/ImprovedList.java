package com.utilsTemplate.utils.sync;

import java.util.ArrayList;

public class ImprovedList<T> extends ArrayList<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	@Override
	public synchronized boolean add(String e) {
		System.out.println("===================");
		if(super.contains(e)){
			return super.add(e);
		}
		return Boolean.FALSE;
	}
}
