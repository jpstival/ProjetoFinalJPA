package br.com.faculdadedelta.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Entity
public class PratoPrincipal extends BaseEntity<Integer>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "Id_prato", nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer Id;

	@ManyToOne(cascade = { CascadeType.PERSIST }, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_bebida", referencedColumnName = "id_bebida", nullable = false, insertable = true, updatable = false)
	private Bebida bebida;

	@ManyToMany(cascade = { CascadeType.PERSIST }, fetch = FetchType.LAZY)
	@JoinTable(name = "acompanhamentos_prato", joinColumns = @JoinColumn(name = "id_prato"), inverseJoinColumns = @JoinColumn(name = "id_acompanhamento"))
	private List<Acompanhamento> acompanhamentos;

	@ManyToOne(cascade = { CascadeType.PERSIST }, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_carne", referencedColumnName = "id_carne", nullable = false, insertable = true, updatable = false)
	private Carne carne;

	@Temporal(TemporalType.TIMESTAMP)
	private Date data;

	public Integer getId() {
		return Id;
	}

	public Bebida getBebida() {
		return bebida;
	}

	public void setBebida(Bebida bebida) {
		this.bebida = bebida;
	}

	public List<Acompanhamento> getAcompanhamentos() {
		if (acompanhamentos == null) {
			acompanhamentos = new ArrayList<>();
		}
		return acompanhamentos;
	}

	public Carne getCarne() {
		return carne;
	}

	public void setCarne(Carne carne) {
		this.carne = carne;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

}
