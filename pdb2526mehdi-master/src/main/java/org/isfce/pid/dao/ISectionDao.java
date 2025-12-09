package org.isfce.pid.dao;

import org.isfce.pid.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ISectionDao extends JpaRepository<Section, String> {

}
