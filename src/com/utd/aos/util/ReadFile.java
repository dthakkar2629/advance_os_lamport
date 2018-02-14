package com.utd.aos.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.Callable;

public class ReadFile implements Callable {

    private String directoryPath;
    private String fileName;

    public ReadFile(String directoryPath, String fileName)  {
        this.directoryPath = directoryPath;
        this.fileName = fileName;
    }

    public String call()   {

        String lastLine = "";
        try {
            FileReader fileReader = new FileReader(directoryPath + "/" + fileName);

            BufferedReader bufferedReader = new BufferedReader(fileReader);
            if (bufferedReader == null) {
                System.out.println("can't read file: " + fileName);
                return "can't read file: " + fileName;
            }
            String line = bufferedReader.readLine();;
            while (line != null) {
                lastLine = line;
                line = bufferedReader.readLine();
            }

            // Always close files.
            System.out.println(lastLine);
            bufferedReader.close();
            return lastLine;
        }
        catch(Exception ex) {
            System.out.println("Error occurred while reading file " + fileName);
            return "Error occurred while reading file " + fileName;
        }
    }
}
