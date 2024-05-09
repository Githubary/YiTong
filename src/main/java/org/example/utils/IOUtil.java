package org.example.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * description:
 *
 * @author liuhuayang
 * date: 2024/5/9 17:29
 */
public class IOUtil {

    public static void writeJson2File(String filePath,String content){
        try (FileWriter fileWriter = new FileWriter(filePath, true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.write(content+";");
            bufferedWriter.newLine(); // Add newline character to maintain line breaks
            System.out.println("Content has been appended to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readJsonFromFile(String filePath){
        StringBuilder content = new StringBuilder();

        try (FileReader fileReader = new FileReader(filePath);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line);
                content.append("\n"); // Add newline character to maintain line breaks
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

}
