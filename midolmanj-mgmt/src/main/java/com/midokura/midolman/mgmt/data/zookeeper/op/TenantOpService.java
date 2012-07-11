/*
 * Copyright 2011 Midokura KK
 * Copyright 2012 Midokura PTE LTD.
 */
package com.midokura.midolman.mgmt.data.zookeeper.op;

import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.Op;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.midokura.midolman.mgmt.data.dao.PortGroupDao;
import com.midokura.midolman.mgmt.data.dao.zookeeper.ChainZkDao;
import com.midokura.midolman.mgmt.data.dao.zookeeper.TenantZkDao;
import com.midokura.midolman.mgmt.data.dto.Chain;
import com.midokura.midolman.state.NoStatePathException;
import com.midokura.midolman.state.StateAccessException;

/**
 * Tenant Op service.
 */
public class TenantOpService {

    private final static Logger log = LoggerFactory
            .getLogger(TenantOpService.class);
    private final TenantZkDao zkDao;
    private final TenantOpBuilder opBuilder;
    private final ChainZkDao chainZkDao;
    private final BridgeOpService bridgeOpService;
    private final RouterOpService routerOpService;
    private final PortGroupDao groupDao;

    /**
     * Constructor
     *
     * @param opBuilder
     *            TenantOpBuilder object
     * @param bridgeOpService
     *            BridgeOpService object
     * @param routerOpService
     *            RouterOpService object
     * @param zkDao
     *            Tenant DAO.
     */
    public TenantOpService(TenantOpBuilder opBuilder,
            BridgeOpService bridgeOpService, RouterOpService routerOpService,
            TenantZkDao zkDao, ChainZkDao chainZkDao, PortGroupDao groupDao) {
        this.opBuilder = opBuilder;
        this.bridgeOpService = bridgeOpService;
        this.routerOpService = routerOpService;
        this.chainZkDao = chainZkDao;
        this.groupDao = groupDao;
        this.zkDao = zkDao;
    }

    /**
     * Build list of Op objects to create a tenant
     *
     * @param id
     *            Tenant ID
     * @return List of Op objects
     * @throws StateAccessException
     *             Data access error
     */
    public List<Op> buildCreate(String id) throws StateAccessException {
        log.debug("TenantOpService.buildCreate entered: id={}", id);

        List<Op> ops = new ArrayList<Op>();
        ops.add(opBuilder.getTenantCreateOp(id));
        ops.add(opBuilder.getTenantBridgesCreateOp(id));
        ops.add(opBuilder.getTenantRoutersCreateOp(id));
        ops.add(opBuilder.getTenantChainsCreateOp(id));
        ops.add(opBuilder.getTenantBridgeNamesCreateOp(id));
        ops.add(opBuilder.getTenantRouterNamesCreateOp(id));
        ops.add(opBuilder.getTenantChainNamesCreateOp(id));
        ops.add(opBuilder.getTenantPortGroupNamesCreateOp(id));

        log.debug("TenantOpService.buildCreate exiting: ops count={}",
                ops.size());
        return ops;
    }

    /**
     * Build list of Op objects to delete a tenant
     *
     * @param id
     *            Tenant ID
     * @return List of Op objects
     * @throws StateAccessException
     *             Data access error
     */
    public List<Op> buildDelete(String id) throws StateAccessException {
        log.debug("TenantOpService.buildDelete entered: id={}", id);

        if (!zkDao.exists(id)) {
            throw new NoStatePathException("Tenant " + id + " does not exist");
        }

        List<Op> ops = new ArrayList<Op>();

        ops.addAll(groupDao.buildTenantPortGroupsDelete(id));
        ops.addAll(routerOpService.buildTenantRoutersDelete(id));
        ops.addAll(bridgeOpService.buildTenantBridgesDelete(id));

        // Remove chains
        List<Chain> chains = chainZkDao.list(id);
        for (Chain chain : chains) {
            ops.addAll(chainZkDao.prepareDelete(chain));
        }

        ops.add(opBuilder.getTenantPortGroupNamesDeleteOp(id));
        ops.add(opBuilder.getTenantChainNamesDeleteOp(id));
        ops.add(opBuilder.getTenantRouterNamesDeleteOp(id));
        ops.add(opBuilder.getTenantBridgeNamesDeleteOp(id));
        ops.add(opBuilder.getTenantChainsDeleteOp(id));
        ops.add(opBuilder.getTenantRoutersDeleteOp(id));
        ops.add(opBuilder.getTenantBridgesDeleteOp(id));
        ops.add(opBuilder.getTenantDeleteOp(id));

        log.debug("TenantOpService.buildDelete exiting: ops count={}",
                ops.size());
        return ops;
    }
}
