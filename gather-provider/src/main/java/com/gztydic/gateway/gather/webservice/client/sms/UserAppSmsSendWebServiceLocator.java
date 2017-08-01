/**
 * UserAppSmsSendWebServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.gztydic.gateway.gather.webservice.client.sms;

public class UserAppSmsSendWebServiceLocator extends org.apache.axis.client.Service implements com.gztydic.gateway.gather.webservice.client.sms.UserAppSmsSendWebService {

    public UserAppSmsSendWebServiceLocator() {
    }


    public UserAppSmsSendWebServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public UserAppSmsSendWebServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for UserAppSmsSendWebServiceHttpPort
    private java.lang.String UserAppSmsSendWebServiceHttpPort_address = "http://10.0.9.167:8080/UserAppSmsSendWebService/services/UserAppSmsSendWebService";

    public java.lang.String getUserAppSmsSendWebServiceHttpPortAddress() {
        return UserAppSmsSendWebServiceHttpPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String UserAppSmsSendWebServiceHttpPortWSDDServiceName = "UserAppSmsSendWebServiceHttpPort";

    public java.lang.String getUserAppSmsSendWebServiceHttpPortWSDDServiceName() {
        return UserAppSmsSendWebServiceHttpPortWSDDServiceName;
    }

    public void setUserAppSmsSendWebServiceHttpPortWSDDServiceName(java.lang.String name) {
        UserAppSmsSendWebServiceHttpPortWSDDServiceName = name;
    }

    public com.gztydic.gateway.gather.webservice.client.sms.UserAppSmsSendWebServicePortType getUserAppSmsSendWebServiceHttpPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(UserAppSmsSendWebServiceHttpPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getUserAppSmsSendWebServiceHttpPort(endpoint);
    }

    public com.gztydic.gateway.gather.webservice.client.sms.UserAppSmsSendWebServicePortType getUserAppSmsSendWebServiceHttpPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.gztydic.gateway.gather.webservice.client.sms.UserAppSmsSendWebServiceHttpBindingStub _stub = new com.gztydic.gateway.gather.webservice.client.sms.UserAppSmsSendWebServiceHttpBindingStub(portAddress, this);
            _stub.setPortName(getUserAppSmsSendWebServiceHttpPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setUserAppSmsSendWebServiceHttpPortEndpointAddress(java.lang.String address) {
        UserAppSmsSendWebServiceHttpPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.gztydic.gateway.gather.webservice.client.sms.UserAppSmsSendWebServicePortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.gztydic.gateway.gather.webservice.client.sms.UserAppSmsSendWebServiceHttpBindingStub _stub = new com.gztydic.gateway.gather.webservice.client.sms.UserAppSmsSendWebServiceHttpBindingStub(new java.net.URL(UserAppSmsSendWebServiceHttpPort_address), this);
                _stub.setPortName(getUserAppSmsSendWebServiceHttpPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("UserAppSmsSendWebServiceHttpPort".equals(inputPortName)) {
            return getUserAppSmsSendWebServiceHttpPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://service.com", "UserAppSmsSendWebService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://service.com", "UserAppSmsSendWebServiceHttpPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("UserAppSmsSendWebServiceHttpPort".equals(portName)) {
            setUserAppSmsSendWebServiceHttpPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
