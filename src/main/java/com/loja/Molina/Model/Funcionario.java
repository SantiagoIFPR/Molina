package com.loja.Molina.Model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="funcionario")
public class Funcionario extends Pessoa implements Serializable {

	private static final long serialVersionUID = 1L;
	@ManyToOne
	private Papeis papeis;
	private String email;
	private String senha;
	public Papeis getPapeis() {
		return papeis;
	}
	public void setPapeis(Papeis papeis) {
		this.papeis = papeis;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
}