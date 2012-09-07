package com.midokura.midonet.client.dto;

/*
 * Copyright 2011 Midokura Europe SARL
 */

import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.UUID;

@XmlRootElement
public class DtoPortGroup {

    private UUID id;
    private String tenantId;
    private String name;
    private URI uri;
    private URI ports;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public URI getPorts() {
        return ports;
    }

    public void setPorts(URI ports) {
        this.ports = ports;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DtoPortGroup that = (DtoPortGroup) o;

        if (id != null ? !id.equals(that.id) : that.id != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null)
            return false;
        if (ports != null ? !ports.equals(that.ports) : that.ports != null)
            return false;
        if (tenantId != null ?
                !tenantId.equals(that.tenantId) : that.tenantId != null)
            return false;
        if (uri != null ? !uri.equals(that.uri) : that.uri != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (tenantId != null ? tenantId.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (uri != null ? uri.hashCode() : 0);
        result = 31 * result + (ports != null ? ports.hashCode() : 0);
        return result;
    }
}
