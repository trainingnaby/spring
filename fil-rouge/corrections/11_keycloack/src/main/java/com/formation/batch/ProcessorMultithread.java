package com.formation.batch;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.formation.domain.Duplicata;

@Component
public class ProcessorMultithread implements ItemProcessor<Duplicata, Duplicata>{

	@Override
	public Duplicata process(Duplicata duplicata) throws Exception {
		System.out.println("Processing duplicata : "+duplicata.toString()); 
		return duplicata;
	}

}
