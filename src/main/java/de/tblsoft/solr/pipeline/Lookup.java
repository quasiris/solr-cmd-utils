package de.tblsoft.solr.pipeline;

import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.filter.LookupFilter;

import java.util.Map;

/**
 * Created by tblsoft on 09.08.17.
 */
public class Lookup {

    public Lookup(String pipeline) {
        this.pipeline = pipeline;
    }

    private Map<String, Document> lookupMap;
    private String pipeline;

    void init() {
        PipelineExecuter pipelineExecuter = new PipelineExecuter(pipeline);
        pipelineExecuter.execute();
        LookupFilter lookupFilter = (LookupFilter) pipelineExecuter.getFilterById("lookup");
        lookupMap = lookupFilter.getLookup();
    }

    public Document get(String key) {
        if(lookupMap == null) {
            init();
        }
        return lookupMap.get(key);
    }
}
