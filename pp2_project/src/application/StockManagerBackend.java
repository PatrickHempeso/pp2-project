package application;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StockManagerBackend {

    private List<StockItem> stockItems;

    public StockManagerBackend() {
        stockItems = new ArrayList<>();
    }

    public List<StockItem> getStockItems() {
        return stockItems;
    }

    public void loadStockItemsFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            // Skip the first line which contains headers
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) { // Updated to expect 5 elements
                    stockItems.add(new StockItem(parts[0], Double.parseDouble(parts[1]), Integer.parseInt(parts[2]), parts[3], parts[4]));
                } else {
                    System.err.println("Invalid input line: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void saveStockItemsToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("Name,Price,Quantity,Category,Description,ImagePath\n"); // Updated to include ImagePath
            for (StockItem item : stockItems) {
                writer.write(item.getName() + "," + item.getPrice() + "," + item.getQuantity() + "," + item.getCategory() + "," + item.getDescription() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addStockItem(String name, double price, int quantity, String category, String description) {
        stockItems.add(new StockItem(name, price, quantity, category, description));
    }

    public void removeStockItem(int index) {
        stockItems.remove(index);
    }

    public boolean itemExists(String name) {
        for (StockItem item : stockItems) {
            if (item.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public void updateStockItem(int index, String name, double price, int quantity, String category, String description) {
        StockItem item = stockItems.get(index);
        item.setName(name);
        item.setPrice(price);
        item.setQuantity(quantity);
        item.setCategory(category);
        item.setDescription(description);
    }

    public void addImage(String name, File imageFile) {
        for (StockItem item : stockItems) {
            if (item.getName().equalsIgnoreCase(name)) {
                item.setImagePath(imageFile.getAbsolutePath());
                break;
            }
        }
    }

    public static class StockItem {
        private String name;
        private double price;
        private int quantity;
        private String category;
        private String description;
        private String imagePath;

        public StockItem(String name, double price, int quantity, String category, String description) {
            this.name = name;
            this.price = price;
            this.quantity = quantity;
            this.category = category;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getImagePath() {
            return imagePath;
        }

        public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
        }
    }
}
