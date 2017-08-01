/**
 * IDataServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.gztydic.gateway.gather.webservice.client.data;

public class IDataServiceLocator extends org.apache.axis.client.Service implements com.gztydic.gateway.gather.webservice.client.data.IDataService {

    public IDataServiceLocator() {
    }


    public IDataServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public IDataServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for IDataServiceHttpPort
    private java.lang.String IDataServiceHttpPort_address = "http://localhost:8080/mamp/services/IDataService";

    public java.lang.String getIDataServiceHttpPortAddress() {
        return IDataServiceHttpPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String IDataServiceHttpPortWSDDServiceName = "IDataServiceHttpPort";

    public java.lang.String getIDataServiceHttpPortWSDDServiceName() {
        return IDataServiceHttpPortWSDDServiceName;
    }

    public void setIDataServiceHttpPortWSDDServiceName(java.lang.String name) {
        IDataServiceHttpPortWSDDServiceName = name;
    }

    public com.gztydic.gateway.gather.webservice.client.data.IDataServicePortType getIDataServiceHttpPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(IDataServiceHttpPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getIDataServiceHttpPort(endpoint);
    }

    public com.gztydic.gateway.gather.webservice.client.data.IDataServicePortType getIDataServiceHttpPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.gztydic.gateway.gather.webservice.client.data.IDataServiceHttpBindingStub _stub = new com.gztydic.gateway.gather.webservice.client.data.IDataServiceHttpBindingStub(portAddress, this);
            _stub.setPortName(getIDataServiceHttpPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setIDataServiceHttpPortEndpointAddress(java.lang.String address) {
        IDataServiceHttpPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.gztydic.gateway.gather.webservice.client.data.IDataServicePortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.gztydic.gateway.gather.webservice.client.data.IDataServiceHttpBindingStub _stub = new com.gztydic.gateway.gather.webservice.client.data.IDataServiceHttpBindingStub(new java.net.URL(IDataServiceHttpPort_address), this);
                _stub.setPortName(getIDataServiceHttpPortWSDDServiceName());
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
        if ("IDataServiceHttpPort".equals(inputPortName)) {
            return getIDataServiceHttpPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://execute.service.mamp.bonc.com", "IDataService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://execute.service.mamp.bonc.com", "IDataServiceHttpPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("IDataServiceHttpPort".equals(portName)) {
            setIDataServiceHttpPortEndpointAddress(address);
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
