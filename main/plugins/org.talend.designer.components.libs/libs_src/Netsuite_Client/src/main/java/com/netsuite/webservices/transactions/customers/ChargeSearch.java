
package com.netsuite.webservices.transactions.customers;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import com.netsuite.webservices.platform.common.ChargeSearchBasic;
import com.netsuite.webservices.platform.common.CustomSearchJoin;
import com.netsuite.webservices.platform.common.EmployeeSearchBasic;
import com.netsuite.webservices.platform.common.JobSearchBasic;
import com.netsuite.webservices.platform.common.TimeBillSearchBasic;
import com.netsuite.webservices.platform.common.TransactionSearchBasic;
import com.netsuite.webservices.platform.core.SearchRecord;


/**
 * <p>Java class for ChargeSearch complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ChargeSearch"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{urn:core_2014_2.platform.webservices.netsuite.com}SearchRecord"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="basic" type="{urn:common_2014_2.platform.webservices.netsuite.com}ChargeSearchBasic" minOccurs="0"/&gt;
 *         &lt;element name="invoiceJoin" type="{urn:common_2014_2.platform.webservices.netsuite.com}TransactionSearchBasic" minOccurs="0"/&gt;
 *         &lt;element name="jobJoin" type="{urn:common_2014_2.platform.webservices.netsuite.com}JobSearchBasic" minOccurs="0"/&gt;
 *         &lt;element name="salesOrderJoin" type="{urn:common_2014_2.platform.webservices.netsuite.com}TransactionSearchBasic" minOccurs="0"/&gt;
 *         &lt;element name="timeJoin" type="{urn:common_2014_2.platform.webservices.netsuite.com}TimeBillSearchBasic" minOccurs="0"/&gt;
 *         &lt;element name="transactionJoin" type="{urn:common_2014_2.platform.webservices.netsuite.com}TransactionSearchBasic" minOccurs="0"/&gt;
 *         &lt;element name="userJoin" type="{urn:common_2014_2.platform.webservices.netsuite.com}EmployeeSearchBasic" minOccurs="0"/&gt;
 *         &lt;element name="customSearchJoin" type="{urn:common_2014_2.platform.webservices.netsuite.com}CustomSearchJoin" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChargeSearch", propOrder = {
    "basic",
    "invoiceJoin",
    "jobJoin",
    "salesOrderJoin",
    "timeJoin",
    "transactionJoin",
    "userJoin",
    "customSearchJoin"
})
public class ChargeSearch
    extends SearchRecord
{

    protected ChargeSearchBasic basic;
    protected TransactionSearchBasic invoiceJoin;
    protected JobSearchBasic jobJoin;
    protected TransactionSearchBasic salesOrderJoin;
    protected TimeBillSearchBasic timeJoin;
    protected TransactionSearchBasic transactionJoin;
    protected EmployeeSearchBasic userJoin;
    protected List<CustomSearchJoin> customSearchJoin;

    /**
     * Gets the value of the basic property.
     * 
     * @return
     *     possible object is
     *     {@link ChargeSearchBasic }
     *     
     */
    public ChargeSearchBasic getBasic() {
        return basic;
    }

    /**
     * Sets the value of the basic property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChargeSearchBasic }
     *     
     */
    public void setBasic(ChargeSearchBasic value) {
        this.basic = value;
    }

    /**
     * Gets the value of the invoiceJoin property.
     * 
     * @return
     *     possible object is
     *     {@link TransactionSearchBasic }
     *     
     */
    public TransactionSearchBasic getInvoiceJoin() {
        return invoiceJoin;
    }

    /**
     * Sets the value of the invoiceJoin property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionSearchBasic }
     *     
     */
    public void setInvoiceJoin(TransactionSearchBasic value) {
        this.invoiceJoin = value;
    }

    /**
     * Gets the value of the jobJoin property.
     * 
     * @return
     *     possible object is
     *     {@link JobSearchBasic }
     *     
     */
    public JobSearchBasic getJobJoin() {
        return jobJoin;
    }

    /**
     * Sets the value of the jobJoin property.
     * 
     * @param value
     *     allowed object is
     *     {@link JobSearchBasic }
     *     
     */
    public void setJobJoin(JobSearchBasic value) {
        this.jobJoin = value;
    }

    /**
     * Gets the value of the salesOrderJoin property.
     * 
     * @return
     *     possible object is
     *     {@link TransactionSearchBasic }
     *     
     */
    public TransactionSearchBasic getSalesOrderJoin() {
        return salesOrderJoin;
    }

    /**
     * Sets the value of the salesOrderJoin property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionSearchBasic }
     *     
     */
    public void setSalesOrderJoin(TransactionSearchBasic value) {
        this.salesOrderJoin = value;
    }

    /**
     * Gets the value of the timeJoin property.
     * 
     * @return
     *     possible object is
     *     {@link TimeBillSearchBasic }
     *     
     */
    public TimeBillSearchBasic getTimeJoin() {
        return timeJoin;
    }

    /**
     * Sets the value of the timeJoin property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeBillSearchBasic }
     *     
     */
    public void setTimeJoin(TimeBillSearchBasic value) {
        this.timeJoin = value;
    }

    /**
     * Gets the value of the transactionJoin property.
     * 
     * @return
     *     possible object is
     *     {@link TransactionSearchBasic }
     *     
     */
    public TransactionSearchBasic getTransactionJoin() {
        return transactionJoin;
    }

    /**
     * Sets the value of the transactionJoin property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionSearchBasic }
     *     
     */
    public void setTransactionJoin(TransactionSearchBasic value) {
        this.transactionJoin = value;
    }

    /**
     * Gets the value of the userJoin property.
     * 
     * @return
     *     possible object is
     *     {@link EmployeeSearchBasic }
     *     
     */
    public EmployeeSearchBasic getUserJoin() {
        return userJoin;
    }

    /**
     * Sets the value of the userJoin property.
     * 
     * @param value
     *     allowed object is
     *     {@link EmployeeSearchBasic }
     *     
     */
    public void setUserJoin(EmployeeSearchBasic value) {
        this.userJoin = value;
    }

    /**
     * Gets the value of the customSearchJoin property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the customSearchJoin property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCustomSearchJoin().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CustomSearchJoin }
     * 
     * 
     */
    public List<CustomSearchJoin> getCustomSearchJoin() {
        if (customSearchJoin == null) {
            customSearchJoin = new ArrayList<CustomSearchJoin>();
        }
        return this.customSearchJoin;
    }

}
