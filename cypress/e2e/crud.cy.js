describe('CRUD Itens - AngularJS + Spring', () => {
  const base = Cypress.config('baseUrl') || 'http://localhost:8080';

  beforeEach(() => {
    cy.visit(`${base}/index.html`);
  });

  it('deve criar, listar, editar e deletar um item', () => {
    // Intercepta chamadas para sincronizar
    cy.intercept('GET', '/api/v1/items*').as('list');
    cy.intercept('POST', '/api/v1/items').as('create');
    cy.intercept('PUT', '/api/v1/items/*').as('update');
    cy.intercept('DELETE', '/api/v1/items/*').as('remove');

    // Garante que a página inicial carregou e listou
    cy.wait('@list');

    // CRIAR
    cy.get('input[ng-model="vm.form.nome"]').clear().type('Teclado');
    cy.get('textarea[ng-model="vm.form.descricao"]').clear().type('Mecânico');
    cy.get('select[ng-model="vm.form.status"]').select('ATIVO');
    cy.get('input[ng-model="vm.form.preco"]').clear().type('350');
    cy.get('input[ng-model="vm.form.categoria"]').clear().type('ACESSORIOS');

    cy.contains('button', 'Criar').should('be.enabled').click();
    cy.wait('@create').its('response.statusCode').should('be.oneOf', [201, 200]);
    cy.wait('@list'); // app recarrega lista

    cy.contains('td', 'Teclado').should('exist');

    // EDITAR
    cy.contains('tr', 'Teclado').within(() => {
      cy.contains('Editar').click();
    });

    cy.get('input[ng-model="vm.form.nome"]').clear().type('Teclado RGB');
    cy.contains('button', 'Atualizar').should('be.enabled').click();
    cy.wait('@update').its('response.statusCode').should('eq', 200);
    cy.wait('@list');

    cy.contains('td', 'Teclado RGB').should('exist');

    // DELETAR (stub do confirm ANTES do clique)
    cy.window().then(win => cy.stub(win, 'confirm').returns(true));
    cy.contains('tr', 'Teclado RGB').within(() => {
      cy.contains('Excluir').click();
    });
    cy.wait('@remove').its('response.statusCode').should('be.oneOf', [200, 204]);
    cy.wait('@list');

    cy.contains('tr', 'Teclado RGB').should('not.exist');
  });
});
