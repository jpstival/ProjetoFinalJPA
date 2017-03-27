package br.com.faculdadedelta.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Acompanhamento extends BaseEntity<Integer>{

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id_acompanhamento", nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer Id;

	private String nome;

	private int quantidade;

	private Double precoUnitario;

	public Acompanhamento() {
		super();
	}

	public Acompanhamento(String nome, int quantidade, Double precoUnitario) {
		this.nome = nome;
		this.quantidade = quantidade;
		this.precoUnitario = precoUnitario;
	}

	public Integer getId() {
		return Id;
	}

	public void setId(Integer id) {
		Id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}

	public Double getPrecoUnitario() {
		return precoUnitario;
	}

	public void setPrecoUnitario(Double precoUnitario) {
		this.precoUnitario = precoUnitario;
	}

	public Double precoAcompanhamento(int qtd, double preco) {
		return qtd * preco;
	}

}
