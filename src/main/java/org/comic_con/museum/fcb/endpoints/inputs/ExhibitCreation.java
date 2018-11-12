package org.comic_con.museum.fcb.endpoints.inputs;

import org.comic_con.museum.fcb.models.Exhibit;
import org.comic_con.museum.fcb.models.User;

import java.time.Instant;

public class ExhibitCreation {
    private String title;
    private String description;
    private String[] tags;

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String[] getTags() { return tags; }
    
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setTags(String[] tags) { this.tags = tags; }
    
    public Exhibit build(User author) {
        // We don't do any validation in here because different endpoints have
        // different requirements, so each one implements its own.
        return new Exhibit(-1, this.title, this.description, author.getId(), Instant.now(), this.tags, null);
    }
}