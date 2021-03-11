package com.loja.Molina.Controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.loja.Molina.Model.Fornecedor;
import com.loja.Molina.Repository.CidadeRepository;
import com.loja.Molina.Repository.FornecedorRepository;

@Controller
public class FornecedorController {
	@Autowired
	private FornecedorRepository repositoryFornecedor;
	
	@Autowired
	public CidadeRepository repositoryCidade;
	
	
	@GetMapping("/administrativo/fornecedores/cadastrar")
	public ModelAndView add(Fornecedor fornecedor) {
		ModelAndView mv = new ModelAndView("/administrativo/fornecedores/cadastro");
		mv.addObject("fornecedor", fornecedor);
		mv.addObject("cidades", repositoryCidade.findAll());
		return mv;
	}
	
	@GetMapping("/administrativo/fornecedores/listar")
	public ModelAndView listar() {
		ModelAndView mv = new ModelAndView("/administrativo/fornecedores/lista");
		mv.addObject("fornecedor", repositoryFornecedor.findAll());
		return mv;
	}
	
	@GetMapping("/administrativo/fornecedores/editarFornecedor/{id}")
	public ModelAndView editar(@PathVariable("id") long id) {
		Optional<Fornecedor> op = repositoryFornecedor.findById(id);
		Fornecedor forn = op.get();
		return add(forn);
	}
	
	@GetMapping("/administrativo/fornecedores/removerFornecedor/{id}")
	public ModelAndView remover(@PathVariable("id") long id) {
		Optional<Fornecedor> op = repositoryFornecedor.findById(id);
		Fornecedor forn = op.get();
		repositoryFornecedor.delete(forn);
		return listar();
	}
	
	@PostMapping("administrativo/fornecedores/salvarFornecedor")
	public ModelAndView salvar(Fornecedor fornecedor) {
		repositoryFornecedor.save(fornecedor);
		return listar();
	}
}

