package org.comic_con.museum.fcb.controllers;

import org.comic_con.museum.fcb.models.dal.ExhibitDAL;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ExhibitSupportEndpoints {
    @RequestMapping(value = "/support/exhibit/{id}", method = RequestMethod.POST)
    public ResponseEntity supportExhibit(@PathVariable int id, @RequestBody String data) {
        String user = "nic";
        boolean newSupporter = ExhibitDAL.addSupporter(id, user, data);
        if (newSupporter) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value = "/support/exhibit/{id}", method = RequestMethod.DELETE)
    public ResponseEntity upvoteExhibit(@PathVariable int id) {
        String user = "nic";
        boolean wasSupporter = ExhibitDAL.removeSupporter(id, user);
        if (wasSupporter) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
