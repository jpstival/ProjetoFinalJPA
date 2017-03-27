package br.com.faculdadedelta.utilteste;

import javax.persistence.*;

import org.junit.*;

import br.com.faculdadedelta.util.JPAUtil;

import static org.junit.Assert.*;

public class JPAUtilTest {

	private EntityManager em;

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
	public void deveTerInstanciaDoEntityManager() {
		assertNotNull("Instancia do EntityManager não pode ser nula!", em);
	}
	
	@Test
	public void deveFecharEntityManager() {
		em.close();
		assertFalse("Instancia do EntityManager não pode ser nula!", em.isOpen());
	}
	
	@Test
	public void deveAbrirUmaTransacao(){
		assertFalse("Transação deve estar fechada!", em.getTransaction().isActive());
		
		em.getTransaction().begin();
		
		assertTrue("Transação deve estar aberta!", em.getTransaction().isActive());
	}
}
