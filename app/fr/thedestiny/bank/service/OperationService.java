package fr.thedestiny.bank.service;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import play.Logger;
import fr.thedestiny.Constants;
import fr.thedestiny.bank.dao.CompteDao;
import fr.thedestiny.bank.dao.MotifOperationDao;
import fr.thedestiny.bank.dao.OperationDao;
import fr.thedestiny.bank.dao.RepetitionDao;
import fr.thedestiny.bank.dto.OperationDto;
import fr.thedestiny.bank.dto.SearchResultDto;
import fr.thedestiny.bank.models.Compte;
import fr.thedestiny.bank.models.MoisAnnee;
import fr.thedestiny.bank.models.MotifOperation;
import fr.thedestiny.bank.models.Operation;
import fr.thedestiny.bank.models.Repetition;
import fr.thedestiny.global.dao.SolrSearchDao;
import fr.thedestiny.global.exception.CoreNotFoundException;
import fr.thedestiny.global.service.AbstractService;
import fr.thedestiny.global.service.InTransactionFunction;

@Service
public class OperationService extends AbstractService {

	private final static String DATE_PATTERN = "[DATE]";
	private final static String REASON_PATTERN = "[RAISON]";

	@Autowired
	private OperationDao operationDao;

	@Autowired
	private RepetitionDao repetitionDao;

	@Autowired
	private CompteDao compteDao;

	@Autowired
	private MotifOperationDao motifDao;

	@Autowired
	private SolrSearchDao searchDao;

	private OperationService() {
		super("bank");
	}

	public List<OperationDto> findAllOperationsForMonth(final int userId, final int accountId, final int monthId) {

		List<Repetition> repetitions = repetitionDao.findAll();
		List<Operation> operations = operationDao.findAll(null, accountId, monthId);
		List<OperationDto> result = new ArrayList<>(operations.size());

		if (operations.size() > 0) {
			if (!operations.get(0).getCompte().getOwner().equals(userId)) {
				throw new SecurityException();
			}

			for (Operation current : operations) {
				OperationDto dto = new OperationDto(current);
				dto.setRepetee(isRepetee(current, repetitions));
				result.add(dto);
			}
		}

		return result;
	}

	public OperationDto addOperation(final OperationDto dto, final int userId, final int accountId, final int moisId) {

		return this.processInTransaction(new InTransactionFunction<OperationDto>() {

			@Override
			public OperationDto doWork(EntityManager em) {

				Operation op = dto.toBO();

				// Vérification que l'utilisateur possède bien le compte
				Compte compte = compteDao.findById(em, accountId);
				if (compte == null || compte.getOwner().equals(userId) == false) {
					throw new SecurityException();
				}

				// Récupération du mois via le compte
				MoisAnnee mois = null;
				for (MoisAnnee current : compte.getMois()) {
					if (current.getId().equals(moisId)) {
						mois = current;
						break;
					}
				}

				if (mois == null) {
					throw new InvalidParameterException();
				}

				// Autocomplétion de la date / raison
				applyOperationPatterns(op, userId);

				// Si une date était forcée, on l'applique
				GregorianCalendar date = getSubmittedDate(dto.getDate());
				if (date != null) {
					op.setDate(date.getTime());
				}

				// Affectation avant sauvegarde
				op.setCompte(compte);
				op.setMois(mois);

				// MaJ du compte (date de dernière modif & solde) & persistance de l'operation
				op = operationDao.save(em, op);

				Double newSolde = compte.getSolde() + op.getMontant();
				compte.setSolde(newSolde);

				compteDao.save(em, compte);

				return convertBean(op);
			}
		});
	}

	public OperationDto updateOperation(final OperationDto dto, final int userId, final int idAccount, final int idMois) {

		return this.processInTransaction(new InTransactionFunction<OperationDto>() {

			@Override
			public OperationDto doWork(EntityManager em) throws Exception {

				Operation op = dto.toBO();

				if (op.getId() == null) {
					throw new Exception("Cannot have empty id");
				}

				// Vérification que l'utilisateur possède bien le compte
				Compte compte = compteDao.findById(em, idAccount);
				if (compte == null || compte.getOwner().equals(userId) == false) {
					throw new SecurityException();
				}

				// Récupération du mois via le compte
				MoisAnnee mois = null;
				for (MoisAnnee current : compte.getMois()) {
					if (current.getId().equals(idMois)) {
						mois = current;
						break;
					}
				}

				if (mois == null) {
					throw new InvalidParameterException();
				}

				applyOperationPatterns(op, userId);

				// Si une date était forcée, on l'applique
				GregorianCalendar date = getSubmittedDate(dto.getDate());
				if (date != null) {
					op.setDate(date.getTime());
				}

				Operation previousOp = operationDao.findById(em, op.getId());
				Double newSolde = compte.getSolde() - previousOp.getMontant();

				op = operationDao.save(em, op);

				newSolde += op.getMontant();
				compte.setSolde(newSolde);

				compteDao.save(em, compte);

				return convertBean(op);
			}
		});
	}

	public boolean deleteOperation(final int userId, final int idAccount, final int idMois, final int id) {

		return this.processInTransaction(new InTransactionFunction<Boolean>() {

			@Override
			public Boolean doWork(EntityManager em) {

				// Vérification que l'utilisateur possède bien le compte
				Compte compte = compteDao.findById(em, idAccount);
				if (compte == null || compte.getOwner().equals(userId) == false) {
					throw new SecurityException();
				}

				// Récupération du mois via le compte
				MoisAnnee mois = null;
				for (MoisAnnee current : compte.getMois()) {
					if (current.getId().equals(idMois)) {
						mois = current;
						break;
					}
				}

				if (mois == null) {
					throw new InvalidParameterException();
				}

				Operation previousOp = operationDao.findById(em, id);

				// Suppression de l'opération et MaJ du compte
				boolean isDeleted = operationDao.delete(em, id);

				Double newSolde = compte.getSolde() - previousOp.getMontant();
				compte.setSolde(newSolde);

				Compte mergedAccount = compteDao.save(em, compte);
				isDeleted &= mergedAccount.getSolde().equals(newSolde);

				return isDeleted;
			}
		});
	}

	private void applyOperationPatterns(final Operation op, final int userId) {

		if (op.getNomComplet() == null || op.getNomComplet().isEmpty()) {
			return;
		}

		String nomComplet = op.getNomComplet();
		List<MotifOperation> motifs = motifDao.findAll(userId);

		Map<String, String> matchedContent = new HashMap<>();
		boolean motifMatched = false;
		for (MotifOperation current : motifs) {

			// Regarde si la raison est avant la date ou inverse
			String motif = current.getMotif();

			Integer indexOfDate = motif.indexOf(DATE_PATTERN);
			Integer indexOfReason = motif.indexOf(REASON_PATTERN);

			// S'il n'y a pas de variables présentes, skip.
			if ((indexOfDate + indexOfReason) < 0) {
				Logger.warn("Pattern #" + current.getId() + " has no variable.");
				continue;
			}

			// Remplacement des mots clefs par des wildcards
			motif = motif.replaceFirst(DATE_PATTERN.replace("[", "\\[").replaceAll("]", "\\]"), "(.*?)").replaceFirst(REASON_PATTERN.replace("[", "\\[").replaceAll("]", "\\]"), "(.*?)");

			Pattern pattern = Pattern.compile(motif);
			Matcher matcher = pattern.matcher(nomComplet);

			if (matcher.find()) {

				String A = matcher.group(1);
				String B = matcher.group(2);

				if (indexOfDate == -1) {
					matchedContent.put(REASON_PATTERN, A);
				} else if (indexOfReason == -1) {
					matchedContent.put(DATE_PATTERN, A);
				} else if (indexOfDate < indexOfReason) {
					matchedContent.put(DATE_PATTERN, A);
					matchedContent.put(REASON_PATTERN, B);
				} else {
					matchedContent.put(DATE_PATTERN, B);
					matchedContent.put(REASON_PATTERN, A);
				}

				motifMatched = true;
				break;
			}
		}

		if (motifMatched) {
			op.setRaison(matchedContent.get(REASON_PATTERN));

			String date = matchedContent.get(DATE_PATTERN);
			if (date != null) {
				String[] buf = new String[3];
				if (date.matches("..\\...\\...")) {
					buf = date.split("\\.");
				} else if (date.matches("[0-9][0-9][0-9][0-9][0-9][0-9]")) {
					buf[0] = date.substring(0, 2);
					buf[1] = date.substring(2, 4);
					buf[2] = date.substring(4);
				}

				if (buf[0] != null) {
					GregorianCalendar calendar = new GregorianCalendar(Integer.valueOf("20" + buf[2]), Integer.valueOf(buf[1]) - 1, Integer.valueOf(buf[0]));
					op.setDate(calendar.getTime());
				}
			}
		}
	}

	private GregorianCalendar getSubmittedDate(final String date) {

		if (date == null || date.length() < 10 || !date.matches("..\\/..\\/....")) {
			return null;
		}

		String[] buf = date.split("\\/");
		return new GregorianCalendar(Integer.valueOf(buf[2]), Integer.valueOf(buf[1]) - 1, Integer.valueOf(buf[0]));
	}

	public List<OperationDto> getCurrentYearOperation(final int accountId) {

		List<Repetition> repetitions = repetitionDao.findAll();
		List<Operation> operations = operationDao.findOperationOfYear(accountId, Calendar.getInstance().get(Calendar.YEAR));
		List<OperationDto> dto = new ArrayList<>(operations.size());

		for (Operation current : operations) {
			OperationDto op = new OperationDto(current);
			op.setRepetee(isRepetee(current, repetitions));
			dto.add(op);
		}

		return dto;
	}

	public List<SearchResultDto> findOperations(final String value, final int userId) throws SolrServerException {

		Map<String, String> criteria = new HashMap<>();
		criteria.put("text", value);

		SolrDocumentList documents;
		try {
			documents = searchDao.search(Constants.BANK_CONTEXT, criteria);
		} catch (CoreNotFoundException ex) {
			Logger.error("Code fault.", ex);
			return null;
		}

		if (documents.isEmpty()) {
			return Collections.emptyList();
		}

		List<Integer> ids = new ArrayList<>();
		for (final SolrDocument document : documents) {
			ids.add(Integer.valueOf(document.get("id").toString()));
		}

		List<Operation> operations = operationDao.findUserOperationsById(ids, userId);

		List<SearchResultDto> dto = new ArrayList<>(operations.size());
		for (final Operation current : operations) {
			dto.add(new SearchResultDto(current));
		}

		return dto;
	}

	public Operation findOperationById(final Integer op, final int userId) {

		if (op == null) {
			throw new IllegalArgumentException();
		}

		Operation operation = operationDao.findById(null, op);
		if (operation.getCompte().getOwner().intValue() != userId) {
			throw new SecurityException("This operation is not owned by this user.");
		}

		return operation;
	}

	private OperationDto convertBean(final Operation op) {
		List<Repetition> repetitions = repetitionDao.findAll();

		OperationDto dto = new OperationDto(op);
		dto.setRepetee(isRepetee(op, repetitions));
		return dto;
	}

	private boolean isRepetee(final Operation op, final List<Repetition> repetitions) {

		for (Repetition current : repetitions) {
			if (current.getNom().equals(op.getNom()) && current.getType().getId().equals(op.getType().getId())) {
				return true;
			}
		}

		return false;
	}
}
