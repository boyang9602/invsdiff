package ca.concordia.apr.invsdiff.ppt;

public class ObjectPpt extends ParentPpt {
	private String name;
	public ObjectPpt(String name) {
		this.name = name;
	}
	@Override
	public String getRawName() {
		return this.name + ":::OBJECT";
	}
	@Override
	public String getName() {
		return name;
	}
}
