package ru.cwcode.tkach.config.webeditor.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

public class RawFileService {
  private static final Set<String> ALLOWED_EXTENSIONS = Set.of("yml", "yaml", "toml", "json", "properties", "txt");
  private static final long MAX_FILE_SIZE = 2L * 1024L * 1024L;
  private final Path pluginsRoot;
  
  public RawFileService(Path pluginsRoot) {
    this.pluginsRoot = pluginsRoot.toAbsolutePath().normalize();
  }
  
  public RawDirectoryData list(String path) throws IOException {
    Path directory = resolveInsidePlugins(path);
    if (!Files.isDirectory(directory)) {
      throw new IllegalArgumentException("Directory not found");
    }
    if (containsSymbolicLink(directory)) {
      throw new SecurityException("Symbolic links are not allowed");
    }
    
    List<RawFileEntry> entries = new ArrayList<>();
    try (Stream<Path> stream = Files.list(directory)) {
      stream.filter(this::isVisibleEntry)
            .map(this::toEntry)
            .forEach(entries::add);
    }
    entries.sort(Comparator.comparing(RawFileEntry::directory).reversed()
                           .thenComparing(entry -> entry.name().toLowerCase(Locale.ROOT)));
    
    String relativePath = toRelativePath(directory);
    return new RawDirectoryData(relativePath, parentPath(relativePath), entries);
  }
  
  public RawFileData read(String path) throws IOException {
    Path file = resolveInsidePlugins(path);
    if (!Files.isRegularFile(file)) {
      throw new IllegalArgumentException("File not found");
    }
    if (containsSymbolicLink(file)) {
      throw new SecurityException("Symbolic links are not allowed");
    }
    if (!isAllowedFile(file)) {
      throw new SecurityException("File type is not allowed");
    }
    
    long size = Files.size(file);
    if (size > MAX_FILE_SIZE) {
      throw new IllegalArgumentException("File is too large");
    }
    
    String relativePath = toRelativePath(file);
    String name = file.getFileName().toString();
    return new RawFileData(relativePath, name, Files.readString(file, StandardCharsets.UTF_8), language(name), size);
  }
  
  public RawWriteResult write(String path, String body) {
    try {
      validateBodySize(body);
      Path file = resolveInsidePlugins(path);
      if (!Files.isRegularFile(file)) {
        return RawWriteResult.error(404, "File not found");
      }
      if (containsSymbolicLink(file)) {
        return RawWriteResult.error(403, "Symbolic links are not allowed");
      }
      if (!isAllowedFile(file)) {
        return RawWriteResult.error(403, "File type is not allowed");
      }
      
      writeAtomic(file, body, true);
      return RawWriteResult.ok(200);
    } catch (SecurityException e) {
      return RawWriteResult.error(403, e.getMessage());
    } catch (IllegalArgumentException e) {
      return RawWriteResult.error(413, e.getMessage());
    } catch (IOException e) {
      return RawWriteResult.error(500, "File write failed");
    }
  }
  
  public RawWriteResult create(String path, String body) {
    try {
      validateBodySize(body);
      Path file = resolveInsidePlugins(path);
      if (Files.exists(file)) {
        return RawWriteResult.error(409, "File already exists");
      }
      if (Files.isSymbolicLink(file)) {
        return RawWriteResult.error(403, "Symbolic links are not allowed");
      }
      if (!isAllowedFile(file)) {
        return RawWriteResult.error(403, "File type is not allowed");
      }
      
      Path parent = file.getParent();
      if (parent == null || !parent.startsWith(pluginsRoot)) {
        return RawWriteResult.error(400, "Invalid file path");
      }
      if (!Files.isDirectory(parent)) {
        return RawWriteResult.error(404, "Parent directory not found");
      }
      if (containsSymbolicLink(parent)) {
        return RawWriteResult.error(403, "Symbolic links are not allowed");
      }
      
      writeAtomic(file, body, false);
      return RawWriteResult.ok(201);
    } catch (FileAlreadyExistsException e) {
      return RawWriteResult.error(409, "File already exists");
    } catch (SecurityException e) {
      return RawWriteResult.error(403, e.getMessage());
    } catch (IllegalArgumentException e) {
      return RawWriteResult.error(413, e.getMessage());
    } catch (IOException e) {
      return RawWriteResult.error(500, "File create failed");
    }
  }
  
  public RawWriteResult delete(String path) {
    try {
      Path file = resolveInsidePlugins(path);
      if (!Files.isRegularFile(file)) {
        return RawWriteResult.error(404, "File not found");
      }
      if (containsSymbolicLink(file)) {
        return RawWriteResult.error(403, "Symbolic links are not allowed");
      }
      if (!isAllowedFile(file)) {
        return RawWriteResult.error(403, "File type is not allowed");
      }
      
      Files.delete(file);
      return RawWriteResult.ok(200);
    } catch (SecurityException e) {
      return RawWriteResult.error(403, e.getMessage());
    } catch (IOException e) {
      return RawWriteResult.error(500, "File delete failed");
    }
  }
  
  public RawWriteResult createDirectory(String path) {
    try {
      Path directory = resolveInsidePlugins(path);
      validatePathName(directory);
      if (Files.exists(directory)) {
        return RawWriteResult.error(409, "Directory already exists");
      }
      if (Files.isSymbolicLink(directory)) {
        return RawWriteResult.error(403, "Symbolic links are not allowed");
      }
      
      Path parent = directory.getParent();
      if (parent == null || !parent.startsWith(pluginsRoot)) {
        return RawWriteResult.error(400, "Invalid directory path");
      }
      if (!Files.isDirectory(parent)) {
        return RawWriteResult.error(404, "Parent directory not found");
      }
      if (containsSymbolicLink(parent)) {
        return RawWriteResult.error(403, "Symbolic links are not allowed");
      }
      
      Files.createDirectory(directory);
      return RawWriteResult.ok(201);
    } catch (FileAlreadyExistsException e) {
      return RawWriteResult.error(409, "Directory already exists");
    } catch (SecurityException e) {
      return RawWriteResult.error(403, e.getMessage());
    } catch (IllegalArgumentException e) {
      return RawWriteResult.error(400, e.getMessage());
    } catch (IOException e) {
      return RawWriteResult.error(500, "Directory create failed");
    }
  }
  
  public RawWriteResult deleteDirectory(String path) {
    try {
      Path directory = resolveInsidePlugins(path);
      if (directory.equals(pluginsRoot)) {
        return RawWriteResult.error(403, "Cannot delete plugins directory");
      }
      if (!Files.isDirectory(directory)) {
        return RawWriteResult.error(404, "Directory not found");
      }
      if (containsSymbolicLink(directory)) {
        return RawWriteResult.error(403, "Symbolic links are not allowed");
      }
      
      Files.delete(directory);
      return RawWriteResult.ok(200);
    } catch (java.nio.file.DirectoryNotEmptyException e) {
      return RawWriteResult.error(409, "Directory is not empty");
    } catch (SecurityException e) {
      return RawWriteResult.error(403, e.getMessage());
    } catch (IOException e) {
      return RawWriteResult.error(500, "Directory delete failed");
    }
  }
  
  public RawWriteResult rename(String path, String newName) {
    try {
      validateName(newName);
      Path source = resolveInsidePlugins(path);
      if (source.equals(pluginsRoot)) {
        return RawWriteResult.error(403, "Cannot rename plugins directory");
      }
      if (!Files.exists(source)) {
        return RawWriteResult.error(404, "Path not found");
      }
      if (containsSymbolicLink(source)) {
        return RawWriteResult.error(403, "Symbolic links are not allowed");
      }
      
      boolean directory = Files.isDirectory(source);
      if (!directory && !Files.isRegularFile(source)) {
        return RawWriteResult.error(403, "Path type is not allowed");
      }
      if (!directory && !isAllowedFile(source)) {
        return RawWriteResult.error(403, "File type is not allowed");
      }
      
      Path target = source.resolveSibling(newName).toAbsolutePath().normalize();
      if (!target.startsWith(pluginsRoot)) {
        return RawWriteResult.error(403, "Path escapes plugins directory");
      }
      if (Files.exists(target)) {
        return RawWriteResult.error(409, "Target already exists");
      }
      if (Files.isSymbolicLink(target)) {
        return RawWriteResult.error(403, "Symbolic links are not allowed");
      }
      if (!directory && !isAllowedFile(target)) {
        return RawWriteResult.error(403, "File type is not allowed");
      }
      
      Files.move(source, target);
      return RawWriteResult.ok(200);
    } catch (FileAlreadyExistsException e) {
      return RawWriteResult.error(409, "Target already exists");
    } catch (SecurityException e) {
      return RawWriteResult.error(403, e.getMessage());
    } catch (IllegalArgumentException e) {
      return RawWriteResult.error(400, e.getMessage());
    } catch (IOException e) {
      return RawWriteResult.error(500, "Rename failed");
    }
  }
  
  private Path resolveInsidePlugins(String path) {
    String safePath = path == null ? "" : path;
    Path resolved = pluginsRoot.resolve(safePath).toAbsolutePath().normalize();
    if (!resolved.startsWith(pluginsRoot)) {
      throw new SecurityException("Path escapes plugins directory");
    }
    return resolved;
  }
  
  private boolean isVisibleEntry(Path path) {
    if (containsSymbolicLink(path)) {
      return false;
    }
    return Files.isDirectory(path) || isAllowedFile(path);
  }
  
  private void validateBodySize(String body) {
    if (body.getBytes(StandardCharsets.UTF_8).length > MAX_FILE_SIZE) {
      throw new IllegalArgumentException("File is too large");
    }
  }
  
  private void validatePathName(Path path) {
    validateName(path.getFileName().toString());
  }
  
  private void validateName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Name is empty");
    }
    if (name.equals(".") || name.equals("..") || name.contains("/") || name.contains("\\")) {
      throw new IllegalArgumentException("Name must not contain path separators");
    }
  }
  
  private void writeAtomic(Path file, String body, boolean replaceExisting) throws IOException {
    Path temp = Files.createTempFile(file.getParent(), file.getFileName().toString(), ".cwtmp");
    try {
      Files.writeString(temp, body, StandardCharsets.UTF_8, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
      try {
        moveTempFile(temp, file, replaceExisting, true);
      } catch (AtomicMoveNotSupportedException e) {
        moveTempFile(temp, file, replaceExisting, false);
      }
    } finally {
      Files.deleteIfExists(temp);
    }
  }
  
  private void moveTempFile(Path temp, Path file, boolean replaceExisting, boolean atomic) throws IOException {
    if (replaceExisting && atomic) {
      Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
      return;
    }
    if (replaceExisting) {
      Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
      return;
    }
    if (atomic) {
      Files.move(temp, file, StandardCopyOption.ATOMIC_MOVE);
      return;
    }
    Files.move(temp, file);
  }
  
  private boolean containsSymbolicLink(Path path) {
    Path relative = pluginsRoot.relativize(path);
    Path current = pluginsRoot;
    for (Path part : relative) {
      current = current.resolve(part);
      if (Files.isSymbolicLink(current)) {
        return true;
      }
    }
    return false;
  }
  
  private RawFileEntry toEntry(Path path) {
    boolean directory = Files.isDirectory(path);
    long size = 0;
    if (!directory) {
      try {
        size = Files.size(path);
      } catch (IOException ignored) {
        size = -1;
      }
    }
    return new RawFileEntry(path.getFileName().toString(), toRelativePath(path), directory, size);
  }
  
  private boolean isAllowedFile(Path path) {
    String fileName = path.getFileName().toString();
    int dot = fileName.lastIndexOf('.');
    if (dot < 0 || dot == fileName.length() - 1) {
      return false;
    }
    return ALLOWED_EXTENSIONS.contains(fileName.substring(dot + 1).toLowerCase(Locale.ROOT));
  }
  
  private String language(String fileName) {
    String lower = fileName.toLowerCase(Locale.ROOT);
    if (lower.endsWith(".yml") || lower.endsWith(".yaml")) return "yaml";
    if (lower.endsWith(".json")) return "json";
    if (lower.endsWith(".toml")) return "toml";
    if (lower.endsWith(".properties")) return "properties";
    return "plaintext";
  }
  
  private String toRelativePath(Path path) {
    return pluginsRoot.relativize(path).toString().replace('\\', '/');
  }
  
  private String parentPath(String path) {
    if (path == null || path.isBlank()) {
      return null;
    }
    int separator = path.lastIndexOf('/');
    if (separator < 0) {
      return "";
    }
    return path.substring(0, separator);
  }
  
  public record RawDirectoryData(String path, String parentPath, List<RawFileEntry> entries) {
  }
  
  public record RawFileEntry(String name, String path, boolean directory, long size) {
  }
  
  public record RawFileData(String path, String name, String content, String language, long size) {
  }
  
  public record RawWriteResult(boolean success, int status, String message) {
    public static RawWriteResult ok(int status) {
      return new RawWriteResult(true, status, "OK");
    }
    
    public static RawWriteResult error(int status, String message) {
      return new RawWriteResult(false, status, message);
    }
  }
}
