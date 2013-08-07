package com.borasoft.radio.log.adif;

public class ADIFObject {
	private String call;
	private String band;
	private String freq;
	private String mode;
	private String name;
	private String qsoDate;
	private String rstReceived;
	private String rstSent;
	private String qslReceived;
	private String qslSent;
	private String timeOn;
	private String timeOff;
	private String comment;
	private String qth;
	private String txPWR;
	private String ituz; // ITU zone
	private String cqz; // CQ zone
	private String dxcc;

	public String getQTH() {
		return qth;
	}
	public void setQTH(String qth) {
		this.qth = qth;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getCall() {
		return call;
	}
	public void setCall(String call) {
		this.call = call;
	}
	public String getFreq() {
		return freq;
	}
	public void setFreq(String freq) {
		this.freq = freq;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getQSODate() {
		return qsoDate;
	}
	public void setQSODate(String qsoDate) {
		this.qsoDate = qsoDate;
	}
	public String getRSTReceived() {
		return rstReceived;
	}
	public void setRSTReceived(String rstReceived) {
		this.rstReceived = rstReceived;
	}
	public String getRSTSent() {
		return rstSent;
	}
	public void setRSTSent(String rstSent) {
		this.rstSent = rstSent;
	}
	public String getTimeOn() {
		return timeOn;
	}
	public void setTimeOn(String timeOn) {
		this.timeOn = timeOn;
	}
	public String getTimeOff() {
		return timeOff;
	}
	public void setTimeOff(String timeOff) {
		this.timeOff = timeOff;
	}
	public void setBand(String band) {
		this.band = band;		
	}
	
	public String getBand() {
		return band;
	}
	public String getQSLReceived() {
		return qslReceived;
	}
	public void setQSLReceived(String qslReceived) {
		this.qslReceived = qslReceived;
	}
	public String getQSLSent() {
		return qslSent;
	}
	public void setQSLSent(String qslSent) {
		this.qslSent = qslSent;
	}
	public String getTXPWR() {
		return txPWR;
	}
	public void setTXPWR(String txPWR) {
		this.txPWR = txPWR;
	}
	public String getITUZone() {
		return ituz;
	}
	public void setITUZone(String ituz) {
		this.ituz = ituz;
	}
	public String getCQZone() {
		return cqz;
	}
	public void setCQZone(String cqz) {
		this.cqz = cqz;
	}
	public String getDXCC() {
		return dxcc;
	}
	public void setDXCC(String dxcc) {
		this.dxcc = dxcc;
	}
}
