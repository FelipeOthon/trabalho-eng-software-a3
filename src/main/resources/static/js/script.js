const barraDePesquisa = document.getElementById('barraDePesquisa');
const filtroCategoria = document.getElementById('filtroCategoria');
const listaDeServicos = document.getElementById('listaDeServicos');
const modalDetalhes = document.getElementById('modalDetalhes'); // Se existir no seu HTML
const tituloModal = document.getElementById('tituloModal');     // Se existir
const empresaModal = document.getElementById('empresaModal');   // Se existir
const categoriaModal = document.getElementById('categoriaModal'); // Se existir
const detalhesModal = document.getElementById('detalhesModal'); // Se existir

// Adiciona listeners apenas se os elementos existirem para evitar erros
if (barraDePesquisa) {
    barraDePesquisa.addEventListener('input', filtrarServicos);
}
if (filtroCategoria) {
    filtroCategoria.addEventListener('change', filtrarServicos);
}

function filtrarServicos() {
    // Garante que os elementos existem antes de tentar usá-los
    if (!barraDePesquisa || !filtroCategoria || !listaDeServicos) {
        console.error("Elementos essenciais para filtro não encontrados (barraDePesquisa, filtroCategoria ou listaDeServicos).");
        return;
    }

    const termoPesquisa = barraDePesquisa.value.toLowerCase();
    const categoriaSelecionada = filtroCategoria.value; // Valor do <option> do select (ex: "Desenvolvimento Front-End" ou "")

    const servicos = listaDeServicos.querySelectorAll('.cartaoDeServico');

    servicos.forEach(servico => {
        const nomeServicoElement = servico.querySelector('h2');
        const nomeServico = nomeServicoElement ? nomeServicoElement.textContent.toLowerCase() : '';

        // LÓGICA CORRIGIDA PARA EXTRAIR A CATEGORIA DO TEXTO DO CARTÃO
        let categoriaServico = ''; // Inicializa para caso o elemento não seja encontrado ou o texto não corresponda
        const paragrafoCategoriaElement = servico.querySelector('p:nth-of-type(3)'); // Pega o TERCEIRO parágrafo

        if (paragrafoCategoriaElement) {
            const textoCompletoCategoria = paragrafoCategoriaElement.textContent.toLowerCase(); // Ex: "categoria: desenvolvimento front-end"
            if (textoCompletoCategoria.startsWith('categoria:')) {
                categoriaServico = textoCompletoCategoria.substring('categoria:'.length).trim(); // Extrai apenas "desenvolvimento front-end"
            }
        }

        const correspondePesquisa = termoPesquisa === '' || nomeServico.includes(termoPesquisa);

        // COMPARAÇÃO DE CATEGORIA AJUSTADA
        // Normaliza o valor do filtro de categoria (vindo do dropdown) para minúsculas
        const categoriaSelecionadaNormalizada = categoriaSelecionada.toLowerCase();
        // Verifica se "Todas as Categorias" foi selecionado OU se a categoria do cartão (já em minúsculas) é igual à categoria do filtro (normalizada)
        const correspondeCategoria = categoriaSelecionada === '' || (categoriaServico && categoriaServico === categoriaSelecionadaNormalizada);

        if (correspondePesquisa && correspondeCategoria) {
            servico.style.display = 'block';
        } else {
            servico.style.display = 'none';
        }
    });
}

// Funções do Modal (mantidas como no seu original, mas dependem do HTML do modal existir)
function abrirModal(nome, empresa, categoria, detalhes) {
    if (modalDetalhes && tituloModal && empresaModal && categoriaModal && detalhesModal) {
        tituloModal.textContent = nome;
        empresaModal.textContent = empresa;
        categoriaModal.textContent = categoria;
        detalhesModal.textContent = detalhes;
        modalDetalhes.style.display = 'flex';
    } else {
        console.warn("Elementos do modal não encontrados para abrirModal.");
    }
}

function fecharModal() {
    if (modalDetalhes) {
        modalDetalhes.style.display = 'none';
    }
}