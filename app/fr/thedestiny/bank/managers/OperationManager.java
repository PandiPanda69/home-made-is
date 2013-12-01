package fr.thedestiny.bank.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.thedestiny.bank.models.Operation;

public class OperationManager {

	/**
	 * Associe les opérations avec un type d'opération et compte, pour chaque nom d'opération, combien
	 * de fois un type lui a été associé.
	 * @return
	 */
	public static Map<String, Map<Integer, Integer>> getOperationNameCountByType() {
		List<Operation> allOperations = new ArrayList<Operation>(); //.findAll();

		Map<String, Map<Integer, Integer>> counter = new HashMap<String, Map<Integer, Integer>>();
		Map<Integer, Integer> countMap = null;
		Integer typeId = null;
		Integer counterValue = null;
		String currentNom = null;
		for (Operation current : allOperations) {

			currentNom = current.getNom().toUpperCase();
			if (counter.containsKey(currentNom) == false) {
				countMap = new HashMap<Integer, Integer>();
				counter.put(currentNom, countMap);
			} else {
				countMap = counter.get(currentNom);
			}

			typeId = current.getType() != null ? current.getType().getId() : -1;
			if (countMap.containsKey(typeId) == false) {
				counterValue = new Integer(1);
			} else {
				counterValue = countMap.get(typeId) + 1;
			}

			countMap.put(typeId, counterValue);
		}

		return counter;
	}
}
