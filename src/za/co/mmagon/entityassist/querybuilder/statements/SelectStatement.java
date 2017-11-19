package za.co.mmagon.entityassist.querybuilder.statements;

import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.Record;
import org.hibernate.Criteria;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.internal.SessionImpl;
import org.hibernate.loader.OuterJoinLoader;
import org.hibernate.loader.criteria.CriteriaLoader;
import org.hibernate.loader.criteria.CriteriaQueryTranslator;
import org.hibernate.persister.entity.OuterJoinLoadable;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class SelectStatement
{

	private SelectStatement()
	{
		//Nothing Needed
	}

	@SuppressWarnings("unchecked")
	public static String getSelectSQLHibernate4(Criteria criteria)
	{
		String sql = "";
		Object[] parameters = null;
		try
		{
			CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
			SessionImpl sessionImpl = (SessionImpl) criteriaImpl.getSession();
			SessionFactoryImplementor factory = sessionImpl.getSessionFactory();
			String[] implementors = factory.getImplementors(criteriaImpl.getEntityOrClassName());
			OuterJoinLoadable persister = (OuterJoinLoadable) factory.getEntityPersister(implementors[0]);
			LoadQueryInfluencers loadQueryInfluencers = new LoadQueryInfluencers();
			CriteriaLoader loader = new CriteriaLoader(persister, factory,
			                                           criteriaImpl, implementors[0], loadQueryInfluencers);
			Field f = OuterJoinLoader.class.getDeclaredField("sql");
			f.setAccessible(true);
			sql = (String) f.get(loader);
			Field fp = CriteriaLoader.class.getDeclaredField("translator");
			fp.setAccessible(true);
			CriteriaQueryTranslator translator = (CriteriaQueryTranslator) fp.get(loader);
			parameters = translator.getQueryParameters().getPositionalParameterValues();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		if (sql != null)
		{
			int fromPosition = sql.indexOf(" from ");
			sql = "\nSELECT * " + sql.substring(fromPosition);

			if (parameters != null && parameters.length > 0)
			{
				for (Object val : parameters)
				{
					String value = "%";
					if (val instanceof Boolean)
					{
						value = ((Boolean) val) ? "1" : "0";
					}
					else if (val instanceof String)
					{
						value = "'" + val + "'";
					}
					else if (val instanceof Number)
					{
						value = val.toString();
					}
					else if (val instanceof Class)
					{
						value = "'" + ((Class) val).getCanonicalName() + "'";
					}
					else if (val instanceof Date)
					{
						SimpleDateFormat sdf = new SimpleDateFormat(
								                                           "yyyy-MM-dd HH:mm:ss.SSS");
						value = "'" + sdf.format((Date) val) + "'";
					}
					else if (val instanceof LocalDate)
					{
						SimpleDateFormat sdf = new SimpleDateFormat(
								                                           "yyyy-MM-dd");
						value = "'" + sdf.format(val) + "'";
					}
					else if (val instanceof LocalDateTime)
					{
						SimpleDateFormat sdf = new SimpleDateFormat(
								                                           "yyyy-MM-dd HH:mm:ss.SSS");
						value = "'" + sdf.format(val) + "'";
					}
					else if (val instanceof Enum)
					{
						value = Integer.toString(((Enum) val).ordinal());
					}
					else
					{
						value = val.toString();
					}
					sql = sql.replaceFirst("\\?", value);
				}
			}
		}
		return sql.replaceAll("left outer join", "\nleft outer join").replaceAll(
				" and ", "\nand ").replaceAll(" on ", "\non ").replaceAll("<>",
		                                                                  "!=").replaceAll("<", " < ").replaceAll(">", " > ");
	}

	@SuppressWarnings("unchecked")
	public static String getSelectSQLHibernate5(CriteriaQuery criteria)
	{
		String sql = "";
		Object[] parameters = null;
		try
		{
			CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
			SessionImpl sessionImpl = (SessionImpl) criteriaImpl.getSession();
			SessionFactoryImplementor factory = sessionImpl.getSessionFactory();
			String[] implementors = factory.getImplementors(criteriaImpl.getEntityOrClassName());
			OuterJoinLoadable persister = (OuterJoinLoadable) factory.getEntityPersister(implementors[0]);
			LoadQueryInfluencers loadQueryInfluencers = new LoadQueryInfluencers();
			CriteriaLoader loader = new CriteriaLoader(persister, factory,
			                                           criteriaImpl, implementors[0], loadQueryInfluencers);
			Field f = OuterJoinLoader.class.getDeclaredField("sql");
			f.setAccessible(true);
			sql = (String) f.get(loader);
			Field fp = CriteriaLoader.class.getDeclaredField("translator");
			fp.setAccessible(true);
			CriteriaQueryTranslator translator = (CriteriaQueryTranslator) fp.get(loader);
			parameters = translator.getQueryParameters().getPositionalParameterValues();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		if (sql != null)
		{
			int fromPosition = sql.indexOf(" from ");
			sql = "\nSELECT * " + sql.substring(fromPosition);

			if (parameters != null && parameters.length > 0)
			{
				for (Object val : parameters)
				{
					String value;
					if (val instanceof Boolean)
					{
						value = ((Boolean) val) ? "1" : "0";
					}
					else if (val instanceof String)
					{
						value = "'" + val + "'";
					}
					else if (val instanceof Number)
					{
						value = val.toString();
					}
					else if (val instanceof Class)
					{
						value = "'" + ((Class) val).getCanonicalName() + "'";
					}
					else if (val instanceof Date)
					{
						SimpleDateFormat sdf = new SimpleDateFormat(
								                                           "yyyy-MM-dd HH:mm:ss.SSS");
						value = "'" + sdf.format((Date) val) + "'";
					}
					else if (val instanceof LocalDate)
					{
						SimpleDateFormat sdf = new SimpleDateFormat(
								                                           "yyyy-MM-dd");
						value = "'" + sdf.format(val) + "'";
					}
					else if (val instanceof LocalDateTime)
					{
						SimpleDateFormat sdf = new SimpleDateFormat(
								                                           "yyyy-MM-dd HH:mm:ss.SSS");
						value = "'" + sdf.format(val) + "'";
					}
					else if (val instanceof Enum)
					{
						value = Integer.toString(((Enum) val).ordinal());
					}
					else
					{
						value = val.toString();
					}
					sql = sql.replaceFirst("\\?", value);
				}
			}
		}
		return sql.replaceAll("left outer join", "\nleft outer join").replaceAll(
				" and ", "\nand ").replaceAll(" on ", "\non ").replaceAll("<>",
		                                                                  "!=").replaceAll("<", " < ").replaceAll(">", " > ");
	}

	public static String getSelectSQLEclipseLink(TypedQuery query, EntityManager em)
	{
		org.eclipse.persistence.sessions.Session session = em.unwrap(JpaEntityManager.class).getActiveSession();
		DatabaseQuery databaseQuery = query.unwrap(EJBQueryImpl.class).getDatabaseQuery();
		databaseQuery.prepareCall(session, new DatabaseRecord());
		databaseQuery.bindAllParameters();
		Record r = databaseQuery.getTranslationRow();
		String bound = databaseQuery.getTranslatedSQLString(session, r);
		bound = bound.replace("{ts ", "");
		bound = bound.replace("'})", "')");
		return bound;
	}


}
