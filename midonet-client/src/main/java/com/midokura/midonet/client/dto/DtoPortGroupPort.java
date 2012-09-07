/*
 * Copyright 2012 Midokura PTE LTD.
 */
package com.midokura.midonet.client.dto;

import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.UUID;

@XmlRootElement
public class DtoPortGroupPort {

    private UUID portGroupId;
    private UUID portId;
    private URI uri;
    private URI portGroup;
    private URI port;

    public UUID getPortGroupId() {
        return portGroupId;
    }

    public void setPortGroupId(UUID portGroupId) {
        this.portGroupId = portGroupId;
    }

    public UUID getPortId() {
        return portId;
    }

    public void setPortId(UUID portId) {
        this.portId = portId;
    }

    public URI getPortGroup() {
        return portGroup;
    }

    public void setPortGroup(URI portGroup) {
        this.portGroup = portGroup;
    }

    public URI getPort() {
        return port;
    }

    public void setPort(URI port) {
        this.port = port;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DtoPortGroupPort that = (DtoPortGroupPort) o;

        if (portGroupId != null ? !portGroupId.equals(that.portGroupId)
                : that.portGroupId != null)
            return false;
        if (portId != null ? !portId.equals(that.portId) : that.portId != null)
            return false;
        if (uri != null ? !uri.equals(that.uri) : that.uri != null)
            return false;
        if (portGroup != null ?
                !portGroup.equals(that.portGroup) : that.portGroup != null)
            return false;
        if (port != null ? !port.equals(that.port) : that.port != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = portGroupId != null ? portGroupId.hashCode() : 0;
        result = 31 * result + (portId != null ? portId.hashCode() : 0);
        result = 31 * result + (portGroup != null ? portGroup.hashCode() : 0);
        result = 31 * result + (uri != null ? uri.hashCode() : 0);
        result = 31 * result + (port != null ? port.hashCode() : 0);
        return result;
    }
}
