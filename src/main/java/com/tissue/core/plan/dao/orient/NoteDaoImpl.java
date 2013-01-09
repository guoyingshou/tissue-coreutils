package com.tissue.core.plan.dao.orient;

import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.util.OrientDataSource;
import com.tissue.core.converter.PostConverter;
import com.tissue.core.profile.User;
import com.tissue.core.plan.Note;
import com.tissue.core.plan.dao.NoteDao;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.nio.charset.Charset;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.tx.OTransaction;

@Component
public class NoteDaoImpl implements NoteDao {

    @Autowired
    private OrientDataSource dataSource;

    public Note create(Note note) {
        OGraphDatabase db = dataSource.getDB();
        try {
            ODocument doc = PostConverter.convert(note);
            doc.save();

            String ridNote = doc.getIdentity().toString();
            String ridUser = OrientIdentityUtil.decode(note.getUser().getId());

            String sql = "create edge EdgeNote from " + 
                          ridUser + 
                          " to " + 
                          ridNote + 
                          " set label = 'note', createTime = sysdate()";

            OCommandSQL cmd = new OCommandSQL(sql);
            db.command(cmd).execute();

            String noteId = OrientIdentityUtil.encode(ridNote);
            note.setId(noteId);
        }
        catch(Exception exc) {
            exc.printStackTrace();
        }
        finally {
            db.close();
        }
        return note;
    }

}
