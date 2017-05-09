package gov.cms.qpp.conversion;


public abstract class TieredDecoder implements Decoder {
	protected int tier;

	public abstract void associateWith(TieredDecoder decoder);

	public void setTier(int tier) {
		this.tier = tier;
	}

	public int getTier() {
		return tier;
	}
}
