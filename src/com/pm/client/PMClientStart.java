// Copyright (c) 2015 D1SM.net

package com.pm.client;
import com.pm.client.ui.PMClientUI;

public class PMClientStart {

	public static void main(final String[] args) throws Exception {
		boolean min=false;
		for(String s:args){
			if(s.equals("-min")){
				min=true;
			}
		}
		new PMClientUI(min);
	}

}
