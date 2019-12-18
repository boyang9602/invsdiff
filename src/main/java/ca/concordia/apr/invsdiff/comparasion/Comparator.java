package ca.concordia.apr.invsdiff.comparasion;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ca.concordia.apr.invsdiff.Ppt;

public class Comparator {
	public static Result compare(String pptName, Map<String, Ppt> pptMap) {
		Result result = new Result(pptName);
		Set<String> common = null;
		boolean hasNull = false;
		for (String key : pptMap.keySet()) {
			Ppt ppt = pptMap.get(key);
			if (ppt == null) {
				hasNull = true;
				break;
			}
			if (common == null) {
				common = new HashSet<String>(ppt.getInvs());
			} else {
				common.retainAll(new HashSet<String>(ppt.getInvs()));
			}
		}
		if (hasNull) {
			result.setCommon(null);
		} else {
			result.setCommon(common);
		}
		for (String key : pptMap.keySet()) {
			Ppt ppt = pptMap.get(key);
			Set<String> dist = ppt == null ? null : new HashSet<String>(ppt.getInvs());
			if (hasNull) {
				result.set(key, dist);
			} else {
				dist.removeAll(common);
				result.set(key, dist);
			}
		}
		return result;
	}
}
