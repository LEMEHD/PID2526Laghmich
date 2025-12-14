package org.isfce.pid.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
import org.isfce.pid.model.SupportingDocument;


public interface ISupportingDocumentDao extends JpaRepository<SupportingDocument, UUID> { }