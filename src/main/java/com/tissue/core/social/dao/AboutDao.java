package com.tissue.core.social.dao;

import com.tissue.core.social.About;
import java.util.List;

public interface AboutDao {

    About create(About about);

    List<About> getAbouts();

}
