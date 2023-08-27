package efakturaplus.models;

public enum InvoiceStatus{
	New("New"),
	Seen("Seen"),
	Reminded("Reminded"),
	ReNotified("ReNotified"),
	Approved("Approved"),
	Rejected("Rejected"),
	Storno("Storno");

	private String statusString;

	InvoiceStatus(String statusString) {
		this.statusString = statusString;
	}

	@Override
	public String toString() {
		return this.statusString;
	}
}
