package com.loja.Molina.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.loja.Molina.Model.Cliente;
import com.loja.Molina.Model.Compra;
import com.loja.Molina.Model.FormaDePagamento;
import com.loja.Molina.Model.ItensCompra;
import com.loja.Molina.Model.Produto;
import com.loja.Molina.Repository.ClienteRepository;
import com.loja.Molina.Repository.CompraRepository;
import com.loja.Molina.Repository.FormaPagamentoRepository;
import com.loja.Molina.Repository.ItensCompraRepository;
import com.loja.Molina.Repository.ProdutoRepository;

@Controller
public class CarrinhoController {

	@Autowired
	private ProdutoRepository repositoryProduto;
	
	@Autowired
	private ClienteRepository repositoryCliente;
	
	@Autowired
	private FormaPagamentoRepository repositoryFormaPg;

	@Autowired
	private CompraRepository repositoryCompra;
	
	@Autowired
	private ItensCompraRepository repositoryItens;
	
	private List<ItensCompra> ItensCompra = new ArrayList<ItensCompra>();
	private Compra compra = new Compra();
	private Cliente cliente;
	
	private void calcularTotal() {
		compra.setValorTotal(0.);
		for(ItensCompra it: ItensCompra) {
			compra.setValorTotal(compra.getValorTotal()+it.getValorTotal());
		}
	}
	
	@GetMapping("/carrinho")
	public ModelAndView carrinho() {
		ModelAndView mv = new ModelAndView("clientes/carrinho");
		calcularTotal();
		mv.addObject("compra", compra);
		mv.addObject("listaItens", ItensCompra);
		return mv;
	}
	
	@GetMapping("/adicionarCart/{id}")
	public String addcarrinho(@PathVariable Long id) {
		Optional<Produto> prod = repositoryProduto.findById(id);
		Produto produto = prod.get();

		int controle = 0;
		for (ItensCompra it : ItensCompra) {
			if (it.getProduto().getId() == produto.getId()) {
				it.setQuantidade(it.getQuantidade() + 1);
				it.setValorTotal(0.);
				it.setValorTotal(it.getValorTotal()+(it.getQuantidade() * it.getValorUnitario()));
				controle = 1;
				break;
			}
		}

		if (controle == 0) {
			ItensCompra itens = new ItensCompra();
			itens.setProduto(produto);
			itens.setValorUnitario(produto.getValor());
			itens.setQuantidade(itens.getQuantidade() + 1);
			itens.setValorTotal(itens.getValorTotal()+(itens.getQuantidade() * itens.getValorUnitario()));
			ItensCompra.add(itens);
		}
		return "redirect:/carrinho";
	}
	
	@GetMapping("/alterarQuantidade/{id}/{acao}")
	public String alterarQuantidade(@PathVariable Long id, @PathVariable Integer acao) {
		
		for (ItensCompra it : ItensCompra) {
			if (it.getProduto().getId() == (id)) {
				if(acao.equals(1)) {
					it.setQuantidade(it.getQuantidade()+1);
					it.setValorTotal(0.);
					it.setValorTotal(it.getValorTotal()+(it.getQuantidade() * it.getValorUnitario()));
				}else if (acao == 0){
					it.setQuantidade(it.getQuantidade() - 1);
					it.setValorTotal(0.);
					it.setValorTotal(it.getValorTotal()+(it.getQuantidade() * it.getValorUnitario()));
				}
				break;
			}
		}
		
		return "redirect:/carrinho";
	}
	
	@GetMapping("/removerProduto/{id}")
	public String removerProdutoCarrinho(@PathVariable Long id) {
		
		for (ItensCompra it : ItensCompra) {
			if (it.getProduto().getId() == (id)) {
				ItensCompra.remove(it);
				break;
			}
		}
		
		return "redirect:/carrinho";
	}
	
	private void buscarUsuarioLogado() {
		Authentication autenticado = SecurityContextHolder.getContext().getAuthentication();
		if(!(autenticado instanceof AnonymousAuthenticationToken)) {
			String email = autenticado.getName();
			cliente = repositoryCliente.buscarClienteEmail(email).get(0);
		}
	}
	
	@GetMapping("/finalizar")
	public ModelAndView finalizarCompra() {
		buscarUsuarioLogado();
		ModelAndView mv = new ModelAndView("clientes/finalizar");
		calcularTotal();
		mv.addObject("compra", compra);
		mv.addObject("listaItens", ItensCompra);
		mv.addObject("cliente", cliente);
		mv.addObject("formapg", repositoryFormaPg.findAll());
		return mv;
	}
	
	@PostMapping("/finalizar/confirmar")
	public ModelAndView confirmarCompra(Long idForma) {
		
		Optional<FormaDePagamento> op = repositoryFormaPg.findById(idForma);
		FormaDePagamento formaPagamento = op.get();
		ModelAndView mv = new ModelAndView("clientes/finalizou");
		compra.setFormaPagamento(formaPagamento);
		compra.setCliente(cliente);
		repositoryCompra.saveAndFlush(compra);
		
		for (ItensCompra c : ItensCompra) {
			c.setCompra(compra);
			repositoryItens.saveAndFlush(c);
		}
		ItensCompra = new ArrayList<>();
		compra = new Compra();
		
		return mv;
	}
	
}
