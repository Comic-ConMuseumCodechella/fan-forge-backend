package integration.given;

import com.zaxxer.hikari.util.DriverDataSource;
import org.comic_conmuseum.fan_forge.backend.models.Artifact;
import org.comic_conmuseum.fan_forge.backend.models.Exhibit;
import org.comic_conmuseum.fan_forge.backend.models.Survey;
import org.comic_conmuseum.fan_forge.backend.models.User;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import states.Stage;

import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.comic_conmuseum.fan_forge.backend.util.SqlTypeConverters.timestampOf;

@Component
public class GivenDB extends Stage<GivenDB> {
    @Value("${spring.datasource.url}")
    String sqlUrl;
    
    @Value("${spring.datasource.driver}")
    String sqlDriver;
    
    @Value("${spring.datasource.username}")
    String sqlUsername;
    
    @Value("${spring.datasource.password}")
    String sqlPassword;
    
    private NamedParameterJdbcTemplate makeSql() {
        return new NamedParameterJdbcTemplate(
                new JdbcTemplate(
                        new DriverDataSource(
                                sqlUrl, sqlDriver,
                                new Properties(),
                                sqlUsername, sqlPassword
                        )
                )
        );
    }
    
    private NamedParameterJdbcTemplate sql;
    
    @Before
    public void setUpSql() {
        this.sql = makeSql();
        this.sql.execute(
                "TRUNCATE artifacts, comments, exhibits, supports",
                PreparedStatement::execute
        );
    }
    
    @Override
    public void reset() { }
    
    @Autowired
    WebApplicationContext wac;
    
    public GivenDB exhibitDoesntExist(long id) {
        sql.update(
                "DELETE FROM exhibits WHERE eid = :eid",
                new MapSqlParameterSource("eid", id)
        );
        return this;
    }
    
    public GivenDB exhibitExists(Exhibit ex) {
        sql.update(
                "INSERT INTO exhibits (" +
                "    eid, title, description, author, tags, created, featured" +
                ") VALUES (" +
                "    :id, :title, :description, :author, :tags, :created, :featured" +
                ")",
                new MapSqlParameterSource()
                        .addValue("id", ex.getId())
                        .addValue("title", ex.getTitle())
                        .addValue("description", ex.getDescription())
                        .addValue("author", ex.getAuthor())
                        .addValue("created", timestampOf(ex.getCreated()))
                        .addValue("tags", ex.getTags())
                        .addValue("featured", false)

        );
        return this;
    }

    public GivenDB authTokenExists(String token, User user) {
        // TODO add token to DB when that's a thing
        return this;
    }
    
    public GivenDB noSupportsFor(long id) {
        sql.update(
                "DELETE FROM supports WHERE exhibit = :id",
                new MapSqlParameterSource("id", id)
        );
        return this;
    }
    
    private static String POPULATION_COLUMNS =
            Arrays.stream(Survey.Population.values())
                    .map(Survey.Population::column)
                    .collect(Collectors.joining(", "));
    private static String POPULATION_PARAMS =
            Arrays.stream(Survey.Population.values())
                    .map(Survey.Population::sqlParam)
                    .collect(Collectors.joining(", "));
    public GivenDB supportExists(long eid, Survey survey) {
        MapSqlParameterSource params =
                new MapSqlParameterSource("exhibit", eid)
                        .addValue("supporter", survey.supporter)
                        .addValue("visits", survey.visits)
                        .addValue("rating", survey.rating);
        for (Survey.Population pop : Survey.Population.values()) {
            params.addValue(pop.column(), survey.populations.get(pop.display()));
        }
        sql.update(
                "INSERT INTO supports (" +
                "    exhibit, supporter, visits, rating, " + POPULATION_COLUMNS +
                ") " +
                "VALUES (" +
                "    :exhibit, :supporter, :visits, :rating, " + POPULATION_PARAMS +
                ")",
                params
        );
        return this;
    }
    
    public GivenDB noCommentsFor(long id) {
        sql.update(
                "DELETE FROM comments WHERE exhibit = :id",
                new MapSqlParameterSource("id", id)
        );
        return this;
    }
    
    public GivenDB noArtifactsFor(long id) {
        sql.update(
                "DELETE FROM artifacts WHERE exhibit = :id",
                new MapSqlParameterSource("id", id)
        );
        return this;
    }

    public GivenDB artifactExists(Artifact ar) {
        sql.update(
                "INSERT INTO artifacts (" +
                "    aid, title, description, cover, creator, created, exhibit " +
                ") " +
                "VALUES (" +
                "    :aid, :title, :description, :cover, :creator, :created, :exhibit " +
                ")",
                new MapSqlParameterSource()
                        .addValue("aid", ar.getId())
                        .addValue("title", ar.getTitle())
                        .addValue("description", ar.getDescription())
                        .addValue("cover", ar.isCover())
                        .addValue("creator", ar.getCreator())
                        .addValue("created", timestampOf(ar.getCreated()))
                        .addValue("exhibit", ar.getParent())
        );
        return this;
    }
}
