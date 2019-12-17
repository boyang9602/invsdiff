package ca.concordia.apr.invsdiff;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import ca.concordia.apr.invsdiff.utils.FileUtils;

public class Diff {
	private InvsFile if1;
	private InvsFile if2;

	private Map<String, Ppt> classPptOnly1 = new HashMap<String, Ppt>();
	private Map<String, Ppt> classPptOnly2 = new HashMap<String, Ppt>();

	private Map<String, Ppt> objectPptOnly1 = new HashMap<String, Ppt>();
	private Map<String, Ppt> objectPptOnly2 = new HashMap<String, Ppt>();

	private Map<String, Map<String, List<Ppt>>> classMethodPptOnly1Map = new HashMap<String, Map<String, List<Ppt>>>();
	private Map<String, Map<String, List<Ppt>>> classMethodPptOnly2Map = new HashMap<String, Map<String, List<Ppt>>>();

	private Map<String, Ppt> classInvOnly1 = new HashMap<String, Ppt>();
	private Map<String, Ppt> classInvOnly2 = new HashMap<String, Ppt>();

	private Map<String, Ppt> objectInvOnly1 = new HashMap<String, Ppt>();
	private Map<String, Ppt> objectInvOnly2 = new HashMap<String, Ppt>();

	private Map<String, Map<String, List<Ppt>>> classMethodInvOnly1Map = new HashMap<String, Map<String, List<Ppt>>>();
	private Map<String, Map<String, List<Ppt>>> classMethodInvOnly2Map = new HashMap<String, Map<String, List<Ppt>>>();
	
	private Set<String> classCompared = new HashSet<String>();
	
	private Set<String> allFiles = new HashSet<String>();

	public Diff(InvsFile if1, InvsFile if2) {
		this.if1 = if1;
		this.if2 = if2;
	}

	public void execute() {
		compareClass();
		compareObject();
	}

	private void compareClass() {
		compare(if1.getClassPpts(), if2.getClassPpts(), classPptOnly1, classPptOnly2, classInvOnly1, classInvOnly2);
	}

	private void compareObject() {
		compare(if1.getObjectPpts(), if2.getObjectPpts(), objectPptOnly1, objectPptOnly2, objectInvOnly1, objectInvOnly2);
	}

	private void compareMethod(String className) {
		if (classCompared.contains(className)) {
			return;
		} 
		classCompared.add(className);
		Map<String, Ppt> mEnter1 = if1.getEnterPpts().get(className);
		Map<String, List<Ppt>> mExit1 = if1.getExitPpts().get(className);
		Map<String, List<Ppt>> mExitnn1 = if1.getExitnnPpts().get(className);

		Map<String, Ppt> mEnter2 = if2.getEnterPpts().get(className);
		Map<String, List<Ppt>> mExit2 = if2.getExitPpts().get(className);
		Map<String, List<Ppt>> mExitnn2 = if2.getExitnnPpts().get(className);

		Set<String> commonMethods = mEnter1 == null ? new HashSet<String>() : new HashSet<String>(mEnter1.keySet());
		if (mEnter2 != null) {
			commonMethods.retainAll(mEnter2.keySet());
		}
		Set<String> mExitSet1 = new HashSet<String>(mExit1.keySet());
		mExitSet1.retainAll(mExit2.keySet());
		commonMethods.addAll(mExitSet1);
		for (String method : commonMethods) {
			Ppt enterPpt1 = mEnter1 == null ? null : mEnter1.get(method);
			Ppt enterPpt2 = mEnter2 == null ? null : mEnter2.get(method);
			Ppt enterPpt12 = null;
			Ppt enterPpt21 = null;
			if (enterPpt1 == null && enterPpt2 == null) {
			} else if (enterPpt1 != null && enterPpt2 == null) {
				enterPpt12 = enterPpt1;
			} else if (enterPpt1 != null && enterPpt2 == null) {
				enterPpt21 = enterPpt2;
			} else {
				enterPpt12 = enterPpt1.diff(enterPpt2);
				enterPpt21 = enterPpt2.diff(enterPpt1);
			}

			List<Ppt> listExit1 = new LinkedList<Ppt>();
			List<Ppt> listExit2 = new LinkedList<Ppt>();
			List<Ppt> originExit1 = mExit1.get(method);
			List<Ppt> originExit2 = mExit2.get(method);
			if (originExit1 == null && originExit2 == null) {
			} else if (originExit1 != null && originExit2 == null) {
				listExit1.addAll(originExit1);
			} else if (originExit1 == null && originExit2 != null) {
				listExit2.addAll(originExit2);
			} else {
				Iterator<Ppt> it1 = new LinkedList<Ppt>(originExit1).iterator();
				Iterator<Ppt> it2 = new LinkedList<Ppt>(originExit2).iterator();
				while (it1.hasNext()) {
					Ppt p1 = it1.next();
					boolean found = false;
					while (it2.hasNext()) {
						Ppt p2 = it2.next();
						if ((p1.getCondition() == null && p2.getCondition() == null) || 
								(p1.getCondition().equals(p2.getCondition()))) {
							listExit1.add(p1.diff(p2));
							listExit2.add(p2.diff(p1));
							it1.remove();
							it2.remove();
							found = true;
							break;
						}
					}
					if (!found) {
						listExit1.add(p1);
					}
				}
				while (it2.hasNext()) {
					listExit2.add(it2.next());
				}
			}

			List<Ppt> listExitnn1 = new LinkedList<Ppt>();
			List<Ppt> listExitnn2 = new LinkedList<Ppt>();
			List<Ppt> originListExitnn1 = mExitnn1 == null ? null : mExitnn1.get(method);
			List<Ppt> originListExitnn2 = mExitnn2 == null ? null : mExitnn2.get(method);
			if (originListExitnn1 == null && originListExitnn2 == null) {
			} else if (originListExitnn1 == null && originListExitnn2 != null) {
				listExitnn2.addAll(originListExitnn2);
			} else if (originListExitnn1 != null && originListExitnn2 == null) {
				listExitnn1.addAll(originListExitnn1);
			} else {
				Iterator<Ppt> it1 = new LinkedList<Ppt>(originListExitnn1).iterator();
				Iterator<Ppt> it2 = new LinkedList<Ppt>(originListExitnn2).iterator();
				Set<Integer> exitnn = new HashSet<Integer>();
				while (it1.hasNext()) {
					Ppt p1 = it1.next();
					boolean found = false;
					while (it2.hasNext()) {
						Ppt p2 = it2.next();
						if (p1.getExitPoint() == p2.getExitPoint()) {
							if ((p1.getCondition() == null && p2.getCondition() == null) || 
									(p1.getCondition() != null && p2.getCondition() != null && p1.getCondition().equals(p2.getCondition()))) {
								listExitnn1.add(p1.diff(p2));
								listExitnn2.add(p2.diff(p1));
								it1.remove();
								it2.remove();
								found = true;
								exitnn.add(p1.getExitPoint());
								break;
							}
						}
					}
					if (!found) {
						listExitnn1.add(p1);
					}
				}
				for (Ppt p2 : originListExitnn2) {
					if (!exitnn.contains(p2.getExitPoint())) {
						listExitnn2.add(p2);
					}
				}
			}

			if ((enterPpt12 == null || enterPpt12.isEmpty()) 
					&& (enterPpt21 == null || enterPpt21.isEmpty())
					&& checkAllEmpty(listExit1, listExit2)
					&& checkAllEmpty(listExitnn1, listExitnn2)) {
			} else {
				List<Ppt> l1 = new LinkedList<Ppt>();
				if (enterPpt12 != null)
					l1.add(enterPpt12);
				l1.addAll(listExit1);
				l1.addAll(listExitnn1);
				Map<String, List<Ppt>> methodInv1 = this.classMethodInvOnly1Map.get(className);
				if (methodInv1 == null) {
					methodInv1 = new HashMap<String, List<Ppt>>();
					this.classMethodInvOnly1Map.put(className, methodInv1);
				}
				methodInv1.put(method, l1);
				List<Ppt> l2 = new LinkedList<Ppt>();
				if (enterPpt21 != null)
					l2.add(enterPpt21);
				l2.addAll(listExit2);
				l2.addAll(listExitnn2);
				Map<String, List<Ppt>> methodInv2 = this.classMethodInvOnly2Map.get(className);
				if (methodInv2 == null) {
					methodInv2 = new HashMap<String, List<Ppt>>();
					this.classMethodInvOnly2Map.put(className, methodInv2);
				}
				methodInv2.put(method, l2);
			}
		}

		Map<String, List<Ppt>> methodInv1 = this.classMethodInvOnly1Map.get(className);
		if(methodInv1 == null) {
			methodInv1 = new HashMap<String, List<Ppt>>();
			this.classMethodInvOnly1Map.put(className, methodInv1);
		}
		Map<String, List<Ppt>> methodInv2 = this.classMethodInvOnly2Map.get(className);
		if(methodInv2 == null) {
			methodInv2 = new HashMap<String, List<Ppt>>();
			this.classMethodInvOnly2Map.put(className, methodInv2);
		}
		
		putAllDistinctPpt(mEnter1, mExit1, mExitnn1, commonMethods, methodInv1);
		putAllDistinctPpt(mEnter2, mExit2, mExitnn2, commonMethods, methodInv2);
	}

	private boolean checkAllEmpty(List<Ppt> lenn1, List<Ppt> lenn2) {
		boolean allEmpty = true;
		for (Ppt p1 : lenn1) {
			if (!p1.isEmpty()) {
				allEmpty = false;
			}
		}
		if (allEmpty) {
			for (Ppt p2 : lenn2) {
				if (!p2.isEmpty()) {
					allEmpty = false;
				}
			}
		}
		return allEmpty;
	}

	private void putAllDistinctPpt(Map<String, Ppt> mEnter, Map<String, List<Ppt>> mExit, Map<String, List<Ppt>> mExitnn,
			Set<String> commonMethods, Map<String, List<Ppt>> receiver) {
		Set<String> mEnterExitSet = mEnter == null ? new HashSet<String>() : new HashSet<String>(mEnter.keySet());
		mEnterExitSet.addAll(mExit == null ? new HashSet<String>() : mExit.keySet());
		mEnterExitSet.removeAll(commonMethods);
		for (String method : mEnterExitSet) {
			Ppt enterPpt = mEnter.get(method);
			List<Ppt> exitPpts = mExit.get(method);
			List<Ppt> exitnnPpts = mExitnn == null ? null : mExitnn.get(method);
			List<Ppt> l = new LinkedList<Ppt>();
			l.add(enterPpt);
			if (exitPpts != null) {
				l.addAll(exitPpts);
			}
			if (exitnnPpts != null) {
				l.addAll(exitnnPpts);
			}
			receiver.put(method, l);
		}
	}

	private void compare(Map<String, Ppt> m1, Map<String, Ppt> m2, Map<String, Ppt> onlyM1Ppt, Map<String, Ppt> onlyM2Ppt, Map<String, Ppt> onlyM1Inv, Map<String, Ppt> onlyM2Inv) {
		Set<String> pptNames1 = new HashSet<String>(m1.keySet());
		Set<String> pptNames2 = new HashSet<String>(m2.keySet());

		pptNames1.retainAll(pptNames2);
		Set<String> commonNames = new HashSet<String>(pptNames1);
		pptNames1 = new HashSet<String>(m1.keySet());
		pptNames1.removeAll(commonNames);
		for (String pptName : pptNames1) {
			onlyM1Ppt.put(pptName, m1.get(pptName));
			addAllDistinctMethodPpts(pptName, this.classMethodPptOnly1Map, this.if1);
		}
		pptNames2.removeAll(commonNames);
		for (String pptName : pptNames2) {
			onlyM2Ppt.put(pptName, m2.get(pptName));
			addAllDistinctMethodPpts(pptName, this.classMethodPptOnly2Map, this.if2);
		}
		for (String pptName : commonNames) {
			Ppt p1 = m1.get(pptName);
			Ppt p2 = m2.get(pptName);
			Ppt p12 = p1.diff(p2);
			Ppt p21 = p2.diff(p1);
			if (p12.isEmpty() && p21.isEmpty()) {
			} else {
				onlyM1Inv.put(pptName, p12);
				onlyM2Inv.put(pptName, p21);
			}

			compareMethod(pptName);
		}
	}

	private void addAllDistinctMethodPpts(String pptName, Map<String, Map<String, List<Ppt>>> classMethodMap, InvsFile ifn) {
		Map<String, List<Ppt>> methodPpts1 = classMethodMap.get(pptName);
		if (methodPpts1 == null) {
			methodPpts1 = new HashMap<String, List<Ppt>>();
			classMethodMap.put(pptName, methodPpts1);
		}
		Map<String, Ppt> enterMethodMap = ifn.getEnterPpts().get(pptName);
		Map<String, List<Ppt>> exitMethodMap = ifn.getExitPpts().get(pptName);
		Map<String, List<Ppt>> exitnnMethodMap = ifn.getExitnnPpts().get(pptName);
		Set<String> methodNameSet = enterMethodMap == null ? new HashSet<String>() : new HashSet<String>(enterMethodMap.keySet());
		if (exitMethodMap != null) {
			methodNameSet.addAll(exitMethodMap.keySet());
		}
		for (String methodName : methodNameSet) {
			Ppt enterPpt = enterMethodMap == null ? null : enterMethodMap.get(methodName);
			List<Ppt> exitPptList = exitMethodMap == null ? null : exitMethodMap.get(methodName);
			List<Ppt> methodPptList = new LinkedList<Ppt>();
			methodPpts1.put(methodName, methodPptList);
			if (enterPpt != null) {
				methodPptList.add(enterPpt);
			}
			if (exitPptList != null) {
				methodPptList.addAll(exitPptList);
			}
			if (exitnnMethodMap != null) {
				List<Ppt> exitnnPptList = exitnnMethodMap.get(methodName);
				if (exitnnPptList != null) {
					methodPptList.addAll(exitnnPptList);
				}
			}
		}
		this.classCompared.add(pptName);
	}

	public void writeJSONTo(String folderName) throws IOException, NoSuchAlgorithmException {
		writeOnlyExistOneSide(this.classPptOnly1, this.objectPptOnly1, this.classMethodPptOnly1Map, folderName, if1.getFilename(), if2.getFilename());
		writeOnlyExistOneSide(this.classPptOnly2, this.objectPptOnly2, this.classMethodPptOnly2Map, folderName, if2.getFilename(), if1.getFilename());
		Set<String> classNames1 = new HashSet<String>(this.classInvOnly1.keySet());
		classNames1.addAll(this.objectInvOnly1.keySet());
		classNames1.addAll(this.classMethodInvOnly1Map.keySet());
		for (String classPptName : classNames1) {
			JSONObject root = new JSONObject();
			JSONArray layer = new JSONArray();
			root.put(classPptName, layer);
			JSONObject tmp;

			Ppt classPpt1 = this.classInvOnly1.get(classPptName);
			Ppt classPpt2 = this.classInvOnly2.get(classPptName);
			if (classPpt1 != null) {
				tmp = new JSONObject();
				tmp.append("CLASS", new JSONObject().put(if1.getFilename(), classPpt1.toJSON(false))).append("CLASS", new JSONObject().append(if2.getFilename(), classPpt2.toJSON(false)));
			}
			
			Ppt objectPpt1 = this.objectInvOnly1.get(classPptName);
			Ppt objectPpt2 = this.objectInvOnly2.get(classPptName);
			if (objectPpt1 != null) {
				tmp = new JSONObject();
				tmp.append("OBJECT", new JSONObject().put(if1.getFilename(), objectPpt1.toJSON(false))).append("OBJECT", new JSONObject().append(if2.getFilename(), objectPpt2.toJSON(false)));
			}

			Map<String, List<Ppt>> methodPptMap1 = this.classMethodInvOnly1Map.get(classPptName);
			Map<String, List<Ppt>> methodPptMap2 = this.classMethodInvOnly2Map.get(classPptName);
			if (methodPptMap1 != null) {
				for (String methodName : methodPptMap1.keySet()) {
					tmp = new JSONObject();
					Set<Integer> pptExit1 = new HashSet<Integer>();
					Set<Integer> pptExit2 = new HashSet<Integer>();
					for (Ppt ppt1 : methodPptMap1.get(methodName)) {
						if (ppt1.getType() == Ppt.PPT_TYPE.ENTER || ppt1.getType() == Ppt.PPT_TYPE.EXIT) {
							if (methodPptMap2.get(methodName) != null) {
								for (Ppt ppt2 : methodPptMap2.get(methodName)) {
									if (ppt1.getType() == ppt2.getType()) {
										JSONArray sides = new JSONArray();
										sides.put(new JSONObject().put(if1.getFilename(), ppt1.toJSON(false)));
										sides.put(new JSONObject().put(if2.getFilename(), ppt2.toJSON(false)));
										tmp.append(ppt1.getMethodName(), 
												new JSONObject().put(ppt1.getType().toString(), sides));
										break;
									}
								}
							}
						} else {
							if (methodPptMap2.get(methodName) != null) {
								for (Ppt ppt2 : methodPptMap2.get(methodName)) {
									if (ppt2.getType() == Ppt.PPT_TYPE.EXITNN && ppt1.getExitPoint() == ppt2.getExitPoint()) {
										JSONArray sides = new JSONArray();
										sides.put(new JSONObject().put(if1.getFilename(), ppt1.toJSON(false)));
										sides.put(new JSONObject().put(if2.getFilename(), ppt2.toJSON(false)));
										tmp.append(ppt1.getMethodName(), 
												new JSONObject().append(ppt1.getType().toString(), 
														new JSONObject().put("" + ppt1.getExitPoint(), sides)));
										pptExit1.add(ppt1.getExitPoint());
										pptExit2.add(ppt2.getExitPoint());
										break;
									}
								}
							}
						}
					}
					for (Ppt ppt1 : methodPptMap1.get(methodName)) {
						if (ppt1.getType() == Ppt.PPT_TYPE.EXITNN && !pptExit1.contains(ppt1.getExitPoint())) {
							JSONArray sides = new JSONArray();
							sides.put(new JSONObject().put(if1.getFilename(), ppt1.toJSON(false)));
							sides.put(new JSONObject().put(if2.getFilename(), "N/A"));
							tmp.append(ppt1.getMethodName(), 
									new JSONObject().append(ppt1.getType().toString(), 
											new JSONObject().put("" + ppt1.getExitPoint(), sides)));
						}
					}
					if (methodPptMap2.get(methodName) != null) {
						for (Ppt ppt2 : methodPptMap2.get(methodName)) {
							if (ppt2.getType() == Ppt.PPT_TYPE.EXITNN && !pptExit2.contains(ppt2.getExitPoint())) {
								JSONArray sides = new JSONArray();
								sides.put(new JSONObject().put(if2.getFilename(), ppt2.toJSON(false)));
								sides.put(new JSONObject().put(if1.getFilename(), "N/A"));
								tmp.append(ppt2.getMethodName(), 
										new JSONObject().append(ppt2.getType().toString(), 
												new JSONObject().put("" + ppt2.getExitPoint(), sides)));
							}
						}
					}
					layer.put(tmp);
				}
			}
			if (!layer.isEmpty()) {
				FileUtils.writeTo(folderName + "/" + classPptName, root.toString());
				if(!allFiles.add(classPptName)) {
					throw new RuntimeException("duplicated file");
				}
			}
		}
	}

	private void writeOnlyExistOneSide(Map<String, Ppt> classMap, Map<String, Ppt> objectMap, 
			Map<String, Map<String, List<Ppt>>> classMethodMap, String filename,
			String f1name, String f2name) throws NoSuchAlgorithmException, IOException {
		Set<String> classNames1 = new HashSet<String>(classMap.keySet());
		classNames1.addAll(objectMap.keySet());
		classNames1.addAll(classMethodMap.keySet());
		for (String classPptName : classNames1) {
			JSONObject root = new JSONObject();
			JSONArray layer = new JSONArray();
			root.put(classPptName, layer);
			JSONObject tmp;

			Ppt classPpt = classMap.get(classPptName);
			if (classPpt != null) {
				tmp = new JSONObject();
				JSONArray tmpArr = new JSONArray();
				tmpArr.put(new JSONObject().put(f1name, classPpt.toJSON(false)));
				tmpArr.put(new JSONObject().put(f2name, "N/A"));
				tmp.put("CLASS", tmpArr);
				layer.put(tmp);
			}
			Ppt objectPpt = objectMap.get(classPptName);
			if (objectPpt != null) {
				tmp = new JSONObject();
				JSONArray tmpArr = new JSONArray();
				tmpArr.put(new JSONObject().put(f1name, objectPpt.toJSON(false)));
				tmpArr.put(new JSONObject().put(f2name, "N/A"));
				tmp.put("OBJECT", tmpArr);
				layer.put(tmp);
			}
			
			Map<String, List<Ppt>> methodPptMap = classMethodMap.get(classPptName);
			if (methodPptMap != null) {
				for (String methodName : methodPptMap.keySet()) {
					tmp = new JSONObject();
					for (Ppt ppt : methodPptMap.get(methodName)) {
						if (ppt.getType() == Ppt.PPT_TYPE.ENTER || ppt.getType() == Ppt.PPT_TYPE.EXIT) {
							JSONArray sides = new JSONArray();
							sides.put(new JSONObject().put(f1name, ppt.toJSON(false)));
							sides.put(new JSONObject().put(f2name, "N/A"));
							tmp.append(ppt.getMethodName(), 
									new JSONObject().put(ppt.getType().toString(), sides));
						} else {
							JSONArray sides = new JSONArray();
							sides.put(new JSONObject().put(f1name, ppt.toJSON(false)));
							sides.put(new JSONObject().put(f2name, "N/A"));
							tmp.append(ppt.getMethodName(), 
									new JSONObject().append(ppt.getType().toString(), 
											new JSONObject().put("" + ppt.getExitPoint(), sides)));
						}
					}
					layer.put(tmp);
				}
			}
			if (!layer.isEmpty()) {
				FileUtils.writeTo(filename + "/" + classPptName, root.toString());
				if(!allFiles.add(classPptName)) {
					throw new RuntimeException("duplicated file");
				}
			}
		}
	}
}
