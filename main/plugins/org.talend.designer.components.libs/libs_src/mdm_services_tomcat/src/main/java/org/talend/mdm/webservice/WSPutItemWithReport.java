/**
 * WSPutItemWithReport.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.talend.mdm.webservice;

public class WSPutItemWithReport  implements java.io.Serializable {
    private java.lang.Boolean invokeBeforeSaving;

    private java.lang.String source;

    private org.talend.mdm.webservice.WSPutItem wsPutItem;

    public WSPutItemWithReport() {
    }

    public WSPutItemWithReport(
           java.lang.Boolean invokeBeforeSaving,
           java.lang.String source,
           org.talend.mdm.webservice.WSPutItem wsPutItem) {
           this.invokeBeforeSaving = invokeBeforeSaving;
           this.source = source;
           this.wsPutItem = wsPutItem;
    }


    /**
     * Gets the invokeBeforeSaving value for this WSPutItemWithReport.
     * 
     * @return invokeBeforeSaving
     */
    public java.lang.Boolean getInvokeBeforeSaving() {
        return invokeBeforeSaving;
    }


    /**
     * Sets the invokeBeforeSaving value for this WSPutItemWithReport.
     * 
     * @param invokeBeforeSaving
     */
    public void setInvokeBeforeSaving(java.lang.Boolean invokeBeforeSaving) {
        this.invokeBeforeSaving = invokeBeforeSaving;
    }


    /**
     * Gets the source value for this WSPutItemWithReport.
     * 
     * @return source
     */
    public java.lang.String getSource() {
        return source;
    }


    /**
     * Sets the source value for this WSPutItemWithReport.
     * 
     * @param source
     */
    public void setSource(java.lang.String source) {
        this.source = source;
    }


    /**
     * Gets the wsPutItem value for this WSPutItemWithReport.
     * 
     * @return wsPutItem
     */
    public org.talend.mdm.webservice.WSPutItem getWsPutItem() {
        return wsPutItem;
    }


    /**
     * Sets the wsPutItem value for this WSPutItemWithReport.
     * 
     * @param wsPutItem
     */
    public void setWsPutItem(org.talend.mdm.webservice.WSPutItem wsPutItem) {
        this.wsPutItem = wsPutItem;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof WSPutItemWithReport)) return false;
        WSPutItemWithReport other = (WSPutItemWithReport) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.invokeBeforeSaving==null && other.getInvokeBeforeSaving()==null) || 
             (this.invokeBeforeSaving!=null &&
              this.invokeBeforeSaving.equals(other.getInvokeBeforeSaving()))) &&
            ((this.source==null && other.getSource()==null) || 
             (this.source!=null &&
              this.source.equals(other.getSource()))) &&
            ((this.wsPutItem==null && other.getWsPutItem()==null) || 
             (this.wsPutItem!=null &&
              this.wsPutItem.equals(other.getWsPutItem())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getInvokeBeforeSaving() != null) {
            _hashCode += getInvokeBeforeSaving().hashCode();
        }
        if (getSource() != null) {
            _hashCode += getSource().hashCode();
        }
        if (getWsPutItem() != null) {
            _hashCode += getWsPutItem().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(WSPutItemWithReport.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.talend.com/mdm", "WSPutItemWithReport"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("invokeBeforeSaving");
        elemField.setXmlName(new javax.xml.namespace.QName("", "invokeBeforeSaving"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("source");
        elemField.setXmlName(new javax.xml.namespace.QName("", "source"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("wsPutItem");
        elemField.setXmlName(new javax.xml.namespace.QName("", "wsPutItem"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.talend.com/mdm", "WSPutItem"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
