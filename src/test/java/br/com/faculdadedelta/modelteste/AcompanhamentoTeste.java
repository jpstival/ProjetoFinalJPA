package br.com.faculdadedelta.modelteste;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import br.com.faculdadedelta.model.Acompanhamento;
import br.com.faculdadedelta.util.JPAUtil;

public class AcompanhamentoTeste {

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
	public void deveSalvarAcompanhamento() {
		Acompanhamento acompanhamento = new Acompanhamento();
		acompanhamento.setNome("Arroz");
		acompanhamento.setQuantidade(1);
		acompanhamento.setPrecoUnitario(4.00);
		
		assertTrue("entidade não possui ID.", acompanhamento.isTransient());

		em.getTransaction().begin();
		em.persist(acompanhamento);
		em.getTransaction().commit();

		assertFalse("entidade agora tem id ainda.", acompanhamento.isTransient());

	}

	@Test
	public void devePesquisarAcompanhamento() {
		for (int i = 0; i < 10; i++) {
			deveSalvarAcompanhamento();
		}

		TypedQuery<Acompanhamento> query = em.createQuery("SELECT a FROM Acompanhamento a", Acompanhamento.class);

		List<Acompanhamento> acompanhamentos = query.getResultList();

		assertFalse("deve ter itens na lista.", acompanhamentos.isEmpty());
		assertTrue("deve ter pelo menos 10 itens na lista.", acompanhamentos.size() >= 10);
	}

	@Test
	public void deveAlterarAcompanhamento() {
		deveSalvarAcompanhamento();

		TypedQuery<Acompanhamento> query = em.createQuery("Select a from Acompanhamento a", Acompanhamento.class).setMaxResults(1);
		Acompanhamento acompanhamento = query.getSingleResult();

		assertNotNull("deve ter encontrado um acompanhamento", acompanhamento);

		Integer versao = acompanhamento.getVersion();

		em.getTransaction().begin();

		acompanhamento.setPrecoUnitario(5.00);;

		acompanhamento = em.merge(acompanhamento);

		em.getTransaction().commit();

		assertNotEquals("deve ter versao incrementada", versao.intValue(), acompanhamento.getVersion().intValue());
	}

	@Test
	public void deveExcluirAcompanhamento() {
		deveSalvarAcompanhamento();

		TypedQuery<Integer> query = em.createQuery("select max(a.id) from Acompanhamento a", Integer.class);
		Integer id = query.getSingleResult();

		em.getTransaction().begin();

		Acompanhamento acompanhamento = em.find(Acompanhamento.class, id);
		em.remove(acompanhamento);

		em.getTransaction().commit();

		Acompanhamento acompanhamentoExcluido = em.find(Acompanhamento.class, id);

		assertNull("não deve encontrar o acompanhamento", acompanhamentoExcluido);
	}
	
	@Test
	public void deveConsultarTodosAcompanhamentos() {
		deveSalvarAcompanhamento();
		deveSalvarAcompanhamento();
		deveSalvarAcompanhamento();
		
		Criteria criteria = criarCriteria(Acompanhamento.class, "A");

		List<Acompanhamento> acompanhamentos = criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

		assertTrue("verifica se a quantidade de acompanhamentos é pelo menos 3", acompanhamentos.size() >= 3);

		acompanhamentos.forEach(acompanhamento -> assertFalse(acompanhamento.isTransient()));
	}
	
	@Test
	public void deveConsultarMaiorIdAcompnhamento() {
		deveSalvarAcompanhamento();
		deveSalvarAcompanhamento();

		Criteria criteria = criarCriteria(Acompanhamento.class, "A");
		criteria.setProjection(Projections.max("A.id"));

		Integer maiorId = (Integer) criteria.setResultTransformer(criteria.PROJECTION).uniqueResult();

		assertTrue("verifica se a quantidade de acompanhamentos é pelo menos 2", maiorId >= 2);

	}
	
	@Test
	public void deveConsultarDezPrimeirosRegistro() {
		deveSalvarAcompanhamento();
		deveSalvarAcompanhamento();
		deveSalvarAcompanhamento();
		deveSalvarAcompanhamento();
		deveSalvarAcompanhamento();
		deveSalvarAcompanhamento();
		deveSalvarAcompanhamento();
		deveSalvarAcompanhamento();
		deveSalvarAcompanhamento();
		deveSalvarAcompanhamento();
		deveSalvarAcompanhamento();

		Criteria criteria = criarCriteria(Acompanhamento.class).setFirstResult(1).setMaxResults(10)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		List<Acompanhamento> acompanhamentos = criteria.list();

		assertFalse("deve ter acompanhamentos", acompanhamentos.isEmpty());

		assertTrue("deve ter 10 acompanhamentos", acompanhamentos.size() == 10);

		acompanhamentos.forEach(acompanhamento -> assertFalse(acompanhamento.isTransient()));
	}
	
	@AfterClass
	public static void deveLimparBase(){
		EntityManager em = JPAUtil.INSTANCE.getEntityManager();
		
		em.getTransaction().begin();
		
		Query query = em.createQuery("delete from Acompanhamento");
		
		int registrosExcluidos = query.executeUpdate();
		
		em.getTransaction().commit();
		
		assertTrue("deve ter excluido registros", registrosExcluidos > 0);
		
	}
}

