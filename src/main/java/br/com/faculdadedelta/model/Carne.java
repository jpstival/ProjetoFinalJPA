package br.com.faculdadedelta.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Carne extends BaseEntity<Integer>{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_carne", nullable = false)
	private Integer Id;

	private String nome;

	private Double preco;

	public Carne() {

	}

	public Carne(Integer id, String nome) {
		this.Id = id;
		this.nome = nome;
	}

	public Carne(String nome, Double preco) {
		this.nome = nome;
		this.preco = preco;
	}

	public Integer getId() {
		return Id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Double getPreco() {
		return preco;
	}

	public void setPreco(Double preco) {
		this.preco = preco;
	}

}
