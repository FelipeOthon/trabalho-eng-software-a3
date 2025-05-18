// Espera que o DOM esteja completamente carregado para executar os scripts
document.addEventListener('DOMContentLoaded', () => {
    carregarServicos();
    carregarCategoriasNoFiltro();
});

/**
 * Carrega os serviços da API e os exibe na página.
 */
async function carregarServicos() {
    try {
        // Faz a requisição para o endpoint da API que lista os serviços
        // O DTO ServicoPainelDTO no backend deve fornecer os campos esperados aqui
        const response = await fetch('/api/v1/prestadores/servicos-disponiveis');
        if (!response.ok) {
            // Se a resposta não for OK, lança um erro com o status
            throw new Error(`Erro ao carregar serviços: ${response.status} ${response.statusText}`);
        }
        const servicos = await response.json(); // Converte a resposta para JSON
        exibirServicos(servicos); // Chama a função para mostrar os serviços na tela
    } catch (error) {
        console.error('Erro detalhado ao carregar os serviços:', error);
        const listaDeServicos = document.getElementById('listaDeServicos');
        if(listaDeServicos) {
            listaDeServicos.innerHTML = '<p class="text-danger">Não foi possível carregar os serviços. Tente novamente mais tarde.</p>';
        }
        // alert('Não foi possível carregar os serviços. Verifique o console para mais detalhes.');
    }
}

/**
 * Exibe os serviços (cartões) na lista da página.
 * @param {Array} servicos - Uma lista de objetos de serviço (DTOs) vindos da API.
 */
function exibirServicos(servicos) {
    const listaDeServicos = document.getElementById('listaDeServicos');
    if (!listaDeServicos) {
        console.error('Elemento #listaDeServicos não encontrado no DOM.');
        return;
    }
    listaDeServicos.innerHTML = ''; // Limpa a lista antes de popular com novos cartões

    if (!servicos || servicos.length === 0) {
        listaDeServicos.innerHTML = '<p>Nenhum serviço encontrado no momento.</p>';
        return;
    }

    servicos.forEach(servico => { // 'servico' aqui é um objeto ServicoPainelDTO
        const cartao = document.createElement('div');
        cartao.className = 'cartaoDeServico'; // Classe para estilização do cartão

        // Monta o HTML interno do cartão com os dados do serviço
        // Usamos 'N/A' como fallback se algum campo estiver faltando
        cartao.innerHTML = `
             <h2>${servico.descricao || 'Descrição não disponível'}</h2> 
            <p><strong>Prestador:</strong> ${servico.nomePrestador || (servico.emailPrestador || 'Não informado')}</p> 
            <p><strong>Email:</strong> ${servico.emailPrestador || 'Não informado'}</p>
            <p><strong>Categoria:</strong> ${servico.nomeCategoria || 'Não informada'}</p>
            <p><strong>Detalhes:</strong> ${servico.infoComplementares || 'Sem detalhes adicionais'}</p>
        `;
        listaDeServicos.appendChild(cartao);
    });
}

/**
 * Carrega as categorias da API e as popula no elemento <select> do filtro.
 */
async function carregarCategoriasNoFiltro() {
    const filtroCategoriaSelect = document.getElementById('filtroCategoria');
    if (!filtroCategoriaSelect) {
        console.error('Elemento #filtroCategoria não encontrado no DOM.');
        return;
    }

    // Limpa opções antigas, mantendo a primeira opção "Todas as Categorias"
    while (filtroCategoriaSelect.options.length > 1) {
        filtroCategoriaSelect.remove(1);
    }

    try {
        // Faz a requisição para o endpoint da API que lista as categorias
        const response = await fetch('/api/v1/categorias'); // Usando o CategoriaApiController
        if (!response.ok) {
            throw new Error(`Erro ao buscar categorias: ${response.status} ${response.statusText}`);
        }
        const categorias = await response.json(); // Converte a resposta para JSON

        // Para cada categoria retornada, cria um <option> e adiciona ao <select>
        categorias.forEach(categoria => {
            const option = document.createElement('option');
            // O `value` da option será usado pelo script.js para filtrar
            // O `textContent` é o que o usuário vê
            option.value = categoria.nomeCategoria;
            option.textContent = categoria.nomeCategoria;
            filtroCategoriaSelect.appendChild(option);
        });
    } catch (error) {
        console.error('Erro detalhado ao carregar categorias no filtro:', error);
        // Poderia informar o usuário, mas por enquanto só log no console.
    }
}

/**
 * Funções para o Modal (se você decidir usá-lo)
 * Certifique-se que os IDs no HTML do modal (tituloModal, empresaModal, etc.) existem.
 * O modal está comentado no indexEmpresas.html.
 */
function abrirModal(descricao, nomePrestador, nomeCategoria, infoComplementares) {
    const modalDetalhes = document.getElementById('modalDetalhes');
    const tituloModal = document.getElementById('tituloModal');
    const empresaModal = document.getElementById('empresaModal');
    const categoriaModal = document.getElementById('categoriaModal');
    const detalhesModal = document.getElementById('detalhesModal');

    if (modalDetalhes && tituloModal && empresaModal && categoriaModal && detalhesModal) {
        tituloModal.textContent = descricao || 'N/A';
        empresaModal.textContent = nomePrestador || 'N/A';
        categoriaModal.textContent = nomeCategoria || 'N/A';
        detalhesModal.textContent = infoComplementares || 'N/A';
        modalDetalhes.style.display = 'flex'; // ou 'block', dependendo do seu CSS do modal
    } else {
        console.error('Um ou mais elementos do modal não foram encontrados.');
    }
}

function fecharModal() {
    const modalDetalhes = document.getElementById('modalDetalhes');
    if (modalDetalhes) {
        modalDetalhes.style.display = 'none';
    }
}