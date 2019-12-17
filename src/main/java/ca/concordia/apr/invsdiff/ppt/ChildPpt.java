package ca.concordia.apr.invsdiff.ppt;

public abstract class ChildPpt extends Ppt {
	protected ParentPpt parent;
	public ChildPpt(ParentPpt parent) {
		this.parent = parent;
		this.parent.addChild(this);
	}
}
