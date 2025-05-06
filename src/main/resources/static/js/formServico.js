// src/main/resources/static/js/formServico.js

document.addEventListener('DOMContentLoaded', function() {
    // Adiciona o event listener ao formulário se ele existir.
    // O formCadastro.html tem um botão com onclick="salvarServico()",
    // então o event listener no submit do formulário pode não ser estritamente necessário
    // se o botão não for type="submit". Mas é uma boa prática tê-lo.
    const form = document.getElementById('formServico');
    if (form) {
        form.addEventListener('submit', function(event) {
            event.preventDefault(); // Impede o envio tradicional do formulário
            salvarServico(); // Chama a função que faz a requisição fetch
        });
    }
});

function salvarServico() {
    const form = document.getElementById('formServico'); // Pega o formulário
    if (!form) {
        console.error('Formulário #formServico não encontrado!');
        alert('Erro interno: Formulário não encontrado.');
        return;
    }

    // Cria um objeto FormData a partir do formulário HTML.
    // Isso vai pegar todos os campos com seus 'name' e valores, incluindo o arquivo.
    const formData = new FormData(form);

    // Os nomes dos campos no FormData virão dos atributos 'name' dos inputs no HTML.
    // Vamos verificar se eles correspondem ao que o controller espera nos @RequestParam:
    // HTML name -> Controller @RequestParam name
    // cpf -> cpf (OK)
    // nome -> nome (OK)
    // email -> email (OK)
    // celular -> celular (OK, o controller tem @RequestParam("celular"))
    // endereco -> endereco (OK, o controller tem @RequestParam("endereco"))
    // senha -> senha (OK)
    // categoria -> categoria (OK, o controller tem @RequestParam("categoria"))
    // descricao -> descricao (OK, o controller tem @RequestParam("descricao"))
    // informacoesComplementares -> informacoesComplementares (OK, o controller tem @RequestParam("informacoesComplementares"))
    // anexo -> anexo (OK, o controller tem @RequestParam("anexo"))

    fetch('/api/v1/prestadores/cadastrar', { // URL do endpoint Spring Boot
        method: 'POST',
        body: formData // Enviar o FormData diretamente
        // Não defina o 'Content-Type' header quando usar FormData;
        // o navegador o fará automaticamente com o 'boundary' correto para multipart/form-data.
    })
        .then(response => {
            // Tenta primeiro obter o JSON, mesmo para respostas não-OK, pois pode conter erro do backend
            return response.json().then(data => {
                if (!response.ok) {
                    // Se o backend enviou um JSON com uma chave 'error', usa essa mensagem.
                    const errorMsg = data.error || `Erro HTTP ${response.status} ao salvar.`;
                    throw new Error(errorMsg); // Joga um erro para ser pego pelo .catch()
                }
                return data; // Retorna os dados JSON se a resposta for OK
            }).catch(jsonError => {
                // Se o corpo não for JSON válido ou se já era um erro e o response.json() falhou
                if (!response.ok) {
                    // Se não conseguiu parsear JSON de uma resposta de erro, usa o statusText
                    throw new Error(`Erro HTTP ${response.status}: ${response.statusText || 'Erro ao processar resposta do servidor.'}`);
                }
                // Se era uma resposta OK mas o JSON parse falhou (improvável se o backend envia JSON correto)
                throw new Error('Erro ao processar JSON da resposta do servidor.');
            });
        })
        .then(data => {
            if (data.message) {
                alert(data.message); // Ex: "Prestador cadastrado com sucesso!"
                form.reset(); // Limpa o formulário após o sucesso
            } else {
                // Isso não deveria acontecer se o backend sempre retornar 'message' ou 'error'
                alert('Resposta recebida, mas formato inesperado.');
            }
        })
        .catch(error => {
            console.error('Erro no fetch ao salvar serviço:', error);
            alert('Erro ao salvar serviço: ' + error.message);
        });
}