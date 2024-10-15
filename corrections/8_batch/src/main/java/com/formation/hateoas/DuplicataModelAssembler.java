package com.formation.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.formation.domain.Duplicata;
import com.formation.exception.DuplicataException;
import com.formation.web.DuplicataControlleur;

@Component
public class DuplicataModelAssembler implements RepresentationModelAssembler<Duplicata, EntityModel<Duplicata>> {

	@Override
	public EntityModel<Duplicata> toModel(Duplicata duplicata) {
		
		try {
			return EntityModel.of(duplicata,
					linkTo(methodOn(DuplicataControlleur.class).getDuplicatas(duplicata.getId())).withSelfRel(),
					linkTo(methodOn(DuplicataControlleur.class).listDuplicatas()).withRel("duplicas"));
		} catch (DuplicataException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public CollectionModel<EntityModel<Duplicata>> toCollectionModel(Iterable<? extends Duplicata> entities) {
		return RepresentationModelAssembler.super.toCollectionModel(entities)
			.add(linkTo(methodOn(DuplicataControlleur.class).listDuplicatas()).withSelfRel());
	}

}
