package co.kirikiri;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Component
@ActiveProfiles("test")
@Transactional
public class DatabaseCleaner implements InitializingBean {

    private static final List<String> generatedTableNames = List.of("MEMBER", "MEMBER_PROFILE",
            "MEMBER_PROFILE_IMAGE", "ROADMAP", "ROADMAP_CATEGORY", "ROADMAP_CONTENT", "ROADMAP_NODE",
            "ROADMAP_NODE_IMAGE");

    @PersistenceContext
    private EntityManager entityManager;

    private List<String> tableNames;

    @Override
    public void afterPropertiesSet() {
        final Session session = entityManager.unwrap(Session.class);
        session.doWork(this::extractTableNames);
    }

    private void extractTableNames(final Connection conn) throws SQLException {
        final List<String> tableNames = new ArrayList<>();

        final ResultSet tables = conn.getMetaData()
                .getTables(conn.getCatalog(), null, "%", new String[]{"TABLE"});

        while (tables.next()) {
            tableNames.add(tables.getString("table_name"));
        }

        this.tableNames = generatedTableNames;
    }

    public void execute() {
        final Session session = entityManager.unwrap(Session.class);
        session.doWork(this::cleanUpDatabase);
    }

    private void cleanUpDatabase(final Connection conn) throws SQLException {
        final Statement statement = conn.createStatement();
        statement.executeUpdate("SET REFERENTIAL_INTEGRITY FALSE");

        for (final String tableName : tableNames) {
            statement.executeUpdate("TRUNCATE TABLE " + tableName);
            statement.executeUpdate("ALTER TABLE " + tableName + " ALTER COLUMN id RESTART WITH 1");
        }

        statement.executeUpdate("SET REFERENTIAL_INTEGRITY TRUE");
    }
}
