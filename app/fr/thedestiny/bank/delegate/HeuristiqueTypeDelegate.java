package fr.thedestiny.bank.delegate;

import java.util.Map;

import fr.thedestiny.bank.dao.HeuristiqueTypeDao;
import fr.thedestiny.bank.managers.OperationManager;
import fr.thedestiny.bank.models.HeuristiqueType;

/**
 * Delegate pour le controleur des heuristiques sur les types
 * @author Sébastien
 */
public class HeuristiqueTypeDelegate {

	public static void compute() {

		Map<String, Map<Integer, Integer>> counter = OperationManager.getOperationNameCountByType();

		HeuristiqueTypeDao.deleteAll();

		Map<Integer, Integer> countMap = null;
		HeuristiqueType type = null;
		Integer higherCpt = null;
		Integer sum = null;
		for (String current : counter.keySet()) {
			type = new HeuristiqueType();
			higherCpt = -1;
			sum = 0;

			type.setNom(current);

			countMap = counter.get(current);
			for (Integer id : countMap.keySet()) {

				if (countMap.get(id) > higherCpt) {
					higherCpt = countMap.get(id);
					// TODO : Optimiser !
					//					type.setType(OperationTypeDao.findById(id));
				}

				sum += countMap.get(id);
			}

			type.setThreshold((double) higherCpt / (double) sum);
			HeuristiqueTypeDao.save(type);
		}
	}
}
