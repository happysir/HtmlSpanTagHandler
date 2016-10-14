package com.cz.sample.taghandler;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.TypedValue;

import org.xml.sax.XMLReader;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cz on 10/12/16.
 */
public class HtmlSpanTagHandler implements Html.TagHandler {
    private static final String TAG_STYLE="style";
    private static final String TAG_FONT_SIZE="font-size";
    private static final String TAG_FONT_WEIGHT="font-weight";
    private static final String TAG_BACKGROUND="background";
    private static final String TAG_TEXT_DECORATION="text-decoration";
    private static final String TAG_COLOR="color";
    private static final HashMap<String,Integer> TYPEFACE_ITEMS=new HashMap<>();
    private HashMap<String,String> styleItems;
    private Context context;
    static {
        TYPEFACE_ITEMS.put("normal", Typeface.NORMAL);
        TYPEFACE_ITEMS.put("bold", Typeface.BOLD);
        TYPEFACE_ITEMS.put("italic", Typeface.ITALIC);
        TYPEFACE_ITEMS.put("bold_italic", Typeface.BOLD_ITALIC);
    }

    public HtmlSpanTagHandler(Context context) {
        this.styleItems=new HashMap<>();
        this.context=context;
    }

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        if("span".equals(tag)){
            int len = output.length();
            if(opening){
                try {
                    Field elementField = xmlReader.getClass().getDeclaredField("theNewElement");
                    elementField.setAccessible(true);
                    Object element = elementField.get(xmlReader);
                    Field attsField = element.getClass().getDeclaredField("theAtts");
                    attsField.setAccessible(true);
                    Object elementAttr = attsField.get(element);
                    Field dataField = elementAttr.getClass().getDeclaredField("data");
                    dataField.setAccessible(true);
                    String[] data = (String[])dataField.get(elementAttr);
                    Field lengthField = elementAttr.getClass().getDeclaredField("length");
                    lengthField.setAccessible(true);
                    int length = (int)lengthField.get(elementAttr);
                    if(0<length){
                        for(int i=0;i<length;i++){
                            styleItems.put(data[i * 5 + 2],data[i*5+4]);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                output.setSpan(new StrikethroughSpan(), len, len, Spannable.SPAN_MARK_MARK);
            } else if(!styleItems.isEmpty()){
                for(Map.Entry<String,String> entry:styleItems.entrySet()){
                    String tagName = entry.getKey();
                    if(TAG_STYLE.equals(tagName)) {
                        Pattern pattern = Pattern.compile("(([\\w-]+):([\\p{ASCII}&&[^;]]+);?)");
                        Matcher matcher = pattern.matcher(entry.getValue());
                        Object obj = getLastIndex(output, StrikethroughSpan.class);
                        int where = output.getSpanStart(obj);
                        output.removeSpan(obj);
                        Log.e("test","start:"+where+" end:"+len);
                        if (where != len) {
                            while (matcher.find()) {
                                String key = matcher.group(2);
                                String value = matcher.group(3);
                                if (TAG_FONT_SIZE.equals(key)) {
                                    int intValue = getIntValue(value);
                                    if(0<intValue){
                                        output.setSpan(new AbsoluteSizeSpan(applyDimension(intValue)), where, where+len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    }
                                } else if (TAG_COLOR.equals(key)) {
                                    try{
                                        int color = Color.parseColor(value);
                                        output.setSpan(new ForegroundColorSpan(color), where, where+len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    } catch (Exception e){
                                        e.printStackTrace();
                                    }
                                } else if(TAG_FONT_WEIGHT.equals(key)){
                                    Integer typeFaceStyle = TYPEFACE_ITEMS.get(value);
                                    if(null!=typeFaceStyle){
                                        output.setSpan(new StyleSpan(typeFaceStyle), where, where+len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    }
                                } else if(TAG_BACKGROUND.equals(key)){
                                    try{
                                        int color = Color.parseColor(value);
                                        output.setSpan(new BackgroundColorSpan(color), where, where+len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    } catch (Exception e){
                                        e.printStackTrace();
                                    }
                                } else if(TAG_TEXT_DECORATION.equals(key)){
                                    if("line-through".equals(value)){
                                        output.setSpan(new StrikethroughSpan(), where, where+len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    } else if("underline".equals(value)){
                                        output.setSpan(new UnderlineSpan(), where, where+len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    }
                                }
                            }
                        }
                    } else {
                        //other tag
                    }
                }
                styleItems.clear();
            }
        }
    }

    private int getIntValue(String value) {
        int intValue=0;
        if(!TextUtils.isEmpty(value)){
            try{
                intValue= Integer.valueOf(value.replaceAll("[^\\d]+", ""));
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return intValue;
    }


    private Object getLastIndex(Editable text, Class kind) {
        Object result=null;
        Object[] objArray = text.getSpans(0, text.length(), kind);
        for(int i = objArray.length;i>0;i--) {
            if(text.getSpanFlags(objArray[i-1]) == Spannable.SPAN_MARK_MARK) {
                result=objArray[i-1];
                break;
            }
        }
        return result;
    }

    private int applyDimension(float value){
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,value,context.getResources().getDisplayMetrics());
    }
}
