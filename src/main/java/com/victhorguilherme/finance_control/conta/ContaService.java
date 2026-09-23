package com.victhorguilherme.finance_control.conta;
import com.victhorguilherme.finance_control.exceptions.AccountNameDuplicate;
import com.victhorguilherme.finance_control.exceptions.ResourceNotFound;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ContaService {

    private final ContaRepository contaRepository;

    public ContaService(ContaRepository contaRepository) {
        this.contaRepository = contaRepository;
    }


    public Conta criarConta(String nome) {
        String nomeLimpo = nome.strip();
        if(contaRepository.existsByNomeIgnoreCase(nomeLimpo)){
            throw new AccountNameDuplicate("Já existe uma conta cadastrada com o nome: " + nomeLimpo);
        }

        Conta novaConta = new Conta(nomeLimpo);
        return contaRepository.save(novaConta);
    }

    public List<Conta> listarContas() {
        return contaRepository.findAll();
    }

    public Conta buscarPorId(long id) {
        return contaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Conta de ID: " + id + " , não encontrada."));

}

    public Conta atualizarConta(long id, String nome){

        Conta contaEncontrada = buscarPorId(id);
        String nomeLimpo = nome.strip();

        if (contaRepository.existsByNomeIgnoreCaseAndIdNot(nomeLimpo, id)) {
            throw new AccountNameDuplicate("Já existe uma conta cadastrada com o nome: " + nomeLimpo);
        }

        contaEncontrada.setNome(nomeLimpo);
        return contaRepository.save(contaEncontrada);
    }

    public void deletarConta(long id){
        buscarPorId(id);
        contaRepository.deleteById(id);
    }



}
