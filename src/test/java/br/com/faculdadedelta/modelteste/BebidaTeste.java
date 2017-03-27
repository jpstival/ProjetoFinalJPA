package br.com.faculdadedelta.modelteste;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import br.com.faculdadedelta.model.Bebida;
import br.com.faculdadedelta.util.JPAUtil;

public class BebidaTeste {

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
	public void deveSalvarBebida() {
		Bebida bebida = new Bebida("Fanta", "Grande", 8.00);

		assertTrue("entidade não possui ID.", bebida.isTransient());

		em.getTransaction().begin();
		em.persist(bebida);
		em.getTransaction().commit();

		assertFalse("entidade agora tem id", bebida.isTransient());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void deveConsultarPreco() {
		deveSalvarBebida();
		String filtro = "Fanta";

		Query query = em.createQuery("select b.preco from Bebida b where b.nome like :nome");
		query.setParameter("nome", filtro);

		List<Double> listaPreco = query.getResultList();

		assertFalse("verifica se há registros na lista", listaPreco.isEmpty());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void deveConsultarApenasIdeNome() {
		deveSalvarBebida();

		Query query = em.createQuery("SELECT new Bebida(b.id, b.nome) FROM Bebida b");

		List<Bebida> bebidas = (List<Bebida>) query.getResultList();

		assertFalse("deve ter encontrado bebidas", bebidas.isEmpty());
		bebidas.forEach(bebida -> {
			assertNull("não deve ter preço", bebida.getPreco());
		});

	}

	@SuppressWarnings("unchecked")
	@Test
	public void deveConsultarIdeNome() {
		deveSalvarBebida();

		Query query = em.createQuery("SELECT b.id, b.nome FROM Bebida b");

		List<Object[]> bebidas = query.getResultList();

		assertFalse("deve ter encontrado bebidas", bebidas.isEmpty());
		bebidas.forEach(linha -> {

			assertTrue("primeiro iten é o ID", linha[0] instanceof Integer);
			assertTrue("primeiro iten é o Nome", linha[1] instanceof String);

			Bebida bebida = new Bebida((Integer) linha[0], (String) linha[1]);
			assertNotNull("deve ter instanciado", bebida);
		});

	}
	
	@Test
	public void deveContarQtdBebidas(){
		deveSalvarBebida();
		
		Query query = em.createQuery("select count(b.id) from Bebida b");
		Long qtdRegistros = (Long) query.getSingleResult();
		
		assertTrue("deve ter pelo menos um registro", qtdRegistros > 0);
	}
	
	@Test(expected = NonUniqueResultException.class)
	public void naoDeveFuncionarSingleResult(){
		deveSalvarBebida();
		deveSalvarBebida();
		
		Query query = em.createQuery("Select b.id from Bebida b");

		
		query.getSingleResult();
		
		fail("deveria ter lançado excessão NonUniqueResultExceptions");
	}
	
	@Test(expected = NoResultException.class)
	public void naoDeveFuncionarSingleResultComNenhumRegistro(){
		deveSalvarBebida();
		deveSalvarBebida();
		
		Query query = em.createQuery("Select b,id from Bebida b where b.nome = :nome");
		query.setParameter("nome", "Coca-Cola");
		query.getSingleResult();
		
		fail("deveria ter lançado excessão NoResultException");
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void deveConsultarBebidasPorParteDoNome() {
		deveSalvarBebida();
		deveSalvarBebida();
		deveSalvarBebida();

		Criteria criteria = criarCriteria(Bebida.class).add(Restrictions.ilike("nome", "Fanta", MatchMode.ANYWHERE))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		List<Bebida> bebidas = criteria.list();

		assertFalse("deve ter encontrado bebidas", bebidas.isEmpty());

	}
	
	@Test
	public void deveConsultarIdENomeBebida(){
		deveSalvarBebida();
		deveSalvarBebida();
		deveSalvarBebida();
		
		ProjectionList projections	= Projections.projectionList();
			projections.add(Projections.property("b.id").as("id"));
			projections.add(Projections.property("b.nome").as("nome"));
			
		Criteria criteria = criarCriteria(Bebida.class, "b")
							.setProjection(projections)
							.setResultTransformer(Criteria.PROJECTION);
		
		List<Object[]> bebidas = criteria.list();
		
		assertFalse("deve ter bebidas", bebidas.isEmpty());
		
		bebidas.forEach(bebida -> {
			assertTrue("primeiro iten é o id", bebida[0] instanceof Integer);
			assertTrue("primeiro iten é o nome", bebida[1] instanceof String);
		});
	}
	
	@Test
	public void deveConsultarIdENomeBebidaMap(){
		deveSalvarBebida();
		deveSalvarBebida();
		deveSalvarBebida();
		
		ProjectionList projections	= Projections.projectionList();
			projections.add(Projections.property("b.id").as("id"));
			projections.add(Projections.property("b.nome").as("nome"));
			
		Criteria criteria = criarCriteria(Bebida.class, "b")
							.setProjection(projections)
							.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		
		List<Map<String, Object>> bebidas = criteria.list();
		
		assertFalse("deve ter bebidas", bebidas.isEmpty());
		
		bebidas.forEach(bebida -> {
			bebida.forEach((chave, valor) -> {
				assertNotNull(chave);
				assertNotNull(valor);
			});
		});
	}
	
	@AfterClass
	public static void deveLimparBase() {
		EntityManager em = JPAUtil.INSTANCE.getEntityManager();

		em.getTransaction().begin();

		Query query = em.createQuery("delete from Bebida b");

		int registrosExcluidos = query.executeUpdate();

		em.getTransaction().commit();

		assertTrue("deve ter excluido registros", registrosExcluidos > 0);

	}
	
}