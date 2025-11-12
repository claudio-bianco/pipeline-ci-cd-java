(function() {
  'use strict';

  angular.module('crudApp', [])
    .constant('API_BASE', 'http://localhost:8080') // ajuste aqui quando publicar
    .controller('CrudController', CrudController);

  CrudController.$inject = ['$http', '$timeout', 'API_BASE'];
  function CrudController($http, $timeout, API_BASE) {
    var vm = this;

    // estado
    vm.items = [];
    vm.page = 0;
    vm.size = 10;
    vm.totalItems = 0;
    vm.totalPages = 0;

    vm.filterText = '';
    vm.msgSuccess = '';
    vm.msgError = '';

    vm.form = {
      nome: '',
      descricao: '',
      status: 'ATIVO',
      preco: null,
      categoria: ''
    };

    vm.lookupId = null;
    vm.lookupItem = null;

    // métodos expostos
    vm.loadPage = loadPage;
    vm.prevPage = function() { if (vm.page > 0) loadPage(vm.page - 1); };
    vm.nextPage = function() { if ((vm.page + 1) < vm.totalPages) loadPage(vm.page + 1); };
    vm.reload = function() { loadPage(vm.page); };

    vm.searchFilter = function(it) {
      if (!vm.filterText) return true;
      var f = vm.filterText.toLowerCase();
      return (it.nome && it.nome.toLowerCase().indexOf(f) >= 0) ||
             (it.descricao && it.descricao.toLowerCase().indexOf(f) >= 0) ||
             (it.categoria && it.categoria.toLowerCase().indexOf(f) >= 0);
    };

    vm.resetForm = resetForm;
    vm.edit = edit;
    vm.save = save;
    vm.remove = remove;
    vm.fetchById = fetchById;

    // init
    loadPage(0);

    // ------------- Implementação -------------

    function loadPage(page) {
      vm.msgSuccess = ''; vm.msgError = '';

      var url = API_BASE + '/api/v1/items?page=' + page + '&size=' + vm.size;
      $http.get(url).then(function(resp) {
        vm.items = resp.data.items || [];
        vm.page = resp.data.page || 0;
        vm.size = resp.data.size || vm.size;
        vm.totalItems = resp.data.totalItems || 0;
        vm.totalPages = resp.data.totalPages || 0;
      }).catch(handleHttpError);
    }

    function resetForm() {
      vm.form = { nome: '', descricao: '', status: 'ATIVO', preco: null, categoria: '' };
      vm.lookupItem = null;
      vm.msgSuccess = ''; vm.msgError = '';
    }

    function edit(item) {
      vm.form = angular.copy(item);
      vm.msgSuccess = ''; vm.msgError = '';
    }

    function save() {
      vm.msgSuccess = ''; vm.msgError = '';

      var payload = {
        nome: vm.form.nome,
        descricao: vm.form.descricao,
        status: vm.form.status,
        preco: vm.form.preco,
        categoria: vm.form.categoria
      };

      if (vm.form.id) {
        // UPDATE
        $http.put(API_BASE + '/api/v1/items/' + vm.form.id, payload)
          .then(function(resp) {
            vm.msgSuccess = 'Item atualizado com sucesso.';
            replaceInList(resp.data);
            // recarrega a página atual para refletir paginação
            loadPage(vm.page);
            // limpa feedback
            clearMsgLater();
          })
          .catch(handleHttpError);
      } else {
        // CREATE
        $http.post(API_BASE + '/api/v1/items', payload)
          .then(function(resp) {
            vm.msgSuccess = 'Item criado com sucesso.';
            // insere no topo visualmente
            vm.items.unshift(resp.data);
            vm.totalItems += 1;
            // pode reconsultar página 0 para ficar consistente
            loadPage(0);
            resetForm();
            clearMsgLater();
          })
          .catch(handleHttpError);
      }
    }

    function remove(item) {
      if (!item || !item.id) return;
      if (!confirm('Confirma excluir o item #' + item.id + '?')) return;

      $http.delete(API_BASE + '/api/v1/items/' + item.id)
        .then(function() {
          vm.msgSuccess = 'Item excluído com sucesso.';
          removeFromList(item.id);
          // ajusta paginação caso necessário
          loadPage(vm.page);
          clearMsgLater();
        })
        .catch(handleHttpError);
    }

    function fetchById() {
      vm.lookupItem = null;
      vm.msgSuccess = ''; vm.msgError = '';

      if (!vm.lookupId) return;
      $http.get(API_BASE + '/api/v1/items/' + vm.lookupId)
        .then(function(resp) {
          vm.lookupItem = resp.data;
        })
        .catch(handleHttpError);
    }

    // ---------- Auxiliares ----------

    function replaceInList(updated) {
      var idx = vm.items.findIndex(function(x) { return x.id === updated.id; });
      if (idx >= 0) vm.items[idx] = updated;
    }

    function removeFromList(id) {
      vm.items = vm.items.filter(function(x) { return x.id !== id; });
    }

    function handleHttpError(err) {
      if (err && err.data) {
        if (err.data.message) vm.msgError = err.data.message;
        else vm.msgError = angular.toJson(err.data);
      } else {
        vm.msgError = 'Erro de comunicação com a API.';
      }
      clearMsgLater();
    }

    function clearMsgLater() {
      $timeout(function() {
        vm.msgSuccess = ''; vm.msgError = '';
      }, 3500);
    }
  }
})();
