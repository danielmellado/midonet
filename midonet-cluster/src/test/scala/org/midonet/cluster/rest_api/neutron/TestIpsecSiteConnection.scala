/*
 * Copyright 2015 Midokura SARL
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.midonet.cluster.rest_api.neutron

import java.util.{ArrayList, UUID}

import com.sun.jersey.api.client.{ClientResponse, WebResource}
import com.sun.jersey.api.client.ClientResponse.Status

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, FeatureSpec, Matchers}

import com.google.common.collect.Lists

import org.midonet.cluster.rest_api.neutron.models._
import org.midonet.cluster.rest_api.neutron.models.IPSecSiteConnection._
import org.midonet.cluster.rest_api.neutron.models.IPSecSiteConnection.{Status => NeutronStatus}
import org.midonet.cluster.rest_api.rest_api.FuncJerseyTest
import org.midonet.cluster.services.rest_api.MidonetMediaTypes._

@RunWith(classOf[JUnitRunner])
class TestIpsecSiteConnection extends FeatureSpec
        with Matchers
        with BeforeAndAfter {

    private var vpnServiceResource: WebResource = _
    private var ipsecSiteConnectionResource: WebResource = _
    private var networkResource: WebResource = _
    private var subnetResource: WebResource = _
    private var routerResource: WebResource = _
    private var portResource: WebResource = _

    var jerseyTest: FuncJerseyTest = _

    before {
        jerseyTest = new FuncJerseyTest
        jerseyTest.setUp()
        vpnServiceResource = jerseyTest.resource().path("/neutron/vpnservices")
        ipsecSiteConnectionResource =
            jerseyTest.resource().path("/neutron/ipsec_site_connections")
        networkResource = jerseyTest.resource().path("/neutron/networks")
        subnetResource = jerseyTest.resource().path("/neutron/subnets")
        routerResource = jerseyTest.resource().path("/neutron/routers")
        portResource = jerseyTest.resource().path("/neutron/ports")
    }

    after {
        jerseyTest.tearDown()
    }

    scenario("Neutron has Ipsec Site Connection") {

        val neutron = jerseyTest.resource().path("/neutron")
            .accept(NEUTRON_JSON_V3)
            .get(classOf[Neutron])
        neutron.ipsecSiteConnections shouldNot be(null)
        neutron.ipsecSiteConnectionTemplate shouldNot be(null)
    }

    scenario("Create, Read, Update, Delete") {
        val network = new Network()
        network.id = UUID.randomUUID
        network.name = "test-network"
        network.adminStateUp = true

        val subnet = new Subnet()
        subnet.id = UUID.randomUUID
        subnet.networkId = network.id
        subnet.cidr = "10.0.0.0/24"
        subnet.gatewayIp = "10.0.0.1"
        subnet.ipVersion = 4

        val extNetwork = new Network()
        extNetwork.id = UUID.randomUUID
        extNetwork.name = "ext-network"
        extNetwork.adminStateUp = true

        val extSubnet = new Subnet()
        extSubnet.id = UUID.randomUUID
        extSubnet.networkId = extNetwork.id
        extSubnet.cidr = "1.0.0.0/24"
        extSubnet.ipVersion = 4

        val extPort = new Port()
        extPort.id = UUID.randomUUID
        extPort.networkId = extNetwork.id
        extPort.macAddress = "ac:ca:ba:33:cb:a2"
        extPort.fixedIps = new ArrayList[IPAllocation]() {
            add(new IPAllocation("1.0.0.1", extSubnet.id));
        }
        extPort.deviceOwner = DeviceOwner.ROUTER_GW

        val router = new Router()
        router.id = UUID.randomUUID
        router.adminStateUp = true
        router.gwPortId = extPort.id
        router.externalGatewayInfo = new ExternalGatewayInfo()
        router.externalGatewayInfo.networkId = extNetwork.id

        networkResource.`type`(NeutronMediaType.NETWORK_JSON_V1)
            .post(classOf[ClientResponse],
                  network).getStatus shouldBe Status.CREATED.getStatusCode
        networkResource.`type`(NeutronMediaType.NETWORK_JSON_V1)
            .post(classOf[ClientResponse],
                  extNetwork).getStatus shouldBe Status.CREATED.getStatusCode
        subnetResource.`type`(NeutronMediaType.SUBNET_JSON_V1)
            .post(classOf[ClientResponse],
                  subnet).getStatus shouldBe Status.CREATED.getStatusCode
        subnetResource.`type`(NeutronMediaType.SUBNET_JSON_V1)
            .post(classOf[ClientResponse],
                  extSubnet).getStatus shouldBe Status.CREATED.getStatusCode
        portResource.`type`(NeutronMediaType.PORT_JSON_V1)
            .post(classOf[ClientResponse],
                  extPort).getStatus shouldBe Status.CREATED.getStatusCode
        routerResource.`type`(NeutronMediaType.ROUTER_JSON_V1)
            .post(classOf[ClientResponse],
                  router).getStatus shouldBe Status.CREATED.getStatusCode

        val vpnService = new VpnService()
        vpnService.id = UUID.randomUUID
        vpnService.name = vpnService.id + "-name"
        vpnService.description = vpnService.id + "-desc"
        vpnService.routerId = router.id
        vpnService.subnetId = subnet.id

        val response = vpnServiceResource.`type`(NEUTRON_VPN_SERVICE_JSON_V1)
            .post(classOf[ClientResponse], vpnService)
        response.getStatus shouldBe Status.CREATED.getStatusCode

        val ipsecSiteConnection = new IPSecSiteConnection()
        ipsecSiteConnection.id = UUID.randomUUID
        ipsecSiteConnection.peerAddress = "100.100.100.1"
        ipsecSiteConnection.peerId = "foobar"
        ipsecSiteConnection.peerCidrs = Lists.newArrayList("192.168.10.0/24")
        ipsecSiteConnection.routeMode = RouteMode.STATIC
        ipsecSiteConnection.mtu = 1234
        ipsecSiteConnection.initiator = Initiator.RESPONSE_ONLY
        ipsecSiteConnection.authMode = AuthMode.PSK
        ipsecSiteConnection.status = NeutronStatus.BUILD
        ipsecSiteConnection.dpdAction = DpdAction.RESTART
        ipsecSiteConnection.dpdTimeout = 54321
        ipsecSiteConnection.vpnServiceId = vpnService.id

        ipsecSiteConnection.ikePolicy = new IkePolicy()
        ipsecSiteConnection.ikePolicy.authAlgorithm = IPSecAuthAlgorithm.SHA1
        ipsecSiteConnection.ikePolicy.encryptionAlgorithm =
            IPSecEncryptionAlgorithm.AES_192
        ipsecSiteConnection.ikePolicy.phase1NegMode =
            IkePolicy.Phase1NegotiationMode.MAIN
        ipsecSiteConnection.ikePolicy.ikeVersion = IkePolicy.IkeVersion.V2
        ipsecSiteConnection.ikePolicy.pfs = IPSecPfs.GROUP5

        ipsecSiteConnection.ipsecPolicy = new IPSecPolicy()
        ipsecSiteConnection.ipsecPolicy.transformProtocol =
            IPSecPolicy.TransformProtocol.AH
        ipsecSiteConnection.ipsecPolicy.authAlgorithm = IPSecAuthAlgorithm.SHA1
        ipsecSiteConnection.ipsecPolicy.encryptionAlgorithm =
            IPSecEncryptionAlgorithm.DES_3
        ipsecSiteConnection.ipsecPolicy.encapsulationMode =
            IPSecPolicy.EncapsulationMode.TRANSPORT
        ipsecSiteConnection.ipsecPolicy.pfs = IPSecPfs.GROUP2

        val response2 = ipsecSiteConnectionResource
            .`type`(NEUTRON_IPSEC_SITE_CONNECTION_JSON_V1)
            .post(classOf[ClientResponse], ipsecSiteConnection)
        response2.getStatus shouldBe Status.CREATED.getStatusCode

        val createdUri = response2.getLocation

        val respDto = ipsecSiteConnectionResource.uri(createdUri)
            .accept(NEUTRON_IPSEC_SITE_CONNECTION_JSON_V1)
            .get(classOf[IPSecSiteConnection])
        respDto shouldBe ipsecSiteConnection

        ipsecSiteConnection.peerId = "foobar2"
        ipsecSiteConnection.peerAddress = "200.200.200.1"
        ipsecSiteConnection.ikePolicy.encryptionAlgorithm =
            IPSecEncryptionAlgorithm.DES_3
        ipsecSiteConnection.ipsecPolicy.encapsulationMode =
            IPSecPolicy.EncapsulationMode.TRANSPORT

        val response3 = ipsecSiteConnectionResource.uri(createdUri)
            .`type`(NEUTRON_IPSEC_SITE_CONNECTION_JSON_V1)
            .put(classOf[ClientResponse], ipsecSiteConnection)
        response3.getStatusInfo
            .getStatusCode shouldBe Status.NO_CONTENT.getStatusCode

        val respDto2 = ipsecSiteConnectionResource.uri(createdUri)
            .accept(NEUTRON_IPSEC_SITE_CONNECTION_JSON_V1)
            .get(classOf[IPSecSiteConnection])
        respDto2 shouldBe ipsecSiteConnection

        val response4 = ipsecSiteConnectionResource.uri(createdUri)
            .delete(classOf[ClientResponse])
        response4.getStatusInfo
            .getStatusCode shouldBe Status.NO_CONTENT.getStatusCode

        val respDto3 = ipsecSiteConnectionResource.uri(createdUri)
            .get(classOf[ClientResponse])
        respDto3.getStatusInfo
            .getStatusCode shouldBe Status.NOT_FOUND.getStatusCode
    }

}
