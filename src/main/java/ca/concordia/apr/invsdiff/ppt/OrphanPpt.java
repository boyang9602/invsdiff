package ca.concordia.apr.invsdiff.ppt;

public class OrphanPpt extends ChildPpt {
	private String name;
	private String type;
	private String condition;

	public OrphanPpt(String name, String type, String condition) {
		this.name = name;
		this.type = type;
		this.condition = condition;
	}
	@Override
	public String getRawName() {
		if (condition != null) {
			return name + ":::" + type + ";" + condition;
		}
		return name + ":::" + type;
	}

	@Override
	public String getName() {
		return name;
	}

}
