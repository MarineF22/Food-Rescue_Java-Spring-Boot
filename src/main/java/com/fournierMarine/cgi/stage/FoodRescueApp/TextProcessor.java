package com.fournierMarine.cgi.stage.FoodRescueApp;

public class TextProcessor {
    public static String removePattern(String text) {
        // Find the index of "DESCRICAO VALOR" and "TOTAL A PAGAR" in the text
        int descricaoValorIndex = text.indexOf("DESCRICAO VALOR");
        int totalAPagarIndex = text.indexOf("TOTAL A PAGAR");

        // If both "DESCRICAO VALOR" and "TOTAL A PAGAR" are found, remove the unwanted text
        if (descricaoValorIndex >= 0 && totalAPagarIndex >= 0) {
            // Remove the text before "DESCRICAO VALOR"
            String trimmedText = text.substring(descricaoValorIndex);

            // Remove the text after "TOTAL A PAGAR"
            int endIndex = trimmedText.indexOf("TOTAL A PAGAR") + "TOTAL A PAGAR".length();
            trimmedText = trimmedText.substring(0, endIndex);

            // Remove text, numbers and caracters
            trimmedText = trimmedText.replaceAll("(?:\\d+\\,\\d+)|(?:\\d+[a-zA-Z]+)|(?:\\d)|(?:\\%)|(?:\\*)|(?:x)|(?:KG)|(?:\\(\\w+\\))|(?:Isento-IVA)|(?:-Lei n.o)|(?:DESCRICAO VALOR)|(?:TOTAL A PAGAR)|(?:DESCONTO DIRETO)|(?:POUPANCA)|(?: X )|(?: CNT)", "");

            // Replace "QJ" with "QUEIJO"
           // trimmedText = trimmedText.replace("QJ", "QUEIJO").replace("TOM ","TOMATE");

            // Define the non-alimentar categories
            String[] nonAlimentarCategories = {
                    "Limpeza do Lar:",
                    "Beleza:",
                    "Casa-Cozinha"
            };

            // Define the alimentar categories
            String[] alimentarCategories = {
                    "Mercearia Doce:",
                    "Mercearia Salgada",
                    "Laticinios/Beb. Veg.:",
                    "Laticinios:",
                    "Talho:",
                    "Charcutaria&Queijos:",
                    "charcutaria:",
                    "Frutas e Legumes:",
                    "Padaria:",
                    "Bio e Saudavel:",
                    "Soft Drinks:",
                    "Take Away:"
            };

            // Remove everything between non-alimentar categories and the subsequent alimentar category
            for (String nonAlimentarCategory : nonAlimentarCategories) {
                trimmedText = removeBetweenCategories(trimmedText, nonAlimentarCategory, alimentarCategories);
            }

            // Remove alimentar categories names
            for (String alimentarCategory : alimentarCategories) {
                trimmedText = trimmedText.replace(alimentarCategory, "");
            }

            return trimmedText;
        }

        return text;
    }

    private static String removeBetweenCategories(String text, String nonAlimentarCategory, String[] alimentarCategories) {
        int startIndex = text.indexOf(nonAlimentarCategory);
        if (startIndex >= 0) {
            int endIndex = findNextAlimentarCategory(text, startIndex, alimentarCategories);
            if (endIndex >= 0) {
                text = text.substring(0, startIndex) + text.substring(endIndex);
            }
        }
        return text;
    }

    private static int findNextAlimentarCategory(String text, int startIndex, String[] alimentarCategories) {
        int minIndex = -1;
        for (String category : alimentarCategories) {
            int index = text.indexOf(category, startIndex);
            if (index >= 0 && (minIndex == -1 || index < minIndex)) {
                minIndex = index;
            }
        }
        return minIndex >= 0 ? minIndex : text.length();
    }
}

