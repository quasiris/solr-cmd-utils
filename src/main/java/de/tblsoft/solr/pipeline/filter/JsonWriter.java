package de.tblsoft.solr.pipeline.filter;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.tblsoft.solr.http.HTTPHelper;
import de.tblsoft.solr.pipeline.AbstractFilter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JsonWriter extends AbstractFilter {

    private Map<String, Object> document = new HashMap<String, Object>();
    private Gson gson;


    private String outputField;
    private String output;

    private String type;

    private String location;




    @Override
    public void init() {
        type = getProperty("type", "file");
        Boolean pretty = getPropertyAsBoolean("pretty", false);
        location = getProperty("location", null);
        verify(location, "For the JsonWriter a location must be defined.");


        GsonBuilder builder = new GsonBuilder();
        if(pretty) {
            builder = builder.setPrettyPrinting();
        }
        gson = builder.create();


        super.init();
    }

    @Override
    public void field(String name, String value) {


        Object docValue = document.get(name);
        if(docValue != null) {
            List<String> values;
            if(docValue instanceof String) {
                values = new ArrayList<String>();
                values.add((String) docValue);

            } else {
                values = (List<String>) docValue;
            }
            values.add(value);
            document.put(name,values);

        } else {
            document.put(name,value);
        }


        super.field(name,value);

    }

    @Override
    public void endDocument() {

        if(!document.isEmpty()) {

            String json = gson.toJson(document);



            if("elastic".equals(type)) {
                HTTPHelper.post(location, json);
            } else if ("file".equals(type)) {
                try {
                    FileUtils.writeStringToFile(new File("foo.txt"), json, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if ("stdout".equals(type)) {
                System.out.println(json);
            }



        }
        document = new HashMap<String, Object>();
        super.endDocument();
    }

    @Override
    public void end() {
        super.end();
    }

}
