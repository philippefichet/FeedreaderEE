/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader;

import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.cdi.CDIFraction;
import org.wildfly.swarm.container.Container;
import org.wildfly.swarm.datasources.DatasourcesFraction;
import org.wildfly.swarm.ejb.EJBFraction;
import org.wildfly.swarm.jaxrs.JAXRSFraction;
import org.wildfly.swarm.jpa.JPAFraction;
import org.wildfly.swarm.transactions.TransactionsFraction;

/**
 *
 * @author glopinous
 */
public class SwarmMain {
    static public void main(String[] args) throws Exception {
        Swarm container = new Swarm();
        container.fraction(new JAXRSFraction());
        container.fraction(new CDIFraction());
        container.fraction(new DatasourcesFraction().jdbcDriver("hsqldb", (d) -> {
            d.driverClassName("org.hsqldb.jdbc.JDBCDriver");
            d.driverModuleName("org.hsqldb");
        }).dataSource("feedreader", (ds) -> {
            ds.driverName("hsqldb");
            ds.connectionUrl(System.getProperty("feedreader.url", "jdbc:hsqldb:file:~/.feedreader/hsqldb"));
            ds.userName(System.getProperty("feedreader.username", "feedreader"));
            ds.password(System.getProperty("feedreader.password", "a5tY6d4u7"));
        }));
        container.fraction(new JPAFraction());
        container.fraction(EJBFraction.createDefaultFraction());
        container.fraction(TransactionsFraction.createDefaultFraction());
        container.start();
        container.deploy();
    }
}
