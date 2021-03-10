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
import com.loja.Molina.Model.Venda;
import com.loja.Molina.Model.FormaDePagamento;
import com.loja.Molina.Model.ItensVenda;
import com.loja.Molina.Model.Produto;
import com.loja.Molina.Repository.ClienteRepository;
import com.loja.Molina.Repository.VendaRepository;
import com.loja.Molina.Repository.FormaPagamentoRepository;
import com.loja.Molina.Repository.ItensVendaRepository;
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
	private VendaRepository repositoryCompra;
	
	@Autowired
	private ItensVendaRepository repositoryItens;
	
	private List<ItensVenda> ItensVenda = new ArrayList<ItensVenda>();
	private Venda venda = new Venda();
	private Cliente cliente;
	
	private void calcularTotal() {
		venda.setValorTotal(0.);
		for(ItensVenda it: ItensVenda) {
			venda.setValorTotal(venda.getValorTotal()+it.getValorTotal());
		}
	}
	
	@GetMapping("/carrinho")
	public ModelAndView carrinho() {
		ModelAndView mv = new ModelAndView("clientes/carrinho");
		calcularTotal();
		mv.addObject("venda", venda);
		mv.addObject("listaItens", ItensVenda);
		return mv;
	}
	
	@GetMapping("/adicionarCart/{id}")
	public String addcarrinho(@PathVariable Long id) {
		Optional<Produto> prod = repositoryProduto.findById(id);
		Produto produto = prod.get();

		int controle = 0;
		for (ItensVenda it : ItensVenda) {
			if (it.getProduto().getId() == produto.getId()) {
				it.setQuantidade(it.getQuantidade() + 1);
				it.setValorTotal(0.);
				it.setValorTotal(it.getValorTotal()+(it.getQuantidade() * it.getValorUnitario()));
				controle = 1;
				break;
			}
		}

		if (controle == 0) {
			ItensVenda itens = new ItensVenda();
			itens.setProduto(produto);
			itens.setValorUnitario(produto.getValor());
			itens.setQuantidade(itens.getQuantidade() + 1);
			itens.setValorTotal(itens.getValorTotal()+(itens.getQuantidade() * itens.getValorUnitario()));
			ItensVenda.add(itens);
		}
		return "redirect:/carrinho";
	}
	
	@GetMapping("/alterarQuantidade/{id}/{acao}")
	public String alterarQuantidade(@PathVariable Long id, @PathVariable Integer acao) {
		
		for (ItensVenda it : ItensVenda) {
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
		
		for (ItensVenda it : ItensVenda) {
			if (it.getProduto().getId() == (id)) {
				ItensVenda.remove(it);
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
		mv.addObject("venda", venda);
		mv.addObject("listaItens", ItensVenda);
		mv.addObject("cliente", cliente);
		mv.addObject("formapg", repositoryFormaPg.findAll());
		return mv;
	}
	
	@PostMapping("/finalizar/confirmar")
	public ModelAndView confirmarCompra(Long idForma) {
		
		Optional<FormaDePagamento> op = repositoryFormaPg.findById(idForma);
		FormaDePagamento formaPagamento = op.get();
		ModelAndView mv = new ModelAndView("clientes/finalizou");
		venda.setFormaPagamento(formaPagamento);
		venda.setCliente(cliente);
		repositoryCompra.saveAndFlush(venda);
		
		for (ItensVenda c : ItensVenda) {
			c.setVenda(venda);
			repositoryItens.saveAndFlush(c);
		}
		ItensVenda = new ArrayList<>();
		venda = new Venda();
		
		return mv;
	}
	
}
