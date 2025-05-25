// src/main/resources/static/js/formDemanda.js

// Funções de máscara
function mascaraCpfCnpj(input) {
    let value = input.value.replace(/\D/g, '');
    if (value.length <= 11) { // CPF
        value = value.replace(/^(\d{3})(\d)/, '$1.$2');
        value = value.replace(/^(\d{3})\.(\d{3})(\d)/, '$1.$2.$3');
        value = value.replace(/\.(\d{3})(\d{1,2})$/, '.$1-$2');
        input.value = value.substring(0, 14);
    } else { // CNPJ
        value = value.substring(0, 14);
        value = value.replace(/^(\d{2})(\d)/, '$1.$2');
        value = value.replace(/^(\d{2})\.(\d{3})(\d)/, '$1.$2.$3');
        value = value.replace(/\.(\d{3})(\d)/, '.$1/$2');
        value = value.replace(/(\d{4})(\d)/, '$1-$2');
        input.value = value.substring(0, 18);
    }
}

function mascaraCelular(celularInput) {
    let value = celularInput.value.replace(/\D/g, '');
    if (value.length > 11) value = value.substring(0, 11);
    if (value.length > 10) {
        value = value.replace(/^(\d{2})(\d{5})(\d{4}).*/, '($1) $2-$3');
    } else if (value.length > 6) {
        value = value.replace(/^(\d{2})(\d{4})(\d{0,4}).*/, '($1) $2-$3');
    } else if (value.length > 2) {
        value = value.replace(/^(\d{2})(\d*)/, '($1) $2');
    } else if (value.length > 0) {
        value = value.replace(/^(\d*)/, '($1');
    }
    celularInput.value = value;
}

function mascaraCep(cepInput) {
    let value = cepInput.value.replace(/\D/g, '');
    value = value.replace(/^(\d{5})(\d)/, '$1-$2');
    cepInput.value = value.substring(0, 9);
}

document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('formCadastroDemanda');
    if (!form) return;

    // Os inputs no formCadastroDemanda.html têm IDs simples como 'nome', 'cpf', 'email'.
    // Os 'name' atributos são 'nomeRequerente', 'cpfRequerente', etc.
    // As máscaras devem ser aplicadas aos inputs pelos seus IDs.
    const nomeInput = form.querySelector('#nome'); // id="nome" no html
    const cpfInput = form.querySelector('#cpf');   // id="cpf" no html
    const emailInput = form.querySelector('#email'); // id="email" no html
    const telefoneInput = form.querySelector('#telefone'); // id="telefone" no html
    const senhaInput = form.querySelector('#senha'); // id="senha" no html
    const cepInput = form.querySelector('#cep');     // id="cep" no html

    // Inputs da requisição
    const categoriaSelect = form.querySelector('#categoria'); // id="categoria" no html
    const descricaoTextarea = form.querySelector('#descricao'); // id="descricao" no html
    const frequenciaSelect = form.querySelector('#frequencia'); // id="frequencia" no html
    const infoCompTextarea = form.querySelector('#informacoesComplementares'); // id="informacoesComplementares" no html


    if (cpfInput) {
        cpfInput.addEventListener('input', function() { mascaraCpfCnpj(this); });
    }
    if (telefoneInput) {
        telefoneInput.addEventListener('input', function() { mascaraCelular(this); });
    }
    if (cepInput) {
        cepInput.addEventListener('input', function() { mascaraCep(this); });
    }
});

function salvarDemanda() {
    const form = document.getElementById('formCadastroDemanda');
    if (!form) {
        console.error("Formulário #formCadastroDemanda não encontrado.");
        return;
    }

    let mensagemGeralDiv = document.getElementById('formDemandaMensagemGeral');
    if (!mensagemGeralDiv) { // Cria dinamicamente se não existir
        mensagemGeralDiv = document.createElement('div');
        mensagemGeralDiv.id = 'formDemandaMensagemGeral';
        mensagemGeralDiv.className = 'form-message';
        mensagemGeralDiv.style.display = 'none';
        const formHeader = form.querySelector('.form-header');
        if (formHeader && formHeader.parentNode) { // Adiciona após o header do formulário
            formHeader.parentNode.insertBefore(mensagemGeralDiv, formHeader.nextSibling);
        } else { // Adiciona no topo do formulário como fallback
            form.insertBefore(mensagemGeralDiv, form.firstChild);
        }
    }

    // Mapeia nomes de campos do DTO Java para IDs dos spans de erro no HTML
    const errorSpanIds = {
        cpfRequerente: 'cpfRequerenteErrorMessage',
        nomeRequerente: 'nomeRequerenteErrorMessage',
        emailRequerente: 'emailRequerenteErrorMessage',
        telefoneRequerente: 'telefoneRequerenteErrorMessage',
        enderecoRequerente: 'enderecoRequerenteErrorMessage',
        senhaRequerente: 'senhaRequerenteErrorMessage',
        idCategoriaRequisicao: 'idCategoriaRequisicaoErrorMessage',
        descricaoRequisicao: 'descricaoRequisicaoErrorMessage',
        frequenciaRequisicao: 'frequenciaRequisicaoErrorMessage',
        infoCompRequisicao: 'infoCompRequisicaoErrorMessage'
    };

    // Mapeia nomes de campos do DTO Java para nomes amigáveis
    const fieldDisplayNames = {
        cpfRequerente: 'CPF/CNPJ',
        nomeRequerente: 'Nome',
        emailRequerente: 'E-mail',
        telefoneRequerente: 'Celular',
        enderecoRequerente: 'CEP',
        senhaRequerente: 'Senha',
        idCategoriaRequisicao: 'Categoria',
        descricaoRequisicao: 'Descrição do Serviço',
        frequenciaRequisicao: 'Frequência',
        infoCompRequisicao: 'Informações Complementares'
    };

    // Mapeia os nomes dos campos do DTO para os IDs dos inputs no HTML
    // Isso é importante para focar no campo correto.
    const inputIdsForFocus = {
        cpfRequerente: 'cpf', // ID do input no HTML
        nomeRequerente: 'nome',
        emailRequerente: 'email',
        telefoneRequerente: 'telefone',
        enderecoRequerente: 'cep',
        senhaRequerente: 'senha',
        idCategoriaRequisicao: 'categoria',
        descricaoRequisicao: 'descricao',
        frequenciaRequisicao: 'frequencia',
        infoCompRequisicao: 'informacoesComplementares'
    };


    function limparMensagens() {
        if (mensagemGeralDiv) {
            mensagemGeralDiv.style.display = 'none';
            mensagemGeralDiv.textContent = '';
            mensagemGeralDiv.className = 'form-message';
        }
        for (const fieldKey in errorSpanIds) {
            const spanId = errorSpanIds[fieldKey];
            const span = document.getElementById(spanId);
            if (span) {
                span.textContent = '';
                span.style.display = 'none';
            }
        }
    }

    function exibirMensagem(elemento, mensagem, tipo) {
        if (elemento) {
            elemento.textContent = mensagem;
            if (elemento.id === 'formDemandaMensagemGeral') {
                elemento.className = (tipo === 'sucesso') ? 'form-message form-message-success' : 'form-message form-message-error';
            } else {
                elemento.className = 'field-error-message';
            }
            elemento.style.display = 'block';
        }
    }

    limparMensagens();

    // Coleta dos dados do formulário para o payload JSON
    // Os 'name' atributos no HTML devem corresponder às chaves esperadas pelo DTO no backend.
    const payload = {
        nomeRequerente: form.elements.nomeRequerente.value,
        cpfRequerente: form.elements.cpfRequerente.value,
        emailRequerente: form.elements.emailRequerente.value,
        telefoneRequerente: form.elements.telefoneRequerente.value,
        senhaRequerente: form.elements.senhaRequerente.value,
        enderecoRequerente: form.elements.enderecoRequerente.value,
        idCategoriaRequisicao: parseInt(form.elements.idCategoriaRequisicao.value, 10) || null, // Garante que é número ou null
        descricaoRequisicao: form.elements.descricaoRequisicao.value,
        frequenciaRequisicao: form.elements.frequenciaRequisicao.value,
        infoCompRequisicao: form.elements.infoCompRequisicao.value
    };

    console.log("Payload JSON que será enviado (Demanda):", JSON.stringify(payload, null, 2));


    fetch('/api/v1/requerentes/cadastrar-com-requisicao', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
    })
        .then(response => {
            return response.json().then(data => ({ status: response.status, body: data, ok: response.ok }))
                .catch(jsonError => {
                    console.error('Erro ao parsear JSON da resposta (Demanda):', response.status, response.statusText, jsonError);
                    return response.text().then(textData => {
                        return { status: response.status, body: { error: `Erro ${response.status}: ${response.statusText}. O servidor não retornou um JSON válido.` }, ok: response.ok };
                    });
                });
        })
        .then(({ status, body, ok }) => {
            limparMensagens();

            if (ok) {
                exibirMensagem(mensagemGeralDiv, body.message || 'Demanda cadastrada com sucesso!', 'sucesso');
                form.reset();
                setTimeout(() => {
                    if (mensagemGeralDiv) mensagemGeralDiv.style.display = 'none';
                }, 5000);
            } else {
                let primeiroInputComErro = null;
                let mensagemParaDivGeral = "Ocorreu um erro. Verifique os campos.";
                let houveErroDeCampoEspecifico = false;

                if (body && typeof body === 'object') {
                    if (body.error) {
                        mensagemParaDivGeral = body.error;
                    } else {
                        console.log("Erros de validação recebidos (Demanda):", body);
                        let camposComErroNomesAmigaveis = [];

                        for (const fieldNameFromError in body) {
                            if (body.hasOwnProperty(fieldNameFromError)) {
                                houveErroDeCampoEspecifico = true;
                                const fieldErrorMessage = body[fieldNameFromError];
                                const spanId = errorSpanIds[fieldNameFromError];

                                camposComErroNomesAmigaveis.push(fieldDisplayNames[fieldNameFromError] || fieldNameFromError);

                                if (spanId) {
                                    const errorSpan = document.getElementById(spanId);
                                    if (errorSpan) {
                                        exibirMensagem(errorSpan, fieldErrorMessage, 'erro');
                                        const inputIdParaFoco = inputIdsForFocus[fieldNameFromError]; // Usa o mapeamento
                                        const inputDoErro = inputIdParaFoco ? document.getElementById(inputIdParaFoco) : null;
                                        if (!primeiroInputComErro && inputDoErro && typeof inputDoErro.focus === 'function') {
                                            primeiroInputComErro = inputDoErro;
                                        }
                                    } else {
                                        console.warn(`Span de erro com ID '${spanId}' para campo '${fieldNameFromError}' não encontrado.`);
                                    }
                                } else {
                                    console.warn(`Mapeamento para campo de erro '${fieldNameFromError}' não encontrado em errorSpanIds.`);
                                }
                            }
                        }
                        if(camposComErroNomesAmigaveis.length > 0){
                            mensagemParaDivGeral = `Corrija os erros nos seguintes campos: ${camposComErroNomesAmigaveis.join(', ')}.`;
                        } else if (houveErroDeCampoEspecifico && Object.keys(body).length > 0) {
                            mensagemParaDivGeral = "Erro de validação. Verifique os campos.";
                        }
                    }
                } else {
                    mensagemParaDivGeral = `Erro ${status} ao processar a solicitação. Tente novamente.`;
                }

                exibirMensagem(mensagemGeralDiv, mensagemParaDivGeral, 'erro');
                if (primeiroInputComErro) {
                    primeiroInputComErro.focus();
                }
            }
        })
        .catch(error => {
            limparMensagens();
            console.error('Erro crítico no fetch ao cadastrar demanda:', error);
            exibirMensagem(mensagemGeralDiv, 'Erro ao cadastrar demanda: ' + error.message, 'erro');
        });
}