package com.formation;

import org.springframework.batch.item.ItemProcessor;

public class UpperCaseProcessor implements ItemProcessor<String, String>{
	
	// recoit les données du itemReader, les transforme ou pas et les renvoie au writer
	@Override
	public String process(String item) throws Exception {
		String result = item.toUpperCase();
		System.out.println("Processed : "+result);
		return result;
	}

}
