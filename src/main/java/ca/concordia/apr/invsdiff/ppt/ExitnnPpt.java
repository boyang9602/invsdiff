package ca.concordia.apr.invsdiff.ppt;

public class ExitnnPpt extends ChildPpt {
	private int exitPoint;
	private String name;
	public ExitnnPpt(ParentPpt parent, String name, int exitPoint) {
		super(parent);
		this.name = name;
		this.exitPoint = exitPoint;
	}
	public ExitnnPpt(ParentPpt parent, String name, int exitPoint, String condition) {
		super(parent);
		this.name = name;
		this.exitPoint = exitPoint;
		this.condition = condition;
	}
	public String getName() {
		return name;
	}
	public final String getRawName() {
		if (this.condition != null) {
			return parent.getName() + "." + name + ":::EXIT" + exitPoint + ";" + condition;
		}
		return parent.getName() + "." + name + ":::EXIT" + exitPoint;
	}
}
