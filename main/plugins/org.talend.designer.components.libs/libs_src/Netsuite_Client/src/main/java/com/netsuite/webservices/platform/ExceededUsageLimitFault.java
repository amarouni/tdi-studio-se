
package com.netsuite.webservices.platform;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.7.7
 * 2015-04-08T13:30:12.695+08:00
 * Generated source version: 2.7.7
 */

@WebFault(name = "exceededUsageLimitFault", targetNamespace = "urn:faults_2014_2.platform.webservices.netsuite.com")
public class ExceededUsageLimitFault extends Exception {
    
    private com.netsuite.webservices.platform.faults.ExceededUsageLimitFault exceededUsageLimitFault;

    public ExceededUsageLimitFault() {
        super();
    }
    
    public ExceededUsageLimitFault(String message) {
        super(message);
    }
    
    public ExceededUsageLimitFault(String message, Throwable cause) {
        super(message, cause);
    }

    public ExceededUsageLimitFault(String message, com.netsuite.webservices.platform.faults.ExceededUsageLimitFault exceededUsageLimitFault) {
        super(message);
        this.exceededUsageLimitFault = exceededUsageLimitFault;
    }

    public ExceededUsageLimitFault(String message, com.netsuite.webservices.platform.faults.ExceededUsageLimitFault exceededUsageLimitFault, Throwable cause) {
        super(message, cause);
        this.exceededUsageLimitFault = exceededUsageLimitFault;
    }

    public com.netsuite.webservices.platform.faults.ExceededUsageLimitFault getFaultInfo() {
        return this.exceededUsageLimitFault;
    }
}