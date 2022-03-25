package org.hibernate.bugs;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using the Java Persistence API.
 */
public class JPAUnitTestCase {

	private EntityManagerFactory entityManagerFactory;

	@Before
	public void init() {
		entityManagerFactory = Persistence.createEntityManagerFactory( "templatePU" );
	}

	@After
	public void destroy() {
		entityManagerFactory.close();
	}

	// Entities are auto-discovered, so just add them anywhere on class-path
	// Add your tests, using standard JUnit.
	@Test
	public void hhh123Test() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		// Do stuff...
		entityManager.persist(new Person("Person 1"));
		entityManager.persist(new Person("Person 2"));


		final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		final CriteriaQuery<Person> personQuery = cb.createQuery(Person.class);
		final Root<Person> root = personQuery.from(Person.class);
		final ParameterExpression<String> pattern = cb.parameter(String.class);
		CriteriaQuery<Person> criteriaQuery = personQuery
				.where(cb.like(
						root.get("name"),
						pattern,
						cb.literal('\\')
				));


		for (int i = 0; i < 2; i++) {
			System.out.println("Iteration: " + i);

			final TypedQuery<Person> query = entityManager.createQuery(criteriaQuery); //  <--- This fails when executed for the second time.
			query.setParameter(pattern, "%_1");
			final List<Person> result = query.getResultList();

			assertThat(result).hasSize(1);
		}


		entityManager.getTransaction().commit();
		entityManager.close();
	}
}
