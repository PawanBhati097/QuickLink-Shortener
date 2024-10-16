import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

class URLMapping {
    private String originalURL;
    private String shortURL;

    public URLMapping(String originalURL, String shortURL) {
        this.originalURL = originalURL;
        this.shortURL = shortURL;
    }

    public String getOriginalURL() {
        return originalURL;
    }

    public String getShortURL() {
        return shortURL;
    }
}

class FileStorage {
    private static final String FILE_NAME = "url_mappings.txt";

    public void saveMapping(URLMapping mapping) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(mapping.getOriginalURL() + "," + mapping.getShortURL());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving URL mapping: " + e.getMessage());
        }
    }

    // Specify java.util.List to avoid ambiguity
    public java.util.List<URLMapping> loadMappings() {
        java.util.List<URLMapping> mappings = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    mappings.add(new URLMapping(parts[0], parts[1]));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading URL mappings: " + e.getMessage());
        }
        return mappings;
    }
}

public class QuickLinkShortener {
    private static final String BASE_URL = "http://short.ly/";
    private Map<String, String> urlMap;
    private FileStorage fileStorage;
    private JFrame frame;
    private JTextField longURLField;
    private JTextField shortURLField;
    private JTextArea outputArea;

    public QuickLinkShortener() {
        urlMap = new HashMap<>();
        fileStorage = new FileStorage();
        loadExistingMappings();
        createGUI();
    }

    private void loadExistingMappings() {
        java.util.List<URLMapping> mappings = fileStorage.loadMappings(); // Use java.util.List here
        for (URLMapping mapping : mappings) {
            urlMap.put(mapping.getShortURL(), mapping.getOriginalURL());
        }
    }

    public String shortenURL(String originalURL) {
        if (urlMap.containsValue(originalURL)) {
            return getShortURL(originalURL);
        }

        String shortURL = generateShortURL();
        urlMap.put(shortURL, originalURL);
        fileStorage.saveMapping(new URLMapping(originalURL, shortURL));
        return shortURL;
    }

    private String generateShortURL() {
        return BASE_URL + UUID.randomUUID().toString().substring(0, 8);
    }

    public String retrieveOriginalURL(String shortURL) {
        return urlMap.get(shortURL);
    }

    private String getShortURL(String originalURL) {
        for (Map.Entry<String, String> entry : urlMap.entrySet()) {
            if (entry.getValue().equals(originalURL)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void createGUI() {
        frame = new JFrame("QuickLink Shortener");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Create input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 2));

        inputPanel.add(new JLabel("Long URL:"));
        longURLField = new JTextField();
        inputPanel.add(longURLField);

        inputPanel.add(new JLabel("Short URL:"));
        shortURLField = new JTextField();
        inputPanel.add(shortURLField);

        frame.add(inputPanel, BorderLayout.NORTH);

        // Create button panel
        JPanel buttonPanel = new JPanel();
        JButton shortenButton = new JButton("Shorten URL");
        shortenButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                shortenURL();
            }
        });
        buttonPanel.add(shortenButton);

        JButton retrieveButton = new JButton("Retrieve Original URL");
        retrieveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                retrieveOriginalURL();
            }
        });
        buttonPanel.add(retrieveButton);

        frame.add(buttonPanel, BorderLayout.CENTER);

        // Create output area
        outputArea = new JTextArea(10, 30);
        outputArea.setEditable(false); // Make the output area non-editable
        frame.add(new JScrollPane(outputArea), BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }

    private void shortenURL() {
        String longURL = longURLField.getText();
        String shortURL = shortenURL(longURL);
        outputArea.append("Shortened URL: " + shortURL + "\n");
    }

    private void retrieveOriginalURL() {
        String shortURL = shortURLField.getText();
        String originalURL = retrieveOriginalURL(shortURL);
        if (originalURL != null) {
            outputArea.append("Original URL: " + originalURL + "\n");
        } else {
            outputArea.append("No mapping found for the given short URL.\n");
        }
    }

    public static void main(String[] args) {
        new QuickLinkShortener();
    }
}
