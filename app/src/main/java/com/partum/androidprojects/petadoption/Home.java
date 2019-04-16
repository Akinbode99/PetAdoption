package com.partum.androidprojects.petadoption;


import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class Home extends AppCompatActivity {


    private int current_page;
    private boolean isinvisible;

    private ImageView imageView;
    private TextView form_title;
    private TextView page_number;
    private EditText date_view;
    private RadioGroup choice;

    private ArrayList<TextView> sectionlabels;
    private ArrayList<TextView> elementlabels;
    private ArrayList<EditText> data_fields;
    private ArrayList<Boolean> json_ismandatory;
    private ArrayList<Boolean> form_ismandatory;
    private DatePickerDialog.OnDateSetListener date;
    private JSONArray pages;

    private TextView label1;
    private TextView label2;
    private TextView label3;
    private TextView label4;
    private TextView label5;
    private TextView label6;

    private EditText view1;
    private EditText view2;
    private EditText view3;


    private Button next;
    private Calendar myCalendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        form_title = (TextView) findViewById(R.id.form_name);
        page_number = (TextView) findViewById(R.id.page_number);
        imageView = (ImageView) findViewById(R.id.embeddedphoto_1);
        next = (Button) findViewById(R.id.next);
        choice = (RadioGroup) findViewById(R.id.yesno_1);

        sectionlabels = new ArrayList<TextView>();
        elementlabels = new ArrayList<TextView>();
        data_fields = new ArrayList<EditText>();


         label1 = findViewById(R.id.label1);
         label2 = findViewById(R.id.label2);
         label3 = findViewById(R.id.label3);
         label4 = findViewById(R.id.label4);
         label5 = findViewById(R.id.label5);
         label6 = findViewById(R.id.label6);

        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view3= findViewById(R.id.view3);
        date_view = findViewById(R.id.view4);




        sectionlabels.add(label1);
        sectionlabels.add(label2);

        elementlabels.add(label3);
        elementlabels.add(label4);
        elementlabels.add(label5);
        elementlabels.add(label6);

        data_fields.add(view1);
        data_fields.add(view2);
        data_fields.add(view3);
        data_fields.add(date_view);

        current_page = 0;
        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };



        String json = null ;
        try{
            InputStream inputStream = getAssets().open("pet_adoption.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
        }
        catch (Exception e){
            e.printStackTrace();
        }

        if(json != null){
            try{
                JSONObject jsonObj = new JSONObject(json);
                form_title.setText(jsonObj.getString("name"));
                pages = jsonObj.getJSONArray("pages");
                showpage(current_page);
                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clearpage();
                        for(EditText fields: data_fields){
                            fields.setText("");
                        }
                        current_page++;
                        showpage(current_page);
                        if(current_page == pages.length()-1){
                            next.setText("submit");
                            next.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                  validateform();

                                }
                            });
                        }

                    }
                });

            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else{
            Toast.makeText(Home.this, "Please,select a correct file", Toast.LENGTH_SHORT).show();
        }

    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        date_view.setText(sdf.format(myCalendar.getTime()));
    }
    private void showpage(int pagenumber){

        try{
            page_number.setVisibility(View.VISIBLE);
            page_number.setText(pages.getJSONObject(pagenumber).getString("label"));
            JSONArray sections = pages.getJSONObject(pagenumber).getJSONArray("sections");
            for(int j = 0;  j<=sections.length()-1;j++){
                sectionlabels.get(j).setVisibility(View.VISIBLE);
                sectionlabels.get(j).setText(sections.getJSONObject(j).getString("label"));
                final JSONArray elements = sections.getJSONObject(j).getJSONArray("elements");
                for(int k = 0; k<=elements.length()-1;k++){
                    String element_type;
                    final String element_label;
                    element_type = elements.getJSONObject(k).getString("type");
                    if (element_type.equals("embeddedphoto")){
                        imageView.setVisibility(View.VISIBLE);
                        Picasso.get().load(elements.getJSONObject(k).getString("file")).into(imageView);
                    }

                    else if (element_type.equals("text")){
                        if (!isinvisible){
                            element_label = elements.getJSONObject(k).getString("label");
                            elementlabels.get(k).setText(element_label);
                            elementlabels.get(k).setVisibility(View.VISIBLE);
                            data_fields.get(k).setVisibility(View.VISIBLE);
                        }
                        else{
                            element_label = elements.getJSONObject(k).getString("label");
                            elementlabels.get(k).setText(element_label);
                        }

                    }

                    else if (element_type.equals("yesno")){
                        final int dependent_datafield_position = k +1;
                        element_label = elements.getJSONObject(k).getString("label");
                        elementlabels.get(k).setText(element_label);
                        elementlabels.get(k).setVisibility(View.VISIBLE);
                        choice.setVisibility(View.VISIBLE);
                        data_fields.get(dependent_datafield_position).setVisibility(View.INVISIBLE);
                        elementlabels.get(dependent_datafield_position).setVisibility(View.INVISIBLE);
                        isinvisible = true;
                        choice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                                 if (i == R.id.yes_1){
                                     elementlabels.get(dependent_datafield_position).setVisibility(View.VISIBLE);
                                     data_fields.get(dependent_datafield_position).setVisibility(View.VISIBLE);
                                     isinvisible = false;

                                 }
                                 else if (i == R.id.no_1){
                                     elementlabels.get(dependent_datafield_position).setVisibility(View.INVISIBLE);
                                     data_fields.get(dependent_datafield_position).setVisibility(View.INVISIBLE);
                                     isinvisible = false;

                                 }
                                 else{

                                 }
                            }
                        });


                    }
                    else if(element_type.equals("formattednumeric"))
                    {
                        data_fields.get(k).setVisibility(View.VISIBLE);
                        data_fields.get(k).setInputType(InputType.TYPE_CLASS_PHONE);
                        element_label = elements.getJSONObject(k).getString("label");
                        elementlabels.get(k).setText(element_label);
                        elementlabels.get(k).setVisibility(View.VISIBLE);
                    }
                    else if (element_type.equals("datetime")){
                        element_label = elements.getJSONObject(k).getString("label");
                        elementlabels.get(k).setText(element_label);
                        data_fields.get(k).setBackground(getDrawable(R.drawable.date_edittext_backgroud));
                        data_fields.get(k).setHint("Select Date....");
                        data_fields.get(k).setPadding(10,0,0,0);
                        data_fields.get(k).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new DatePickerDialog(Home.this, date, myCalendar
                                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                            }
                        });
                        data_fields.get(k).setVisibility(View.VISIBLE);
                        elementlabels.get(k).setVisibility(View.VISIBLE);
                    }
                }
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }


    }

    private void clearpage(){
        choice.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.INVISIBLE);
        page_number.setVisibility(View.INVISIBLE);
        label1.setVisibility(View.INVISIBLE);
        label2.setVisibility(View.INVISIBLE);
        label3.setVisibility(View.INVISIBLE);
        label4.setVisibility(View.INVISIBLE);
        label5.setVisibility(View.INVISIBLE);
        label6.setVisibility(View.INVISIBLE);
        view1.setVisibility(View.INVISIBLE);
        view2.setVisibility(View.INVISIBLE);
        view3.setVisibility(View.INVISIBLE);
        date_view.setVisibility(View.INVISIBLE);
    }
    private void validateform(){

        /*try {
            for (int i = 0; i <= pages.length() - 1; i++) {
                JSONArray sections = pages.getJSONObject(i).getJSONArray("sections");
                for(int j = 0; j <= sections.length()-1;j++){
                    JSONArray elements = sections.getJSONObject(j).getJSONArray("elements");
                    for(int k = 0; k<=elements.length()-1;k++){
                        boolean element_ismandatory;
                        element_ismandatory = elements.getJSONObject(k).getBoolean("isMandatory");
                        json_ismandatory.add(element_ismandatory);
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
         for (int s =0; s <= json_ismandatory.size();s++){
           if (json_ismandatory == form_ismandatory){
               return true;
           }
           else
               return false;
         }

         return true;*/
        }
    }










