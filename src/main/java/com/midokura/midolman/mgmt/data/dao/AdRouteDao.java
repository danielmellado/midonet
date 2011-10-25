package com.midokura.midolman.mgmt.data.dao;

import java.util.List;
import java.util.UUID;

import com.midokura.midolman.mgmt.data.dto.AdRoute;
import com.midokura.midolman.state.StateAccessException;

public interface AdRouteDao extends OwnerQueryable {

    UUID create(AdRoute adRoute) throws StateAccessException;

    AdRoute get(UUID id) throws StateAccessException;

    List<AdRoute> list(UUID bgpId) throws StateAccessException;

    void delete(UUID id) throws StateAccessException;
}
