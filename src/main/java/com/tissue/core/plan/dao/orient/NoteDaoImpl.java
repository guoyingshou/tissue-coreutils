package com.tissue.core.plan.dao.orient;

import com.tissue.core.orient.dao.OrientDao;
import com.tissue.core.util.OrientIdentityUtil;
import com.tissue.core.mapper.PostMapper;
import com.tissue.core.social.User;
import com.tissue.core.plan.Note;
import com.tissue.core.plan.dao.NoteDao;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;

@Component
public class NoteDaoImpl extends OrientDao implements NoteDao {

    public Note create(Note note) {
        OGraphDatabase db = dataSource.getDB();

        try {
            ODocument doc = PostMapper.convert(note);
            saveDoc(doc);

            String ridNote = doc.getIdentity().toString();
            String ridUser = OrientIdentityUtil.decode(note.getUser().getId());
            String ridPlan = OrientIdentityUtil.decode(note.getPlan().getId());

            String sql = "update " + ridNote + " set plan = " + ridPlan;
            executeCommand(db, sql);

            sql = "create edge EdgePost from " + ridUser + " to " + ridNote+" set label = 'note', createTime = sysdate()";
            executeCommand(db, sql);

            note.setId(OrientIdentityUtil.encode(ridNote));
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
