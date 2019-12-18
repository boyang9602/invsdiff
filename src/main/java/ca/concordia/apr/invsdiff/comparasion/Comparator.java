package ca.concordia.apr.invsdiff.comparasion;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import ca.concordia.apr.invsdiff.InvsFile;
import ca.concordia.apr.invsdiff.ppt.*;

public class Comparator {
	public static Map<String, Map<String, Result>> compareInvsFile(InvsFile...invsFiles) {
		Set<String> finished = new TreeSet<String>();
		Map<String, Map<String, Result>> classPptResultMap = new HashMap<String, Map<String, Result>>();
		for (int i = 0; i < invsFiles.length; i++) {
			for (String name : invsFiles[i].getClassPptKeys()) {
				ParentPpt[] ppts = new ParentPpt[invsFiles.length];
				for (int j = 0; j < ppts.length; j++) {
					ppts[j] = invsFiles[i].getClassPpt(name);
				}
				classPptResultMap.put(name, compareParent(ppts));
				finished.add(name);
			}
			for (String name : invsFiles[i].getObjectPptKeys()) {
				ParentPpt[] ppts = new ParentPpt[invsFiles.length];
				for (int j = 0; j < ppts.length; j++) {
					ppts[j] = invsFiles[i].getObjectPpt(name);
				}
				classPptResultMap.put(name, compareParent(ppts));
				finished.add(name);
			}
		}
		return classPptResultMap;
	}
	public static Map<String, Result> compareParent(ParentPpt... parentPpts) {
		Map<String, Result> resultMap = new HashMap<String, Result>();
		resultMap.put(parentPpts[0].getRawName(), compare(parentPpts));
		Set<String> commonChildren = new HashSet<String>(parentPpts[1].getChildrenNameSet());
		for (int i = 1; i < parentPpts.length; i++) {
			commonChildren.retainAll(parentPpts[i].getChildrenNameSet());
		}
		for (String childName : commonChildren) {
			Ppt[] ppts = new Ppt[parentPpts.length];
			for (int i = 0; i < ppts.length; i++) {
				ppts[i] = parentPpts[i].getChild(childName);
			}
			resultMap.put(childName, compare(ppts));
		}
		return resultMap;
	}
	public static Result compare(Ppt...ppts) {
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
