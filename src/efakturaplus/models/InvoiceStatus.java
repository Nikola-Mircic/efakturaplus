package efakturaplus.models;

public enum InvoiceStatus{
	                  
	// Purchase [7] - New, Seen, Reminded, ReNotified, Approved, Rejected, Storno
	
	// Sales [9] - New, Draft, Sent, Mistake, Sending, Approved, Rejected, Cancelled, Storno
	
	New("New"),
	Seen("Seen"),
	Reminded("Reminded"),
	ReNotified("ReNotified"),
	Approved("Approved"),
	Rejected("Rejected"),
	Storno("Storno"),
	
	Draft("Draft"),
	Mistake("Mistake"),
	Sending("Sending"),
	Cancelled("Cancelled");

	private String statusString;

	InvoiceStatus(String statusString) {
		this.statusString = statusString;
	}

	@Override
	public String toString() {
		return this.statusString;
	}
}
