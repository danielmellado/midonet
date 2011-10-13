/*
 * @(#)Router        1.6 11/09/05
 *
 * Copyright 2011 Midokura KK
 */
package com.midokura.midolman.mgmt.data.dto;

import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;

import com.midokura.midolman.mgmt.data.dao.RouterZkManagerProxy.RouterMgmtConfig;
import com.midokura.midolman.mgmt.data.dao.RouterZkManagerProxy.RouterNameMgmtConfig;

/**
 * Class representing Virtual Router.
 * 
 * @version 1.6 05 Sept 2011
 * @author Ryu Ishimoto
 */
@XmlRootElement
public class Router {

    private UUID id = null;
    private String name = null;
    private String tenantId = null;

    /**
     * Get router ID.
     * 
     * @return Router ID.
     */
    public UUID getId() {
        return id;
    }

    /**
     * Set router ID.
     * 
     * @param id
     *            ID of the router.
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Get router name.
     * 
     * @return Router name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set router name.
     * 
     * @param name
     *            Name of the router.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get tenant ID.
     * 
     * @return Tenant ID.
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * Set tenant ID.
     * 
     * @param tenantId
     *            Tenant ID of the router.
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public RouterMgmtConfig toMgmtConfig() {
        return new RouterMgmtConfig(this.getTenantId(), this.getName());
    }

    public static Router createRouter(UUID id, RouterMgmtConfig config) {
        Router router = new Router();
        router.setName(config.name);
        router.setTenantId(config.tenantId);
        router.setId(id);
        return router;
    }

    public RouterNameMgmtConfig toNameMgmtConfig() {
        return new RouterNameMgmtConfig(this.getId());
    }
}
