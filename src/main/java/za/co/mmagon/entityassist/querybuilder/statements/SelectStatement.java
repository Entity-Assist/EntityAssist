package za.co.mmagon.entityassist.querybuilder.statements;

import za.co.mmagon.entityassist.querybuilder.EntityAssistStrings;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;

public class SelectStatement implements EntityAssistStrings
{

	private SelectStatement()
	{
		//Nothing Needed
	}


	@SuppressWarnings("unchecked")
	public static String getSelectSQLHibernate5(CriteriaQuery criteria, EntityManager em)
	{
		return em.unwrap(org.hibernate.Session.class).createQuery(criteria).getQueryString();
	}

	/**
	 * Returns the select the query and entity manager
	 *
	 * @param query
	 * @param em
	 *
	 * @return
	 */
	public static String getSelectSQLEclipseLink(TypedQuery query, EntityManager em)
	{
		org.eclipse.persistence.sessions.Session session = em.unwrap(org.eclipse.persistence.jpa.JpaEntityManager.class).getActiveSession();
		org.eclipse.persistence.queries.DatabaseQuery databaseQuery = query.unwrap(org.eclipse.persistence.internal.jpa.EJBQueryImpl.class).getDatabaseQuery();
		databaseQuery.prepareCall(session, new org.eclipse.persistence.sessions.DatabaseRecord());
		databaseQuery.bindAllParameters();
		org.eclipse.persistence.sessions.Record r = databaseQuery.getTranslationRow();
		String bound = databaseQuery.getTranslatedSQLString(session, r);
		bound = bound.replace("{ts ", "");
		bound = bound.replace("'})", "')");
		return bound;
	}
}
