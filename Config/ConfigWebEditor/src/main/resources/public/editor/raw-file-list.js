(function () {
  const config = window.CW_RAW_DIRECTORY;
  const form = document.getElementById('rawCreateForm');
  const nameInput = document.getElementById('rawCreateName');
  const directoryForm = document.getElementById('rawCreateDirectoryForm');
  const directoryNameInput = document.getElementById('rawCreateDirectoryName');
  const errorBlock = document.getElementById('rawCreateError');
  const dropZone = document.getElementById('rawDropZone');

  function showError(message) {
    errorBlock.textContent = message;
    errorBlock.classList.remove('d-none');
  }

  function clearError() {
    errorBlock.classList.add('d-none');
  }

  function pathJoin(base, name) {
    return base ? base + '/' + name : name;
  }

  function encodePath(path) {
    return path.split('/').map(encodeURIComponent).join('/');
  }

  function validateName(name) {
    return name && !name.includes('/') && !name.includes('\\') && name !== '.' && name !== '..';
  }

  function requestText(response) {
    if (!response.ok) {
      return response.text().then((error) => {
        throw new Error(error);
      });
    }
    return response.text();
  }

  function reload() {
    window.location.reload();
  }

  form.addEventListener('submit', (event) => {
    event.preventDefault();
    clearError();

    const name = nameInput.value.trim();
    if (!validateName(name)) {
      showError('Введите только имя файла без пути');
      return;
    }

    const filePath = pathJoin(config.directoryPath, name);
    const filePathUrl = encodePath(filePath);
    fetch(config.basePath + '/raw/file/' + filePathUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'text/plain; charset=utf-8'
      },
      body: ''
    })
      .then((response) => {
        if (!response.ok) {
          return response.text().then((error) => {
            throw new Error(error);
          });
        }
        window.location.href = config.basePath + '/raw/view/' + filePathUrl;
      })
      .catch((error) => showError(error.message || 'Не удалось создать файл'));
  });

  directoryForm.addEventListener('submit', (event) => {
    event.preventDefault();
    clearError();

    const name = directoryNameInput.value.trim();
    if (!validateName(name)) {
      showError('Введите только имя папки без пути');
      return;
    }

    fetch(config.basePath + '/raw/dir/' + encodePath(pathJoin(config.directoryPath, name)), {
      method: 'POST'
    })
      .then(requestText)
      .then(reload)
      .catch((error) => showError(error.message || 'Не удалось создать папку'));
  });

  document.querySelectorAll('[data-rename-entry]').forEach((button) => {
    button.addEventListener('click', () => {
      clearError();
      const entry = button.closest('[data-entry-path]');
      const oldName = entry.dataset.entryName;
      const newName = prompt('Новое имя', oldName);
      if (newName === null) {
        return;
      }
      const trimmed = newName.trim();
      if (!validateName(trimmed)) {
        showError('Введите только новое имя без пути');
        return;
      }
      fetch(config.basePath + '/raw/rename/' + entry.dataset.entryPathUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'text/plain; charset=utf-8'
        },
        body: trimmed
      })
        .then(requestText)
        .then(reload)
        .catch((error) => showError(error.message || 'Не удалось переименовать'));
    });
  });

  document.querySelectorAll('[data-delete-entry]').forEach((button) => {
    button.addEventListener('click', () => {
      clearError();
      const entry = button.closest('[data-entry-path]');
      const type = entry.dataset.entryType;
      const path = entry.dataset.entryPath;
      if (!confirm('Удалить ' + (type === 'directory' ? 'папку ' : 'файл ') + 'plugins/' + path + '?')) {
        return;
      }
      fetch(config.basePath + (type === 'directory' ? '/raw/dir/' : '/raw/file/') + entry.dataset.entryPathUrl, {
        method: 'DELETE'
      })
        .then(requestText)
        .then(reload)
        .catch((error) => showError(error.message || 'Не удалось удалить'));
    });
  });

  dropZone.addEventListener('dragover', (event) => {
    event.preventDefault();
    dropZone.classList.add('bg-light');
  });

  dropZone.addEventListener('dragleave', () => {
    dropZone.classList.remove('bg-light');
  });

  dropZone.addEventListener('drop', (event) => {
    event.preventDefault();
    clearError();
    dropZone.classList.remove('bg-light');

    const files = Array.from(event.dataTransfer.files || []);
    if (files.length === 0) {
      return;
    }

    Promise.all(files.map((file) => {
      if (!validateName(file.name)) {
        return Promise.reject(new Error('Некорректное имя файла: ' + file.name));
      }
      return file.text().then((content) => fetch(config.basePath + '/raw/file/' + encodePath(pathJoin(config.directoryPath, file.name)), {
        method: 'POST',
        headers: {
          'Content-Type': 'text/plain; charset=utf-8'
        },
        body: content
      }).then(requestText));
    }))
      .then(reload)
      .catch((error) => showError(error.message || 'Не удалось загрузить файл'));
  });
})();
