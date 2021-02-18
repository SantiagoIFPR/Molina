package com.loja.Molina.Controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.loja.Molina.Model.Estado;
import com.loja.Molina.Repository.EstadoRepository;

@Controller
public class EstadoController {
	@Autowired
	public EstadoRepository repositoryEstado;
	
	@GetMapping("/administrativo/estados/cadastrar")
	public ModelAndView add(Estado estado) {
		ModelAndView mv = new ModelAndView("/administrativo/estados/cadastro");
		mv.addObject("estado", estado);
		return mv;
	}
	
	@GetMapping("/administrativo/estados/listar")
	public ModelAndView lista() {
		ModelAndView mv = new ModelAndView("/administrativo/estados/lista");
		List<Estado> estado = repositoryEstado.findAll();
		mv.addObject("estado", estado);
		return mv;
	}
	
	@GetMapping("/administrativo/estados/editarEstado/{id}")
	public ModelAndView editar(@PathVariable("id") Long id){
		Optional<Estado> estado = repositoryEstado.findById(id);
		Estado e = estado.get();
		return add(e);
		
	}
	@GetMapping("/administrativo/estados/removerEstado/{id}")
	public ModelAndView remover(@PathVariable("id") Long id){
		Optional<Estado> estado = repositoryEstado.findById(id);
		Estado e = estado.get();
		repositoryEstado.delete(e);
		return lista();
		
	}
	@PostMapping("/administrativo/estados/salvarEstado")
	public ModelAndView salvar(@Valid Estado estado, BindingResult result ) {
		repositoryEstado.save(estado);
		return lista();
	}

}
