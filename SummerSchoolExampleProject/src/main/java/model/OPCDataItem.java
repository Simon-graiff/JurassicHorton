package model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by julian
 */
@XmlRootElement(name = "OpcDataItem")
public class OPCDataItem<T> {

    private T value;

    private String status;

    private String itemName;

    private long timestamp;

    /**
     * Default empty constructor needed for JAXB marshalling / unmarshalling
     */
    public OPCDataItem() {

    }    
    
    public OPCDataItem(T v, String name) {
        this.value = v;
        this.itemName = name;
        this.timestamp = System.currentTimeMillis();
        this.status = "GOOD";
    }


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
