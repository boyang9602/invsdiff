package ca.concordia.apr.invsdiff.diff;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONArray;

import ca.concordia.apr.invsdiff.InvsFile;
import ca.concordia.apr.invsdiff.Ppt;
import ca.concordia.apr.invsdiff.utils.FileUtils;

public class DiffByType {
	private InvsFile if1;
	private InvsFile if2;

	private List<Ppt> classPptOnly1 = new LinkedList<Ppt>();
	private List<Ppt> classPptOnly2 = new LinkedList<Ppt>();

	private List<Ppt> objectPptOnly1 = new LinkedList<Ppt>();
	private List<Ppt> objectPptOnly2 = new LinkedList<Ppt>();

	private Map<String, List<Ppt>> methodPptOnly1 = new HashMap<String, List<Ppt>>();
	private Map<String, List<Ppt>> methodPptOnly2 = new HashMap<String, List<Ppt>>();

	private List<Ppt> classInvOnly1 = new LinkedList<Ppt>();
	private List<Ppt> classInvOnly2 = new LinkedList<Ppt>();

	private List<Ppt> objectInvOnly1 = new LinkedList<Ppt>();
	private List<Ppt> objectInvOnly2 = new LinkedList<Ppt>();

	private Map<String, List<Ppt>> methodInvOnly1 = new HashMap<String, List<Ppt>>();
	private Map<String, List<Ppt>> methodInvOnly2 = new HashMap<String, List<Ppt>>();

	public DiffByType (InvsFile if1, InvsFile if2) {
		this.if1 = if1;
		this.if2 = if2;
	}

	public void execute() {
		compareClass();
		compareObject();
		compareMethod();
	}

	private void compareClass() {
		compare(if1.getClassPpts(), if2.getClassPpts(), classPptOnly1, classPptOnly2, classInvOnly1, classInvOnly2);
	}

	private void compareObject() {
		compare(if1.getObjectPpts(), if2.getObjectPpts(), objectPptOnly1, objectPptOnly2, objectInvOnly1, objectInvOnly2);
	}

	private void compareMethod() {
		Map<String, Ppt> mEnter1 = if1.getEnterPpts();
		Map<String, Ppt> mExit1 = if1.getExitPpts();
		Map<String, List<Ppt>> mExitnn1 = if1.getExitnnPpts();
		
		Map<String, Ppt> mEnter2 = if2.getEnterPpts();
		Map<String, Ppt> mExit2 = if2.getExitPpts();
		Map<String, List<Ppt>> mExitnn2 = if2.getExitnnPpts();
		
		Set<String> mEntrySet1 = new HashSet<String>(mEnter1.keySet());
		mEntrySet1.retainAll(mEnter2.keySet());
		Set<String> commonMethods = mEntrySet1;
		for (String method : commonMethods) {
			Ppt enterPpt12 = mEnter1.get(method).diff(mEnter2.get(method));
			Ppt enterPpt21 = mEnter2.get(method).diff(mEnter1.get(method));

			Ppt exitPpt12 = mExit1.get(method).diff(mExit2.get(method));
			Ppt exitPpt21 = mExit2.get(method).diff(mExit1.get(method));

			List<Ppt> listExitnn1 = new LinkedList<Ppt>();
			List<Ppt> listExitnn2 = new LinkedList<Ppt>();
			List<Ppt> originListExitnn1 = mExitnn1.get(method);
			List<Ppt> originListExitnn2 = mExitnn2.get(method);
			if (originListExitnn1 == null && originListExitnn2 == null) {
			} else if (originListExitnn1 == null && originListExitnn2 != null) {
				listExitnn2.addAll(originListExitnn2);
			} else if (originListExitnn1 != null && originListExitnn2 == null) {
				listExitnn1.addAll(originListExitnn1);
			} else {
				Iterator<Ppt> it1 = new LinkedList<Ppt>(originListExitnn1).iterator();
				Iterator<Ppt> it2 = new LinkedList<Ppt>(originListExitnn2).iterator();
				while(it1.hasNext()) {
					Ppt p1 = it1.next();
					boolean found = false;
					while(it2.hasNext()) {
						Ppt p2 = it2.next();
						if (p1.getExitPoint() == p2.getExitPoint()) {
							listExitnn1.add(p1.diff(p2));
							listExitnn2.add(p2.diff(p1));
							it1.remove();
							it2.remove();
							found = true;
							break;
						}
					}
					if (!found) {
						listExitnn1.add(p1);
					}
				}
				listExitnn2.addAll(originListExitnn2);
				while(it2.hasNext()) {
					listExitnn2.add(it2.next());
				}
			}

			if (enterPpt12.isEmpty() && enterPpt21.isEmpty() 
					&& exitPpt12.isEmpty() && exitPpt21.isEmpty() 
					&& checkAllEmpty(listExitnn1, listExitnn2)) {
			} else {
				List<Ppt> l1 = new LinkedList<Ppt>();
				l1.add(enterPpt12);
				l1.add(exitPpt12);
				l1.addAll(listExitnn1);
				methodInvOnly1.put(method, l1);
				List<Ppt> l2 = new LinkedList<Ppt>();
				l2.add(enterPpt21);
				l2.add(exitPpt21);
				l2.addAll(listExitnn2);
				methodInvOnly2.put(method, l2);
			}
		}

		putAllDistinctPpt(mEnter1, mExit1, mExitnn1, commonMethods, methodPptOnly1);
		putAllDistinctPpt(mEnter2, mExit2, mExitnn2, commonMethods, methodPptOnly2);
	}

	private boolean checkAllEmpty(List<Ppt> lenn1, List<Ppt> lenn2) {
		boolean allEmpty = true;
		for (Ppt p1 : lenn1) {
			if(!p1.isEmpty()) {
				allEmpty = false;
			}
		}
		if (allEmpty) {
			for (Ppt p2 : lenn2) {
				if(!p2.isEmpty()) {
					allEmpty = false;
				}
			}
		}
		return allEmpty;
	}

	private void putAllDistinctPpt(Map<String, Ppt> mEnter, Map<String, Ppt> mExit, Map<String, 
			List<Ppt>> mExitnn,	Set<String> commonMethods, Map<String, List<Ppt>> receiver) {
		Set<String> mEnterSet = new HashSet<String>(mEnter.keySet());
		mEnterSet.removeAll(commonMethods);
		for (String method : mEnterSet) {
			Ppt enterPpt = mEnter.get(method);
			Ppt exitPpt = mExit.get(method);
			List<Ppt> exitnnPpts = mExitnn.get(method);
			List<Ppt> l = new LinkedList<Ppt>();
			l.add(enterPpt);
			l.add(exitPpt);
			l.addAll(exitnnPpts);
			receiver.put(method, l);
		}
	}

	private void compare(Map<String, Ppt> m1, Map<String, Ppt> m2, List<Ppt> onlyM1Ppt, List<Ppt> onlyM2Ppt, List<Ppt> onlyM1Inv, List<Ppt> onlyM2Inv) {
		Set<String> pptNames1 = new HashSet<String>(m1.keySet());
		Set<String> pptNames2 = new HashSet<String>(m2.keySet());
		
		pptNames1.retainAll(pptNames2);
		Set<String> commonNames = new HashSet<String>(pptNames1);
		pptNames1 = new HashSet<String>(m1.keySet());
		pptNames1.removeAll(commonNames);
		for (String pptName : pptNames1) {
			onlyM1Ppt.add(m1.get(pptName));
		}
		pptNames2.removeAll(commonNames);
		for (String pptName : pptNames2) {
			onlyM2Ppt.add(m2.get(pptName));
		}
		for (String pptName : commonNames) {
			Ppt p1 = m1.get(pptName);
			Ppt p2 = m2.get(pptName);
			Ppt p12 = p1.diff(p2);
			Ppt p21 = p2.diff(p1);
			if (p12.isEmpty() && p21.isEmpty()) continue;
			
			onlyM1Inv.add(p12);
			onlyM2Inv.add(p21);
		}
	}

	public void writeJSONTo(String folderName) throws IOException {
		for (Ppt ppt : classPptOnly1) {
			FileUtils.writeTo(folderName + "/ppts_only_in_" + if1.getFilename() + "/" + ppt.getRawName(), ppt.toJSON().toString());
		}
		for (Ppt ppt : objectPptOnly1) {
			FileUtils.writeTo(folderName + "/ppts_only_in_" + if1.getFilename() + "/" + ppt.getRawName(), ppt.toJSON().toString());
		}
		for (Entry<String, List<Ppt>> pptList : methodPptOnly1.entrySet()) {
			JSONArray pptListJSONArray = new JSONArray(pptList.getValue());
			FileUtils.writeTo(folderName + "/ppts_only_in_" + if1.getFilename() + "/" + pptList.getKey(), pptListJSONArray.toString());
		}
	}
}
