/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.feedreader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ModelBuilder;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.model.idea.IdeaDependency;
import org.gradle.tooling.model.idea.IdeaModule;
import org.gradle.tooling.model.idea.IdeaProject;
import org.gradle.tooling.model.idea.IdeaSingleEntryLibraryDependency;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 *
 * @author glopinous
 */
public class Bootstrap {

    public static WebArchive addAsLibrary(WebArchive war) throws IOException {

        File build = new File("./build.gradle");
        File buildCache = new File("./build/.dependencies-" + build.lastModified());
        long startDependency = System.currentTimeMillis();
        if (buildCache.exists()) {
            System.out.println("Load dependency from file \"" + buildCache.getAbsolutePath() + "\"");
            List<String> files = Files.readAllLines(buildCache.toPath());
            for (String file : files) {
                if (file != null) {
                    war.addAsLibrary(new File(file));
                }
            }
        } else {
            StringBuilder sb = new StringBuilder();
            File project = new File(".");
            System.out.println("Load dependency from project \"" + project.getAbsolutePath() + "\"");
            GradleConnector newConnector = org.gradle.tooling.GradleConnector.newConnector();
            ProjectConnection connect = newConnector.forProjectDirectory(project).connect();
            ModelBuilder<IdeaProject> model = connect.model(IdeaProject.class);
            for (IdeaModule module : model.get().getModules()) {
                for (IdeaDependency dependency : module.getDependencies()) {
                    sb.append(((IdeaSingleEntryLibraryDependency)dependency).getFile().getAbsolutePath()).append("\n");
                    war.addAsLibraries(((IdeaSingleEntryLibraryDependency)dependency).getFile());
                }
            }
            Files.write(buildCache.toPath(), sb.toString().getBytes());
        }
        System.out.println("Add dependency file in " + (System.currentTimeMillis() - startDependency) + " ms.");
        return war;
    }
    
//    public static WebArchive createWebArchive() throws IOException {
//        WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war")
//            .addClasses(Bootstrap.class,
//                UserProduct.class,
//                Ticket.class,
//                Client.class,
//                ClientForgottenPassword.class,
//                ClientBuisness.class
//            )
//            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
//            .addAsResource("fr/philippefichet/ticketbooster/db/db.changelog-1.0.xml", "src/main/resources/fr/philippefichet/ticketbooster/db/db.changelog-1.0.xml")
//            .addAsResource("fr/philippefichet/ticketbooster/db/db.changelog-1.1.xml", "src/main/resources/fr/philippefichet/ticketbooster/db/db.changelog-1.1.xml")
//            .addAsResource("fr/philippefichet/ticketbooster/db/db.changelog-1.2.xml", "src/main/resources/fr/philippefichet/ticketbooster/db/db.changelog-1.2.xml")
//            .addAsResource("fr/philippefichet/ticketbooster/db/db.changelog-1.3.xml", "src/main/resources/fr/philippefichet/ticketbooster/db/db.changelog-1.3.xml")
//            .addAsResource("fr/philippefichet/ticketbooster/db/db.changelog-1.4.xml", "src/main/resources/fr/philippefichet/ticketbooster/db/db.changelog-1.4.xml")
//            .addAsResource("fr/philippefichet/ticketbooster/db/db.changelog-1.5.xml", "src/main/resources/fr/philippefichet/ticketbooster/db/db.changelog-1.5.xml")
//            .addAsResource("fr/philippefichet/ticketbooster/db/db.changelog-1.6.xml", "src/main/resources/fr/philippefichet/ticketbooster/db/db.changelog-1.6.xml")
//            .addAsResource("fr/philippefichet/ticketbooster/db/db.changelog-1.7.xml", "src/main/resources/fr/philippefichet/ticketbooster/db/db.changelog-1.7.xml")
//            .addAsResource("fr/philippefichet/ticketbooster/db/db.changelog-master.xml", "fr/philippefichet/ticketbooster/db/db.changelog-master.xml")
//            .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml");
//        return addAsLibrary(war);
//    }
    
//    public static void initDatabases(EntityManager em) throws LiquibaseException, SQLException {
//        ClassLoaderResourceAccessor ra = new ClassLoaderResourceAccessor(Bootstrap.class.getClassLoader());
//        MySQLDatabase mysql = new MySQLDatabase();
//        SessionImpl session = em.unwrap(SessionImpl.class);
//        mysql.setConnection(new JdbcConnection(session.connection()));
//        Liquibase lq = new Liquibase("fr/philippefichet/ticketbooster/db/db.changelog-master.xml", ra, mysql);
//        lq.update("");
//        try (Connection connection = session.connection(); Statement createStatement = connection.createStatement()) {
//            createStatement.addBatch("SET FOREIGN_KEY_CHECKS=0;");
//            for (EntityType<?> entityType : em.getMetamodel().getEntities()) {
//                String tableName = entityType.getJavaType().getAnnotation(Table.class).name();
//                createStatement.addBatch("TRUNCATE TABLE " + tableName);
//            }
//            createStatement.executeBatch();
//        } catch(SQLException e) {
//            throw e;
//        }
//    }
}
