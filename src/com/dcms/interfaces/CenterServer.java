package com.dcms.interfaces;

import java.rmi.Remote;

public interface CenterServer extends Remote{

	public String createTRecord(final String firstName, final String lastName, final String address, final String phone, final String specialization, final String location, final String managerId);
	public String createSRecord(final String firstName, final String lastName, final String coursesRegistered, final String status, final String statusDate, final String managerId);
	public String editRecord(final String recordId, final String fieldName, final String newValue, final String managerId);
	public String getRecordCounts(final String managerId);
	public String displayRecord(final String recordId, final String managerId);

}
