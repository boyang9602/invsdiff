package ca.concordia.apr.invsdiff.comparasion;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ca.concordia.apr.invsdiff.ppt.*;

public class Comparator {
	public Map<String, Result> compareParent(ParentPpt... parentPpts) {
		Map<String, Result> resultMap = new HashMap<String, Result>();
		resultMap.put(parentPpts[0].getRawName(), compare(parentPpts));
		Set<String> commonChildren = new HashSet<String>(parentPpts[1].getChildrenNameSet());
		for (int i = 1; i < parentPpts.length; i++) {
			commonChildren.retainAll(parentPpts[i].getChildrenNameSet());
		}
		
		return resultMap;
	}
	public Result compare(Ppt...ppts) {
		String name = ppts[0].getRawName();
		Set<String> commonInvs = new HashSet<String>(ppts[0].getInvs());
		Result result = new Result(ppts.length);
		for (int i = 1; i < ppts.length; i++) {
			if (ppts[i] == null) {
				result.setNull(i);
			} else {
				if (!name.equals(ppts[i].getRawName())) {
					throw new RuntimeException("uncomparable ppts:" + name + " - " + ppts[i].getRawName());
				}
				commonInvs.retainAll(ppts[i].getInvs());
			}
		}
		for (int i = 0; i < ppts.length; i++) {
			Set<String> tmp = new HashSet<String>(ppts[i].getInvs());
			tmp.removeAll(commonInvs);
			result.set(i, tmp);
		}
		return result;
	}
}
