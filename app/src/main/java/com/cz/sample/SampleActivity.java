package com.cz.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;

import com.cz.sample.taghandler.HtmlSpanTagHandler;

/**
 * Created by cz on 10/14/16.
 */
public class SampleActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        TextView textView1= (TextView) findViewById(R.id.text1);
        TextView textView2= (TextView) findViewById(R.id.text2);
        TextView textView3= (TextView) findViewById(R.id.text3);
        TextView textView4= (TextView) findViewById(R.id.text4);
        TextView textView5= (TextView) findViewById(R.id.text5);


        textView1.setText(Html.fromHtml(getString(R.string.html_sample_value1),null,new HtmlSpanTagHandler(getBaseContext())));
        textView2.setText(Html.fromHtml(getString(R.string.html_sample_value2),null,new HtmlSpanTagHandler(getBaseContext())));
        textView3.setText(Html.fromHtml(getString(R.string.html_sample_value3),null,new HtmlSpanTagHandler(getBaseContext())));
        textView4.setText(Html.fromHtml(getString(R.string.html_sample_value4),null,new HtmlSpanTagHandler(getBaseContext())));
        textView5.setText(Html.fromHtml(getString(R.string.html_sample_value5),null,new HtmlSpanTagHandler(getBaseContext())));
    }
}
