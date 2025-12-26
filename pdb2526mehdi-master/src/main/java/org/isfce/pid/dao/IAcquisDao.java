package org.isfce.pid.dao;

import org.isfce.pid.model.Acquis;
import org.isfce.pid.model.Acquis.IdAcquis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IAcquisDao extends JpaRepository<Acquis,IdAcquis>
{
	int countByIdFkUE(String codeUE);

}
