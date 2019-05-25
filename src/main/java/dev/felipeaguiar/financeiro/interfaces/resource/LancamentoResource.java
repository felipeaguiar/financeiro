package dev.felipeaguiar.financeiro.interfaces.resource;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.github.fge.jsonpatch.JsonPatch;

import dev.felipeaguiar.financeiro.application.lancamento.LancamentoService;
import dev.felipeaguiar.financeiro.domain.lancamento.Lancamento;
import dev.felipeaguiar.financeiro.domain.lancamento.filter.LancamentoFilter;
import dev.felipeaguiar.financeiro.interfaces.dto.LancamentoDto;
import dev.felipeaguiar.financeiro.interfaces.map.LancamentoMapper;
import dev.felipeaguiar.financeiro.interfaces.resource.util.ResourceHelper;

@RestController
@RequestMapping("/lancamento")
public class LancamentoResource {
	
	@Autowired
	private LancamentoService lancamentoService;

	@Autowired
	private ResourceHelper helper;

	@Autowired
	private LancamentoMapper mapper;

	@GetMapping
	public Page<LancamentoDto> listar(LancamentoFilter filter, Pageable pageable) {
		List<LancamentoDto> lancamentos = lancamentoService.filtrar(filter, pageable).stream()
			.map(mapper::toDto)
			.collect(Collectors.toList());
		
		Long count = lancamentoService.count(filter);
		
		return new PageImpl<>(lancamentos, pageable, count);
	}

	@GetMapping("/{id}")
	public ResponseEntity<LancamentoDto> buscarPorId(@PathVariable Long id) {
		Lancamento lancamento = lancamentoService.buscarPorId(id);
		return ResponseEntity.ok(mapper.toDto(lancamento));
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long id) {
		lancamentoService.remover(id);
	}

	@PostMapping
	public ResponseEntity<LancamentoDto> salvar(@Valid @RequestBody LancamentoDto lancamentoDto) {
		Lancamento lancamento = mapper.fromDto(lancamentoDto);
		Lancamento lancamentoSalva = lancamentoService.salvar(lancamento);

		LancamentoDto lancamentoDtoSalva = mapper.toDto(lancamentoSalva);

		URI uri = helper.getUriOf(lancamentoDtoSalva.getId());
		return ResponseEntity.created(uri).body(lancamentoDtoSalva);
	}

	@PutMapping("/{id}")
	public ResponseEntity<LancamentoDto> atualizar(@PathVariable Long id, @Valid @RequestBody LancamentoDto lancamentoDto) {
		Lancamento lancamento = mapper.fromDto(lancamentoDto);
		Lancamento lancamentoSalvo = lancamentoService.atualizar(id, lancamento);

		LancamentoDto lancamentoDtoSalvo = mapper.toDto(lancamentoSalvo);

		return ResponseEntity.ok(lancamentoDtoSalvo);
	}

	@PatchMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void patch(@PathVariable Long id, @RequestBody JsonPatch patch) throws MethodArgumentNotValidException {
		LancamentoDto lancamentoDto = mapper.toDto(lancamentoService.buscarPorId(id));

		lancamentoDto = helper.applyPatch(patch, lancamentoDto, LancamentoDto.class);

		Lancamento lancamento = mapper.fromDto(lancamentoDto);
		lancamentoService.atualizar(id, lancamento);

	}

}
