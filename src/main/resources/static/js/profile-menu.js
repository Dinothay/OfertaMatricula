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

    const userChips = document.querySelectorAll('.user-chip');

    userChips.forEach((chip) => {
        const trigger = chip.querySelector('.user-chip-trigger');
        const menu = chip.querySelector('.user-menu');

        if (!trigger || !menu) return;

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
});
