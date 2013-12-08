package fr.thedestiny.bank.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import fr.thedestiny.bank.dao.TauxInteretDao;
import fr.thedestiny.bank.dao.TypeCompteDao;
import fr.thedestiny.bank.dto.TypeCompteDto;
import fr.thedestiny.bank.models.TauxInteret;
import fr.thedestiny.bank.models.TypeCompte;
import fr.thedestiny.bank.service.exception.TypeCompteInUseException;
import fr.thedestiny.global.dto.GenericModelDto;
import fr.thedestiny.global.service.AbstractService;
import fr.thedestiny.global.service.InTransactionAction;

public class TypeCompteService extends AbstractService {

	private static TypeCompteService thisInstance = new TypeCompteService();
	private TypeCompteDao typeDao;
	private TauxInteretDao interetDao;

	public static TypeCompteService getInstance() {
		return thisInstance;
	}

	private TypeCompteService() {
		super("bank");
		this.typeDao = new TypeCompteDao("bank");
		this.interetDao = new TauxInteretDao("bank");
	}

	public List<TypeCompteDto> findAllTypes() {

		List<TypeCompte> types = typeDao.findAll(null);
		List<TypeCompteDto> result = new ArrayList<TypeCompteDto>(types.size());

		for (TypeCompte current : types) {
			result.add(new TypeCompteDto(current));
		}

		return result;
	}

	public TypeCompteDto saveTypeCompte(final GenericModelDto<TypeCompte> dto) throws Exception {

		return this.processInTransaction(new InTransactionAction() {

			@SuppressWarnings("unchecked")
			@Override
			public TypeCompteDto doWork(EntityManager em) throws Exception {
				TypeCompte type = dto.asObject();

				for (TauxInteret current : type.getTaux()) {
					current.setType(type);
				}

				interetDao.purge(em, type.getId());
				type = typeDao.save(em, type);

				return new TypeCompteDto(type);
			}
		});
	}

	public void deleteTypeCompte(final Integer typeId) throws Exception {

		this.processInTransaction(new InTransactionAction() {

			@SuppressWarnings("unchecked")
			@Override
			public TypeCompteDto doWork(EntityManager em) throws Exception {

				// Check type isn't in use.
				boolean isUsed = typeDao.isTypeInUse(typeId);
				if (isUsed) {
					throw new TypeCompteInUseException();
				}

				typeDao.delete(em, typeId);

				return null;
			}
		});
	}

}
