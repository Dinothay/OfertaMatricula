document.addEventListener('DOMContentLoaded', () => {
    const currentPath = window.location.pathname;
    const shouldShowBackButton = !['/', '/index', '/indexAluno'].includes(currentPath);

    if (shouldShowBackButton && !document.querySelector('.floating-back-btn')) {
        const backButton = document.createElement('button');
        backButton.type = 'button';
        backButton.className = 'floating-back-btn';
        backButton.textContent = '← Voltar';
        backButton.setAttribute('aria-label', 'Voltar para a página anterior');
        backButton.addEventListener('click', () => window.history.back());
        document.body.appendChild(backButton);
    }

    const lerTema = () => {
        const cookie = document.cookie
            .split(';')
            .map((item) => item.trim())
            .find((item) => item.startsWith('tema_meta='));

        if (!cookie) return 'claro';

        try {
            const valor = decodeURIComponent(cookie.substring('tema_meta='.length));
            return valor.includes('escuro') ? 'escuro' : 'claro';
        } catch (erro) {
            return 'claro';
        }
    };

    const temaInicial = lerTema();
    document.body.classList.toggle('dark', temaInicial === 'escuro');

    const userChips = document.querySelectorAll('.user-chip');

    userChips.forEach((chip) => {
        const trigger = chip.querySelector('.user-chip-trigger');
        const menu = chip.querySelector('.user-menu');

        if (!trigger || !menu) return;

        if (!menu.querySelector('.theme-toggle-button')) {
            const formTema = document.createElement('form');
            formTema.method = 'post';
            formTema.action = '/set';

            const temaAtual = lerTema();
            const temaNovo = temaAtual === 'escuro' ? 'claro' : 'escuro';

            const inputTema = document.createElement('input');
            inputTema.type = 'hidden';
            inputTema.name = 'tema';
            inputTema.value = temaNovo;

            const inputDestino = document.createElement('input');
            inputDestino.type = 'hidden';
            inputDestino.name = 'redirectTo';
            inputDestino.value = window.location.pathname + window.location.search;

            const botaoTema = document.createElement('button');
            botaoTema.type = 'submit';
            botaoTema.className = 'user-menu-item theme-toggle-button';
            botaoTema.textContent = temaAtual === 'escuro' ? 'Tema claro' : 'Tema escuro';

            botaoTema.addEventListener('click', () => {
                document.body.classList.toggle('dark', temaNovo === 'escuro');
                document.cookie = `tema_meta=${encodeURIComponent('tema=' + temaNovo)}; path=/; max-age=86400; samesite=lax`;
            });

            formTema.append(inputTema, inputDestino, botaoTema);
            menu.insertBefore(formTema, menu.querySelector('form') || null);
        }

        trigger.addEventListener('click', (event) => {
            event.stopPropagation();
            const isOpen = chip.classList.toggle('open');
            trigger.setAttribute('aria-expanded', String(isOpen));

            userChips.forEach((otherChip) => {
                if (otherChip !== chip) {
                    otherChip.classList.remove('open');
                    const otherTrigger = otherChip.querySelector('.user-chip-trigger');
                    if (otherTrigger) {
                        otherTrigger.setAttribute('aria-expanded', 'false');
                    }
                }
            });
        });
    });

    document.addEventListener('click', () => {
        userChips.forEach((chip) => {
            chip.classList.remove('open');
            const trigger = chip.querySelector('.user-chip-trigger');
            if (trigger) {
                trigger.setAttribute('aria-expanded', 'false');
            }
        });
    });

    const normalizarTexto = (texto) => texto
        .normalize('NFD')
        .replace(/[\u0300-\u036f]/g, '')
        .toLowerCase()
        .trim();

    document.querySelectorAll('.table-wrap table').forEach((tabela, indiceTabela) => {
        if (tabela.querySelector('.search-box')) return;

        const cabecalhos = Array.from(tabela.querySelectorAll('thead th, tr:first-child th'));
        if (cabecalhos.length === 0) return;

        const palavrasPrioritarias = [
            'nome', 'usuario', 'usuário', 'titulo', 'título',
            'curso', 'disciplina', 'livro', 'aluno', 'professor',
            'bibliotecario', 'bibliotecário', 'reserva', 'prova'
        ];

        let indicePesquisa = cabecalhos.findIndex((cabecalho) => {
            const texto = normalizarTexto(cabecalho.textContent);
            return palavrasPrioritarias.some((palavra) => texto.includes(palavra));
        });

        if (indicePesquisa < 0) indicePesquisa = Math.min(1, cabecalhos.length - 1);

        const linhaToolbar = document.createElement('div');
        linhaToolbar.className = 'table-toolbar';

        const rotulo = document.createElement('label');
        rotulo.htmlFor = `pesquisa-lista-${indiceTabela}`;
        rotulo.textContent = `Pesquisar por ${normalizarTexto(cabecalhos[indicePesquisa].textContent)}`;

        const campo = document.createElement('input');
        campo.id = `pesquisa-lista-${indiceTabela}`;
        campo.type = 'search';
        campo.className = 'search-box';
        campo.placeholder = `Digite ${cabecalhos[indicePesquisa].textContent.toLowerCase()}...`;
        campo.autocomplete = 'off';

        linhaToolbar.append(rotulo, campo);
        tabela.parentElement.insertBefore(linhaToolbar, tabela);

        const linhas = Array.from(tabela.querySelectorAll('tbody tr'));

        if (linhas.length === 0) {
            const todasLinhas = Array.from(tabela.querySelectorAll('tr'));
            linhas.push(...todasLinhas.filter((linha) => !linha.querySelector('th')));
        }

        campo.addEventListener('input', () => {
            const termo = normalizarTexto(campo.value);

            linhas.forEach((linha) => {
                const celulas = linha.querySelectorAll('td');
                const textoCelula = celulas[indicePesquisa]
                    ? celulas[indicePesquisa].textContent
                    : linha.textContent;

                linha.hidden = !normalizarTexto(textoCelula).includes(termo);
            });
        });
    });
});
