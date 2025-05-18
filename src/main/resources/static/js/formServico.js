// src/main/resources/static/js/formServico.js

document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('formServico');
    if (form) {
// O botão "Salvar" é type="button" e tem um onclick="salvarServico()",
// então um listener de 'submit' no formulário não é estritamente necessário aqui.
// Se o botão fosse type="submit", o listener abaixo seria útil:
// form.addEventListener('submit', function(event) {
//     event.preventDefault(); // Impede o envio tradicional do formulário
//     salvarServico(); // Chama a função que faz a requisição fetch
// });
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

// Referências aos elementos de mensagem no HTML (do formCadastro.html)
    const mensagemGeralDiv = document.getElementById('formServicoMensagemGeral');
    const nomeErrorSpan = null; // Assumindo que não há um span específico para nome ainda
    const cpfErrorSpan = document.getElementById('cpfErrorMessage');
    const emailErrorSpan = document.getElementById('emailErrorMessage');
    const celularErrorSpan = document.getElementById('celularErrorMessage');       // Referência adicionada
    const senhaErrorSpan = null; // Assumindo que não há um span específico para senha ainda
    const enderecoErrorSpan = document.getElementById('enderecoErrorMessage');     // Referência para CEP (endereco)
    const categoriaErrorSpan = document.getElementById('categoriaErrorMessage');
    const descricaoErrorSpan = document.getElementById('descricaoErrorMessage');

// Função helper para limpar todas as mensagens de erro/sucesso antes de uma nova submissão
    function limparMensagens() {
        if (mensagemGeralDiv) {
            mensagemGeralDiv.style.display = 'none';
            mensagemGeralDiv.textContent = '';
            mensagemGeralDiv.className = 'form-message'; // Reseta classes de estilo
        }
// Limpa spans de erro específicos de campos
        const errorSpans = [
            nomeErrorSpan, cpfErrorSpan, emailErrorSpan, celularErrorSpan,
            senhaErrorSpan, enderecoErrorSpan, categoriaErrorSpan, descricaoErrorSpan
        ];
        errorSpans.forEach(span => {
            if (span) { // Verifica se o span existe antes de tentar manipulá-lo
                span.textContent = '';
                span.style.display = 'none';
            }
        });
    }

// Função helper para exibir mensagens na UI
    function exibirMensagem(elemento, mensagem, tipo) { // tipo pode ser 'sucesso' ou 'erro'
        if (elemento) {
            elemento.textContent = mensagem;
// Aplica classes de estilo com base no tipo de mensagem
            if (elemento.id === 'formServicoMensagemGeral') {
                elemento.className = (tipo === 'sucesso') ? 'form-message form-message-success' : 'form-message form-message-error';
            } else { // Para spans de erro de campo (tags <small>)
                elemento.className = 'field-error-message'; // Mantém a classe base para estilo
                if (tipo === 'erro') {
                    // A cor já é definida pela classe .field-error-message, mas pode forçar aqui se necessário
                    // elemento.style.color = '#721c24';
                }
            }
            elemento.style.display = 'block'; // Torna o elemento visível
        }
    }

    limparMensagens(); // Limpa mensagens de tentativas anteriores

    // --- VALIDAÇÕES NO LADO DO CLIENTE ---
    let formValido = true;
    const nome = formData.get('nome');
    const cpf = formData.get('cpf');
    const email = formData.get('email');
    const celular = formData.get('celular'); // Valor do campo celular
    const senha = formData.get('senha');
    const endereco = formData.get('endereco'); // Valor do campo CEP (chamado 'endereco' no form)
    const categoria = formData.get('categoria'); // Valor do select
    const descricao = formData.get('descricao');

    // Validação de campos obrigatórios (HTML 'required' já faz a primeira barreira)
    if (!nome.trim()) {
        // Se você criar um <small id="nomeErrorMessage">...</small> no HTML:
        // if (nomeErrorSpan) exibirMensagem(nomeErrorSpan, 'O nome é obrigatório.', 'erro');
        formValido = false;
    }
    if (!cpf.trim()) {
        if (cpfErrorSpan) exibirMensagem(cpfErrorSpan, 'O CPF é obrigatório.', 'erro');
        formValido = false;
    }
    if (!email.trim()) {
        if (emailErrorSpan) exibirMensagem(emailErrorSpan, 'O e-mail é obrigatório.', 'erro');
        formValido = false;
    } else {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            if (emailErrorSpan) exibirMensagem(emailErrorSpan, 'Formato de e-mail inválido (ex: nome@dominio.com).', 'erro');
            formValido = false;
        }
    }

    // Validação para Celular
    if (!celular.trim()) {
        if (celularErrorSpan) exibirMensagem(celularErrorSpan, 'O celular é obrigatório.', 'erro');
        formValido = false;
    } else {
        const apenasNumerosCelular = celular.replace(/\D/g, ''); // Remove tudo que não for dígito
        if (apenasNumerosCelular.length < 10 || apenasNumerosCelular.length > 11) {
            if (celularErrorSpan) exibirMensagem(celularErrorSpan, 'Celular inválido. Deve conter 10 ou 11 dígitos (Ex: (XX) XXXXX-XXXX).', 'erro');
            formValido = false;
        }
    }

    if (!senha) { // Senha é obrigatória
        // Se você criar um <small id="senhaErrorMessage">...</small> no HTML:
        // if (senhaErrorSpan) exibirMensagem(senhaErrorSpan, 'A senha é obrigatória.', 'erro');
        formValido = false;
    }

    // Validação para CEP (campo "endereco")
    if (!endereco.trim()) {
        if (enderecoErrorSpan) exibirMensagem(enderecoErrorSpan, 'O CEP é obrigatório.', 'erro');
        formValido = false;
    } else {
        const apenasNumerosCep = endereco.replace(/\D/g, ''); // Remove tudo que não for dígito
        if (apenasNumerosCep.length !== 8) {
            if (enderecoErrorSpan) exibirMensagem(enderecoErrorSpan, 'CEP inválido. Deve conter 8 números (somente números).', 'erro');
            formValido = false;
        }
    }

    if (!categoria) { // Categoria é obrigatória (value="" se não selecionado)
        if (categoriaErrorSpan) exibirMensagem(categoriaErrorSpan, 'Por favor, selecione uma categoria.', 'erro');
        formValido = false;
    }
    if (!descricao.trim()) {
        if (descricaoErrorSpan) exibirMensagem(descricaoErrorSpan, 'A descrição do serviço é obrigatória.', 'erro');
        formValido = false;
    }
    // Validação para Informações Complementares (se também for obrigatório)
    // const informacoesComplementares = formData.get('informacoesComplementares');
    // if (!informacoesComplementares.trim()) {
    //     // Adicionar span de erro e exibir mensagem
    //     formValido = false;
    // }


    if (!formValido) {
        exibirMensagem(mensagemGeralDiv, 'Por favor, corrija os erros indicados no formulário.', 'erro');
        // Tenta focar no primeiro campo com erro para melhor UX
        const primeiroErroVisivel = document.querySelector('.field-error-message[style*="display: block"]');
        if (primeiroErroVisivel && primeiroErroVisivel.previousElementSibling && typeof primeiroErroVisivel.previousElementSibling.focus === 'function') {
            primeiroErroVisivel.previousElementSibling.focus();
            primeiroErroVisivel.scrollIntoView({ behavior: 'smooth', block: 'center' });
        } else if (primeiroErroVisivel) {
            primeiroErroVisivel.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }
        return; // Interrompe o envio se houver erros de validação do cliente
    }
    // --- FIM DAS VALIDAÇÕES NO LADO DO CLIENTE ---


    fetch('/api/v1/prestadores/cadastrar', {
        method: 'POST',
        body: formData
    })
        .then(response => {
            return response.json().then(data => ({ status: response.status, body: data, ok: response.ok }));
        })
        .then(({ status, body, ok }) => {
            limparMensagens(); // Limpa mensagens de validação do cliente antes de mostrar a do servidor

            if (ok) { // Sucesso
                exibirMensagem(mensagemGeralDiv, body.message || 'Prestador cadastrado com sucesso!', 'sucesso');
                form.reset();
                setTimeout(() => { // Oculta a mensagem de sucesso após um tempo
                    if (mensagemGeralDiv) mensagemGeralDiv.style.display = 'none';
                }, 5000);
            } else { // Erro vindo do backend
                const errorMessage = body.error || `Erro ${status} ao processar a solicitação.`;
                exibirMensagem(mensagemGeralDiv, errorMessage, 'erro');

                // Direciona mensagens de erro específicas do backend para os campos corretos
                if (errorMessage.toLowerCase().includes('cpf')) {
                    if (cpfErrorSpan) exibirMensagem(cpfErrorSpan, errorMessage, 'erro');
                } else if (errorMessage.toLowerCase().includes('email')) {
                    if (emailErrorSpan) exibirMensagem(emailErrorSpan, errorMessage, 'erro');
                } else if (errorMessage.toLowerCase().includes('categoria')) {
                    if (categoriaErrorSpan) exibirMensagem(categoriaErrorSpan, errorMessage, 'erro');
                }
                // Você pode adicionar mais 'else if' se o backend retornar erros mais específicos para outros campos
            }
        })
        .catch(error => { // Erro de rede ou parse do JSON
            limparMensagens(); // Limpa qualquer mensagem de validação do cliente
            console.error('Erro crítico no fetch ao salvar serviço:', error);
            const genericNetworkErrorMessage = 'Ocorreu um erro de comunicação. Verifique sua conexão ou tente novamente mais tarde.';
            exibirMensagem(mensagemGeralDiv, genericNetworkErrorMessage, 'erro');
        });
}