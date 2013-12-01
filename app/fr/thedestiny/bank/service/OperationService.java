package fr.thedestiny.bank.service;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;

import org.hibernate.MappingException;

import play.Logger;
import fr.thedestiny.bank.dao.CompteDao;
import fr.thedestiny.bank.dao.MotifOperationDao;
import fr.thedestiny.bank.dao.OperationDao;
import fr.thedestiny.bank.dto.OperationDto;
import fr.thedestiny.bank.models.Compte;
import fr.thedestiny.bank.models.MoisAnnee;
import fr.thedestiny.bank.models.MotifOperation;
import fr.thedestiny.bank.models.Operation;
import fr.thedestiny.global.service.AbstractService;
import fr.thedestiny.global.service.InTransactionAction;

public class OperationService extends AbstractService {

	private static OperationService thisInstance = new OperationService();

	private final static String DATE_PATTERN = "[DATE]";
	private final static String REASON_PATTERN = "[RAISON]";

	private OperationDao operationDao;
	private CompteDao compteDao;
	private MotifOperationDao motifDao;

	public static OperationService getInstance() {
		return thisInstance;
	}

	private OperationService() {
		super("bank");
		operationDao = new OperationDao("bank");
		compteDao = new CompteDao("bank");
		motifDao = new MotifOperationDao("bank");
	}

	public List<OperationDto> findAllOperationsForMonth(Integer userId, Integer accountId, Integer monthId) {

		List<Operation> operations = operationDao.findAll(null, accountId, monthId);
		List<OperationDto> result = new ArrayList<OperationDto>(operations.size());

		if (operations.size() > 0) {
			if (!operations.get(0).getCompte().getOwner().equals(userId)) {
				throw new SecurityException();
			}

			for (Operation current : operations) {
				OperationDto dto = new OperationDto(current);
				result.add(dto);
			}
		}

		return result;
	}

	public OperationDto addOperation(final OperationDto dto, final Integer userId, final Integer accountId, final Integer moisId) throws Exception {

		return this.processInTransaction(new InTransactionAction() {

			@SuppressWarnings("unchecked")
			@Override
			public OperationDto doWork(EntityManager em) throws Exception {

				if (dto == null) {
					throw new MappingException("Cannot map dto with model.");
				}

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

				return new OperationDto(op);
			}

		});
	}

	public OperationDto updateOperation(final OperationDto dto, final Integer userId, final Integer idAccount, final Integer idMois) throws Exception {

		return this.processInTransaction(new InTransactionAction() {

			@SuppressWarnings("unchecked")
			@Override
			public OperationDto doWork(EntityManager em) throws Exception {

				if (dto == null) {
					throw new MappingException("Cannot map dto with model.");
				}

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

				return new OperationDto(op);
			}
		});
	}

	public void deleteOperation(final Integer userId, final Integer idAccount, final Integer idMois, final Integer id) throws Exception {

		this.processInTransaction(new InTransactionAction() {

			@SuppressWarnings("unchecked")
			@Override
			public Object doWork(EntityManager em) throws Exception {

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
				operationDao.delete(em, id);

				Double newSolde = compte.getSolde() - previousOp.getMontant();
				compte.setSolde(newSolde);

				compteDao.save(em, compte);

				return null;
			}
		});
	}

	private void applyOperationPatterns(Operation op, Integer userId) {

		if (op.getNomComplet() == null || op.getNomComplet().length() == 0) {
			return;
		}

		String nomComplet = op.getNomComplet();
		List<MotifOperation> motifs = motifDao.findAll(userId);

		Map<String, String> matchedContent = new HashMap<String, String>();
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

	private GregorianCalendar getSubmittedDate(String date) {

		if (date == null || date.length() < 10 || !date.matches("..\\/..\\/....")) {
			return null;
		}

		String[] buf = date.split("\\/");
		return new GregorianCalendar(Integer.valueOf(buf[2]), Integer.valueOf(buf[1]) - 1, Integer.valueOf(buf[0]));
	}
}
