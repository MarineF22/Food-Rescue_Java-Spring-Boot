package com.fournierMarine.cgi.stage.FoodRescueApp.controllers;

import com.fournierMarine.cgi.stage.FoodRescueApp.*;
import com.fournierMarine.cgi.stage.FoodRescueApp.component.IngredientsExtractor;
import com.fournierMarine.cgi.stage.FoodRescueApp.component.UserIngredients;
import com.fournierMarine.cgi.stage.FoodRescueApp.models.Items;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.ArrayList;
import javax.servlet.http.HttpSession;
@Controller
public class FoodRescue {
    private ITesseract tesseract;
    private DataBaseQueries query;
    private ArrayList<String> itemsList;
    private ArrayList<Items> filteredIngredient;
    private ArrayList<String> extractedApiIngredientsList;
    private ArrayList<Map<String, String>> recipesList;
    private UserIngredients userIngredients;


    @Autowired
    public FoodRescue(DataBaseQueries query, UserIngredients userIngredients) {
        tesseract = new Tesseract();
        // tesseract.setLanguage("eng"); // Set the language for OCR to English
        tesseract.setLanguage("por"); // Set the language for OCR to Portuguese
        this.query = query;
        itemsList = new ArrayList<>();
        filteredIngredient = new ArrayList<>();
        extractedApiIngredientsList = new ArrayList<>();
        recipesList = new ArrayList<>();
        this.userIngredients = userIngredients;
    }


    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        // Check if the user is logged in
        String loggedInUser = (String) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            // User is logged in
            model.addAttribute("loggedInUser", loggedInUser);
        }

        return "index";
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model, HttpSession session) {
        String loggedInUser = (String) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            // User is logged in
            model.addAttribute("loggedInUser", loggedInUser);
        }

        if (file.isEmpty()) {
            model.addAttribute("message", "Please select a file to upload.");
            return "index";
        }

        try {
            String fileName = file.getOriginalFilename();

            // Verify if the file is a PDF
            String contentType = file.getContentType();
            if (!contentType.equals(MediaType.APPLICATION_PDF_VALUE)) {
                model.addAttribute("message", "Please upload a PDF file.");
                return "index";
            }

            // Create the PDF directory within the FoodRescueApp folder
            String directoryPath = System.getProperty("user.dir") + File.separator + "pdfs";
            File uploadDir = new File(directoryPath);
            if (uploadDir.exists()) {
                FileUtils.deleteDirectory(uploadDir);
            }
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Save the uploaded PDF in the directory
            File pdfFile = new File(uploadDir, fileName);
            Path pdfPath = pdfFile.toPath();
            Files.copy(file.getInputStream(), pdfPath, StandardCopyOption.REPLACE_EXISTING);

            model.addAttribute("message", "File uploaded successfully.");
            model.addAttribute("filename", fileName);

            // Perform OCR on the uploaded PDF
            String result = extractTextFromPDF(pdfPath.toString());

            // Remove specific part from OCR text
            String processedResult = TextProcessor.removePattern(result);
            System.out.println(processedResult);

            itemsList = getItemsList(processedResult);
            System.out.println(itemsList);

            filteredIngredient  = IngredientsExtractor.getFilteredIngredients(itemsList, query);
            System.out.println(filteredIngredient);

            extractedApiIngredientsList = IngredientsExtractor.getExtractedApiIngredientsList(filteredIngredient);
            System.out.println(extractedApiIngredientsList);

            recipesList = RecipesApiConnection.getRecipesList(extractedApiIngredientsList);
            System.out.println(recipesList);

            // To Template
            //model.addAttribute("ocrResult", processedResult);

            model.addAttribute("Ingredients", itemsList);
            model.addAttribute("extractedApiIngredients", extractedApiIngredientsList);
            model.addAttribute("recipes", recipesList);

            if (loggedInUser != null) {
                userIngredients.addToListUsers(extractedApiIngredientsList, loggedInUser, query);
            }

            for (Map<String, String> recipe : recipesList) {
                System.out.println(recipe);
            }


        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("message", "Failed to upload file.");
        }

        return "index";
    }

    @PostMapping("/recipe/{idMeal}")
    public String viewRecipe(@PathVariable("idMeal") String idMeal, Model model, HttpSession session) {
        // Check if the user is logged in
        String loggedInUser = (String) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            // User is logged in
            model.addAttribute("loggedInUser", loggedInUser);
        }
        // Perform operations with the ID (e.g., retrieve recipe details from the database)
        Map<String, String> recipeMap = RecipesApiConnection.getRecipeMap(idMeal);

        model.addAttribute("idContent", recipeMap);

        return "recipe";
    }

    // Method to extract text from a PDF file using OCR
    public String extractTextFromPDF(String filePath) {
        try {
            File pdfFile = new File(filePath);
            String result = tesseract.doOCR(pdfFile);
            return result;
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
            return "Error while performing OCR on PDF";
        }
    }

    // Method to get a ArrayList of items from processed OCR text
    public ArrayList<String> getItemsList(String processedResult) {
        ArrayList<String> newItemList = new ArrayList<>();
        String[] resultLines = processedResult.split("\n");
        for (String line : resultLines) {

            if (line.startsWith("IS")) {
                line = line.replace("IS", "");
            }
            if (!line.trim().isEmpty()) {
                newItemList.remove(String.valueOf("/"));
                newItemList.add(line.trim());
            }
        }
        return newItemList;
    }

}
