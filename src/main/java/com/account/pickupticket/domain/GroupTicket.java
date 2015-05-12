package com.account.pickupticket.domain;

import java.io.Serializable;

public class GroupTicket implements Serializable {
	String visitDate;
	String visitTimeStart;
	String visitTimeEnd;
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
	@Override
	public String toString() {
		return "GroupTicket [visitDate=" + visitDate + ", visitTimeStart="
				+ visitTimeStart + ", visitTimeEnd=" + visitTimeEnd + "]";
	}
}
