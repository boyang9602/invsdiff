package ca.concordia.apr.invsdiff.ppt;

public class ClassPpt extends ParentPpt {
	private String name;
	public ClassPpt(String name) {
		this.name = name;
	}
	@Override
	public String getRawName() {
		return this.name + ":::CLASS";
	}
	@Override
	public String getName() {
		return this.name;
	}
}
