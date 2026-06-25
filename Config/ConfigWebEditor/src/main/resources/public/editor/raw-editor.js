(function () {
  const config = window.CW_CONFIG_EDITOR;

  function showToast(id, timeout) {
    const toast = document.getElementById(id);
    toast.style.display = 'block';
    setTimeout(() => {
      toast.style.display = 'none';
    }, timeout);
  }

  function showError(error) {
    document.getElementById('errorMessage').textContent = error.message || 'Неизвестная ошибка';
    showToast('errorToast', 10000);
  }

  document.querySelectorAll('[data-toast-close]').forEach((button) => {
    button.addEventListener('click', () => {
      button.closest('.toast').style.display = 'none';
    });
  });

  document.querySelectorAll('[data-back-button]').forEach((button) => {
    button.addEventListener('click', () => history.back());
  });

  const yamlModelUri = monaco.Uri.parse('inmemory://model/' + config.name + '.yaml');
  const diagnosticsOptions = {
    enableSchemaRequest: false,
    hover: true,
    completion: true,
    validate: true,
    format: true,
    schemas: config.schemaEnabled ? [
      {
        fileMatch: ['*'],
        uri: 'inmemory://schema/' + config.name + '.json',
        schema: config.schema
      }
    ] : []
  };

  monacoYaml.configureMonacoYaml(monaco, diagnosticsOptions);

  const model = monaco.editor.createModel(config.currentYaml, 'yaml', yamlModelUri);
  const editor = monaco.editor.create(document.getElementById('editor'), {
    model: model,
    theme: 'vs-dark',
    automaticLayout: true,
    fontSize: 13,
    tabSize: 2,
    insertSpaces: true,
    minimap: { enabled: false },
    scrollBeyondLastLine: false,
    wordWrap: 'on',
    formatOnPaste: true,
    formatOnType: true,
    lineNumbers: 'on',
    renderWhitespace: 'none',
    padding: { top: 40 }
  });

  editor.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyCode.KeyS, () => {
    document.getElementById('submit').click();
  });

  document.getElementById('submit').addEventListener('click', () => {
    fetch(config.basePath + config.updatePath + config.namespacePath + '/' + config.namePath, {
      method: 'PUT',
      headers: {
        'Content-Type': 'text/plain; charset=utf-8'
      },
      body: editor.getValue()
    })
      .then((response) => {
        if (!response.ok) {
          return response.text().then((error) => {
            throw new Error(error);
          });
        }
        return response.json();
      })
      .then(() => showToast('successToast', 5000))
      .catch(showError);
  });
})();
