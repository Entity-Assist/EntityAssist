package com.entityassist;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.*;

import java.io.*;

/**
 * @author MMagon
 * @since
 */

@Entity
@Table(name = "Trans_Ytd")
@XmlRootElement
public class TransYtd
		extends BaseEntity<TransYtd, TransYtdQueryBuilder, TransYtdPK>
		implements Serializable
{
	private static final long serialVersionUID = 1L;
	@EmbeddedId
	protected TransYtdPK id;

	public TransYtd()
	{
	}

	@Override
	public TransYtdPK getId()
	{
		return id;
	}

	@Override
	public TransYtd setId(TransYtdPK id)
	{
		this.id = id;
		return this;
	}

	@Override
	public String toString()
	{
		return "" + id;
	}

}
