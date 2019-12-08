package com.entityassist;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

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
