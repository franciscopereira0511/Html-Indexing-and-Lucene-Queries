/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adrian.proyectoprueba;

/**
 *
 * @author Adrian
 */

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class PruebaJsoup {

    private static List<String> stopWords;
    
    public static void main(String[] args) throws IOException {
        loadStopWords("E:\\Projects\\TEC\\III SEMESTRE\\prueba-htm\\stopWords.txt");
        PruebaJsoup.readDocumentFromCollection("E:\\Projects\\TEC\\III SEMESTRE\\Geografia");
        //PruebaJsoup.readDocumentFromCollection("E:\\Projects\\TEC\\III SEMESTRE\\Geografia\\América\\Estados_soberanos");
        //PruebaJsoup.readDocumentFromCollection("E:\\Projects\\TEC\\III SEMESTRE\\prueba-htm");
        
    }
    
    public static void readDocumentFromCollection( String path ) throws IOException {
        File parentDir = new File(path);
        File[] documentList = parentDir.listFiles();
        
        if( documentList == null ) {
            return;
        }
        
        for( File doc : documentList ) {
            if( doc.isDirectory() ) {
                readDocumentFromCollection(doc.getAbsolutePath());
            }
            else {
                readDocumentContents(doc);
            }
        }
    }
    
    public static void readDocumentContents( File input ) throws IOException {
        // Declares doc variables
        Document doc;
        String titulo, texto, ref, resumen, url, latinized, removeWhiteSpace,cleanText;
        String[] wordList;
        // Parses de input document
        doc = Jsoup.parse(input, "UTF-8", "");
        // Get the document TITLE markup
        titulo = doc.title();
        // Get ALL the elements with the tag <p>
        texto = doc.body().getElementsByTag("p").text();
        // Get ALL the elements with the tag <a>
        ref = doc.body().getElementsByTag("a").text();
        // Captures first 200 characters of body
        resumen = String.format("%.200s", texto);
        // Captures document relative route
        url = input.getPath();
        // Formats text in 3 steps: removes other alphabets, removes reduntant whitespace, removes stopwords
        latinized = texto.replaceAll("[^A-Za-zÁÉÍÓÚÜáéíóúüÑñ ]+", "");
        removeWhiteSpace = latinized.replaceAll("^ +| +$|( )+", "$1");
        cleanText = removeStopWordsFromDocument(removeWhiteSpace);
        // Separates each word from the formatted variable into wordList
        wordList = cleanText.split("\\s+");
        // Print outputs
        System.out.println("Título: " + titulo);
        System.out.println("Texto: " + texto);
        System.out.println("Texto Formateado: " + cleanText);
        System.out.println("Referencias: " + ref);
        System.out.println("Resumen: " + resumen);
        System.out.println("Cantidad de palabras: " + wordList.length);
        System.out.println("Ruta: " + url);
        /*
        System.out.print("[");
        for( String word : wordList ) {
            System.out.print(word + ", ");
        }
        */
        System.out.println("]");
        System.out.println("----------------------------------------------------");
        //createFile(titulo, cleanText);
    }
    
    public static void loadStopWords( String stopWordPath ) throws IOException {
        // Loads stopWords
        stopWords = Files.readAllLines(Paths.get(stopWordPath));
    }
    
    public static String removeStopWordsFromDocument( String originalText ) {
        // Turn input text to ArrayList><String>
        ArrayList<String> originalTextStream = Stream.of(originalText.toLowerCase().split(" ")).collect(Collectors.toCollection(ArrayList<String>::new));
        // Remove all stopWords from input
        originalTextStream.removeAll(stopWords);
        // Returns the new stream converted to String
        return originalTextStream.stream().collect(Collectors.joining(" "));
    }
    
    public static void createFile( String docTitle , String docContent ) throws IOException {
        String fileName = docTitle.replaceAll(" \\| CDPedia, la Wikipedia offline", "");
        List<String> contentToWrite = Arrays.asList(docContent);
        Path docName = Paths.get("E:\\Projects\\TEC\\III SEMESTRE\\prueba-htm\\"+ fileName + ".txt");
        Files.write(docName, contentToWrite, StandardCharsets.UTF_8);
    }
}
