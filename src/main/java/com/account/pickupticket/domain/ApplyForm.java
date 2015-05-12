package com.account.pickupticket.domain;

public class ApplyForm {
	String visitDate;
	String visitTimeStart;
	String visitTimeEnd;
	int expectPersonCount;
	public String getVisitDate() {
		return visitDate;
	}
	public void setVisitDate(String visitDate) {
		this.visitDate = visitDate;
	}
	public String getVisitTimeStart() {
		return visitTimeStart;
	}
	public void setVisitTimeStart(String visitTimeStart) {
		this.visitTimeStart = visitTimeStart;
	}
	public String getVisitTimeEnd() {
		return visitTimeEnd;
	}
	public void setVisitTimeEnd(String visitTimeEnd) {
		this.visitTimeEnd = visitTimeEnd;
	}
	public int getExpectPersonCount() {
		return expectPersonCount;
	}
	public void setExpectPersonCount(int expectPersonCount) {
		this.expectPersonCount = expectPersonCount;
	}
}
