(function () {
  const config = window.CW_CONFIG_EDITOR;

  document.querySelectorAll('[data-back-button]').forEach((button) => {
    button.addEventListener('click', (event) => {
      event.preventDefault();
      history.back();
    });
  });

  const editor = new JSONEditor(document.getElementById('editor_holder'), {
    schema: config.schema,
    theme: 'bootstrap5',
    iconlib: 'bootstrap',
    startval: config.starting,
    use_default_values: true,
    use_name_attributes: true,
    prompt_before_delete: true,
    show_opt_in: true,
    enable_array_copy: true,
    remove_button_labels: true,
    show_errors: 'interaction',
    disable_properties: true,
    no_additional_properties: true
  });

  document.getElementById('submit').addEventListener('click', () => {
    fetch(config.basePath + '/update/' + config.namespacePath + '/' + config.namePath, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(editor.getValue())
    })
      .then((response) => {
        if (!response.ok) {
          return response.text().then((error) => {
            throw new Error(error);
          });
        }
        return response.json();
      })
      .then(() => {
        new bootstrap.Toast(document.getElementById('successToast')).show();
      })
      .catch((error) => {
        document.getElementById('errorMessage').textContent = error.message || 'Неизвестная ошибка';
        new bootstrap.Toast(document.getElementById('errorToast')).show();
      });
  });

  document.querySelectorAll('.toast').forEach((toast) => new bootstrap.Toast(toast));
})();
