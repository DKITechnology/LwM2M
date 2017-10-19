package com.dkitec.lwm2m.common.util;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Sequence domain.
 * @author <ul>
 *         <li>Sang June Lee < blue4444eye@hanmail.net > < blue4444eye@gmail.com ></li>
 *         </ul>
 */
@Document
public class Sequence {
	private String type = "";
	
	private long sequence = 1;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getSequence() {
		return sequence;
	}

	public void setSequence(long sequence) {
		this.sequence = sequence;
	}
}
