/**
 *
 * WriteFile.java - Read the last line of a given file by TCPServer
 * @author  Saurav Sharma
 *
 */

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
                return "can't read file: " + fileName;
            }
            String line = bufferedReader.readLine();
            while (line != null) {
                lastLine = line;
                line = bufferedReader.readLine();
            }

            // Always close files.
            bufferedReader.close();
            return lastLine;
        }
        catch(Exception ex) {
            ex.printStackTrace();
            return String.valueOf(ex.getStackTrace());
//            return "Error occurred while reading file " + fileName;
        }
    }
}
