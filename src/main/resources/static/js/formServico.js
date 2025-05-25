// src/main/resources/static/js/formServico.js

// Função para aplicar máscara de CPF (XXX.XXX.XXX-XX)
function mascaraCpf(cpfInput) {
    let value = cpfInput.value.replace(/\D/g, ''); // Remove tudo que não for dígito
    value = value.replace(/^(\d{3})(\d)/, '$1.$2');
    value = value.replace(/^(\d{3})\.(\d{3})(\d)/, '$1.$2.$3');
    value = value.replace(/\.(\d{3})(\d{1,2})$/, '.$1-$2');
    cpfInput.value = value.substring(0, 14);
}

// Função para aplicar máscara de Celular ((XX) XXXXX-XXXX ou (XX) XXXX-XXXX)
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

// Função para aplicar máscara de CEP (XXXXX-XXX)
function mascaraCep(cepInput) {
    let value = cepInput.value.replace(/\D/g, '');
    value = value.replace(/^(\d{5})(\d)/, '$1-$2');
    cepInput.value = value.substring(0, 9);
}

document.addEventListener('DOMContentLoaded', function() {
    const cpfInput = document.getElementById('cpf');
    if (cpfInput) {
        cpfInput.addEventListener('input', function() { mascaraCpf(this); });
    }

    const celularInput = document.getElementById('celular');
    if (celularInput) {
        celularInput.addEventListener('input', function() { mascaraCelular(this); });
    }

    const cepInput = document.getElementById('endereco');
    if (cepInput) {
        cepInput.addEventListener('input', function() { mascaraCep(this); });
    }
});

function salvarServico() {
    const form = document.getElementById('formServico');
    if (!form) {
        console.error('Formulário #formServico não encontrado!');
        alert('Erro interno crítico: Formulário de serviço não encontrado na página.');
        return;
    }

    const formData = new FormData(form);
    const mensagemGeralDiv = document.getElementById('formServicoMensagemGeral');

    const errorSpanIds = {
        cpf: 'cpfErrorMessage',
        nome: 'nomeErrorMessage',
        email: 'emailErrorMessage',
        celular: 'celularErrorMessage',
        senha: 'senhaErrorMessage',
        endereco: 'enderecoErrorMessage',
        categoria: 'categoriaErrorMessage',
        descricao: 'descricaoErrorMessage',
        informacoesComplementares: 'informacoesComplementaresErrorMessage',
        anexo: 'anexoErrorMessage'
    };

    const fieldDisplayNames = {
        cpf: 'CPF',
        nome: 'Nome',
        email: 'E-mail',
        celular: 'Celular',
        senha: 'Senha',
        endereco: 'CEP',
        categoria: 'Categoria',
        descricao: 'Descrição',
        informacoesComplementares: 'Informações Complementares',
        anexo: 'Anexo'
    };


    function limparMensagens() {
        if (mensagemGeralDiv) {
            mensagemGeralDiv.style.display = 'none';
            mensagemGeralDiv.textContent = '';
            mensagemGeralDiv.className = 'form-message';
        }
        for (const fieldKey in errorSpanIds) {
            const span = document.getElementById(errorSpanIds[fieldKey]);
            if (span) {
                span.textContent = '';
                span.style.display = 'none';
            }
        }
    }

    function exibirMensagem(elemento, mensagem, tipo) {
        if (elemento) {
            elemento.textContent = mensagem;
            if (elemento.id === 'formServicoMensagemGeral') {
                elemento.className = (tipo === 'sucesso') ? 'form-message form-message-success' : 'form-message form-message-error';
            } else {
                elemento.className = 'field-error-message';
            }
            elemento.style.display = 'block';
        }
    }

    limparMensagens();

    fetch('/api/v1/prestadores/cadastrar', {
        method: 'POST',
        body: formData
    })
        .then(response => {
            return response.json().then(data => ({ status: response.status, body: data, ok: response.ok }))
                .catch(jsonError => {
                    console.error('Erro ao parsear JSON da resposta:', response.status, response.statusText, jsonError);
                    return response.text().then(textData => {
                        return { status: response.status, body: { error: `Erro ${response.status}: ${response.statusText}. O servidor não retornou um JSON válido.` }, ok: response.ok };
                    });
                });
        })
        .then(({ status, body, ok }) => {
            limparMensagens();

            if (ok) {
                exibirMensagem(mensagemGeralDiv, body.message || 'Prestador cadastrado com sucesso!', 'sucesso');
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
                        console.log("Erros de validação recebidos do JS:", body);
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
                                        const inputDoErro = document.getElementById(fieldNameFromError);
                                        if (!primeiroInputComErro && inputDoErro && typeof inputDoErro.focus === 'function') {
                                            primeiroInputComErro = inputDoErro;
                                        }
                                    } else {
                                        console.warn(`Span de erro com ID '${spanId}' para campo '${fieldNameFromError}' não encontrado no HTML.`);
                                    }
                                } else {
                                    console.warn(`Mapeamento para campo de erro '${fieldNameFromError}' não encontrado em errorSpanIds. Mensagem: ${fieldErrorMessage}`);
                                }
                            }
                        }
                        if(camposComErroNomesAmigaveis.length > 0){
                            mensagemParaDivGeral = `Corrija os erros nos seguintes campos: ${camposComErroNomesAmigaveis.join(', ')}.`;
                        } else if (houveErroDeCampoEspecifico) {
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
            console.error('Erro crítico na requisição fetch:', error);
            const genericNetworkErrorMessage = 'Ocorreu um erro de comunicação. Verifique sua conexão ou tente novamente mais tarde.';
            exibirMensagem(mensagemGeralDiv, genericNetworkErrorMessage, 'erro');
        });
}