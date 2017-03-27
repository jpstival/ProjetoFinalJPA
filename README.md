# jpa-seed
Projeto base para utilização de JPA

Questões:
Qual a responsabilidade/objeto das anotações:

@MappedSuperclass: Faz o mapeamento de classes que são herdadas por entidades do JPA. 
@Version: Funciona como um controle de versão da entidade, ao tentar ser persistida, é verificada a versão atual com a versão anterior da entidade. 
@Entity: define que uma classe Java será uma entidade que pode ser persistida
@Table: Define o nome de tabela que uma entidade Java terá, se não for utilizada, o nome da tabela será igual ao nome da classe.
@Id: define qual coluna será a chave primária de uma entidade Java.
@GeneratedValue: especifica um valor que será gerado automaticamente para a coluna de uma entidade, geralmente usado para definir valores de colunas que são chaves primárias.
@Column: define algumas características de uma coluna de uma entidade Java, como nome, se pode ser nula ou não, tamanho e se é de valor único (unique).
@Basic: pode ser utilizado para definir se uma coluna da entidade pode ser nula ou não e também se o carregamento de seu conteúdo em uma consulta será LAZY ou EAGER.
@GeneratedValue: especifica um valor que será gerado automaticamente para a coluna de uma entidade, geralmente usado para definir valores de colunas que são chaves primárias.
@Column: define algumas características de uma coluna de uma entidade Java, como nome, se pode ser nula ou não, tamanho e se é de valor único (unique).
@Basic: pode ser utilizado para definir se uma coluna da entidade pode ser nula ou não e também se o carregamento de seu conteúdo em uma consulta será LAZY ou EAGER.
@Temporal: define qual o formato de data que um atributo do tipo Date de uma classe Java que é uma entidade terá, podendo ser DATE (somente data, no formada DD/MM/AAAA), TIME (somente a hora no formado HH:MM:SS) ou TIMESTAMP (data e hora no formato DD/MM/AAAA HH:MM:SS).


Qual a responsabilidade/objeto das anotações:

@ManyToOne: mapear o relacionamento entre entidades de muitos-para-um. 
@ManyToMany: mapear o relacionamento entre entidades de muitos para muitos. 
@OneToOne: mapear bi-direcionalmente o relacionamento entre entidades de um para um. 
@JoinColumn: em uma associação @ManyToOne, é responsável por indicar qual é a chave estrangeira requerida por esta associação. 
@JoinTable: cria uma tabela de associação que faz o relacionamento entre outras duas tabelas.


Qual a responsabilidade/objeto dos métodos do EntityManager: 

isOpen: verifica se uma entidade está aberta.
close: fecha uma entidade que está aberta.
createQuery: cria uma consulta JPQL a ser executada pelo JPA.
find: executa a busca à um objeto ou uma coleção de objetos de uma entidade.
merge: executa a alteração de dados de um objeto de uma entidade.
persist: faz a persistência de relacionamentos entre entidades, em cascata. 
remove: remove os relacionamentos entre entidades, em cascata.

Como instânciar Criteria do Hibernate através do EntityManager?  

Como abrir uma transação? 
em.getTransaction().begin();

Como fechar uma transação? 
public void fecharEntityManager() { 
	if (em.isOpen()) { 
		em.close(); 
		} 
	} 
	
Como criar e executar uma query com JPQL?

	StringBuilder jpql = new StringBuilder();
	jpql.append(" SELECT COUNT(c.id) ");
	jpql.append("   FROM PratoPrincipal p ");
	jpql.append("  INNER JOIN p.Carne c ");
	jpql.append("  INNER JOIN p.Bebida b ");
	jpql.append("  WHERE b.nome = :nome ");
	
	Query query = em.createQuery(jpql.toString());
	query.setParameter("nome", "Fanta");
	
	
Qual a responsabilidade dos valores FetchType.LAZY e FetchType.EAGER? 
Definem como será o carregamento dos objetos de uma consulta realizada pela JPA, se for LAZY trará para memória somente os objetos que atendem as condições da consulta, se for EAGER, trará todos os objetos da entidade.

Qual a responsabilidade dos valores CascadeType.PERSIST e CascadeType.REMOVE? 
PERSIST: Salva entidade filha quanto uma entidade pai é salva. 
REMOVE: Remove uma entidade filha quando se remove a entidade pai, ou vice-versa.

Como fazer uma operação BATCH (DELETE ou UPDATE) através do EntityManager?
public static void deveLimparBase() {
	EntityManager entityManager = JPAUtil.INSTANCE.getEntityManager();
	entityManager.getTransaction().begin();

	Query query = entityManager.createQuery("DELETE FROM PratoPrincipal p");
	int registrosExcluidos = query.executeUpdate();

	entityManager.getTransaction().commit();

	assertTrue("certifica que a base foi limpa", registrosExcluidos > 0);
}
.
Qual a explicação para a exception LazyInitializationException? 
Essa exceção ocorre quanto o JPA/Hibernate tenta acessar o banco para buscar informações definidas como LAZY, mas estas informações não estão mais disponíveis, ou seja, foram "descarregadas" da consulta.