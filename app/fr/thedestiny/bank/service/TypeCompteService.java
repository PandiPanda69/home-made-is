package fr.thedestiny.bank.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.thedestiny.bank.dao.TauxInteretDao;
import fr.thedestiny.bank.dao.TypeCompteDao;
import fr.thedestiny.bank.dto.TypeCompteDto;
import fr.thedestiny.bank.models.TauxInteret;
import fr.thedestiny.bank.models.TypeCompte;
import fr.thedestiny.bank.service.exception.TypeCompteInUseException;
import fr.thedestiny.global.service.AbstractService;
import fr.thedestiny.global.service.InTransactionFunction;

@Service
public class TypeCompteService extends AbstractService {

	@Autowired
	private TypeCompteDao typeDao;

	@Autowired
	private TauxInteretDao interetDao;

	private TypeCompteService() {
		super("bank");
	}

	public List<TypeCompteDto> findAllTypes() {

		List<TypeCompte> types = typeDao.findAll(null);
		List<TypeCompteDto> result = new ArrayList<>(types.size());

		for (TypeCompte current : types) {
			result.add(new TypeCompteDto(current));
		}

		return result;
	}

	public TypeCompteDto saveTypeCompte(final TypeCompte type) {

		return this.processInTransaction(new InTransactionFunction<TypeCompteDto>() {

			@Override
			public TypeCompteDto doWork(EntityManager em) {
                if(type.getType().equals("SAVING")) {
    				for (TauxInteret current : type.getTaux()) {
	    				current.setType(type);
		    		}

			    	interetDao.purge(em, type.getId());
                } else if(!type.getTaux().isEmpty()) {
			    	interetDao.purge(em, type.getId());
                    type.getTaux().clear();
                }
				return new TypeCompteDto(typeDao.save(em, type));
			}
		});
	}

	public boolean deleteTypeCompte(final int typeId) throws TypeCompteInUseException {

		return this.processInTransaction(new InTransactionFunction<Boolean>() {

			@Override
			public Boolean doWork(EntityManager em) throws TypeCompteInUseException {

				// Check type isn't in use.
				boolean isUsed = typeDao.isTypeInUse(typeId);
				if (isUsed) {
					throw new TypeCompteInUseException();
				}

				return typeDao.delete(em, typeId);
			}
		});
	}

}
