// src/main/resources/static/js/formDemanda.js

// Adiciona o event listener quando o DOM estiver carregado
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('formCadastroDemanda');
    if (form) {
        // Se você mantiver o botão como type="button" e onclick="salvarDemanda()",
        // este event listener no 'submit' do formulário é um backup ou alternativa.
        // Se o botão for type="submit", este listener é crucial.
        form.addEventListener('submit', function(event) {
            event.preventDefault(); // Impede o envio tradicional do formulário
            salvarDemanda();
        });
    }
});

function salvarDemanda() {
    // Coleta dos dados do Requerente
    const nomeRequerente = document.getElementById('nome').value;
    const cpfRequerente = document.getElementById('cpf').value;
    const emailRequerente = document.getElementById('email').value;
    const telefoneRequerente = document.getElementById('telefone').value;
    const senhaRequerente = document.getElementById('senha').value;
    const enderecoRequerente = document.getElementById('cep').value; // Campo CEP no HTML é usado para endereço

    // Coleta dos dados da Requisição
    const idCategoriaRequisicao = document.getElementById('categoria').value;
    const descricaoRequisicao = document.getElementById('descricao').value;
    const frequenciaRequisicao = document.getElementById('frequencia').value;
    const infoCompRequisicao = document.getElementById('informacoesComplementares').value;

    // Monta o objeto de payload para a API
    const payload = {
        // Requerente
        cpfRequerente: cpfRequerente,
        nomeRequerente: nomeRequerente,
        emailRequerente: emailRequerente,
        telefoneRequerente: telefoneRequerente,
        enderecoRequerente: enderecoRequerente,
        senhaRequerente: senhaRequerente,
        // Requisição
        idCategoriaRequisicao: parseInt(idCategoriaRequisicao), // Converte para número, se necessário
        descricaoRequisicao: descricaoRequisicao,
        frequenciaRequisicao: frequenciaRequisicao,
        infoCompRequisicao: infoCompRequisicao
    };

    // Envia para o endpoint do backend
    fetch('/api/v1/requerentes/cadastrar-com-requisicao', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
    })
        .then(response => {
            return response.json().then(data => { // Tenta ler JSON independentemente do status
                if (!response.ok) {
                    // Joga um erro com a mensagem do backend se disponível, ou um genérico
                    const errorMsg = data.error || `Erro HTTP ${response.status} ao cadastrar demanda.`;
                    throw new Error(errorMsg);
                }
                return data; // Retorna os dados JSON se a resposta for OK
            }).catch(jsonParseError => {
                // Se o corpo não for JSON válido (mesmo para erro) OU
                // se era uma resposta OK mas o parse falhou (improvável com backend bem formado)
                if (!response.ok) {
                    throw new Error(`Erro HTTP ${response.status}: ${response.statusText || 'Não foi possível processar a resposta do servidor.'}`);
                }
                // Se era OK mas não JSON, o que é inesperado
                console.error("Resposta OK mas não era JSON:", jsonParseError);
                throw new Error('Resposta inesperada do servidor (não JSON).');
            });
        })
        .then(data => {
            if (data.message) {
                alert(data.message); // Ex: "Requerente e requisição cadastrados com sucesso!"
                const form = document.getElementById('formCadastroDemanda');
                if (form) {
                    form.reset(); // Limpa o formulário
                }
            } else {
                alert('Cadastro realizado, mas resposta inesperada.');
            }
        })
        .catch(error => {
            console.error('Erro no fetch ao cadastrar demanda:', error);
            alert('Erro ao cadastrar demanda: ' + error.message);
        });
}