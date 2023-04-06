package com.example.finalyearproject.utils;

import com.google.mlkit.vision.text.Text;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* helper class for processing text recognition result on receipt */
public class TextReceipt {
    /* sameLineBound = heightOfLine * SAME_LINE_BOUND_RATIO
     * if diff_y of two Text.Line < sameLineBound, treat them as in same TextReceiptLine */
    public static final double SAME_LINE_BOUND_RATIO = 0.5;
    /* sameBlockBound = heightOfLine * SAME_BLOCK_BOUND_RATIO
     * if diif_y of two TextReceiptLine > SAME_BLOCK_BOUND,treat as new block, separate by empty line */
    public static final double SAME_BLOCK_BOUND_RATIO = 0.5;
    private final Text visionText; // reference to TextRecognition result
    private String text; // Processed receipt's text to match physical receipt's text (line order, position)
    private List<TextReceiptLine> receiptLines;

    public TextReceipt(Text visionText) {
        this.visionText = visionText;
        this.text = "";
        this.receiptLines = new ArrayList<>();
        getReceiptLines();
        initText();
    }

    /* return totalAmount: first find ReceiptLine of total, then combine all digits and dots in sequence */
    public double extractTotalAmount() {
        for (TextReceiptLine receiptLine : receiptLines) {
            String text = receiptLine.getText().toLowerCase();
            if (text.contains("total")) {
                // load all digits and dot in sequence as result;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < text.length(); i++) {
                    char ch = text.charAt(i);
                    if (Character.isDigit(ch))
                        sb.append(ch);
                    else if (ch == '.' || ch == '-' || ch == '_') {
                        // digit appended before; add dot in case of missing dot;
                        sb.append('.');
                    }
                }
                if (isValidNumber(sb.toString()))
                    return Double.parseDouble(sb.toString());
            }
        }
        // return 0.01 as default if total amount not properly extracted
        return 0.01;
    }

    /* Extract tile of this receipt, which is the first ReceiptLine's text */
    public String extractTitle() {
        if (receiptLines.size() == 0)
            return "New Receipt"; // No text in image
        return receiptLines.get(0).getText();
    }

    /* Extract date of receipt using regex; return today if not found */
    public String extractDate() {
        String date;
        // try match pattern: dd/mm/yy;
        Pattern pattern = Pattern.compile("\\d{2}/\\d{2}/\\d{2}");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            date = matcher.group();
            date = date.substring(0, 6) + "20" + date.substring(6); // convert dd/mm/yy to dd/mm/yyyy;
            return date;
        }
        // try match dd/mm/yyyy
        pattern = Pattern.compile("\\d{2}/\\d{2}/\\d{4}");
        matcher = pattern.matcher(text);
        if (matcher.find()) {
            date = matcher.group();
            return date;
        }
        // try match yyyy-MM-dd
        pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
        matcher = pattern.matcher(text);
        if (matcher.find()) {
            date = matcher.group();
            date = date.substring(8) + "/" + date.substring(5, 7) + "/" + date.substring(0, 4);// convert to dd/mm/yyyy;
            return date;
        }
        // not found: return today;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        date = String.format(Locale.US, "%02d/%02d/%04d", dayOfMonth, month, year); // dd/mm/yyyy
        return date;
    }

    /* return the text in natural order */
    public String getText() {
        return text;
    }

    /* return the text grouped by blocks */
    public String getBlockText() {
        StringBuilder sb = new StringBuilder();
        List<Text.TextBlock> textBlocks = visionText.getTextBlocks();
        for (Text.TextBlock textBlock : textBlocks) {
            sb.append(textBlock.getText());
            sb.append("\n\n");
        }
        return sb.toString();
    }

    /* check if s contains a valid double by checking number of dots */
    private boolean isValidNumber(String s) {
        if (s.length() == 0) return false;
        int nDot = 0;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '.')
                nDot++;
        }
        return nDot <= 1;
    }

    /* get texts from receiptLines and try separate blocks */
    private void initText() {
        if (receiptLines.size() == 0) return;

        int lineHeight = getAvgReceiptLineHeight(receiptLines);
        StringBuilder sb = new StringBuilder();
        TextReceiptLine receiptLine = receiptLines.get(0);
        sb.append(receiptLine.getText());
        for (int i = 1; i < receiptLines.size(); i++) {
            receiptLine = receiptLines.get(i);
            int y1 = receiptLines.get(i - 1).getBoundingBox().bottom;
            int y2 = receiptLine.getBoundingBox().top;
            int diff = Math.abs(y2 - y1);
            // if diff_y of two receiptLines < newSectionBound => consider as same ReceiptLine;
            if (diff > lineHeight * SAME_BLOCK_BOUND_RATIO)
                sb.append("\n");
            sb.append("\n");
            sb.append(receiptLine.getText());
        }
        this.text = sb.toString();
    }

    /* convert List<Text.Line> into List<ReceiptLine> */
    private void getReceiptLines() {
        List<Text.Line> allLines = getAllTextLines();
        if (allLines.size() == 0) return;

        // sort allLines by centerY
        allLines.sort(Comparator.comparingInt(line -> line.getBoundingBox().centerY()));
        int lineHeight = getAvgLineHeight(allLines);
        Text.Line line = allLines.get(0);
        TextReceiptLine currReceiptLine = new TextReceiptLine(line);
        receiptLines.add(currReceiptLine);
        for (int i = 1; i < allLines.size(); i++) {
            line = allLines.get(i);
            int y1 = currReceiptLine.getBoundingBox().centerY();
            int y2 = line.getBoundingBox().centerY();
            int diff = Math.abs(y1 - y2);
            // no horizontal overlap && y_iff < sameLineBound => same ReceiptLine
            if (!currReceiptLine.horizontalOverlapWith(line) && diff < lineHeight * SAME_LINE_BOUND_RATIO) {
                currReceiptLine.add(line);
            }
            // consider this as a new ReceiptLine;
            else {
                currReceiptLine = new TextReceiptLine(line);
                receiptLines.add(currReceiptLine);
            }
        }
    }

    private int getAvgReceiptLineHeight(List<TextReceiptLine> lines) {
        int total = 0;
        for (TextReceiptLine line : lines) {
            total += line.getBoundingBox().height();
        }
        return total / lines.size();
    }

    /* allLines guaranteed not empty */
    private int getAvgLineHeight(List<Text.Line> allLines) {
        int total = 0;
        for (Text.Line line : allLines) {
            total += line.getBoundingBox().height();
        }
        return total / allLines.size();
    }

    /* get all Text.Line from visionText */
    private List<Text.Line> getAllTextLines() {
        int n = getNumberOfTextLines();
        List<Text.Line> allLines = new ArrayList<>(n); //avoid multiple ArrayList auto size growth
        for (Text.TextBlock textBlock : visionText.getTextBlocks()) {
            allLines.addAll(textBlock.getLines());
        }
        return allLines;
    }

    /* get total number of Text.Line from visionText */
    private int getNumberOfTextLines() {
        int nLines = 0;
        for (Text.TextBlock textBlock : visionText.getTextBlocks()) {
            nLines += textBlock.getLines().size();
        }
        return nLines;
    }


}
