package team4.howest.be.androidapp.util;

import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import org.xml.sax.XMLReader;

/**
 * Created by anthony on 15.17.11.
 */
public class TextDisplayer implements Html.TagHandler {
    public static final String TAG = "TextDisplayer";
    private static Html htmlProcessor;

    public TextDisplayer(){
    }

    public Spanned getSpannedText(String htmlString){

        return htmlProcessor.fromHtml(htmlString);
    }

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {

    }
}