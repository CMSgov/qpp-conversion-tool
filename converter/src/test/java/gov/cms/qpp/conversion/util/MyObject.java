package gov.cms.qpp.conversion.util;

import java.util.Map;
import java.util.TreeMap;

public class MyObject {
    private Map<String, String> data;
    private String name;
    public MyObject() {
        data = new TreeMap<String, String>();
    }
    public void addData(String key, String value) {
        data.put(key, value);
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getData() {
        return data;
    }

    public String getName() {
        return name;
    }
}
