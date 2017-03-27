package br.com.faculdadedelta.modelteste;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.Transformers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import br.com.faculdadedelta.model.Carne;
import br.com.faculdadedelta.util.JPAUtil;

public class CarneTeste {

	private EntityManager em;

	private Session getSession() {
		return (Session) em.getDelegate();
	}

	private Criteria criarCriteria(Class<?> clazz) {
		return getSession().createCriteria(clazz);
	}

	private Criteria criarCriteria(Class<?> clazz, String alias) {
		return getSession().createCriteria(clazz, alias);
	}
	
	@Before
	public void instanciarEntityManager() {
		em = JPAUtil.INSTANCE.getEntityManager();
	}

	@After
	public void fecharEntityManager() {
		if (em.isOpen()) {
			em.close();
		}
	}

	@Test
	public void deveSalvarCarne() {
		Carne carne = new Carne("maminha", 8.00);

		assertTrue("entidade não possui ID.", carne.isTransient());

		em.getTransaction().begin();
		em.persist(carne);
		em.getTransaction().commit();

		assertFalse("entidade agora tem id", carne.isTransient());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void deveConsultarPreco() {
		deveSalvarCarne();
		String filtro = "maminha";

		Query query = em.createQuery("select c.preco from Carne c where c.nome like :nome");
		query.setParameter("nome", filtro);

		List<Double> listaPreco = query.getResultList();

		assertFalse("verifica se há registros na lista", listaPreco.isEmpty());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void deveConsultarApenasIdeNome() {
		deveSalvarCarne();

		Query query = em.createQuery("SELECT new Carne(c.id, c.nome) FROM Carne c");

		List<Carne> carnes = (List<Carne>) query.getResultList();

		assertFalse("deve ter encontrado carnes", carnes.isEmpty());
		carnes.forEach(carne -> {
			assertNull("não deve ter preço", carne.getPreco());
		});

	}

	@SuppressWarnings("unchecked")
	@Test
	public void deveConsultarIdeNome() {
		deveSalvarCarne();

		Query query = em.createQuery("SELECT c.id, c.nome FROM Carne c");

		List<Object[]> carnes = query.getResultList();

		assertFalse("deve ter encontrado carnes", carnes.isEmpty());
		carnes.forEach(linha -> {

			assertTrue("primeiro iten é o ID", linha[0] instanceof Integer);
			assertTrue("primeiro iten é o Nome", linha[1] instanceof String);

			Carne carne = new Carne((Integer) linha[0], (String) linha[1]);
			assertNotNull("deve ter instanciado", carne);
		});

	}
	
	@Test
	public void deveContarQtdCarnes(){
		deveSalvarCarne();
		
		Query query = em.createQuery("select count(c.id) from Carne c");
		Long qtdRegistros = (Long) query.getSingleResult();
		
		assertTrue("deve ter pelo menos um registro", qtdRegistros > 0);
	}
	
	@Test(expected = NonUniqueResultException.class)
	public void naoDeveFuncionarSingleResult(){
		deveSalvarCarne();
		deveSalvarCarne();
		
		Query query = em.createQuery("Select c.id from Carne c");

		
		query.getSingleResult();
		
		fail("deveria ter lançado excessão NonUniqueResultExceptions");
	}
	
	@Test(expected = NoResultException.class)
	public void naoDeveFuncionarSingleResultComNenhumRegistro(){
		deveSalvarCarne();
		deveSalvarCarne();
		
		Query query = em.createQuery("Select c,id from Carne c where c.nome = :nome");
		query.setParameter("nome", "picanha");
		query.getSingleResult();
		
		fail("deveria ter lançado excessão NoResultException");
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void deveConsultarPrecoCarne(){
		deveSalvarCarne();
		deveSalvarCarne();
		
		ProjectionList projections	= Projections.projectionList();
		projections.add(Projections.property("c.id").as("id"));
		projections.add(Projections.property("c.preco").as("nome"));
		
		Criteria criteria = criarCriteria(Carne.class, "c")
							.setProjection(projections)
							.setResultTransformer(Criteria.PROJECTION);
		
		List<Object[]> carnes = criteria.list();
		
		assertFalse("deve ter carnes", carnes.isEmpty());
		
		carnes.forEach(carne -> {
			assertTrue("primeiro iten é o id", carne[0] instanceof Integer);
			assertTrue("segunda item é o preço", carne[1] instanceof Double);
		});
	}
	
	@AfterClass
	public static void deveLimparBase() {
		EntityManager em = JPAUtil.INSTANCE.getEntityManager();

		em.getTransaction().begin();

		Query query = em.createQuery("delete from Carne c");

		int registrosExcluidos = query.executeUpdate();

		em.getTransaction().commit();

		assertTrue("deve ter excluido registros", registrosExcluidos > 0);

	}
	


}