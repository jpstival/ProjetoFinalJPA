package br.com.faculdadedelta.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Bebida extends BaseEntity<Integer>{

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id_bebida", nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer Id;

	private String nome;

	private String tamanho;

	private Double preco;

	@OneToMany(mappedBy = "bebida", fetch = FetchType.LAZY)
	private List<PratoPrincipal> pratos;

	public Bebida() {
	}

	public Bebida(Integer id, String nome) {
		super();
		Id = id;
		this.nome = nome;
	}



	public Bebida(String nome, String tamanho, Double preco) {
		this.nome = nome;
		this.tamanho = tamanho;
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

	public String getTamanho() {
		return tamanho;
	}

	public void setTamanho(String tamanho) {
		this.tamanho = tamanho;
	}

	public Double getPreco() {
		return preco;
	}

	public void setPreco(Double preco) {
		this.preco = preco;
	}

	public List<PratoPrincipal> getPratos() {
		return pratos;
	}

}
