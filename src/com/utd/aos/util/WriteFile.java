package com.utd.aos.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Callable;

public class WriteFile implements Callable {

    private String directoryPath;
    private String fileName;
    private String line;

    public WriteFile(String directoryPath, String fileName, String line)  {
        this.directoryPath = directoryPath;
        this.fileName = fileName;
        this.line = line;
    }

    public Boolean call()   {
        try {
            // Assume default encoding.
            FileWriter fileWriter = new FileWriter(directoryPath + "/" + fileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            // append a newline character.
            bufferedWriter.write(line);
            bufferedWriter.newLine();
            bufferedWriter.close();
            return true;
        }
        catch(IOException ex) {
            System.out.println("Error writing to file '" + fileName + "'");
            return false;
        }
    }
}
