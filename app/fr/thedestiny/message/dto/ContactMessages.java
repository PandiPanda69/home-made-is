package fr.thedestiny.message.dto;

import java.sql.Date;

import lombok.Getter;

@Getter
public class ContactMessages {

	private final int id;

	private final String name;

	private final long count;

	private final Date lastReception;

	public ContactMessages(final int id, final String name, final long count, final Date lastReception) {
		this.id = id;
		this.name = name;
		this.count = count;
		this.lastReception = lastReception;
	}
}
