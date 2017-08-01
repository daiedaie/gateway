/**
 * IDataServicePortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.gztydic.gateway.gather.webservice.client.data;

public interface IDataServicePortType extends java.rmi.Remote {
    public java.lang.String getMetaDataById(com.gztydic.gateway.gather.webservice.client.data.ServiceRequestParam in0) throws java.rmi.RemoteException;
    public java.lang.String getModelInfobyServiceId(com.gztydic.gateway.gather.webservice.client.data.ServiceRequestParam in0) throws java.rmi.RemoteException;
    public java.lang.String getDataByServiceId(com.gztydic.gateway.gather.webservice.client.data.ServiceRequestParam in0) throws java.rmi.RemoteException;
}
