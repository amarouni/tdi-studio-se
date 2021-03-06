/**
 * WSDeleteItem.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.talend.mdm.webservice;

public class WSDeleteItem  implements java.io.Serializable {
    private java.lang.Boolean invokeBeforeDeleting;

    private java.lang.Boolean override;

    private java.lang.String source;

    private java.lang.Boolean withReport;

    private org.talend.mdm.webservice.WSItemPK wsItemPK;

    public WSDeleteItem() {
    }

    public WSDeleteItem(
           java.lang.Boolean invokeBeforeDeleting,
           java.lang.Boolean override,
           java.lang.String source,
           java.lang.Boolean withReport,
           org.talend.mdm.webservice.WSItemPK wsItemPK) {
           this.invokeBeforeDeleting = invokeBeforeDeleting;
           this.override = override;
           this.source = source;
           this.withReport = withReport;
           this.wsItemPK = wsItemPK;
    }


    /**
     * Gets the invokeBeforeDeleting value for this WSDeleteItem.
     * 
     * @return invokeBeforeDeleting
     */
    public java.lang.Boolean getInvokeBeforeDeleting() {
        return invokeBeforeDeleting;
    }


    /**
     * Sets the invokeBeforeDeleting value for this WSDeleteItem.
     * 
     * @param invokeBeforeDeleting
     */
    public void setInvokeBeforeDeleting(java.lang.Boolean invokeBeforeDeleting) {
        this.invokeBeforeDeleting = invokeBeforeDeleting;
    }


    /**
     * Gets the override value for this WSDeleteItem.
     * 
     * @return override
     */
    public java.lang.Boolean getOverride() {
        return override;
    }


    /**
     * Sets the override value for this WSDeleteItem.
     * 
     * @param override
     */
    public void setOverride(java.lang.Boolean override) {
        this.override = override;
    }


    /**
     * Gets the source value for this WSDeleteItem.
     * 
     * @return source
     */
    public java.lang.String getSource() {
        return source;
    }


    /**
     * Sets the source value for this WSDeleteItem.
     * 
     * @param source
     */
    public void setSource(java.lang.String source) {
        this.source = source;
    }


    /**
     * Gets the withReport value for this WSDeleteItem.
     * 
     * @return withReport
     */
    public java.lang.Boolean getWithReport() {
        return withReport;
    }


    /**
     * Sets the withReport value for this WSDeleteItem.
     * 
     * @param withReport
     */
    public void setWithReport(java.lang.Boolean withReport) {
        this.withReport = withReport;
    }


    /**
     * Gets the wsItemPK value for this WSDeleteItem.
     * 
     * @return wsItemPK
     */
    public org.talend.mdm.webservice.WSItemPK getWsItemPK() {
        return wsItemPK;
    }


    /**
     * Sets the wsItemPK value for this WSDeleteItem.
     * 
     * @param wsItemPK
     */
    public void setWsItemPK(org.talend.mdm.webservice.WSItemPK wsItemPK) {
        this.wsItemPK = wsItemPK;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof WSDeleteItem)) return false;
        WSDeleteItem other = (WSDeleteItem) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.invokeBeforeDeleting==null && other.getInvokeBeforeDeleting()==null) || 
             (this.invokeBeforeDeleting!=null &&
              this.invokeBeforeDeleting.equals(other.getInvokeBeforeDeleting()))) &&
            ((this.override==null && other.getOverride()==null) || 
             (this.override!=null &&
              this.override.equals(other.getOverride()))) &&
            ((this.source==null && other.getSource()==null) || 
             (this.source!=null &&
              this.source.equals(other.getSource()))) &&
            ((this.withReport==null && other.getWithReport()==null) || 
             (this.withReport!=null &&
              this.withReport.equals(other.getWithReport()))) &&
            ((this.wsItemPK==null && other.getWsItemPK()==null) || 
             (this.wsItemPK!=null &&
              this.wsItemPK.equals(other.getWsItemPK())));
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
        if (getInvokeBeforeDeleting() != null) {
            _hashCode += getInvokeBeforeDeleting().hashCode();
        }
        if (getOverride() != null) {
            _hashCode += getOverride().hashCode();
        }
        if (getSource() != null) {
            _hashCode += getSource().hashCode();
        }
        if (getWithReport() != null) {
            _hashCode += getWithReport().hashCode();
        }
        if (getWsItemPK() != null) {
            _hashCode += getWsItemPK().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(WSDeleteItem.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.talend.com/mdm", "WSDeleteItem"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("invokeBeforeDeleting");
        elemField.setXmlName(new javax.xml.namespace.QName("", "invokeBeforeDeleting"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("override");
        elemField.setXmlName(new javax.xml.namespace.QName("", "override"));
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
        elemField.setFieldName("withReport");
        elemField.setXmlName(new javax.xml.namespace.QName("", "withReport"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("wsItemPK");
        elemField.setXmlName(new javax.xml.namespace.QName("", "wsItemPK"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.talend.com/mdm", "WSItemPK"));
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
