package com.loja.Molina.Controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.loja.Molina.Model.Categoria;
import com.loja.Molina.Repository.CategoriaRepository;

@Controller
public class CategoriaController {

	@Autowired
	private CategoriaRepository repositoryCategoria;
	
	@GetMapping("administrativo/categorias/cadastrar")
	public ModelAndView add(Categoria categoria) {
		ModelAndView mv = new ModelAndView("/administrativo/categorias/cadastro");
		mv.addObject("categoria", categoria);
		return mv;
	}
	
	@GetMapping("administrativo/categorias/listar")
	public ModelAndView listar() {
		ModelAndView mv = new ModelAndView("/administrativo/categorias/lista");
		mv.addObject("categoria", repositoryCategoria.findAll());
		return mv;
	}
	
	@GetMapping("administrativo/categorias/editarCategoria/{id}")
	public ModelAndView editar(@PathVariable("id") long id) {
		Optional<Categoria> op = repositoryCategoria.findById(id);
		Categoria cad = op.get();
		return add(cad);
	}
	
	@GetMapping("administrativo/categorias/removerCategoria/{id}")
	public ModelAndView remover(@PathVariable("id") long id) {
		Optional<Categoria> op = repositoryCategoria.findById(id);
		Categoria cat = op.get();
		repositoryCategoria.delete(cat);
		return listar();
	}
	
	@PostMapping("administrativo/categorias/salvarCategoria")
	public ModelAndView salvar(Categoria categoria) {
		repositoryCategoria.save(categoria);
		return listar();
	}
}
