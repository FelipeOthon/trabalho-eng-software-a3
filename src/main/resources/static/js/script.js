const barraDePesquisa = document.getElementById('barraDePesquisa');
const filtroCategoria = document.getElementById('filtroCategoria');
const listaDeServicos = document.getElementById('listaDeServicos');
const modalDetalhes = document.getElementById('modalDetalhes');
const tituloModal = document.getElementById('tituloModal');
const empresaModal = document.getElementById('empresaModal');
const categoriaModal = document.getElementById('categoriaModal');
const detalhesModal = document.getElementById('detalhesModal');

barraDePesquisa.addEventListener('input', filtrarServicos);
filtroCategoria.addEventListener('change', filtrarServicos);

function filtrarServicos() {
    const termoPesquisa = barraDePesquisa.value.toLowerCase();
    const categoriaSelecionada = filtroCategoria.value;

    const servicos = listaDeServicos.querySelectorAll('.cartaoDeServico');

    servicos.forEach(servico => {
        const nomeServico = servico.querySelector('h2').textContent.toLowerCase();
        const categoriaServico = servico.querySelector('p:nth-of-type(2)').textContent.toLowerCase();

        const correspondePesquisa = nomeServico.includes(termoPesquisa);
        const correspondeCategoria = categoriaSelecionada === '' || categoriaServico.includes(categoriaSelecionada.toLowerCase());

        if (correspondePesquisa && correspondeCategoria) {
            servico.style.display = 'block';
        } else {
            servico.style.display = 'none';
        }
    });
}

function abrirModal(nome, empresa, categoria, detalhes) {
    tituloModal.textContent = nome;
    empresaModal.textContent = empresa;
    categoriaModal.textContent = categoria;
    detalhesModal.textContent = detalhes;

    modalDetalhes.style.display = 'flex';
}

function fecharModal() {
    modalDetalhes.style.display = 'none';
}
