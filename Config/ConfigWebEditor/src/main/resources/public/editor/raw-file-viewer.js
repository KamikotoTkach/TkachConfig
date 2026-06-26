(function () {
  const config = window.CW_RAW_FILE_VIEWER;

  document.querySelectorAll('[data-back-button]').forEach((button) => {
    button.addEventListener('click', () => history.back());
  });
  
  function showError(error) {
    alert(error.message || 'Неизвестная ошибка');
  }
  
  function parentPath(path) {
    const separator = path.lastIndexOf('/');
    if (separator < 0) {
      return '';
    }
    return path.substring(0, separator);
  }
  
  function encodePath(path) {
    return path.split('/').map(encodeURIComponent).join('/');
  }
  
  function validateName(name) {
    return name && !name.includes('/') && !name.includes('\\') && name !== '.' && name !== '..';
  }

  const modelUri = monaco.Uri.parse('inmemory://raw-file/' + config.name);
  const model = monaco.editor.createModel(config.content, config.language || 'plaintext', modelUri);
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
    lineNumbers: 'on',
    renderWhitespace: 'none',
    padding: { top: 40 }
  });
  
  editor.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyCode.KeyS, () => {
    document.getElementById('submit').click();
  });
  
  document.getElementById('submit').addEventListener('click', () => {
    fetch(config.basePath + '/raw/file/' + config.pathUrl, {
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
      .then(() => alert('Файл сохранён'))
      .catch(showError);
  });
  
  document.getElementById('rename').addEventListener('click', () => {
    const newName = prompt('Новое имя', config.name);
    if (newName === null) {
      return;
    }
    const trimmed = newName.trim();
    if (!validateName(trimmed)) {
      showError(new Error('Введите только новое имя без пути'));
      return;
    }
    fetch(config.basePath + '/raw/rename/' + config.pathUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'text/plain; charset=utf-8'
      },
      body: trimmed
    })
      .then((response) => {
        if (!response.ok) {
          return response.text().then((error) => {
            throw new Error(error);
          });
        }
        const targetPath = parentPath(config.path);
        const renamedPath = targetPath ? targetPath + '/' + trimmed : trimmed;
        window.location.href = config.basePath + '/raw/view/' + encodePath(renamedPath);
      })
      .catch(showError);
  });
  
  document.getElementById('delete').addEventListener('click', () => {
    if (!confirm('Удалить файл plugins/' + config.path + '?')) {
      return;
    }
    fetch(config.basePath + '/raw/file/' + config.pathUrl, {
      method: 'DELETE'
    })
      .then((response) => {
        if (!response.ok) {
          return response.text().then((error) => {
            throw new Error(error);
          });
        }
        history.back();
      })
      .catch(showError);
  });
})();
