package util;

public interface IFileHandler {
    boolean readFromFile(String filePath);
    boolean writeToFile(String filePath);
}

