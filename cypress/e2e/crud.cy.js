describe('CRUD Itens - AngularJS + Spring', () => {
  const base = 'http://localhost:8080';

  it('deve criar, listar, editar e deletar um item', () => {
    // abre a home (index.html)
    cy.visit(`${base}/index.html`);

    // criar
    cy.get('input[ng-model="vm.form.nome"]').type('Teclado');
    cy.get('textarea[ng-model="vm.form.descricao"]').type('Mecânico');
    cy.get('select[ng-model="vm.form.status"]').select('ATIVO');
    cy.get('input[ng-model="vm.form.preco"]').type('350');
    cy.get('input[ng-model="vm.form.categoria"]').type('ACESSORIOS');
    cy.contains('button', 'Criar').click();

    // deve aparecer na lista
    cy.contains('td', 'Teclado').should('exist');

    // editar
    cy.contains('tr', 'Teclado').within(() => {
      cy.contains('Editar').click();
    });
    cy.get('input[ng-model="vm.form.nome"]').clear().type('Teclado RGB');
    cy.contains('button', 'Atualizar').click();
    cy.contains('td', 'Teclado RGB').should('exist');

    // excluir
    cy.contains('tr', 'Teclado RGB').within(() => {
      cy.contains('Excluir').click();
    });
    // confirma o confirm()
    cy.on('window:confirm', () => true);
    cy.contains('tr', 'Teclado RGB').should('not.exist');
  });
});
