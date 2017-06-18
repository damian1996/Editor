package sample.view;

import com.sun.javafx.scene.control.skin.CustomColorDialog;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;
import sample.controller.Controller;
import sample.model.*;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.*;
import javafx.util.Pair;

public class Card extends Tab {
    private final int zoom = 2;
    private Color colorBack;
    private Color colorFont;
    private String colorBackRGB;
    private String colorFontRGB;
    private String fileName = "";
    private final long id;
    private double sizeFont = 0;
    private File file;
    private boolean nowI = false;
    private CodeArea t;
    private Subscription sub;

    private static final String[] KEYWORDS = new String[] {
            "abstract", "assert", "boolean", "break", "byte",
            "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else",
            "enum", "extends", "final", "finally", "float",
            "for", "goto", "if", "implements", "import",
            "instanceof", "int", "interface", "long", "native",
            "new", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super",
            "switch", "synchronized", "this", "throw", "throws",
            "transient", "try", "void", "volatile", "while"
    };
    private static final String[] KEYWORDSCPP = new String[] {
            "alignas", "alignof", "and", "and_eq", "asm",
            "atomic_cancel", "atomic_commit", "atomic_noexcept ", "auto",
            "bitand", "bitor", "bool", "break", "case",
            "catch", "char", "char16_t", "char32_t", "class",
            "compl", "concept", "const", "constexpr", "const_cast",
            "continue", "decltype", "default", "delete", "do",
            "double", "dynamic_cast", "else", "enum", "explicit",
            "export", "extern", "false", "float", "for", "friend",
            "goto", "if", "import", "inline", "int", "long", "module",
            "mutable", "namespace", "new", "noexcept", "not", "not_eq",
            "nullptr", "operator", "or", "or_eq", "private", "protected",
            "public", "register", "reinterpret_cast", "requires", "return",
            "short", "signed", "sizeof", "static", "static_assert",
            "static_cast", "struct", "switch", "synchronized", "template",
            "this", "thread_local", "throw", "true", "try", "typedef",
            "typeid", "typename", "union", "unsigned", "using", "virtual",
            "void", "volatile", "wchar_t", "while", "xor"
    };


    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String KEYWORD_PATTERN_CPP = "\\b(" + String.join("|", KEYWORDSCPP) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );
    private static final Pattern PATTERN_CPP = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN_CPP + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );


    public Card(long id){
        super("Untitled File");
        fileName = "Untitled File";
        this.id = id;
        t = new CodeArea();
        t.setWrapText(true);
        t.setStyle("-fx-font-family: monospace");
        t.setParagraphGraphicFactory(LineNumberFactory.get(t));

        //sizeFont = t.getFont().getSize();
        sizeFont = 15;
        this.setContent(this.t);
        nowI = true;
    }

    public void subscribeAddJava(){
        sub = t.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
                .subscribe(change -> {
                    try{
                        t.setStyleSpans(0, computeHighlighting(t.getText()));
                    } catch(Exception e){}
                });
    }

    public void subscribeAddCpp(){
        sub = t.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
                .subscribe(change -> {
                    try{
                        t.setStyleSpans(0, computeHighlightingCpp(t.getText()));
                    } catch(Exception e){}
                });
    }

    public void subscribeRemove(){
        if(sub!=null)
            sub.unsubscribe();
    }

    public void copyInCard(){
        t.copy();
    }

    public void pasteInCard(){
        t.paste();
    }

    public void cutInCard(){
        t.cut();
    }

    public void selectAllinCard(){
        int from = 0;
        int to = t.getText().length();
        t.selectRange(from, to);
    }

    public void setFont(double font){
        sizeFont = font;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setExtendFileName(String fileName){
        setFileName(fileName);
        setText(fileName);
    }

    public void setFile(File file1){
        setFileName(file1.getName());
        setText(file1.getName());
        this.file = file1;
    }

    public void increaseFont(){
        sizeFont = sizeFont+zoom;
        if(colorFontRGB!=null)
            t.setStyle("-fx-text-inner-color: " + colorFontRGB + ";" + " -fx-font-size: " + sizeFont + " px;");
        else
            t.setStyle("" + "-fx-font-size: " + sizeFont + " px;");
    }

    public void decreaseFont(){
        sizeFont = sizeFont-zoom;
        if(colorFontRGB!=null)
            t.setStyle("-fx-text-inner-color: " + colorFontRGB + ";" + " -fx-font-size: " + sizeFont + " px;");
        else
            t.setStyle("" + "-fx-font-size: " + sizeFont + " px;");
    }

    public void setFontAgain(){
        t.setStyle("" + "-fx-font-size: " + sizeFont + " px;");
    }

    public void changeColors(Stage stage){
        final Stage dialog = new Stage();
        dialog.setTitle("Set colors background and font");
        dialog.initOwner(stage);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 10, 0, 10));
        final ColorPicker background = new ColorPicker();
        final ColorPicker font = new ColorPicker();

        background.setOnAction((ActionEvent t) -> {
            colorBack = background.getValue();
        });
        font.setOnAction((ActionEvent t) -> {
            colorFont = font.getValue();
        });
        grid.add(new Label("Background:"), 0, 0);
        grid.add(background, 0, 1);
        grid.add(new Label("Font:"), 1, 0);
        grid.add(font, 1, 1);
        Scene scene = new Scene(grid, 300, 80);
        dialog.setScene(scene);
        dialog.showAndWait();

       // Region region = ( Region ) t.lookup( ".content" );
        if(colorBack!=null){
            t.setStyle( "-fx-background-color: " + toRGB(colorBack) );
           // region.setStyle( "-fx-background-color: " + toRGB(colorBack) );
            colorBackRGB = toRGB(colorBack);
        }
        if(colorFont!=null){
            t.setStyle("-fx-text-inner-color: " + toRGB(colorFont) + ";");// + " -fx-font-size: " + sizeFont + " px;");
            colorFontRGB = toRGB(colorFont);
        }
        //setFontAgain();
    }

    public void changeColorsOtherCards(String back, String font){
        if(back!=null){
            colorBackRGB = back;
            //Region region = ( Region ) t.lookup( ".content" );
            if(font!=null)
                t.setStyle( "-fx-background-color: " + colorBackRGB + " -fx-text-inner-color: " + colorFontRGB + ";" + " -fx-font-size: " + sizeFont + " px;" );
            else
                t.setStyle( "-fx-background-color: " + colorBackRGB );
        }
        else if(font!=null){
            colorFontRGB = font;
            t.setStyle("-fx-text-inner-color: " + colorFontRGB + ";" + " -fx-font-size: " + sizeFont + " px;");
        }
    }

    public String toRGB(Color color){
        return String.format("#%02X%02X%02X",
                (int) (color.getRed()*255),
                (int) (color.getGreen()*255),
                (int) (color.getBlue()*255));
    }

    public void cardKeyMap(Stage stage){
        Label[] labels = new Label[32];
        URL url = getClass().getResource("KeyMap");
        File file = new File(url.getPath());
        GridPane gp = new GridPane();
        int i=0;
        String toStr = null;
        try{
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(file));
            String text;
            while((text = br.readLine()) != null){
                labels[i] = new Label();
                labels[i].setText(text);
                gp.add(labels[i],i%2, i /2);
                sb.append(text);
                sb.append('\n');
                i++;
            }
            toStr = sb.toString();
        } catch(Exception e){}
        for(int j=0; j<16; j++)
            gp.getRowConstraints().add(new RowConstraints(5, Control.USE_COMPUTED_SIZE, Double.POSITIVE_INFINITY, Priority.ALWAYS, VPos.CENTER, true));
        for(int j=0; j<2; j++)
            gp.getColumnConstraints().add(new ColumnConstraints(5, Control.USE_COMPUTED_SIZE, Double.POSITIVE_INFINITY, Priority.ALWAYS, HPos.CENTER, true));
        final Stage dialog = new Stage();
        dialog.setTitle("KeyMap Reference");
        dialog.initOwner(stage);
        Scene scene = new Scene(gp, 400, 500);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    public String getTextInArea(){
        return t.getText();
    }

    public String getColorFontRGB(){return colorFontRGB;}

    public String getColorBackRGB(){return colorBackRGB;}

    public File getFile() {
        return file;
    }

    public double getSizeFont() {
        return sizeFont;
    }

    public long getCardId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public CodeArea getArea(){return t;}

    public void setTextInArea(String str){
        t.replaceText(str);
    }

    public boolean isNowI() {
        return nowI;
    }

    public void setNowI(boolean nowI) {
        this.nowI = nowI;
    }

    public Subscription getSub(){

        return null;
    }

    public void setSub(Subscription sub){
        this.sub = sub;
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("PAREN") != null ? "paren" :
                                    matcher.group("BRACE") != null ? "brace" :
                                            matcher.group("BRACKET") != null ? "bracket" :
                                                    matcher.group("SEMICOLON") != null ? "semicolon" :
                                                            matcher.group("STRING") != null ? "string" :
                                                                    matcher.group("COMMENT") != null ? "comment" :
                                                                            "text"; /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
    private static StyleSpans<Collection<String>> computeHighlightingCpp(String text) {
        Matcher matcher = PATTERN_CPP.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("PAREN") != null ? "paren" :
                                    matcher.group("BRACE") != null ? "brace" :
                                            matcher.group("BRACKET") != null ? "bracket" :
                                                    matcher.group("SEMICOLON") != null ? "semicolon" :
                                                            matcher.group("STRING") != null ? "string" :
                                                                    matcher.group("COMMENT") != null ? "comment" :
                                                                            "text"; /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

}
