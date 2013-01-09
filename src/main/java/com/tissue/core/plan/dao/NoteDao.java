package com.tissue.core.plan.dao;

import com.tissue.core.plan.Note;
import java.util.List;

public interface NoteDao {

    /**
     * Add a note.
     */
    Note create(Note note);


}
