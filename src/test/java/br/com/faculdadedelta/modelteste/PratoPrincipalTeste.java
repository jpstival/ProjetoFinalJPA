package br.com.faculdadedelta.modelteste;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import br.com.faculdadedelta.model.Acompanhamento;
import br.com.faculdadedelta.model.Bebida;
import br.com.faculdadedelta.model.Carne;
import br.com.faculdadedelta.model.PratoPrincipal;
import br.com.faculdadedelta.util.JPAUtil;

public class PratoPrincipalTeste {

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
	public void deveSalvarPratoPrincipalComRelacionamentoEmCascata() {
		PratoPrincipal prato = criarPrato();
		prato.getAcompanhamentos().add(criarAcompanhamento("Arroz", 1, 4.00));
		prato.getAcompanhamentos().add(criarAcompanhamento("feijao", 1, 3.00));
		prato.getAcompanhamentos().add(criarAcompanhamento("Ovo", 2, 1.00));

		assertTrue("prato não foi persistido ainda", prato.isTransient());

		em.getTransaction().begin();
		em.persist(prato);
		em.getTransaction().commit();

		assertFalse("prato foi persistido", prato.isTransient());

		assertFalse("Carne foi persistida", prato.getCarne().isTransient());

		prato.getAcompanhamentos().forEach(acompanhamento -> {
			assertFalse("acompanhamento foi persistido", acompanhamento.isTransient());
		});
	}

	@Test(expected = IllegalStateException.class)
	public void naoDeveFazerMergeEmCascata() {
		PratoPrincipal prato = criarPrato();

		prato.getAcompanhamentos().add(criarAcompanhamento("Arroz", 1, 4.00));
		prato.getAcompanhamentos().add(criarAcompanhamento("feijao", 1, 3.00));

		assertTrue("prato não foi persistido ainda", prato.isTransient());

		em.getTransaction().begin();
		em.merge(prato);
		em.getTransaction().commit();

		fail("não deveria fazer merge em cascata");
	}

	@Test
	public void deveConsultarQtdItensPratoPrincipal() {
		PratoPrincipal prato = criarPrato("picanha", "Coca-Cola");

		for (int i = 0; i < 10; i++) {
			prato.getAcompanhamentos().add(criarAcompanhamento("produto" + i, i, 1.00));
		}

		em.getTransaction().begin();
		em.persist(prato);
		em.getTransaction().commit();

		assertFalse("deve ter salvado a prato", prato.isTransient());

		int qtdItens = prato.getAcompanhamentos().size();

		StringBuilder hql = new StringBuilder();
		hql.append("SELECT COUNT(a.id) ");
		hql.append("   FROM PratoPrincipal p ");
		hql.append("  INNER JOIN p.acompanhamentos a ");
		hql.append("   INNER JOIN p.carne c ");
		hql.append(" WHERE c.nome = :nome ");

		Query query = em.createQuery(hql.toString());

		query.setParameter("nome", "picanha");

		Long qtdRegistros = (Long) query.getSingleResult();

		assertEquals("devem ser iguais", qtdItens, qtdRegistros.intValue());

	}

	@AfterClass
	public static void deveLimparBase() {
		EntityManager em = JPAUtil.INSTANCE.getEntityManager();

		em.getTransaction().begin();

		Query query = em.createQuery("delete from PratoPrincipal c");

		int registrosExcluidos = query.executeUpdate();

		em.getTransaction().commit();

		assertTrue("deve ter excluido registros", registrosExcluidos > 0);

	}

	@Test
    @SuppressWarnings("unchecked")
    public void deveConsultarPratosPorNomeCarneUsandoSubquery() {
        salvarPrato();
        
        DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Carne.class, "c")
                .add(Restrictions.in("c.id", 1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
                .setProjection(Projections.property("c.nome"));
        
        Criteria criteria = criarCriteria(PratoPrincipal.class, "p")
                .createAlias("p.carne", "carne")
                .add(Subqueries.propertyIn("carne.nome", detachedCriteria));
        
        List<PratoPrincipal> pratos = criteria
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
        
        assertTrue("verifica se a quantidade de vendas é pelo menos 1", pratos.size() >= 1);
        
        pratos.forEach(prato -> assertFalse("trouxe os itens corretamente", prato.getCarne().isTransient()));
    }
	
	@Test
	public void deveConsultarQtdVendaCarne() {
		criarPrato();
		criarPrato();
		criarPrato();
		
		Criteria criteria = criarCriteria(PratoPrincipal.class, "p").createAlias("p.carne", "c")
				.setProjection(Projections.rowCount()).add(Restrictions.eq("c.nome", "maminha"))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		Long qtdCompras = (Long) criteria.uniqueResult();

		assertTrue("deve ter pelo menos 3 pratos com maminha", qtdCompras >= 3);
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void deveConsultarPratosDaUltimaSemana() {
		salvarPrato();
		salvarPrato();
		salvarPrato();
		
		Calendar ultimaSemana = Calendar.getInstance();
		ultimaSemana.add(Calendar.WEEK_OF_YEAR, -1);

		Criteria criteria = criarCriteria(PratoPrincipal.class, "p");
		criteria.add(Restrictions.between("p.data", ultimaSemana.getTime(), new Date()))
				.setProjection(Projections.rowCount());

		Long qtdVendas = (Long) criteria.setResultTransformer(criteria.DISTINCT_ROOT_ENTITY).uniqueResult();

		assertTrue("qtd de pratos é pelo menos 3", qtdVendas >= 3);
	}
	
	private Acompanhamento criarAcompanhamento(String nome, int quantidade, Double precoUnitario) {
		Acompanhamento acompanhamento = new Acompanhamento();
		acompanhamento.setNome(nome);
		acompanhamento.setQuantidade(1);
		acompanhamento.setPrecoUnitario(3.00);

		return acompanhamento;
	}

	private PratoPrincipal criarPrato() {
		return criarPrato(null, null);
	}

	private PratoPrincipal criarPrato(String nomeCarne, String nomeBebida) {
		Carne carne = new Carne();
		carne.setNome(nomeCarne != null ? nomeCarne : "maminha");
		carne.setPreco(8.00);
		;

		Bebida bebida = new Bebida();
		bebida.setNome(nomeBebida != null ? nomeBebida : "Fanta");
		carne.setPreco(5.00);
		;

		PratoPrincipal prato = new PratoPrincipal();
		prato.setData(new Date());
		prato.setCarne(carne);
		prato.setBebida(bebida);

		return prato;
	}
	
	private void salvarPrato() {
		Carne carne = new Carne();
		carne.setNome("maminha");
		carne.setPreco(8.00);
		;

		Bebida bebida = new Bebida();
		bebida.setNome("Fanta");
		carne.setPreco(5.00);
		;

		PratoPrincipal prato = new PratoPrincipal();
		prato.setData(new Date());
		prato.setCarne(carne);
		prato.setBebida(bebida);

		em.getTransaction().begin();
		em.persist(prato);
		em.getTransaction().commit();
	}
}