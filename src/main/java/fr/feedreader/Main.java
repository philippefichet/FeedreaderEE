/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.wildfly.swarm.container.Container;
import org.wildfly.swarm.datasources.Datasource;
import org.wildfly.swarm.datasources.DatasourcesFraction;
import org.wildfly.swarm.datasources.Driver;
import org.wildfly.swarm.ejb.EJBFraction;
import org.wildfly.swarm.jaxrs.JAXRSFraction;
import org.wildfly.swarm.jpa.JPAFraction;
import org.wildfly.swarm.logging.LoggingFraction;
import org.wildfly.swarm.transactions.TransactionsFraction;
import org.wildfly.swarm.undertow.UndertowFraction;
import org.wildfly.swarm.undertow.WARArchive;
import org.wildfly.swarm.weld.WeldFraction;
/**
 *
 * @author glopinous
 */
public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("Wildfly swarm powa !!!!!");
        Container container = new Container();
//        container.subsystem(LoggingFraction.createTraceLoggingFraction());
        container.subsystem(LoggingFraction.createDefaultLoggingFraction());
        container.subsystem(new UndertowFraction());
        container.subsystem(new WeldFraction());
        container.subsystem(new TransactionsFraction());
        container.subsystem(new EJBFraction());
        container.subsystem(new JAXRSFraction());
        
        container.subsystem(new DatasourcesFraction()
            .driver(new Driver("hsqldb")
                .datasourceClassName("org.hsqldb.jdbc.JDBCDriver")
                .module("org.hsqldb")
            ).datasource(new Datasource("feedreader")
                .driver("hsqldb")
                .connectionURL("jdbc:hsqldb:file:~/.feedreader/hsqldb")
                .authentication("feedreader", "a5tY6d4u7")
            )
        );
        container.subsystem(new JPAFraction()
            .defaultDatasourceName("feedreader")
        );
        
        WARArchive archive = ShrinkWrap.create(WARArchive.class);
        archive.addPackages(true, "fr.feedreader");
        
        archive.addAsWebInfResource(
            new ClassLoaderAsset("META-INF/persistence.xml", Main.class.getClassLoader()), "classes/META-INF/persistence.xml"
        );
        
//        archive.addAsWebResource(
//            new ClassLoaderAsset("index.jsp", Main.class.getClassLoader()), "index.jsp"
//        );
        URL resource = Main.class.getClassLoader().getResource("index.jsp");
        switch (resource.getProtocol()) {
            case "file":
                File f = new File(resource.getPath());
                File p = new File(f.getParent());
                Files.walk(p.toPath()).parallel().filter((path) -> {
                    File file = path.toFile();
                    if (file.isFile()) {
                        return file.toString().lastIndexOf(".class") == -1;
                    }
                    return false;
                }).forEach((path) -> {
                    String file = path.toFile().toString().replace(p.getAbsolutePath() + File.separator, "");
                    archive.addAsWebResource(
                            new ClassLoaderAsset(file, Main.class.getClassLoader()), file
                    );
                }); break;
            case "jar":
                String jarPath = resource.toString().replace("!/index.jsp", "").replace("jar:file:", "");
                JarFile jar = new JarFile(new File(jarPath));
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry nextElement = entries.nextElement();
                    String name = nextElement.getName();
                    if (nextElement.isDirectory() == false && name.lastIndexOf(".class") == -1 && name.startsWith(".") == false) {
                        archive.addAsWebResource(
                                new ClassLoaderAsset(name, Main.class.getClassLoader()), name
                        );
                    }
                }
                break;
        }
        
        archive.addAllDependencies();
        container.start().deploy(archive);
    }
}
